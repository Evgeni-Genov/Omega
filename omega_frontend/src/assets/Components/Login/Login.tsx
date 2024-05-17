import React, {useState} from 'react'
import {useNavigate} from 'react-router-dom'
import axiosInstance from "../../../AxiosConfiguration.ts";

const Login = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [usernameError, setUsernameError] = useState('')
    const [passwordError, setPasswordError] = useState('')
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate()

    const onButtonClick = async () => {
        setUsernameError('')
        setPasswordError('')
        setErrorMessage('')

        if ('' == username) {
            setUsernameError('Please enter your username!')
            return
        }

        if ('' === password) {
            setPasswordError('Please enter a password')
            return
        }

        try {
            const response = await axiosInstance.post('/auth/signin', {username: username, password: password})
            const userId = response.data.id;
            navigate(`/user-profile/}`, {state: {userId}});
        } catch (error) {
            if (error.response && error.response.status === 400) {
                setUsernameError(error.response.data.message)
            } else {
                console.log("Login Failed:", error)
            }
        }
    }

    return (
        <div className={'mainContainer'}>
            <div className={'titleContainer'}>
                <div>Login</div>
            </div>
            <br/>
            <div className={'inputContainer'}>
                <input
                    value={username}
                    placeholder="Enter your username here"
                    onChange={(ev) => setUsername(ev.target.value)}
                    className={'inputBox'}
                />
                <label className="errorLabel">{usernameError}</label>
            </div>
            <br/>
            <div className={'inputContainer'}>
                <input
                    value={password}
                    type="password"
                    placeholder="Enter your password here"
                    onChange={(ev) => setPassword(ev.target.value)}
                    className={'inputBox'}
                />
                <label className="errorLabel">{passwordError}</label>
            </div>
            <br/>
            <div className={'inputContainer'}>
                <input className={'inputButton'} type="button" onClick={onButtonClick} value={'Log in'}/>
            </div>
            {errorMessage && <div className="errorLabel">{errorMessage}</div>}
        </div>
    )
}

export default Login