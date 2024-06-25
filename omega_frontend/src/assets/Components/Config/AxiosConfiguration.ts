import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Refresh token function
async function refreshToken() {
    const refreshToken = localStorage.getItem('REFRESH_TOKEN');
    if (!refreshToken) {
        return null;
    }

    try {
        // Sending the refresh token as a plain string
        const response = await axios.post('http://localhost:8080/auth/refresh-token', refreshToken, {
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

// Axios response interceptor
axiosInstance.interceptors.response.use(
    response => response,
    async (error) => {
        const originalRequest = error.config;

        // Check if the error is due to expired token
        if (error.response.status === 400 && error.response.data.message === 'JWT token is expired' && !originalRequest._retry) {
            originalRequest._retry = true; // Set retry flag

            const newToken = await refreshToken(); // Attempt to refresh token
            if (newToken) {
                axiosInstance.defaults.headers['Authorization'] = `Bearer ${newToken}`; // Update default headers
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`; // Update original request headers
                return axiosInstance(originalRequest); // Retry original request
            }
        }

        // Check for 403 Forbidden error
        if (error.response.status === 403 && !originalRequest._retry) {
            originalRequest._retry = true; // Set retry flag

            const newToken = await refreshToken(); // Attempt to refresh token
            if (newToken) {
                axiosInstance.defaults.headers['Authorization'] = `Bearer ${newToken}`; // Update default headers
                originalRequest.headers['Authorization'] = `Bearer ${newToken}`; // Update original request headers
                return axiosInstance(originalRequest); // Retry original request
            }
        }
        return Promise.reject(error); // Reject promise if retry fails
    }
);

export default axiosInstance;
