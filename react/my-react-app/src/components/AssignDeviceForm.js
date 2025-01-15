import React, { useState } from 'react';
import { assignDeviceToUser } from '../api';

const AssignDeviceForm = () => {
    const [userId, setUserId] = useState('');
    const [deviceId, setDeviceId] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (userId && deviceId) {
            assignDeviceToUser(userId, deviceId);
        } else {
            alert('Please enter both User ID and Device ID');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="assign-device-form">
            <div>
                <label>User ID:</label>
                <input
                    type="text"
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                    placeholder="Enter User ID"
                    required
                />
            </div>
            <div>
                <label>Device ID:</label>
                <input
                    type="text"
                    value={deviceId}
                    onChange={(e) => setDeviceId(e.target.value)}
                    placeholder="Enter Device ID"
                    required
                />
            </div>
            <button type="submit">Assign Device</button>
        </form>
    );
};

export default AssignDeviceForm;
