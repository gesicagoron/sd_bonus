import React, { useState } from 'react';
import { addUser } from '../users';

const AddUser = ({ onAdd }) => {
    const [user, setUser] = useState({
        name: '',
        age: '',
        address: '',
        role: '',
        password: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUser({ ...user, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await addUser(user);
            onAdd();
        } catch (error) {
            console.error('Error adding user:', error);
        }
    };

    return (
        <div>
            <h2>Add New User</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="name"
                    value={user.name}
                    placeholder="Name"
                    onChange={handleChange}
                    required
                />
                <input
                    type="number"
                    name="age"
                    value={user.age}
                    placeholder="Age"
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="address"
                    value={user.address}
                    placeholder="Address"
                    onChange={handleChange}
                    required
                />
                <input
                    type="text"
                    name="role"
                    value={user.role}
                    placeholder="Role"
                    onChange={handleChange}
                    required
                />
                <input
                    type="password"
                    name="password"
                    value={user.password}
                    placeholder="Password"
                    onChange={handleChange}
                    required
                />
                <button type="submit">Add User</button>
            </form>
        </div>
    );
};

export default AddUser;
