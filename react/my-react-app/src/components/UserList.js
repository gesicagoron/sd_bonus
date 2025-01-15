import React, { useEffect, useState } from 'react';
import { getUsers, deleteUser } from '../users';
import './UserList.css';

const UserList = ({ onEdit }) => {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const data = await getUsers(); // Await the fixed API call
                setUsers(data); // Set the user list
            } catch (error) {
                console.error('Error fetching users:', error);
            }
        };

        fetchUsers();
    }, []);


    const handleDelete = async (id) => {
        try {
            await deleteUser(id);
            setUsers(users.filter(user => user.id !== id));
        } catch (error) {
            console.error('Error deleting user:', error);
        }
    };

    return (
        <div>
            <h2>User List</h2>
            {users.length > 0 ? (
                <table border="1" cellPadding="10" cellSpacing="0" style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Age</th>
                            <th>Address</th>
                            <th>Role</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((user) => (
                            <tr key={user.id}>
                                <td>{user.id}</td>
                                <td>{user.name}</td>
                                <td>{user.age}</td>
                                <td>{user.address}</td>
                                <td>{user.role}</td>
                                <td>
                                    <button onClick={() => onEdit(user)}>Edit</button>
                                    <button onClick={() => handleDelete(user.id)} style={{ marginLeft: '10px' }}>Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No users available.</p>
            )}
        </div>
    );
};

export default UserList;
