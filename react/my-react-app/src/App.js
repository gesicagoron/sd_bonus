import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import DeviceList from './components/DeviceList';
import AddDevice from './components/AddDevice';
import EditDevice from './components/EditDevice';
import UserList from './components/UserList';
import AddUser from './components/AddUser';
import EditUser from './components/EditUser';
import AdminChat from './components/AdminChat';
import ClientChat from './components/ClientChat';
import Login from './components/Login';
import AssignDeviceForm from './components/AssignDeviceForm';
import UserDevicesTable from './components/UserDevicesTable';
import './App.css';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { Line, Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    Title,
    Tooltip,
    Legend
);

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [role, setRole] = useState(null);
    const [userId, setUserId] = useState(null); // Store user ID
    const [devices, setDevices] = useState([]); // Devices associated with the user
    const [editingDevice, setEditingDevice] = useState(null);
    const [editingUser, setEditingUser] = useState(null);
    const [refreshList, setRefreshList] = useState(true);
    const [notifications, setNotifications] = useState([]); // Notifications list
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [chartData, setChartData] = useState(null); // Chart data for the selected date
    const [chartType, setChartType] = useState('line'); // Chart type: line or bar
    const navigate = useNavigate();
    const [deviceId, setDeviceId] = useState(null); // Store the first device ID

    const handleLogin = (userRole, userId) => {
        setIsAuthenticated(true);
        setRole(userRole);
        setUserId(userId); // Set user ID after login
        localStorage.setItem('role', userRole);
        localStorage.setItem('userId', userId); // Store user ID
        navigate(userRole === 'ROLE_admin' ? '/admin' : '/client');
    };


    // Step 2: Fetch devices and set deviceId
    useEffect(() => {
        const fetchDevices = async () => {
            try {
                console.log("Fetched User ID:", userId); // Debug log
                if (!userId) {
                    console.error("User ID not set.");
                    return;
                }

                const response = await fetch(`http://localhost:80/device/users/devices/${userId}`);
                //http://localhost:80/device/users/devices/${userId}
                //http://localhost:8082/users/devices/${userId}
                if (!response.ok) {
                    throw new Error("Failed to fetch devices.");
                }

                const devices = await response.json();
                console.log("Fetched devices:", devices); // Debug log

                if (devices.length > 0) {
                    setDevices(devices);
                    setDeviceId(devices[0].id); // Automatically select the first device
                } else {
                    console.warn("No devices found for this user.");
                    setDevices([]);
                    setDeviceId(null);
                }
            } catch (error) {
                console.error("Error fetching devices:", error);
            }
        };

        if (userId) {
            fetchDevices();
        }
    }, [userId]); // Only run fetchDevices when userId changes


    // Step 1: Fetch chart data once deviceId is set
    useEffect(() => {
        if (!deviceId) {
            console.warn("Device ID not set yet. Waiting to fetch chart data...");
            return; // Exit the effect early if deviceId is not set
        }

        const fetchChartData = async () => {
            try {
                const formattedDate = selectedDate.toISOString().split("T")[0];
                const response = await fetch(
                    `http://localhost:80/monitor/devices/${deviceId}/energy-consumption?date=${formattedDate}`
                );
                //http://localhost:80/monitor/devices/${deviceId}/energy-consumption?date=${formattedDate}
                //http://localhost:8083/devices/${deviceId}/energy-consumption?date=${formattedDate}

                if (response.status === 204) { // Handle 204 No Content
                    console.warn("No data available for the selected date.");
                    setChartData(null);
                    return;
                }

                const data = await response.json(); // Parse JSON only if response is not empty

                if (Array.isArray(data) && data.length > 0) {
                    const uniqueData = data.reduce((acc, current) => {
                        if (!acc.find(entry => entry.timestamp === current.timestamp)) {
                            acc.push(current);
                        }
                        return acc;
                    }, []);

                    setChartData({
                        labels: uniqueData.map(entry =>
                            new Date(entry.timestamp).toLocaleTimeString("en-US", {
                                hour: "2-digit",
                                minute: "2-digit",
                            })
                        ),
                        datasets: [
                            {
                                label: "Energy Consumption (kWh)",
                                data: uniqueData.map(entry => entry.energyValue),
                                backgroundColor: "rgba(75,192,192,0.4)",
                                borderColor: "rgba(75,192,192,1)",
                                borderWidth: 2,
                                pointRadius: 3,
                                tension: 0.1,
                            },
                        ],
                    });
                } else {
                    setChartData(null);
                    console.warn("No energy consumption data available for the selected date.");
                }
            } catch (error) {
                console.error("Error fetching energy consumption data:", error.message);
            }
        };



        fetchChartData(); // Call fetchChartData only if deviceId is available
    }, [deviceId, selectedDate]); // Trigger only when deviceId or selectedDate changes


   // WebSocket for Notifications
   useEffect(() => {
       const socket = new WebSocket("ws://localhost:80/monitor/websocket");
       //ws://localhost:80/monitor/websocket
       //ws://localhost:8083/monitor/websocket

       socket.onopen = () => {
           console.log("WebSocket connection established.");
       };

       socket.onmessage = (event) => {
           try {
               console.log("Raw notification received:", event.data);

               // Extract relevant details from the plain text message
               const messageParts = event.data.match(/Device (\S+) exceeded the maximum hourly energy consumption! Current: ([\d.]+), Max: ([\d.]+)/);

               if (messageParts) {
                   const [_, deviceId, currentConsumption, maxConsumption] = messageParts;

                   // Check if the deviceId matches the user's devices
                   if (devices.some((device) => device.id === deviceId)) {
                       setNotifications((prev) => [
                           ...prev,
                           `Device ${deviceId} exceeded the maximum hourly energy consumption! Current: ${currentConsumption}, Max: ${maxConsumption}`,
                       ]);
                   } else {
                       console.warn(`Notification received for unrelated device: ${deviceId}`);
                   }
               } else {
                   console.warn("Message does not match the expected format:", event.data);
               }
           } catch (error) {
               console.error("Error processing notification:", error);
           }
       };


       socket.onerror = (error) => {
           console.error("WebSocket error:", error);
       };

       socket.onclose = () => {
           console.log("WebSocket connection closed.");
       };

       return () => {
           socket.close(); // Cleanup on component unmount
       };
   }, [devices]); // Only re-run if `devices` changes


    const handleDateChange = (date) => {
        setSelectedDate(date);
    };



    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('userId'); // Clear user ID
        setIsAuthenticated(false);
        setRole(null);
        setUserId(null); // Clear user ID state
        navigate('/login');
    };

    const handleEditDevice = (device) => setEditingDevice(device);
    const handleCancelEditDevice = () => setEditingDevice(null);
    const handleUpdateDeviceList = () => setRefreshList(!refreshList);
    const handleEditUser = (user) => setEditingUser(user);
    const handleCancelEditUser = () => setEditingUser(null);
    const handleUpdateUserList = () => setRefreshList(!refreshList);

    return (
        <div className="App">
            {!isAuthenticated ? (
                <Routes>
                    <Route path="/login" element={<Login onLogin={handleLogin} />} />
                    <Route path="*" element={<Navigate to="/login" />} />
                </Routes>
            ) : (
                <>
                    <div className="header">
                        <h1>Device and User Management</h1>
                        <button onClick={handleLogout}>Logout</button>
                    </div>
                    <div className="content">
                        <Routes>
                            <Route path="/admin" element={
                                role === 'ROLE_admin' ? (
                                    <>
                                        <h2 className="left-align">Admin Dashboard</h2>
                                        <h3 className="left-align">Devices</h3>
                                        <h4 className="left-align">Add New Device</h4>
                                        <div className="add-device-form">
                                            {editingDevice ? (
                                                <EditDevice
                                                    deviceToEdit={editingDevice}
                                                    onCancel={handleCancelEditDevice}
                                                    onUpdate={handleUpdateDeviceList}
                                                />
                                            ) : (
                                                <AddDevice onAdd={handleUpdateDeviceList} />
                                            )}
                                        </div>
                                        <h4 className="center-align">Device List</h4>
                                        <DeviceList
                                            role={role}
                                            onEdit={handleEditDevice}
                                            refresh={refreshList}
                                        />

                                        <h3 className="left-align">Users</h3>
                                        <h4 className="left-align">Add New User</h4>
                                        <div className="add-user-form">
                                            {editingUser ? (
                                                <EditUser
                                                    userToEdit={editingUser}
                                                    onCancel={handleCancelEditUser}
                                                    onUpdate={handleUpdateUserList}
                                                />
                                            ) : (
                                                <AddUser onAdd={handleUpdateUserList} />
                                            )}
                                        </div>
                                        <h4 className="center-align">User List</h4>
                                        <UserList
                                            onEdit={handleEditUser}
                                            refresh={refreshList}
                                        />

                                        <AssignDeviceForm />
                                        <UserDevicesTable />
                                        <AdminChat />
                                    </>
                                ) : (
                                    <Navigate to="/client" />
                                )
                            } />
                            <Route path="/client" element={
                                role === 'ROLE_client' ? (
                                    <>
                                        <h2 className="center-align">Client Dashboard</h2>
                                        <DeviceList
                                            role={role}
                                            userId={userId}
                                            refresh={refreshList}
                                        />

                                        <h3>Select a Date to View Energy Consumption</h3>
                                        <DatePicker
                                            selected={selectedDate}
                                            onChange={handleDateChange}
                                            dateFormat="yyyy-MM-dd"
                                        />

                                        <h3>Energy Consumption Chart</h3>
                                        {chartData ? (
                                            chartType === 'line' ? (
                                                <Line data={chartData} />
                                            ) : (
                                                <Bar data={chartData} />
                                            )
                                        ) : (
                                            <p>No data available for the selected date.</p>
                                        )}

                                        <h3>Notifications</h3>
                                        <ul>
                                            {notifications.map((notif, index) => (
                                                <li key={index}>{notif}</li>
                                            ))}
                                        </ul>

                                        <ClientChat userId={userId} />
                                    </>
                                ) : (
                                    <Navigate to="/admin" />
                                )
                            } />
                            <Route path="*" element={<Navigate to={role === 'ROLE_admin' ? '/admin' : '/client'} />} />
                        </Routes>
                    </div>
                </>
            )}
        </div>
    );
}

export default App;