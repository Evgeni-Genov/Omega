import React, {useEffect, useState} from 'react';
import axiosInstance from "../../AxiosConfiguration.ts";

function UserList() {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const loginAndFetchUsers = async () => {
            try {
                const dummyReg = {
                    username: "admin",
                    password: "admin"
                }
                const response = await axiosInstance.post('/auth/signin', dummyReg);
                const token = response.data.token;

                const getAllUsersResponse = await axiosInstance.get("user/users", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                const userList = getAllUsersResponse.data;

                setUsers(userList);
            } catch (error) {
                console.log(error);
            }
        }
        loginAndFetchUsers();
    }, []);

    return (
        <div>
            <h2>All Users</h2>
            <ul>
                {Array.isArray(users) && users.map(user => (
                    <li key={user.id}>
                        id: {user.id},
                        username: {user.username},
                        email: {user.email},
                        nameTag: {user.nameTag},
                        role: {user.role}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default UserList;
