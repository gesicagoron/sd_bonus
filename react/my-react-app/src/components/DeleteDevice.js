import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:80/device/devices';
//http://localhost:80/device/devices
//http://localhost:8082/devices

const DeviceList = ({ onEdit }) => {
    const [devices, setDevices] = useState([]);

    // Fetch devices from the backend
    useEffect(() => {
        const fetchDevices = async () => {
            try {
                const response = await axios.get(API_URL);
                setDevices(response.data);
            } catch (error) {
                console.error('Error fetching devices:', error);
            }
        };

        fetchDevices();
    }, []);

    const handleDelete = async (id) => {
        try {
            await axios.delete(`${API_URL}/${id}`);
            setDevices(devices.filter((device) => device.id !== id));
        } catch (error) {
            console.error('Error deleting device:', error);
        }
    };

    return (
        <div>
            <h2>Device List</h2>
            <ul>
                {devices.map((device) => (
                    <li key={device.id}>
                        {device.description} - {device.address} - {device.maxHourlyEnergyConsumption} kWh
                        <button onClick={() => onEdit(device)}>Edit</button>
                        <button onClick={() => handleDelete(device.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default DeviceList;
