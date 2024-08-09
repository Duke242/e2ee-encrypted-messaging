import React, { useState, useEffect } from 'react';

interface Message {
  id: number;
  senderId: number;
  recipientId: number;
  content: string;
  timestamp: string;
  isEncrypted: boolean;
}

interface ConversationsData {
  [otherUserId: string]: Message[];
}

const Messages: React.FC = () => {
  const [conversations, setConversations] = useState<ConversationsData>({});
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const userId: number = 1

  useEffect(() => {
    const fetchConversations = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem('token')
        const response = await fetch(`http://localhost:8080/api/messages/user/${userId}/conversations`, {
          method: "GET", 
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error('Failed to fetch conversations');
        }
        const data: ConversationsData = await response.json();
        console.log('Data received')
        console.log({data});
        setConversations(data);
        setLoading(false);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An unknown error occurred');
        setLoading(false);
      }
    };

    fetchConversations();
  }, [userId]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h1>Conversations</h1>
      {Object.entries(conversations).map(([otherUserId, messages]) => (
        <div key={otherUserId}>
          <h2>Conversation with User {otherUserId}</h2>
          {messages.map((message: Message) => (
            <div key={message.id}>
              <p>{message.senderId === userId ? 'You' : 'Other'}: {message.content}</p>
              <small>{new Date(message.timestamp).toLocaleString()}</small>
            </div>
          ))}
        </div>
      ))}
    </div>
  );
};

export default Messages;