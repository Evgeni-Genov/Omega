import axios from 'axios';

export const getNewToken = async (refreshToken: string) => {
    const response = await axios.post('https://localhost:8080/auth/refresh-token', {
        refreshToken,
    });
    return response.data.token;
};