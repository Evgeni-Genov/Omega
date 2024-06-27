import React from 'react';
import {useNavigate} from 'react-router-dom';
import {Box, Button, Card, CardContent, Container, Grid, Paper, Typography} from '@mui/material';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import SendIcon from '@mui/icons-material/Send';
import StoreIcon from '@mui/icons-material/Store';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import SecurityIcon from '@mui/icons-material/Security';
import './Home.css';

const theme = createTheme({
    palette: {
        primary: {
            main: '#663399',
        },
        secondary: {
            main: '#FF4081',
        },
    },
    typography: {
        h1: {
            fontSize: '3rem',
            fontWeight: 'bold',
            marginBottom: '1rem',
            color: 'white',
        },
        h2: {
            fontSize: '2rem',
            fontWeight: 'bold',
            marginBottom: '0.5rem',
            color: 'white',
        },
        h5: {
            fontSize: '1.5rem',
            color: 'white',
        },
        body1: {
            fontSize: '1rem',
            marginBottom: '0.5rem',
        },
    },
});

const Home = () => {
    const navigate = useNavigate();

    const handleGetStartedClick = () => {
        navigate('/signup');
    };

    return (
        <ThemeProvider theme={theme}>
            <Container maxWidth="lg" className="mainContainer">
                <Box className="heroSection" sx={{textAlign: 'center', color: 'white', py: 8}}>
                    <Typography variant="h1">
                        Welcome to Omega!
                    </Typography>
                    <Typography variant="h5" sx={{mb: 4}}>
                        Your trusted app for secure and easy transactions
                    </Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        size="large"
                        onClick={handleGetStartedClick}
                        sx={{backgroundColor: theme.palette.primary.main}}
                    >
                        Get Started
                    </Button>
                </Box>

                <Grid container spacing={4} sx={{mt: 4}}>
                    <Grid item xs={12} sm={6} md={3}>
                        <Card className="featureCard" sx={{height: '100%'}}>
                            <CardContent>
                                <SendIcon sx={{fontSize: 40, mb: 2, color: theme.palette.primary.main}}/>
                                <Typography variant="h2" color="primary">
                                    Send Money
                                </Typography>
                                <Typography variant="body1" color="textSecondary">
                                    Instantly send money to your friends and family with just a few clicks.
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Card className="featureCard" sx={{height: '100%'}}>
                            <CardContent>
                                <StoreIcon sx={{fontSize: 40, mb: 2, color: theme.palette.primary.main}}/>
                                <Typography variant="h2" color="primary">
                                    Pay in Stores
                                </Typography>
                                <Typography variant="body1" color="textSecondary">
                                    Use Omega to pay in stores quickly and securely without carrying cash.
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Card className="featureCard" sx={{height: '100%'}}>
                            <CardContent>
                                <AccountBalanceIcon sx={{fontSize: 40, mb: 2, color: theme.palette.primary.main}}/>
                                <Typography variant="h2" color="primary">
                                    Manage Finances
                                </Typography>
                                <Typography variant="body1" color="textSecondary">
                                    Keep track of your spending and manage your finances with ease using our tools.
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid item xs={12} sm={6} md={3}>
                        <Card className="featureCard" sx={{height: '100%'}}>
                            <CardContent>
                                <SecurityIcon sx={{fontSize: 40, mb: 2, color: theme.palette.primary.main}}/>
                                <Typography variant="h2" color="primary">
                                    Secure Transactions
                                </Typography>
                                <Typography variant="body1" color="textSecondary">
                                    We use advanced security measures to ensure your transactions are safe.
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                </Grid>

                <Box sx={{mt: 8, textAlign: 'center'}}>
                    <Typography variant="h2" color="primary" gutterBottom>
                        What Our Users Say
                    </Typography>
                    <Grid container spacing={4}>
                        <Grid item xs={12} sm={6} md={4}>
                            <Paper className="testimonialCard" sx={{
                                p: 3,
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                height: '100%'
                            }}>
                                <Typography variant="body1" color="textSecondary">
                                    "Omega is fantastic! I can easily send money to my friends and family. The app is
                                    secure and easy to use."
                                </Typography>
                                <Typography variant="body2" color="primary" sx={{mt: 2}}>
                                    - Jane Doe
                                </Typography>
                            </Paper>
                        </Grid>
                        <Grid item xs={12} sm={6} md={4}>
                            <Paper className="testimonialCard" sx={{
                                p: 3,
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                height: '100%'
                            }}>
                                <Typography variant="body1" color="textSecondary">
                                    "I love using Omega to manage my finances. The features are great, and I feel my
                                    money is safe."
                                </Typography>
                                <Typography variant="body2" color="primary" sx={{mt: 2}}>
                                    - John Smith
                                </Typography>
                            </Paper>
                        </Grid>
                        <Grid item xs={12} sm={6} md={4}>
                            <Paper className="testimonialCard" sx={{
                                p: 3,
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between',
                                height: '100%'
                            }}>
                                <Typography variant="body1" color="textSecondary">
                                    "Paying in stores has never been easier. Omega is my go-to app for quick and secure
                                    transactions."
                                </Typography>
                                <Typography variant="body2" color="primary" sx={{mt: 2}}>
                                    - Sarah Johnson
                                </Typography>
                            </Paper>
                        </Grid>
                    </Grid>
                </Box>

                <Box className="footer" sx={{
                    mt: 8,
                    py: 4,
                    textAlign: 'center',
                    backgroundColor: theme.palette.primary.main,
                    color: 'white'
                }}>
                    <Typography variant="body1">
                        © {new Date().getFullYear()} Omega. All rights reserved.
                    </Typography>
                </Box>
            </Container>
        </ThemeProvider>
    );
};

export default Home;
