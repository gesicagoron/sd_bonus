// src/components/ClientPage.js
import React, { useEffect, useState } from 'react';
import axios from '../config/axiosConfig';

const ClientPage = () => {
    const [devices, setDevices] = useState([]);

    useEffect(() => {
        const fetchDevices = async () => {
            try {
                const response = await axios.get('/devices');
                setDevices(response.data);
            } catch (err) {
                console.error('Failed to fetch devices:', err);
            }
        };
        fetchDevices();
    }, []);

    return (
        <div>
            <h2>Device List</h2>
            {devices.length > 0 ? (
                <ul>
                    {devices.map(device => (
                        <li key={device.id}>{device.name} - {device.description}</li>
                    ))}
                </ul>
            ) : (
                <p>No devices available.</p>
            )}
        </div>
    );
};

export default ClientPage;
