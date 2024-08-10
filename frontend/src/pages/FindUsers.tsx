import React, { useEffect, useState } from "react"
import { JwtPayload, jwtDecode } from "jwt-decode"
import { Mail } from "lucide-react"
import { Link } from "react-router-dom"

interface Profile {
  id: number
  email: string
}

interface CustomJwtPayload extends JwtPayload {
  id?: number
}

const FindUsers: React.FC = () => {
  const [profiles, setProfiles] = useState<Profile[]>([])
  const [userId, setUserId] = useState<number | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [popupVisible, setPopupVisible] = useState<boolean>(false)
  const [selectedProfile, setSelectedProfile] = useState<Profile | null>(null)
  const [messageContent, setMessageContent] = useState<string>("")
  const [sending, setSending] = useState<boolean>(false)

  useEffect(() => {
    const fetchProfiles = async () => {
      try {
        setLoading(true)
        const token = localStorage.getItem("token")

        if (token) {
          try {
            const decodedToken = jwtDecode<CustomJwtPayload>(token)

            const userId = decodedToken.id ?? null

            setUserId(userId)

            const response = await fetch("http://localhost:8080/api/profiles", {
              method: "GET",
              headers: {
                Authorization: `Bearer ${token}`,
              },
            })

            if (!response.ok) {
              throw new Error("Failed to fetch profiles")
            }

            const data: Profile[] = await response.json()
            setProfiles(data)
          } catch (error) {
            console.error("Failed to decode token or fetch profiles:", error)
            setError("An error occurred while fetching profiles.")
          }
        }
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred"
        )
      } finally {
        setLoading(false)
      }
    }

    fetchProfiles()
  }, [])

  const handleSendMessageClick = (profile: Profile) => {
    setSelectedProfile(profile)
    setPopupVisible(true)
  }

  const handleClosePopup = () => {
    setPopupVisible(false)
    setSelectedProfile(null)
    setMessageContent("")
    setError(null)
  }

  const handleSendMessage = async () => {
    if (selectedProfile && messageContent.trim() && userId) {
      setSending(true)
      const token = localStorage.getItem("token")
      try {
        const response = await fetch("http://localhost:8080/api/messages/send", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            senderId: userId,
            recipientEmail: selectedProfile.email,
            content: messageContent,
          }),
        })

        if (response.ok) {
          alert("Message sent successfully!")
          handleClosePopup()
        } else {
          throw new Error("Failed to send message")
        }
      } catch (err) {
        setError("An error occurred while sending the message.")
      } finally {
        setSending(false)
      }
    } else {
      setError("Please enter a message.")
    }
  }

  if (loading) return <div>Loading...</div>
  if (error) return <div>{error}</div>

  const filteredProfiles = userId
    ? profiles.filter((profile) => profile.id !== userId)
    : profiles

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
                    localStorage.removeItem("token")
                    window.location.reload()
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
          <h2 className="text-4xl font-bold mb-4">Find Users</h2>
          <p className="text-xl mb-6">Browse and connect with other users.</p>
        </section>

        <section className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {filteredProfiles.length > 0 ? (
            filteredProfiles.map((profile) => (
              <div
                key={profile.id}
                className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition mb-6"
              >
                <h3 className="text-xl font-semibold mb-2">{profile.email}</h3>
                <p>Connect with this user to start chatting.</p>
                <button
                  onClick={() => handleSendMessageClick(profile)}
                  className="text-blue-600 hover:underline mt-4 inline-block"
                >
                  Send Message
                  <Mail className="inline w-4 h-4 ml-2" />
                </button>
              </div>
            ))
          ) : (
            <p>No users available</p>
          )}
        </section>

        {popupVisible && selectedProfile && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
              <h3 className="text-xl font-semibold mb-4">
                Send Message to {selectedProfile.email}
              </h3>
              <textarea
                className="w-full p-2 border rounded mb-4"
                rows={4}
                placeholder="Type your message here..."
                value={messageContent}
                onChange={(e) => setMessageContent(e.target.value)}
              />
              {error && <p className="text-red-600 mb-4">{error}</p>}
              <div className="flex justify-end">
                <button
                  onClick={handleSendMessage}
                  className={`bg-blue-600 text-white px-4 py-2 rounded mr-2 ${
                    sending ? "opacity-50 cursor-not-allowed" : ""
                  }`}
                  disabled={sending}
                >
                  {sending ? "Sending..." : "Send"}
                </button>
                <button
                  onClick={handleClosePopup}
                  className="bg-gray-300 px-4 py-2 rounded"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default FindUsers
