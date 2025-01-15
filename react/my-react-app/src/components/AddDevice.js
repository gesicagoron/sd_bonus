import React, { useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:80/device/devices';
//http://localhost:80/device/devices
//http://localhost:8082/devices
const AddDevice = ({ onAdd }) => {
    const [device, setDevice] = useState({
        description: '',
        address: '',
        maxHourlyEnergyConsumption: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setDevice({ ...device, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post(API_URL, device);
            onAdd();
        } catch (error) {
            console.error('Error adding device:', error);
        }
    };

    return (
        <div>
            <h2>Add New Device</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="description"
                    value={device.description}
                    placeholder="Description"
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="address"
                    value={device.address}
                    placeholder="Address"
                    onChange={handleChange}
                    required
                />
                <input
                    type="number"
                    name="maxHourlyEnergyConsumption"
                    value={device.maxHourlyEnergyConsumption}
                    placeholder="Max Energy Consumption (kWh)"
                    onChange={handleChange}
                    required
                />
                <button type="submit">Add Device</button>
            </form>
        </div>
    );
};

export default AddDevice;
