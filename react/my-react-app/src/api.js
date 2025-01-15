import axios from 'axios';

const API_URL = 'http://localhost/device';
//http://localhost/device
//http://localhost:8082


export const getDevices = () => {
    return axios.get(API_URL);
};


export const addDevice = (device) => {
    return axios.post(API_URL, device);
};


export const updateDevice = (id, device) => {
    return axios.put(`${API_URL}/${id}`, device);
};


export const deleteDevice = (id) => {
    return axios.delete(`${API_URL}/${id}`);
};

export const assignDeviceToUser = async (userId, deviceId) => {
    try {
        const response = await axios.post(`http://localhost:80/device/devices/${userId}/assign/${deviceId}`);
        //http://localhost:80/device/devices/${userId}/assign/${deviceId}
        //http://localhost:8082/devices/${userId}/assign/${deviceId}
        if (response.status === 204) {
            alert('Device assigned to user successfully!');
        }
    } catch (error) {
        console.error('Error assigning device:', error);
        alert('Failed to assign device to user. Please check the IDs and try again.');
    }
};

