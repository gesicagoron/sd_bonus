// src/components/UserDevicesTable.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:80/device/users/assigned';
//http://localhost:80/device/users/assigned
//http://localhost:8082/users/assigned

const UserDevicesTable = () => {
    const [userDevices, setUserDevices] = useState([]);

    useEffect(() => {
        const fetchUserDevices = async () => {
            try {
                const response = await axios.get(API_URL);
                console.log('Fetched user devices:', response.data);
                setUserDevices(response.data);
            } catch (error) {
                console.error('Error fetching user devices:', error);
            }
        };

        fetchUserDevices();
    }, []);

    return (
        <div>
            <h2>Devices Assigned to Users</h2>
            <table>
                <thead>
                    <tr>
                        <th>User Name</th>
                        <th>Device Description</th>
                        <th>Device Address</th>
                        <th>Max Energy Consumption (kWh)</th>
                    </tr>
                </thead>
                <tbody>
                    {userDevices.length > 0 ? (
                        userDevices.map(user => (
                            user.devices && user.devices.length > 0 ? (
                                user.devices.map(device => (
                                    <tr key={`${user.id}-${device.id}`}>
                                        <td>{user.name}</td>
                                        <td>{device.description}</td>
                                        <td>{device.address}</td>
                                        <td>{device.maxHourlyEnergyConsumption}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr key={user.id}>
                                    <td>{user.name}</td>
                                    <td colSpan="3">No devices assigned</td>
                                </tr>
                            )
                        ))
                    ) : (
                        <tr>
                            <td colSpan="4">Loading...</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default UserDevicesTable;
