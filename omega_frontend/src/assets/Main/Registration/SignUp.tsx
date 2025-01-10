import * as React from 'react';
import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import axiosInstance from "../Config/api.ts";
import omegaLogo from '../Assets/omega.png';

const defaultTheme = createTheme({
    palette: {
        primary: {
            main: '#663399',
        },
    },
    components: {
        MuiLink: {
            styleOverrides: {
                root: {
                    color: '#663399',
                    '&:hover': {
                        color: '#663399',
                    },
                },
            },
        },
        MuiTextField: {
            styleOverrides: {
                root: {
                    '& label.Mui-focused': {
                        color: '#663399',
                    },
                    '& .MuiInput-underline:after': {
                        borderBottomColor: '#663399',
                    },
                    '& .MuiOutlinedInput-root': {
                        '& fieldset': {
                            borderColor: '#663399',
                        },
                        '&:hover fieldset': {
                            borderColor: '#663399',
                        },
                        '&.Mui-focused fieldset': {
                            borderColor: '#663399',
                        },
                    },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                containedPrimary: {
                    backgroundColor: '#663399',
                    '&:hover': {
                        backgroundColor: '#663399',
                    },
                },
            },
        },
    },
});


export default function SignUp() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');

    const [usernameError, setUsernameError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [emailError, setEmailError] = useState('');
    const [passwordConfirmError, setPasswordConfirmError] = useState('');
    const [firstNameError, setFirstNameError] = useState('');
    const [lastNameError, setLastNameError] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const navigate = useNavigate();

    const validateEmail = (email: string) => {
        const emailRegex = /^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/;
        return emailRegex.test(email);
    };

    const validatePassword = (password: string) => {
        const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=-])(?=\S+$).{8,}$/;
        return passwordRegex.test(password);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
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

        if (username === '') {
            setUsernameError("Please enter your desired username");
            return;
        }

        if (!validatePassword(password)) {
            setPasswordError("Password must be at least 8 characters long and contain: a lowercase and uppercase letter, one digit, and one special character.");
            return;
        }

        if (password !== passwordConfirm) {
            setPasswordConfirmError("The two passwords do not match");
            return;
        }

        if (firstName === '') {
            setFirstNameError("Please enter first name");
            return;
        }

        if (lastName === '') {
            setLastNameError("Please enter last name");
            return;
        }

        try {
            const response = await axiosInstance.post('/auth/signup', {
                username: username,
                password: password,
                email: email,
                firstName: firstName,
                lastName: lastName,
            });

            if (response.status === 200) {
                setSuccessMessage("Registration successful. Click the link in your email to activate your account.");
                setTimeout(() => {
                    setSuccessMessage('');
                    navigate('/signin');
                }, 5000);
            }
        } catch (error: any) {
            if (error.response) {
                const {status, data} = error.response;
                switch (status) {
                    case 400:
                        if (data.message.includes("Email is already registered.")) {
                            setEmailError("Email is already registered.");
                        } else if (data.message.includes("Username is already taken.")) {
                            setUsernameError("Username is already taken.");
                        } else {
                            setErrorMessage(data.message);
                        }
                        break;
                    case 404:
                        setErrorMessage("Registration endpoint not found");
                        break;
                    default:
                        setErrorMessage("An error occurred during registration");
                }
            } else {
                setErrorMessage("An error occurred during registration");
            }
        }
    };

    return (
        <ThemeProvider theme={defaultTheme}>
            <Container component="main" maxWidth="xs"
                       sx={{backgroundColor: 'white', borderRadius: 2, boxShadow: 3, padding: 3, mt: 8}}>
                <CssBaseline/>
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar sx={{m: 1, bgcolor: 'black', width: 100, height: 100}}>
                        <img src={omegaLogo} alt="Omega Logo"
                             style={{width: '80%', height: '80%', objectFit: 'contain', transform: 'translateY(-7%)'}}/>
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Sign up
                    </Typography>
                    <Box component="form" noValidate onSubmit={handleSubmit} sx={{mt: 3}}>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    autoComplete="given-name"
                                    name="firstName"
                                    required
                                    fullWidth
                                    id="firstName"
                                    label="First Name"
                                    autoFocus
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                    error={!!firstNameError}
                                    helperText={firstNameError}
                                />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <TextField
                                    required
                                    fullWidth
                                    id="lastName"
                                    label="Last Name"
                                    name="lastName"
                                    autoComplete="family-name"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                    error={!!lastNameError}
                                    helperText={lastNameError}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    id="username"
                                    label="Username"
                                    name="username"
                                    autoComplete="username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    error={!!usernameError}
                                    helperText={usernameError}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    id="email"
                                    label="Email Address"
                                    name="email"
                                    autoComplete="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    error={!!emailError}
                                    helperText={emailError}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    name="password"
                                    label="Password"
                                    type="password"
                                    id="password"
                                    autoComplete="new-password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    error={!!passwordError}
                                    helperText={passwordError}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    fullWidth
                                    name="passwordConfirm"
                                    label="Confirm Password"
                                    type="password"
                                    id="passwordConfirm"
                                    autoComplete="new-password"
                                    value={passwordConfirm}
                                    onChange={(e) => setPasswordConfirm(e.target.value)}
                                    error={!!passwordConfirmError}
                                    helperText={passwordConfirmError}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <FormControlLabel
                                    control={<Checkbox value="allowExtraEmails" color="primary"/>}
                                    label="I want to receive inspiration, marketing promotions and updates via email."
                                />
                            </Grid>
                        </Grid>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            sx={{mt: 3, mb: 2}}
                        >
                            Sign Up
                        </Button>
                        {errorMessage && (
                            <Typography color="error" align="center">
                                {errorMessage}
                            </Typography>
                        )}
                        {successMessage && (
                            <Typography color="success" align="center">
                                {successMessage}
                            </Typography>
                        )}
                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link href="/signin" variant="body2">
                                    Already have an account? Sign in
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
}