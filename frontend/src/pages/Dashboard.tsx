import React, { useEffect, useState } from "react"
import { User, MessageCircle, LogOut } from "lucide-react"
import { Link } from "react-router-dom"

const Dashboard = () => {
  const [profileEmails, setProfileEmails] = useState([])

  useEffect(() => {
    const fetchProfileEmails = async () => {
      const token = localStorage.getItem("token")
      const response = await fetch(
        "http://localhost:8080/api/profiles/emails",
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )
      if (response.ok) {
        const emails = await response.json()
        setProfileEmails(emails)
        console.log({ emails })
      } else {
        console.error("Error fetching profile emails:", response.status)
      }
    }

    fetchProfileEmails()
  }, [])

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
          <h2 className="text-4xl font-bold mb-4">Welcome Back!</h2>
          <p className="text-xl mb-6">
            Here&apos;s a summary of your recent activity.
          </p>
        </section>
        <section className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
          <div className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition">
            <User className="text-blue-600 w-12 h-12 mb-4" />
            <h3 className="text-xl font-semibold mb-2">Find Users to Chat With</h3>
            <p>Discover and connect with other users to start chatting.</p>
            <Link
              to="/find-users"
              className="text-blue-600 hover:underline mt-4 inline-block"
            >
              Find Users
            </Link>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition">
            <MessageCircle className="text-blue-600 w-12 h-12 mb-4" />
            <h3 className="text-xl font-semibold mb-2">Recent Messages</h3>
            <p>Check out your most recent messages and conversations.</p>
            <Link
              to="/messages"
              className="text-blue-600 hover:underline mt-4 inline-block"
            >
              View Messages
            </Link>
          </div>
        </section>

        <section className="bg-white p-6 rounded-lg shadow-md mb-12">
          <div className="flex items-center justify-between">
            <h3 className="text-xl font-semibold">Account Settings</h3>
            <a onClick={() => {
              localStorage.removeItem("token");
              localStorage.removeItem("privateKey");
              window.location.reload();
            }} className="text-red-600 cursor-pointer">
              <LogOut className="inline w-6 h-6 mr-2" />
              Logout
            </a>
          </div>
          <p className="mt-4">Manage your account settings and log out.</p>
        </section>
      </main>

      <footer className="bg-gray-200 p-4 mt-12">
        <div className="container mx-auto text-center">
          <p>&copy; 2024 SecureChat. All rights reserved.</p>
        </div>
      </footer>
    </div>
  )
}

export default Dashboard
