import { Shield, MessageCircle, Lock } from 'lucide-react';

const App = () => {
  return (
    <div className="min-h-screen bg-gray-100 text-gray-900">
      <header className="bg-blue-600 text-white p-4">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">SecureChat</h1>
          <nav>
            <ul className="flex space-x-4">
              <li><a href="#features" className="text-white hover:text-white hover:underline">Features</a></li>
              <li><a href="#about" className="text-white hover:text-white hover:underline">About</a></li>
              <li><a href="#contact" className="text-white hover:text-white hover:underline">Contact</a></li>
            </ul>
          </nav>
        </div>
      </header>

      <main className="w-full mx-auto mt-8 px-4">
        <section className="text-center mb-12">
          <h2 className="text-4xl font-bold mb-4">Secure Messaging for Everyone</h2>
          <p className="text-xl mb-6">End-to-end encryption keeps your conversations private and secure.</p>
          <button className="bg-blue-600 text-white px-6 py-2 rounded-full text-lg hover:bg-blue-700 transition-colors">
            Get Started
          </button>
        </section>

        <section id="features" className="mb-12">
          <h3 className="text-2xl font-semibold mb-6">Key Features</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition">
              <Shield className="text-blue-600 w-12 h-12 mb-4" />
              <h4 className="text-xl font-semibold mb-2">End-to-End Encryption</h4>
              <p>Your messages are encrypted from sender to recipient, ensuring complete privacy.</p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition">
              <MessageCircle className="text-blue-600 w-12 h-12 mb-4" />
              <h4 className="text-xl font-semibold mb-2">Instant Messaging</h4>
              <p>Send and receive messages instantly with real-time delivery notifications.</p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-md hover:shadow-2xl transition">
              <Lock className="text-blue-600 w-12 h-12 mb-4" />
              <h4 className="text-xl font-semibold mb-2">Self-Destructing Messages</h4>
              <p>Set messages to automatically delete after a specified time for added security.</p>
            </div>
          </div>
        </section>

        <section id="about" className="mb-12">
          <h3 className="text-2xl font-semibold mb-4">About SecureChat</h3>
          <p className="text-lg">
            SecureChat is committed to providing a secure and private messaging experience. 
            Our state-of-the-art encryption ensures that only you and your intended recipient 
            can read your messages. We believe in the fundamental right to privacy in digital 
            communications.
          </p>
        </section>

        <section id="contact" className="mb-12">
          <h3 className="text-2xl font-semibold mb-4">Contact Us</h3>
          <p className="text-lg mb-4">
            Have questions or feedback? We'd love to hear from you!
          </p>
          <a href="mailto:contact@securechat.com" className="text-blue-600 hover:underline">
            contact@securechat.com
          </a>
        </section>
      </main>

      <footer className="bg-gray-200 p-4 mt-12">
        <div className="container mx-auto text-center">
          <p>&copy; 2024 SecureChat. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default App;