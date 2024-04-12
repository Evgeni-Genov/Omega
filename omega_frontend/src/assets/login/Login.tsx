// Login.tsx

import React, {useState} from 'react';

const Login: React.FC = () => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const handleLogin = () => {
        // Here you can perform your login logic
        console.log(`Logging in with username: ${username} and password: ${password}`);
    };

    return (
        <div>
            <h2>Login</h2>
            <form>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <button type="button" onClick={handleLogin}>Login</button>
            </form>
        </div>
    );
};

export default Login;
