import React, { useState, useEffect, useRef, useCallback } from 'react';

const ClientChat = ({ userId }) => {
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState('');
    const [socket, setSocket] = useState(null);
    const [username, setUsername] = useState('');
    const [isTyping, setIsTyping] = useState(false); // Afișare notificare "Typing..."
    const [typingUser, setTypingUser] = useState(''); // Numele celui care tastează

    const lastNotifiedMessageIndexRef = useRef(-1);
    const typingTimeoutRef = useRef(null);
    const typingDebounceRef = useRef(null); // Pentru debounce la typing

    // 📌 Fetch username based on userId
    useEffect(() => {
        const fetchUsername = async () => {
            try {
                const response = await fetch(`http://localhost:80/person/person/${userId}`);
                //http://localhost:80/person/person/${userId}
                //http://localhost:8081/person/${userId}
                if (response.ok) {
                    const data = await response.json();
                    setUsername(data.name);
                } else {
                    console.error('Failed to fetch username');
                }
            } catch (error) {
                console.error('Error fetching username:', error);
            }
        };

        fetchUsername();
    }, [userId]);

    // 📌 Initialize WebSocket
    useEffect(() => {
        if (!username) return;

        const newSocket = new WebSocket(`ws://localhost:80/chat/websocket?username=${username}`);
        //ws://localhost:8085/websocket?username=${username}

        newSocket.onopen = () => {
            console.log('✅ Connected to WebSocket (Client Chat)');
        };

        newSocket.onmessage = (event) => {
            try {
                const receivedMessage = JSON.parse(event.data);
                const { sender, type, content } = receivedMessage;

                // 📌 Handle Typing Notification (Exclude din lista de mesaje)
                if (type === 'typing') {
                    if (sender !== username) {
                        setTypingUser(sender);
                        setIsTyping(true);
                        clearTimeout(typingTimeoutRef.current);
                        typingTimeoutRef.current = setTimeout(() => {
                            setIsTyping(false);
                            setTypingUser('');
                        }, 3000);
                    }
                    return; // NU adăugăm în lista de mesaje!
                }

                // 📌 Handle Read Receipt
                if (type === 'read-receipt') {
                    setMessages((prevMessages) =>
                        prevMessages.map((msg) => {
                            if (
                                msg.sender === username &&
                                msg.receiver === sender &&
                                !msg.isRead
                            ) {
                                return { ...msg, isRead: true };
                            }
                            return msg;
                        })
                    );
                    return;
                }

                // 📌 Handle Regular Chat Messages (Doar aici se adaugă mesajele în listă)
                if (content && (receivedMessage.sender === username || receivedMessage.receiver === username)) {
                    setMessages((prevMessages) => [...prevMessages, receivedMessage]);
                }
            } catch (error) {
                console.warn('❌ Error parsing WebSocket message:', error);
            }
        };


        newSocket.onerror = (error) => {
            console.error('❌ WebSocket error:', error);
        };

        newSocket.onclose = () => {
            console.warn('⚠️ WebSocket closed (Client Chat)');
        };

        setSocket(newSocket);

        return () => {
            if (
                newSocket.readyState === WebSocket.OPEN ||
                newSocket.readyState === WebSocket.CONNECTING
            ) {
                newSocket.close();
            }
        };
    }, [username]);

    // 📌 Send Read Receipt
    const sendReadReceipt = useCallback(
        (sender, index) => {
            if (
                socket &&
                username &&
                sender !== username &&
                lastNotifiedMessageIndexRef.current !== index
            ) {
                const readReceipt = {
                    type: 'read-receipt',
                    sender: username,
                    receiver: sender,
                };
                socket.send(JSON.stringify(readReceipt));
                lastNotifiedMessageIndexRef.current = index;
            }
        },
        [socket, username]
    );

    // 📌 Automatically Send Read Receipt for Each Unread Message
    useEffect(() => {
        messages.forEach((msg, index) => {
            if (!msg.isRead && msg.sender !== username) {
                sendReadReceipt(msg.sender, index);
            }
        });
    }, [messages, sendReadReceipt, username]);

    // 📌 Typing Notification (Debounced)
    const handleTyping = useCallback(() => {
        if (socket && username && !typingDebounceRef.current) {
            const typingNotification = {
                type: 'typing',
                sender: username,
                receiver: 'liana',
            };
            socket.send(JSON.stringify(typingNotification));
            console.log('📝 Typing notification sent');

            typingDebounceRef.current = setTimeout(() => {
                typingDebounceRef.current = null;
            }, 3000); // Trimitem notificarea o dată la fiecare 3 secunde
        }
    }, [socket, username]);

    // 📌 Clear timeouts on component unmount
    useEffect(() => {
        return () => {
            if (typingTimeoutRef.current) {
                clearTimeout(typingTimeoutRef.current);
            }
            if (typingDebounceRef.current) {
                clearTimeout(typingDebounceRef.current);
            }
        };
    }, []);

    // 📌 Send Message
    const sendMessage = () => {
        if (socket && message.trim() !== '') {
            const chatMessage = {
                sender: username,
                receiver: 'liana',
                content: message,
                timestamp: new Date().toISOString(),
            };
            socket.send(JSON.stringify(chatMessage));
            setMessages((prevMessages) => [...prevMessages, { ...chatMessage, isRead: false }]);
            setMessage('');
            setIsTyping(false);
        } else {
            console.warn('Message cannot be empty.');
        }
    };

    return (
        <div className="client-chat">
            <h3>Client Chat</h3>
            <div className="chat-box">
                {messages.map((msg, index) => (
                    <div
                        key={index}
                        className={`chat-message ${
                            msg.sender === username ? 'client' : 'admin'
                        }`}
                    >
                        <strong>
                            {msg.sender === username ? 'You' : msg.sender} ➡️ {msg.receiver}:
                        </strong>{' '}
                        {msg.content}
                        <span className="timestamp">
                            {msg.timestamp
                                ? new Date(msg.timestamp).toLocaleTimeString()
                                : 'Invalid Date'}
                        </span>
                        {msg.isRead && <div className="read-receipt">✔️ Read</div>}
                    </div>
                ))}
            </div>

            {/* ✅ Typing Indicator Afișat Sub Lista de Mesaje */}
            {isTyping && (
                <div
                    className="typing-indicator"
                    style={{ marginTop: '10px', fontStyle: 'italic', color: '#888' }}
                >
                    {typingUser} is typing...
                </div>
            )}

            <div className="chat-input">
                <input
                    type="text"
                    value={message}
                    onChange={(e) => {
                        setMessage(e.target.value);
                        handleTyping();
                    }}
                    placeholder="Type your message..."
                />
                <button onClick={sendMessage} disabled={!username}>
                    Send
                </button>
            </div>
        </div>
    );
};

export default ClientChat;
