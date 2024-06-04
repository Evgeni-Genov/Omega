import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {
    Avatar,
    Box,
    Button,
    Checkbox,
    Container,
    CssBaseline,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    FormControlLabel,
    Grid,
    Link,
    TextField,
    Typography
} from '@mui/material';
import {createTheme, styled, ThemeProvider} from '@mui/material/styles';
import axiosInstance from "../../../AxiosConfiguration";
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

const PurpleButton = styled(Button)(({theme}) => ({
    backgroundColor: 'rebeccapurple',
    '&:hover': {
        backgroundColor: 'rgba(102, 51, 153, 0.8)',
    },
}));

export default function SignIn() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [verificationCode, setVerificationCode] = useState('');
    const [usernameError, setUsernameError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [verificationCodeError, setVerificationCodeError] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [rememberMe, setRememberMe] = useState(false);
    const [forgotPasswordEmail, setForgotPasswordEmail] = useState('');
    const [isForgotPassword, setIsForgotPassword] = useState(false);
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [userId, setUserId] = useState(null); // Store user ID for 2FA verification
    const [token, setToken] = useState(null); // Store token temporarily
    const [refreshToken, setRefreshToken] = useState(null); // Store refresh token temporarily

    const navigate = useNavigate();

    useEffect(() => {
        const storedUsername = localStorage.getItem('rememberedUsername');
        const storedPassword = localStorage.getItem('rememberedPassword');
        if (storedUsername && storedPassword) {
            setUsername(storedUsername);
            setPassword(storedPassword);
            setRememberMe(true);
        }
    }, []);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setUsernameError('');
        setPasswordError('');
        setErrorMessage('');
        setVerificationCodeError('');

        if (username === '') {
            setUsernameError('Please enter your username!');
            return;
        }

        if (password === '') {
            setPasswordError('Please enter a password');
            return;
        }

        try {
            const response = await axiosInstance.post('/auth/signin', {username, password});
            const {id, token, refreshToken, twoFactorAuthentication: twoFactorEnabled} = response.data;

            console.log('Response data:', response.data);

            if (twoFactorEnabled) {
                setUserId(id);
                setToken(token);
                setRefreshToken(refreshToken);
                setOpen(true);
                console.log('Two-factor authentication is enabled, opening dialog...');
            } else {
                completeLogin(id, token, refreshToken);
            }
        } catch (error) {
            console.error('Error during sign-in:', error);
            if (error.response && error.response.status === 400) {
                setUsernameError(error.response.data.message);
            } else {
                setErrorMessage("Login Failed. Please try again.");
            }
        }
    };

    const completeLogin = (userId, token, refreshToken) => {
        localStorage.setItem('TOKEN', token);
        localStorage.setItem('REFRESH_TOKEN', refreshToken);
        localStorage.setItem('USER_ID', userId); // Save userId in localStorage

        if (rememberMe) {
            localStorage.setItem('rememberedUsername', username);
            localStorage.setItem('rememberedPassword', password);
        } else {
            localStorage.removeItem('rememberedUsername');
            localStorage.removeItem('rememberedPassword');
        }

        navigate(`/user-profile/${userId}`);
    };

    const handleVerifyCode = async () => {
        setLoading(true);
        setVerificationCodeError('');
        try {
            const response = await axiosInstance.post('/google-authenticator/verify-code', {
                id: userId,
                twoFactorAuthCode: verificationCode,
            });
            if (response.status === 200) {
                completeLogin(userId, token, refreshToken);
                handleClose();
            }
        } catch (e) {
            console.error('Error during code verification:', e);
            if (e.response && e.response.status === 400) {
                setVerificationCodeError('Invalid verification code!');
            } else {
                setVerificationCodeError('An error occurred while verifying the code.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        setOpen(false);
        setErrorMessage('');
    };

    const handleForgotPassword = async (event) => {
        event.preventDefault();
        setErrorMessage('');

        if (forgotPasswordEmail === '') {
            setErrorMessage('Please enter your email!');
            return;
        }

        try {
            await axiosInstance.post(`/user/reset-password?email=${forgotPasswordEmail}`);
            setErrorMessage('Password reset link has been sent to your email.');
        } catch (error) {
            setErrorMessage('Failed to send password reset link. Please try again.');
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
                        {isForgotPassword ? 'Forgot Password' : 'Sign in'}
                    </Typography>
                    {isForgotPassword ? (
                        <Box component="form" onSubmit={handleForgotPassword} noValidate sx={{mt: 1}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                id="forgotPasswordEmail"
                                label="Email Address"
                                name="forgotPasswordEmail"
                                autoComplete="email"
                                autoFocus
                                value={forgotPasswordEmail}
                                onChange={(e) => setForgotPasswordEmail(e.target.value)}
                            />
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                color="primary"
                                sx={{mt: 3, mb: 2}}
                            >
                                Send Reset Link
                            </Button>
                            {errorMessage && (
                                <Typography color="error" align="center">
                                    {errorMessage}
                                </Typography>
                            )}
                            <Link href="#" variant="body2" onClick={() => setIsForgotPassword(false)}>
                                Remembered your password? Sign in
                            </Link>
                        </Box>
                    ) : (
                        <Box component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                id="username"
                                label="Username"
                                name="username"
                                autoComplete="username"
                                autoFocus
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                error={!!usernameError}
                                helperText={usernameError}
                            />
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                name="password"
                                label="Password"
                                type="password"
                                id="password"
                                autoComplete="current-password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                error={!!passwordError}
                                helperText={passwordError}
                            />
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        value="remember"
                                        color="primary"
                                        checked={rememberMe}
                                        onChange={(e) => setRememberMe(e.target.checked)}
                                    />
                                }
                                label="Remember me"
                            />
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                color="primary"
                                sx={{mt: 3, mb: 2}}
                            >
                                Sign In
                            </Button>
                            {errorMessage && (
                                <Typography color="error" align="center">
                                    {errorMessage}
                                </Typography>
                            )}
                            <Grid container>
                                <Grid item xs>
                                    <Link href="#" variant="body2" onClick={() => setIsForgotPassword(true)}>
                                        Forgot password?
                                    </Link>
                                </Grid>
                                <Grid item>
                                    <Link href="/signup" variant="body2">
                                        {"Don't have an account? Sign Up"}
                                    </Link>
                                </Grid>
                            </Grid>
                        </Box>
                    )}
                </Box>
                <Dialog open={open} onClose={handleClose}>
                    <DialogTitle>Two-Step Verification</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Please open your Google Authenticator app and enter the 6-digit code.
                        </DialogContentText>
                        <TextField
                            label="Enter 6-digit code"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={verificationCode}
                            onChange={(e) => setVerificationCode(e.target.value)}
                            error={!!verificationCodeError}
                            helperText={verificationCodeError}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose} color="secondary">
                            Cancel
                        </Button>
                        <PurpleButton onClick={handleVerifyCode} variant="contained"
                                      disabled={loading || !verificationCode}>
                            Login
                        </PurpleButton>
                    </DialogActions>
                </Dialog>
            </Container>
        </ThemeProvider>
    );
}
