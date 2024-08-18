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
  try {


    const privateKey = await getCurrentUserPrivateKey();


    const parsedPublicKey = JSON.parse(recipientPublicKey);

    const publicKeyObj = await window.crypto.subtle.importKey(
      "jwk",
      parsedPublicKey,
      { name: "ECDH", namedCurve: "P-256" },
      false,
      []
    );


    const sharedSecret = await window.crypto.subtle.deriveBits(
      { name: "ECDH", public: publicKeyObj },
      privateKey,
      256
    );

    const encoder = new TextEncoder();
    const encodedMessage = encoder.encode(message);


    const iv = window.crypto.getRandomValues(new Uint8Array(12));


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


    const result = new Uint8Array(iv.length + new Uint8Array(encryptedData).length);
    result.set(iv);
    result.set(new Uint8Array(encryptedData), iv.length);

    const base64EncryptedMessage = btoa(String.fromCharCode.apply(null, Array.from(result)));


    return base64EncryptedMessage;
  } catch (error) {
    console.error('Encryption error:', error);
    throw error;
  }
}


export async function decryptMessage(
  senderPublicKey: string,
  encryptedMessage: string
): Promise<string> {
  try {

    
    const privateKey = await getCurrentUserPrivateKey();
    if (!privateKey) {
      throw new Error("Failed to retrieve recipient's private key");
    }

    let publicKeyObj;
    try {
      publicKeyObj = await window.crypto.subtle.importKey(
        "jwk",
        JSON.parse(senderPublicKey),
        { name: "ECDH", namedCurve: "P-256" },
        false,
        []
      );

    } catch (error) {
      console.error('Error importing sender public key:', error);
      throw new Error(`Invalid sender public key format: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
    
    let sharedSecret;
    try {
      sharedSecret = await window.crypto.subtle.deriveBits(
        { name: "ECDH", public: publicKeyObj },
        privateKey,
        256
      );

    } catch (error) {
      console.error('Error deriving shared secret:', error);
      throw new Error(`Failed to derive shared secret: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }

    let encryptedData;
    try {
      encryptedData = Uint8Array.from(atob(encryptedMessage), (c) => c.charCodeAt(0));

    } catch (error) {
      console.error('Error decoding encrypted message:', error);
      throw new Error(`Invalid encrypted message format: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }

    const iv = encryptedData.slice(0, 12);
    const ciphertext = encryptedData.slice(12);

    let aesKey;
    try {
      aesKey = await window.crypto.subtle.importKey(
        "raw",
        sharedSecret,
        { name: "AES-GCM" },
        false,
        ["decrypt"]
      );

    } catch (error) {
      console.error('Error importing AES key:', error);
      throw new Error(`Failed to import AES key: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }

    let decryptedData;
    try {
      decryptedData = await window.crypto.subtle.decrypt(
        { name: "AES-GCM", iv: iv },
        aesKey,
        ciphertext
      );

    } catch (error) {
      console.error('Error during decryption:', error);
      throw new Error(`Decryption operation failed: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }

    const decoder = new TextDecoder();
    const decodedMessage = decoder.decode(decryptedData);


    return decodedMessage;
  } catch (error) {
    console.error('Detailed decryption error:', error);
    if (error instanceof Error) {
      throw new Error(`Decryption failed: ${error.name} - ${error.message}`);
    } else {
      throw new Error('Decryption failed: Unknown error');
    }
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
  console.log({publicKey})
  return publicKey
}

export async function sendMessage(
  userId: string,
  recipientEmail: string,
  message: string
): Promise<void> {
  if (!userId || !recipientEmail || !message) {
    return;
  }

  let recipientId: number;

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No token found");
    }

    try {
      const response = await fetch('http://localhost:8080/api/profiles/findId', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',  

        },
        body: JSON.stringify({ email: recipientEmail }), 
      });

      if (!response.ok) {
        throw new Error("Failed to fetch recipient ID");
      }

      recipientId = parseInt(await response.text());
      console.log({recipientId})
    } catch (e) {
      console.log(e);
      throw new Error("Error fetching recipient ID");
    }

    if (recipientId) {
      console.log({recipientId})
      const recipientPublicKey = await fetchRecipientPublicKey(recipientId);
      const encryptedMessage = await encryptMessage(recipientPublicKey, message);

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
      });
    } else {
      throw new Error("Recipient ID is null");
    }
  } catch (error) {
    console.error('Error in sendMessage:', error);
    throw error;
  }
}
