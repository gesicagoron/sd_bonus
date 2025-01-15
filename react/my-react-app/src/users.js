import axios from 'axios';

const API_URL = 'http://localhost:80/person/person';
//'http://localhost:80/person/person'
//http://localhost:8081/person
export const getUsers = async () => {
    try {
        const response = await axios.get(API_URL);
        if (Array.isArray(response.data)) {
            return response.data; // Return user data array
        } else {
            console.error("API response is not an array:", response.data);
            return []; // Return an empty array to prevent frontend crashes
        }
    } catch (error) {
        console.error("Error fetching users:", error);
        return []; // Return an empty array on error
    }
};

export const addUser = (user) => {
    return axios.post(API_URL, user);
};

export const updateUser = (id, user) => {
    return axios.put(`${API_URL}/${id}`, user);
};

export const deleteUser = (id) => {
    return axios.delete(`${API_URL}/${id}`);
};
