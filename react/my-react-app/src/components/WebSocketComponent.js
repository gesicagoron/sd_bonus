import React, { useEffect, useState } from "react";

const WebSocketComponent = () => {
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        // Create a WebSocket connection
        const socket = new WebSocket("ws://localhost:80/monitor/websocket");
        //ws://localhost:80/monitor/websocket
        //ws://localhost:8083/websocket

        // Log when the connection is established
        socket.onopen = () => {
            console.log("WebSocket connection established");
        };

        // Handle messages from the WebSocket
        socket.onmessage = (event) => {
            console.log("Received notification:", event.data);

            // Update the notifications list
            setNotifications((prev) => [...prev, event.data]);
        };

        // Log errors
        socket.error = (error) => {
            console.error("WebSocket error:", error);
        };

        // Cleanup the WebSocket connection on component unmount
        return () => {
            socket.close();
        };
    }, []);

    return (
        <div>
            <h1>Device Notifications</h1>
            <ul>
                {notifications.map((notification, index) => (
                    <li key={index}>{notification}</li>
                ))}
            </ul>
        </div>
    );
};

export default WebSocketComponent;
