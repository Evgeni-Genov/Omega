import {useQuery} from 'react-query';
import axiosInstance from '../Config/api.ts';


const fetchAvatar = async (userId: string) => {
    const token = localStorage.getItem('TOKEN');
    try {
        const response = await axiosInstance.get(`/api/avatar/user/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            responseType: 'blob',
        });
        return URL.createObjectURL(response.data);
    } catch (error: any) {
        if (error.response && error.response.status === 404) {
            throw new Error('AVATAR_NOT_FOUND');
        }
        throw error;
    }
};

export const useAvatar = (userId: string) => {
    return useQuery(
        ['avatar', userId],
        () => fetchAvatar(userId),
        {
            enabled: !!userId,
            refetchOnWindowFocus: false,
            retry: (failureCount, error: any) => {
                // Don't retry if we got our specific "avatar not found" error
                if (error.message === 'AVATAR_NOT_FOUND') {
                    return false;
                }
                // For other errors, only retry once
                return failureCount < 1;
            },
            // Cache the result for a long time if successful
            staleTime: Infinity,
            cacheTime: Infinity,
        }
    );
};