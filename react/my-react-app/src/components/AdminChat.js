import React, { useState, useEffect, useCallback, useRef } from 'react';

const AdminChat = () => {
    const [messages, setMessages] = useState({}); // Mesaje per client
    const [message, setMessage] = useState('');
    const [receiver, setReceiver] = useState('');
    const [clients, setClients] = useState([]);
    const [isTyping, setIsTyping] = useState(false); // Pentru notificare typing
    const [typingUser, setTypingUser] = useState(''); // Numele celui care tastează

    const socket = useRef(null);
    const lastReadMessage = useRef({}); // Pentru a preveni duplicarea notificărilor
    const typingTimeoutRef = useRef(null); // Timeout pentru typing debounce
    const typingDebounceRef = useRef(null); // Pentru a preveni trimiterea excesivă

    // 📌 Fetch Client List
    useEffect(() => {
        const fetchClients = async () => {
            try {
                const response = await fetch('http://localhost:80/person/person');
                //http://localhost:80/person/person
                //http://localhost:8081/person
                if (response.ok) {
                    const data = await response.json();
                    const clientUsernames = data.map(client => client.name).filter(name => name !== 'liana');
                    setClients(clientUsernames);
                } else {
                    console.error('Failed to fetch clients');
                }
            } catch (error) {
                console.error('Error fetching clients:', error);
            }
        };

        fetchClients();
    }, []);

    // 📌 Initialize WebSocket
    useEffect(() => {
        socket.current = new WebSocket(`ws://localhost:80/chat/websocket?username=liana`);
        //ws://localhost:8085/websocket?username=liana

        socket.current.onopen = () => {
            console.log('✅ Connected to WebSocket as Admin (liana)');
        };

        socket.current.onmessage = (event) => {
            try {
                const receivedMessage = JSON.parse(event.data);
                const { sender, type, content } = receivedMessage;

                // 📌 Handle Typing Notification
                if (type === 'typing') {
                    if (sender !== 'liana') {
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
                    setMessages((prevMessages) => ({
                        ...prevMessages,
                        [sender]: prevMessages[sender]?.map((msg) => {
                            if (msg.sender === 'liana' && !msg.isRead) {
                                return { ...msg, isRead: true, readBy: sender };
                            }
                            return msg;
                        }) || [],
                    }));
                    return;
                }

                // 📌 Handle Regular Chat Messages
                setMessages((prevMessages) => ({
                    ...prevMessages,
                    [sender]: [...(prevMessages[sender] || []), receivedMessage],
                }));
            } catch (error) {
                console.warn('❌ Non-JSON message received:', event.data);
            }
        };

        socket.current.onerror = (error) => {
            console.error('❌ WebSocket error:', error);
        };

        socket.current.onclose = () => {
            console.warn('⚠️ WebSocket closed (Admin Chat)');
        };

        return () => {
            if (
                socket.current.readyState === WebSocket.OPEN ||
                socket.current.readyState === WebSocket.CONNECTING
            ) {
                socket.current.close();
            }
        };
    }, []);

    // 📌 Send Read Receipt
    const sendReadReceipt = useCallback((sender, messageId) => {
        if (socket.current && receiver && sender !== 'liana') {
            if (lastReadMessage.current[sender] === messageId) return; // Evităm duplicatele

            const readReceipt = {
                type: 'read-receipt',
                sender: 'liana',
                receiver: sender,
            };
            socket.current.send(JSON.stringify(readReceipt));
            console.log(`✅ Read receipt sent to ${sender}`);
            lastReadMessage.current[sender] = messageId;
        }
    }, [receiver]);

    // 📌 Automatically Send Read Receipt for the Last Unread Message
    useEffect(() => {
        if (receiver && messages[receiver]?.length > 0) {
            const lastMessage = messages[receiver][messages[receiver].length - 1];
            if (
                !lastMessage.isRead &&
                lastMessage.sender !== 'liana' &&
                lastReadMessage.current[receiver] !== lastMessage.timestamp
            ) {
                sendReadReceipt(lastMessage.sender, lastMessage.timestamp);
            }
        }
    }, [receiver, messages, sendReadReceipt]);

    // 📌 Typing Notification (Debounced)
    const handleTyping = useCallback(() => {
        if (socket.current && receiver && !typingDebounceRef.current) {
            const typingNotification = {
                type: 'typing',
                sender: 'liana',
                receiver: receiver,
            };
            socket.current.send(JSON.stringify(typingNotification));
            console.log('📝 Typing notification sent');

            typingDebounceRef.current = setTimeout(() => {
                typingDebounceRef.current = null;
            }, 3000); // Trimitem notificarea o dată la fiecare 3 secunde
        }
    }, [receiver]);

    // 📌 Send Message
    const sendMessage = () => {
        if (socket.current && message.trim() !== '' && receiver.trim() !== '') {
            const chatMessage = {
                sender: 'liana',
                receiver: receiver,
                content: message,
                timestamp: new Date().toISOString(),
            };
            socket.current.send(JSON.stringify(chatMessage));
            setMessages((prevMessages) => ({
                ...prevMessages,
                [receiver]: [...(prevMessages[receiver] || []), { ...chatMessage, isRead: false }],
            }));
            setMessage('');
        } else {
            console.warn('Receiver and message are required.');
        }
    };

    return (
        <div className="admin-chat">
            <h3>Admin Chat</h3>
            <div>
                <label htmlFor="receiver">Select Receiver:</label>
                <select
                    id="receiver"
                    value={receiver}
                    onChange={(e) => setReceiver(e.target.value)}
                >
                    <option value="" disabled>Select a client</option>
                    {clients.map((client, index) => (
                        <option key={index} value={client}>
                            {client}
                        </option>
                    ))}
                </select>
            </div>
            <div className="chat-box">
                {receiver ? (
                    (messages[receiver] || []).map((msg, index) => (
                        <div
                            key={index}
                            className={`chat-message ${msg.sender === 'liana' ? 'admin' : 'client'}`}
                        >
                            <strong>{msg.sender} ➡️ {msg.receiver}:</strong> {msg.content}
                            <span className="timestamp">{new Date(msg.timestamp).toLocaleTimeString()}</span>
                            {msg.isRead && <div className="read-receipt">✔️ Read by {msg.readBy}</div>}
                        </div>
                    ))
                ) : (
                    <p>Select a client to view the chat.</p>
                )}
            </div>
            {isTyping && typingUser && (
                <div className="typing-indicator">{typingUser} is typing...</div>
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
                <button onClick={sendMessage} disabled={!receiver}>Send</button>
            </div>
        </div>
    );
};

export default AdminChat;
