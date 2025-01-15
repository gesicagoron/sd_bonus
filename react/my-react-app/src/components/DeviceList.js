// src/components/DeviceList.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './DeviceList.css';

const ADMIN_API_URL = 'http://localhost:80/device/devices';
//http://localhost:80/device/devices
//http://localhost:8082/devices
const CLIENT_API_URL = 'http://localhost:80/device/users/devices';
//http://localhost:80/device/users/devices
//http://localhost:8082/users/devices

const DeviceList = ({ userId, role, onEdit, refresh }) => {
    const [devices, setDevices] = useState([]);

    useEffect(() => {
        const fetchDevices = async () => {
            try {
                let url;
                if (role === 'ROLE_admin') {
                    url = ADMIN_API_URL; // Fetch all devices for admin
                } else if (role === 'ROLE_client' && userId) {
                    url = `${CLIENT_API_URL}/${userId}`; // Fetch user-specific devices for client
                } else {
                    throw new Error('Invalid role or missing user ID');
                }

                const response = await axios.get(url);
                setDevices(response.data);
            } catch (error) {
                console.error('Error fetching devices:', error);
            }
        };

        fetchDevices();
    }, [userId, role, refresh]);

    const handleDelete = async (id) => {
        try {
            await axios.delete(`${ADMIN_API_URL}/${id}`);
            setDevices(devices.filter(device => device.id !== id));
        } catch (error) {
            console.error('Error deleting device:', error);
        }
    };

    return (
        <div>
            <h2>{role === 'ROLE_admin' ? "All Devices" : "Assigned Devices"}</h2>
            {devices.length > 0 ? (
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Description</th>
                            <th>Address</th>
                            <th>Max Hourly Energy Consumption (kWh)</th>
                            {role === 'ROLE_admin' && <th>Actions</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {devices.map((device) => (
                            <tr key={device.id}>
                                <td>{device.id}</td>
                                <td>{device.description}</td>
                                <td>{device.address}</td>
                                <td>{device.maxHourlyEnergyConsumption}</td>
                                {role === 'ROLE_admin' && (
                                    <td>
                                        <button onClick={() => onEdit(device)}>Edit</button>
                                        <button onClick={() => handleDelete(device.id)}>Delete</button>
                                    </td>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No devices available.</p>
            )}
        </div>
    );
};

export default DeviceList;
