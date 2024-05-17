import React, {useEffect, useState} from 'react';
import axiosInstance from '../../../AxiosConfiguration.ts'; // Adjust the import path as necessary

const UserProfile = ({userId}) => {
    const [userData, setUserData] = useState(null);

    useEffect(() => {
        const fetchUserData = async () => {
            if (!userId) {
                console.error("User ID not found.");
                return;
            }

            try {
                const response = await axiosInstance.get(`/user/${userId}`);
                setUserData(response.data);
            } catch (error) {
                console.error('Failed to fetch user data:', error);
            }
        };

        fetchUserData();
    }, [userId]);

    if (!userData) {
        return <div>Loading...</div>;
    }

    console.log("userData", userData);

    return (
        <div className="user-profile">
            <h1>Welcome, {userData.firstName} {userData.lastName}!</h1>
            <div className="user-details">
                <h2>Your Personal Data</h2>
                <p>Email: {userData.email}</p>
                <p>Username: {userData.username}</p>
                <p>First Name: {userData.firstName}</p>
                <p>Last Name: {userData.lastName}</p>
            </div>
        </div>
    );
};

export default UserProfile;