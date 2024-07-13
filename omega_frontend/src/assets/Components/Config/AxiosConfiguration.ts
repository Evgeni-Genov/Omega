import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'https://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

async function refreshToken() {
    const refreshToken = localStorage.getItem('REFRESH_TOKEN');
    if (!refreshToken) {
        return null;
    }

    try {
        const response = await axios.post('https://localhost:8080/auth/refresh-token', refreshToken, {
            headers: {
                'Content-Type': 'text/plain',
            },
        });

        const {token, refreshToken: newRefreshToken} = response.data;

        localStorage.setItem('TOKEN', token);
        localStorage.setItem('REFRESH_TOKEN', newRefreshToken);

        return token;
    } catch (error) {
        console.error('Token refresh failed:', error);
        localStorage.removeItem('TOKEN');
        localStorage.removeItem('REFRESH_TOKEN');
        return null;
    }
}

// Request interceptor to reset `_retry` flag
axiosInstance.interceptors.request.use(
    config => {
        config._retry = undefined; // Reset `_retry` flag
        return config;
    },
    error => Promise.reject(error)
);


axiosInstance.interceptors.response.use(
    response => response,
    async (error) => {
        const originalRequest = error.config;

        if (error.response.status === 400 && error.response.data.message === 'JWT token is expired' && !originalRequest._retry) {
            originalRequest._retry = true;

            const newToken = await refreshToken();
            if (newToken) {
                axiosInstance.defaults.headers['Authorization'] = `Bearer ${newToken}`;
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
                return axiosInstance(originalRequest);
            }
        }

        // Check for 403 Forbidden error
        if (error.response.status === 403 && !originalRequest._retry) {
            originalRequest._retry = true; // Set retry flag

            const newToken = await refreshToken(); // Attempt to refresh token
            if (newToken) {
                axiosInstance.defaults.headers['Authorization'] = `Bearer ${newToken}`;
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
                return axiosInstance(originalRequest);
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
