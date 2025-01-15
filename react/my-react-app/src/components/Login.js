// src/components/Login.js
import React, { useState } from 'react';
import axios from '../config/axiosConfig';
import { useNavigate } from 'react-router-dom';

const Login = ({ onLogin }) => {
    const [credentials, setCredentials] = useState({ name: '', password: '' });
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCredentials((prevCredentials) => ({
            ...prevCredentials,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('/auth/login', {
                name: credentials.name,
                password: credentials.password,
            });

            const { token, role, userId } = response.data; // Include userId

            if (token && role && userId) {
                localStorage.setItem('token', token);
                localStorage.setItem('role', role);
                localStorage.setItem('userId', userId); // Store userId
                onLogin(role, userId); // Pass userId to onLogin
                navigate('/');
            } else {
                throw new Error('Invalid token, role, or userId');
            }
        } catch (err) {
            console.error('Login failed:', err);
            setError('Invalid credentials. Please try again.');
        }
    };


    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="name"
                    value={credentials.name}
                    placeholder="Username"
                    onChange={handleChange}
                    required
                />
                <input
                    type="password"
                    name="password"
                    value={credentials.password}
                    placeholder="Password"
                    onChange={handleChange}
                    required
                />
                <button type="submit">Login</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
};

export default Login;
