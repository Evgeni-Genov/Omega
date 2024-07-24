import React, {useState} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import {Avatar, Box, Button, Container, CssBaseline, TextField, Typography} from '@mui/material';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import omegaLogo from '../Assets/omega.png';
import axiosInstance from '../Config/AxiosConfiguration.ts';

const defaultTheme = createTheme({
    palette: {
        primary: {
            main: '#663399',
        },
    },
    components: {
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

export default function ResetPassword() {
    const {token} = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const [newPassword, setNewPassword] = useState('');
    const [confirmNewPassword, setConfirmNewPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // Extract userId from the query parameters
    const userId = new URLSearchParams(location.search).get('userId');

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrorMessage('');

        if (newPassword !== confirmNewPassword) {
            setErrorMessage("Passwords do not match!");
            return;
        }

        try {
            await axiosInstance.post(`/api/reset-password/confirm?token=${token}`, {
                id: userId,
                newPassword,
                confirmNewPassword
            });
            navigate('/signin');
        } catch (error) {
            setErrorMessage("Failed to reset password. Please try again.");
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
                        Reset Password
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1}}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="newPassword"
                            label="New Password"
                            type="password"
                            id="newPassword"
                            autoComplete="new-password"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="confirmNewPassword"
                            label="Confirm Password"
                            type="password"
                            id="confirmNewPassword"
                            autoComplete="confirm-password"
                            value={confirmNewPassword}
                            onChange={(e) => setConfirmNewPassword(e.target.value)}
                        />
                        {errorMessage && (
                            <Typography color="error" align="center">
                                {errorMessage}
                            </Typography>
                        )}
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            sx={{mt: 3, mb: 2}}
                        >
                            Reset Password
                        </Button>
                    </Box>
                </Box>
            </Container>
        </ThemeProvider>
    );
}
