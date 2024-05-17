import React, {useState} from "react";
import {useNavigate} from 'react-router-dom';
import axiosInstance from "../../../AxiosConfiguration.ts";

import "./Registration.css"

// import user_icon from '../Assets/user.png'
// import email_icon from '../Assets/email.png'
// import password_icon from '../Assets/shield.png'

const Registration = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [email, setEmail] = useState('')
    const [passwordConfirm, setPasswordConfirm] = useState('')
    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')

    const [usernameError, setUsernameError] = useState('')
    const [passwordError, setPasswordError] = useState('')
    const [emailError, setEmailError] = useState('')
    const [passwordConfirmError, setPasswordConfirmError] = useState('')
    const [firstNameError, setFirstNameError] = useState('')
    const [lastNameError, setLastNameError] = useState('')
    const [errorMessage, setErrorMessage] = useState('');

    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();


    const validateEmail = (email) => {
        const emailRegex = /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/;
        return emailRegex.test(email);
    };

    const validatePassword = (password) => {
        const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=-])(?=\S+$).{8,}$/;
        return passwordRegex.test(password);
    };

    const onButtonClick = async () => {
        setUsernameError('');
        setPasswordError('');
        setEmailError('');
        setPasswordConfirmError('');
        setFirstNameError('');
        setLastNameError('');

        if (!validateEmail(email)) {
            setEmailError("Please enter a valid email");
            return;
        }

        if ('' === username) {
            setUsernameError("Please enter your desired username");
            return;
        }

        if (!validatePassword(password)) {
            setPasswordError("Password must be at 8 characters long and contain: a lowercase and uppercase letter, one digit, and one special character.");
            return;
        }

        if (password !== passwordConfirm) {
            setPasswordConfirmError("The two passwords do not match");
            return;
        }

        if ('' === firstName) {
            setFirstNameError("Please enter first name");
            return;
        }

        if ('' === lastName) {
            setLastNameError("Please enter last name");
            return;
        }

        try {
            // const navigate = useNavigate();
            const response = await axiosInstance.post('/auth/signup',
                {
                    username: username,
                    password: password,
                    email: email,
                    firstName: firstName,
                    lastName: lastName
                });
            if (response.status === 200) {
                setSuccessMessage("Registration successful. Click the link in your email to activate your account.");
                setTimeout(() => {
                    setSuccessMessage('');
                    navigate('/');
                }, 5000);
            }
        } catch (error) {
            if (error.response) {
                const {status, data} = error.response;
                switch (status) {
                    case 400:
                        if (data.message.includes("Email is already registered.")) {
                            setEmailError("Email is already registered.");
                        } else if (data.message.includes("Username is already taken.")) {
                            setUsernameError("Username is already taken.");
                        } else {
                            // General validation error
                            setErrorMessage(data.message);
                        }
                        break;
                    case 404:
                        // Handle not found error
                        setErrorMessage("Registration endpoint not found");
                        break;
                    default:
                        // Handle other errors
                        setErrorMessage("An error occurred during registration");
                }
            } else {
                // Network error or something else
                setErrorMessage("An error occurred during registration");
            }
        }
    }

    return (
        <div className="mainContainer">
            <div className={'titleContainer'}>
                <div>Registration</div>
            </div>
            <br/>
            <div className={'formContainer'}>
                <div className={'inputContainer'}>
                    {/*<img src={email_icon} alt=""/>*/}
                    <input
                        value={email}
                        type="text"
                        placeholder="Enter your email"
                        onChange={(ev) => setEmail(ev.target.value)}
                        className={'inputBox'}
                    />
                    <label className="errorLabel">{emailError}</label>
                </div>
                <br/>
                <div className={'inputContainer'}>
                    {/*<img src={user_icon} alt=""/>*/}
                    <input
                        value={username}
                        type="text"
                        placeholder="Please enter a desired username"
                        onChange={(ev) => setUsername(ev.target.value)}
                        className={'inputBox'}
                    />
                    <label className="errorLabel">{usernameError}</label>
                </div>
                <br/>
                <div className={'inputContainer'}>
                    {/*<img src={password_icon} alt=""/>*/}
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
                    {/*<img src={password_icon} alt=""/>*/}
                    <div className="inputContainer">
                        <input
                            value={passwordConfirm}
                            type="password"
                            placeholder="Confirm your password"
                            onChange={(ev) => setPasswordConfirm(ev.target.value)}
                            className={'inputBox'}
                        />
                    </div>
                    <label className="errorLabel">{passwordConfirmError}</label>
                </div>
                <br/>
                <div className={'inputContainer'}>
                    {/*<img src={user_icon} alt=""/>*/}
                    <input
                        value={firstName}
                        type="text"
                        placeholder="Enter your first name"
                        onChange={(ev) => setFirstName(ev.target.value)}
                        className={'inputBox'}
                    />
                    <label className="errorLabel">{firstNameError}</label>
                </div>
                <br/>
                <div className={'inputContainer'}>
                    {/*<img src={user_icon} alt=""/>*/}
                    <input
                        value={lastName}
                        type="text"
                        placeholder="Enter your last name"
                        onChange={(ev) => setLastName(ev.target.value)}
                        className={'inputBox'}
                    />
                    <label className="errorLabel">{lastNameError}</label>
                </div>
            </div>
            <br/>
            {successMessage && (
                <div className="success-message">
                    {successMessage}
                </div>
            )}
            <br/>
            <div className={'inputContainer'}>
                <input className={'inputButton'} type="button" onClick={onButtonClick} value={'Registration'}/>
            </div>
        </div>
    )
}

export default Registration;

