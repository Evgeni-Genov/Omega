import axios, {AxiosError, AxiosInstance, InternalAxiosRequestConfig} from 'axios';

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

async function refreshAccessToken(): Promise<string | null> {
    const refreshToken = localStorage.getItem('REFRESH_TOKEN');
    if (!refreshToken) return null;

    try {
        const response = await axios.post(`${BASE_URL}/auth/refresh-token`, refreshToken, {
            headers: {'Content-Type': 'text/plain'}
        });

        const {token, refreshToken: newRefreshToken} = response.data;
        localStorage.setItem('TOKEN', token);
        localStorage.setItem('REFRESH_TOKEN', newRefreshToken);

        return token;
    } catch (error) {
        localStorage.removeItem('TOKEN');
        localStorage.removeItem('REFRESH_TOKEN');
        return null;
    }
}

export const api: AxiosInstance = axios.create({
    baseURL: BASE_URL,
    headers: {'Content-Type': 'application/json'}
});

api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('TOKEN');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        config._retry = false;
        return config;
    }
);

api.interceptors.response.use(
    response => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

        if (!originalRequest || originalRequest._retry) {
            return Promise.reject(error);
        }

        if (
            (error.response?.status === 400 && error.response?.data?.message === 'JWT token is expired') ||
            error.response?.status === 403
        ) {
            originalRequest._retry = true;
            const newToken = await refreshAccessToken();

            if (newToken) {
                api.defaults.headers.Authorization = `Bearer ${newToken}`;
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return api(originalRequest);
            }
        }

        return Promise.reject(error);
    }
);

export default api;