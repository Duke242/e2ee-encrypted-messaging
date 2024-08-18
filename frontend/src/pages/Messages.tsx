import React, { useState, useEffect } from "react";
import { JwtPayload, jwtDecode } from "jwt-decode";
import { decryptMessage, sendMessage } from "../libs/crypto";
import { toast } from 'react-hot-toast';

interface Message {
  id: number;
  senderId: number;
  recipientId: number;
  content: string;
  timestamp: string;
  isEncrypted: boolean;
  sender: {
    email: string;
    publicKey: string;
  };
  recipient: {
    email: string;
    publicKey: string;
  };
  decryptedContent?: string;
}

interface ConversationsData {
  [otherUserId: string]: Message[];
}

interface CustomJwtPayload extends JwtPayload {
  id?: number;
  sub?: string;
}

interface DecryptedMessage extends Message {
  decryptedContent: string;
}


interface DecryptedConversations {
  [otherUserId: string]: DecryptedMessage[];
}

const Messages: React.FC = () => {
  const [conversations, setConversations] = useState<ConversationsData>({});
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [userId, setUserId] = useState<number | null>(null);
  const [messageContent, setMessageContent] = useState<string>("");
  const [selectedRecipient, setSelectedRecipient] = useState<string | null>(null);
  const [decryptedConversations, setDecryptedConversations] = useState<ConversationsData>({});
  const [userEmail, setUserEmail] = useState<string | null>(null);

  useEffect(() => {
    const decryptConversations = async () => {
      const decrypted: DecryptedConversations = {};
      for (const [otherUserId, messages] of Object.entries(conversations)) {
        decrypted[otherUserId] = await Promise.all(
          messages.map(async (message) => {
            console.log({message})
            try {
              const decryptedContent = await decryptMessage(
                message.sender.publicKey,
                message.content
              );
              return { ...message, decryptedContent };
            } catch (error) {
              console.error('Error decrypting message:', error);
              return { ...message, decryptedContent: <i>Message that you sent can't be seen by you.</i> };
            }
          })
        );
      }
      setDecryptedConversations(decrypted);
    };

    decryptConversations();
  }, [conversations]);


  const handleSendMessage = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!messageContent || selectedRecipient === null || userId === null) return;

    try {
      const token = localStorage.getItem("token")
      const decodedToken = token ? jwtDecode<CustomJwtPayload>(token) : null
      console.log({selectedRecipient})
      const userId = decodedToken?.id ?? null
      if (userId) {
        await sendMessage(userId.toString(), selectedRecipient, messageContent)
        setMessageContent('')
        toast.success('Your message was sent')
      }
    } catch (error) {
      console.error("Error sending message:", error);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem("token");
        if (token) {
          try {
            const decodedToken = jwtDecode<CustomJwtPayload>(token);
            const id = decodedToken.id ?? null;
            const email = decodedToken.sub ?? null;
            setUserEmail(email);
            setUserId(id);

            if (id !== null) {
              const response = await fetch(
                `http://localhost:8080/api/messages/user/${id}/conversations`,
                {
                  method: "GET",
                  headers: {
                    Authorization: `Bearer ${token}`,
                  },
                }
              );

              if (!response.ok) {
                throw new Error("Failed to fetch conversations");
              }

              const data: ConversationsData = await response.json();
              setConversations(data);
            }
          } catch (error) {
            console.error(
              "Failed to decode token or fetch conversations:",
              error
            );
          }
        }
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred"
        );
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  const recipientEmails = Object.values(conversations)
    .flat()
    .map((message) => message.recipient.email);
  const uniqueEmails = Array.from(new Set(recipientEmails));

  return (
    <div className="min-h-screen bg-gray-100 text-gray-900">
      <header className="bg-blue-600 text-white p-4">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">SecureChat</h1>
          <nav>
            <ul className="flex space-x-4">
              <li>
                <a
                  onClick={() => {
                    localStorage.removeItem("token");
                    localStorage.removeItem("privateKey");
                    window.location.reload();
                  }}
                  className="text-white hover:text-white hover:underline cursor-pointer"
                >
                  Log out
                </a>
              </li>
            </ul>
          </nav>
        </div>
      </header>

      <main className="w-full mx-auto mt-8 px-4">
        <section className="text-center mb-12">
          <h2 className="text-4xl font-bold mb-4">Your Conversations</h2>
          <p className="text-xl mb-6">
            Here&apos;s a list of your recent conversations.
          </p>
        </section>

        <section className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {Object.entries(decryptedConversations).map(
            ([otherUserId, messages]) => (
              <div
                key={otherUserId}
                className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition mb-6"
              >
                <h3 className="text-2xl font-semibold mb-4">
                  Conversation with {messages[0].recipient.email}
                </h3>
                {messages.map((message) => (
                  <div
                    key={message.id}
                    className="border-b border-gray-300 pb-4 mb-4"
                  >
                    <p className="mb-2">
                      <strong>
                        {message.senderId === userId ? "You" : "Other"}:
                      </strong>{" "}
                      {message.decryptedContent}
                    </p>
                    <small>
                      {new Date(message.timestamp).toLocaleString()}
                    </small>
                  </div>
                ))}
              </div>
            )
          )}
        </section>

        <section className="bg-white p-6 rounded-lg shadow-md mt-8">
          <h3 className="text-xl font-semibold mb-4">Send a New Message</h3>
          <form onSubmit={handleSendMessage}>
            <div className="flex flex-col mb-4">
              <label htmlFor="recipient" className="mb-2 text-gray-700">
                Select Recipient
              </label>
              <select
                id="recipient"
                value={selectedRecipient ?? ""}
                onChange={(e) => setSelectedRecipient(e.target.value)}
                className="p-2 border border-gray-300 rounded"
                required
              >
                <option value="" disabled>
                  Select a recipient
                </option>
                {uniqueEmails.length > 0 ? (
                  uniqueEmails.map(
                    (email) =>
                      email !== userEmail && (
                        <option key={email} value={email}>
                          {email}
                        </option>
                      )
                  )
                ) : (
                  <option>No emails available</option>
                )}
              </select>
            </div>
            <div className="flex flex-col mb-4">
              <label htmlFor="message" className="mb-2 text-gray-700">
                Message
              </label>
              <textarea
                id="message"
                value={messageContent}
                onChange={(e) => setMessageContent(e.target.value)}
                rows={4}
                className="p-2 border border-gray-300 rounded"
                placeholder="Type your message here..."
                required
              />
            </div>
            <button
              type="submit"
              className="bg-blue-600 text-white p-2 rounded hover:bg-blue-700 transition"
            >
              Send Message
            </button>
          </form>
        </section>
      </main>
    </div>
  );
};

export default Messages;
