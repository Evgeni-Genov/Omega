import {useQuery} from 'react-query';
import axiosInstance from '../Config/AxiosConfiguration.ts';


const fetchAvatar = async (userId) => {
    const token = localStorage.getItem('TOKEN');
    const response = await axiosInstance.get(`/api/avatar/user/${userId}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
        responseType: 'blob',
    });
    return URL.createObjectURL(response.data);
};

export const useAvatar = (userId) => {
    return useQuery(
        ['avatar', userId],
        () => fetchAvatar(userId),
        {
            enabled: !!userId,
            refetchOnWindowFocus: false,
        }
    );
};