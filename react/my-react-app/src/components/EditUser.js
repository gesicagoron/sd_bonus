// src/components/EditUser.js
import React, { useState, useEffect } from 'react';
import { updateUser } from '../users';

const EditUser = ({ userToEdit, onCancel, onUpdate }) => {
    const [user, setUser] = useState(userToEdit);

    useEffect(() => {
        setUser(userToEdit);
    }, [userToEdit]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUser((prevUser) => ({ ...prevUser, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await updateUser(user.id, user);
            onUpdate();
        } catch (error) {
            console.error('Error updating user:', error);
            alert('Failed to update user. Please try again.');
        }
    };

    return (
        <div>
            <h2>Edit User</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="name"
                    value={user.name || ''}
                    placeholder="Name"
                    onChange={handleChange}
                    required
                />
                <input
                    type="number"
                    name="age"
                    value={user.age || ''}
                    placeholder="Age"
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="address"
                    value={user.address || ''}
                    placeholder="Address"
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="role"
                    value={user.role || ''}
                    placeholder="Role"
                    onChange={handleChange}
                    required
                />
                <input
                    type="password"
                    name="password"
                    value={user.password || ''}
                    placeholder="Password"
                    onChange={handleChange}
                />
                <button type="submit">Update User</button>
                <button type="button" onClick={onCancel}>Cancel</button>
            </form>
        </div>
    );
};

export default EditUser;
