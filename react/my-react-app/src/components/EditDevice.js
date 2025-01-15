import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:80/device/devices';
//http://localhost:80/device/devices
//http://localhost:8082/devices

const EditDevice = ({ deviceToEdit, onCancel, onUpdate }) => {
    const [device, setDevice] = useState(deviceToEdit);

    useEffect(() => {
        setDevice(deviceToEdit);
    }, [deviceToEdit]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setDevice({ ...device, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`${API_URL}/${device.id}`, device);
            onUpdate();
        } catch (error) {
            console.error('Error updating device:', error);
        }
    };

    return (
        <div>
            <h2>Edit Device</h2>
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
                <button type="submit">Update Device</button>
                <button type="button" onClick={onCancel}>Cancel</button>
            </form>
        </div>
    );
};

export default EditDevice;
