import React, {useEffect, useState} from 'react';
import {Box, Button, Card, CardContent, CircularProgress, Container, Grid, Typography} from '@mui/material';
import {createTheme, styled, ThemeProvider} from '@mui/material/styles';
import axiosInstance from "../../AxiosConfiguration.ts";

const defaultTheme = createTheme({
    palette: {
        primary: {
            main: '#663399',
        },
    },
    components: {
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
    color: 'white',
    margin: theme.spacing(1),
}));

const MainPage = () => {
    const [accountBalance, setAccountBalance] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchAccountData = async () => {
            try {
                const token = localStorage.getItem('TOKEN');
                const userId = localStorage.getItem('USER_ID'); // Get the userId from localStorage

                if (!userId) {
                    throw new Error('User ID not found');
                }

                const accountResponse = await axiosInstance.get(`/account-balance/account-balances/user/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const transactionsResponse = await axiosInstance.get(`/transaction/all-transactions/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const accountData = accountResponse.data.length > 0 ? accountResponse.data[0].balance : 0;
                setAccountBalance(accountData);
                setTransactions(transactionsResponse.data);
            } catch (error) {
                console.error('Failed to fetch account data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchAccountData();
    }, []);

    const handleAddFunds = () => {
        // Implement add funds functionality
    };

    const handleSendFunds = () => {
        // Implement send funds functionality
    };

    if (loading) {
        return <Container maxWidth="md"><CircularProgress/></Container>;
    }

    return (
        <ThemeProvider theme={defaultTheme}>
            <Container component="main" maxWidth="md"
                       sx={{backgroundColor: 'white', borderRadius: 2, boxShadow: 3, padding: 3, mt: 8}}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                    <Typography variant="h4">Welcome to Your Dashboard</Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                    <Card sx={{minWidth: 275}}>
                        <CardContent>
                            <Typography variant="h5" component="div">
                                Account Balance
                            </Typography>
                            <Typography variant="h4" color="text.secondary">
                                ${accountBalance}
                            </Typography>
                        </CardContent>
                    </Card>
                    <Box display="flex" flexDirection="column">
                        <PurpleButton variant="contained" onClick={handleAddFunds}>Add Funds</PurpleButton>
                        <PurpleButton variant="contained" onClick={handleSendFunds}>Send Funds</PurpleButton>
                    </Box>
                </Box>
                <Typography variant="h5" gutterBottom>
                    Recent Transactions
                </Typography>
                <Grid container spacing={3}>
                    {transactions.map((transaction, index) => (
                        <Grid item xs={12} key={index}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6">{transaction.description}</Typography>
                                    <Typography color="textSecondary">
                                        Amount: ${transaction.amount} -
                                        Date: {new Date(transaction.date).toLocaleDateString()}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            </Container>
        </ThemeProvider>
    );
};

export default MainPage;
