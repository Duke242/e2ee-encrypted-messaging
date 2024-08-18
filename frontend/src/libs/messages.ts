import { jwtDecode, JwtPayload } from "jwt-decode"

interface CustomJwtPayload extends JwtPayload {
  id?: number
}

export async function getCurrentUserPrivateKey(): Promise<CryptoKey> {
  try {
    const encryptedPrivateKey = localStorage.getItem("privateKey")
    if (!encryptedPrivateKey) {
      throw new Error("No private key found in local storage")
    }
    const privateKeyJwk = JSON.parse(encryptedPrivateKey)
    const privateKey = await window.crypto.subtle.importKey(
      "jwk",
      privateKeyJwk,
      { name: "ECDH", namedCurve: "P-256" },
      false,
      ["deriveKey", "deriveBits"]  
    )
    console.log('Private key imported successfully')
    return privateKey
  } catch (error) {
    console.error('Error in getCurrentUserPrivateKey:', error)
    throw error
  }
}


export async function encryptMessage(
  recipientPublicKey: string,
  message: string
): Promise<string> {
  const privateKey = await getCurrentUserPrivateKey();

  // Import the recipient's public key
  const publicKeyObj = await window.crypto.subtle.importKey(
    "jwk",
    JSON.parse(recipientPublicKey),
    { name: "ECDH", namedCurve: "P-256" },
    false,
    []
  );

  // Derive a shared secret
  const sharedSecret = await window.crypto.subtle.deriveBits(
    { name: "ECDH", public: publicKeyObj },
    privateKey,
    256
  );

  // Use the shared secret to encrypt the message
  const encoder = new TextEncoder();
  const encodedMessage = encoder.encode(message);

  // Generate a random IV
  const iv = window.crypto.getRandomValues(new Uint8Array(12));

  // Derive an AES key from the shared secret
  const aesKey = await window.crypto.subtle.importKey(
    "raw",
    sharedSecret,
    { name: "AES-GCM" },
    false,
    ["encrypt"]
  );

  const encryptedData = await window.crypto.subtle.encrypt(
    { name: "AES-GCM", iv: iv },
    aesKey,
    encodedMessage
  );

  // Combine IV and encrypted data
  const result = new Uint8Array(iv.length + new Uint8Array(encryptedData).length);
  result.set(iv);
  result.set(new Uint8Array(encryptedData), iv.length);

  // Convert to base64 for easy storage/transmission
  return btoa(String.fromCharCode.apply(null, Array.from(result)));
}


export async function decryptMessage(
  senderPublicKey: string,
  encryptedMessage: string
): Promise<string> {
  try {
    console.log('Encrypted message:', encryptedMessage);
    const privateKey = await getCurrentUserPrivateKey();

    // Import the sender's public key
    const publicKeyObj = await window.crypto.subtle.importKey(
      "jwk",
      JSON.parse(senderPublicKey),
      { name: "ECDH", namedCurve: "P-256" },
      false,
      []
    );

    // Derive a shared secret
    const sharedSecret = await window.crypto.subtle.deriveBits(
      { name: "ECDH", public: publicKeyObj },
      privateKey,
      256
    );

    // Decode the base64 encoded encrypted message
    const encryptedData = Uint8Array.from(atob(encryptedMessage), (c) => c.charCodeAt(0));
    console.log('Decoded encrypted data length:', encryptedData.length);

    // Extract IV and ciphertext
    const iv = encryptedData.slice(0, 12);
    const ciphertext = encryptedData.slice(12);

    console.log('IV length:', iv.length);
    console.log('Ciphertext length:', ciphertext.length);

    // Derive an AES key from the shared secret
    const aesKey = await window.crypto.subtle.importKey(
      "raw",
      sharedSecret,
      { name: "AES-GCM" },
      false,
      ["decrypt"]
    );

    const decryptedData = await window.crypto.subtle.decrypt(
      { name: "AES-GCM", iv: iv },
      aesKey,
      ciphertext
    );

    const decoder = new TextDecoder();
    return decoder.decode(decryptedData);
  } catch (error) {
    console.error('Decryption error:', error);
    throw error;
  }
}
export async function fetchSenderPublicKey(): Promise<string> {
  const token = localStorage.getItem("token")
  const decodedToken = token ? jwtDecode<CustomJwtPayload>(token) : null
  const userId = decodedToken?.id ?? null
  const response = await fetch(
    `http://localhost:8080/api/profiles/${userId}/publicKey`
  )
  if (!response.ok) {
    throw new Error("Failed to fetch public key")
  }
  const publicKey = await response.text()
  return publicKey
}

export async function fetchRecipientPublicKey(
  recipientId: number
): Promise<string> {
  const token = localStorage.getItem("token")
  const response = await fetch(
    `http://localhost:8080/api/profiles/${recipientId}/public-key`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  )
  if (!response.ok) {
    throw new Error("Failed to fetch public key")
  }
  const publicKey = await response.text()
  return publicKey
}

export async function sendMessage(
  userId: string,
  recipientEmail: string,
  message: string
): Promise<void> {
  if (!userId || !recipientEmail || !message) {
    return
  }
  try {

    const token = localStorage.getItem("token")
    const decodedToken = token ? jwtDecode<CustomJwtPayload>(token) : null
    const recipientId = decodedToken?.id
    if (recipientId) {
      var recipientPublicKey = await fetchRecipientPublicKey(recipientId)
    } else {
      throw new Error("Recipient ID is null")
    }
    console.log('Recipient public key:', recipientPublicKey)

  
    const encryptedMessage = await encryptMessage(recipientPublicKey, message)
    console.log({ encryptMessage })
    console.log('Message encrypted successfully')

  
    await fetch("http://localhost:8080/api/messages/send", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        senderId: userId,
        recipientEmail: recipientEmail,
        content: encryptedMessage,
      }),
    })
  } catch (error) {
    console.error('Error in sendMessage:', error)
    throw error
  }
}


