import React, {useEffect, useState} from 'react';
import {
    Autocomplete,
    Box,
    Button,
    Card,
    CardContent,
    CircularProgress,
    Container,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid,
    IconButton,
    Menu,
    MenuItem,
    TextField,
    Typography
} from '@mui/material';
import {createTheme, styled, ThemeProvider} from '@mui/material/styles';
import {debounce} from 'lodash';
import {format} from 'date-fns';
import SendIcon from '@mui/icons-material/Send';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AddCardIcon from '@mui/icons-material/AddCard';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import SettingsIcon from '@mui/icons-material/Settings';
import {useNavigate} from 'react-router-dom';
import axiosInstance from "../../../AxiosConfiguration.ts";

const defaultTheme = createTheme({
    palette: {
        primary: {
            main: '#663399',
        },
        secondary: {
            main: '#f5f5f5',
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
    typography: {
        fontFamily: 'Roboto, sans-serif',
        h4: {
            fontWeight: 700,
        },
        h5: {
            fontWeight: 500,
        },
        body1: {
            fontSize: '1rem',
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

const StyledCard = styled(Card)(({theme}) => ({
    backgroundColor: theme.palette.secondary.main,
    borderRadius: theme.spacing(2),
    boxShadow: theme.shadows[3],
    padding: theme.spacing(2),
}));

interface Transaction {
    id: number;
    description: string;
    amount: number;
    date: string;
    userNameTag: string;
    currency: string;
    transactionStatus: string;
}

interface AccountBalance {
    balance: number;
}

const MainPage = () => {
    const [accountBalance, setAccountBalance] = useState<AccountBalance | null>(null);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(true);
    const [open, setOpen] = useState(false);
    const [openAddFunds, setOpenAddFunds] = useState(false);
    const [transactionDetails, setTransactionDetails] = useState({
        userNameTag: '',
        amount: '',
        description: '',
        currency: 'USD',
    });
    const [addFundsDetails, setAddFundsDetails] = useState({
        cardNumber: '',
        cardOwner: '',
        expiryDate: '',
        securityCode: '',
        amount: '',
    });
    const [suggestions, setSuggestions] = useState<string[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const navigate = useNavigate();

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
                const accountData = accountResponse.data.length > 0 ? accountResponse.data[0] : {balance: 0};
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

    const fetchSuggestions = debounce(async (query: string) => {
        setSearchLoading(true);
        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.get(`/user/search-user/${query}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setSuggestions(response.data.map((user: { nameTag: string }) => user.nameTag));
        } catch (error) {
            console.error('Failed to fetch user suggestions:', error);
        } finally {
            setSearchLoading(false);
        }
    }, 300);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setTransactionDetails({
            ...transactionDetails,
            [name]: value,
        });

        if (name === 'userNameTag') {
            if (value.length > 2) {
                fetchSuggestions(value);
            } else {
                setSuggestions([]);
            }
        }
    };

    const handleAddFundsInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setAddFundsDetails({
            ...addFundsDetails,
            [name]: value,
        });
    };

    const handleAddFunds = () => {
        setOpenAddFunds(true);
    };

    const handleClose = () => {
        setOpen(false);
        setErrorMessage('');
    };

    const handleAddFundsClose = () => {
        setOpenAddFunds(false);
        setErrorMessage('');
    };

    const handleSendFunds = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            const response = await axiosInstance.post('/transaction/send-funds', {
                ...transactionDetails,
                senderId: userId,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Transaction successful:', response.data);

            // Update account balance immediately
            setAccountBalance(prevBalance => {
                if (prevBalance) {
                    return {balance: prevBalance.balance - parseFloat(transactionDetails.amount)};
                }
                return prevBalance;
            });

            // Add the new transaction to the transactions list immediately
            setTransactions(prevTransactions => [
                ...prevTransactions,
                {
                    ...response.data,
                    date: new Date().toISOString(), // Add the current date to the transaction
                }
            ]);

            handleClose();
        } catch (error) {
            console.error('Failed to send funds:', error);
            if (error.response && error.response.status === 400) {
                setErrorMessage(error.response.data.message);
            } else {
                setErrorMessage('Internal Server Error. Please try again later.');
            }
        }
    };

    const handleAddFundsSubmit = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            const response = await axiosInstance.post('/account-balance/add-funds', {
                ...addFundsDetails,
                userId: userId,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Add funds successful:', response.data);

            // Update account balance immediately
            setAccountBalance(prevBalance => {
                if (prevBalance) {
                    return {balance: prevBalance.balance + parseFloat(addFundsDetails.amount)};
                }
                return prevBalance;
            });

            handleAddFundsClose();
        } catch (error) {
            console.error('Failed to add funds:', error);
            if (error.response && error.response.status === 400) {
                setErrorMessage(error.response.data.message);
            } else {
                setErrorMessage('Internal Server Error. Please try again later.');
            }
        }
    };

    const formatCurrency = (amount: number) => {
        return amount.toLocaleString('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        });
    };

    const handleMenuClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = (action: string | null) => {
        setAnchorEl(null);
        if (action === 'profile') {
            navigate('/profile'); // Navigate to the profile page
        } else if (action === 'logout') {
            handleLogout();
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('TOKEN');
        navigate('/');
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
                    <IconButton
                        onClick={handleMenuClick}
                        sx={{
                            color: 'rebeccapurple',
                            '&:hover': {
                                color: 'rgba(102, 51, 153, 0.8)',
                            }
                        }}
                    >
                        <SettingsIcon/>
                    </IconButton>
                    <Menu
                        anchorEl={anchorEl}
                        open={Boolean(anchorEl)}
                        onClose={() => handleMenuClose(null)}
                    >
                        <MenuItem
                            onClick={() => handleMenuClose('profile')}
                            sx={{
                                '&:hover': {
                                    backgroundColor: 'var(--hover-color)',
                                    color: 'white',
                                }
                            }}
                        >
                            Profile
                        </MenuItem>
                        <MenuItem
                            onClick={() => handleMenuClose('logout')}
                            sx={{
                                '&:hover': {
                                    backgroundColor: 'var(--hover-color)',
                                    color: 'white',
                                }
                            }}
                        >
                            Logout
                        </MenuItem>
                    </Menu>
                </Box>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                    <StyledCard>
                        <CardContent>
                            <Box display="flex" alignItems="center">
                                <AccountBalanceIcon sx={{fontSize: 40, marginRight: 2, color: '#663399'}}/>
                                <Box>
                                    <Typography variant="h5" component="div">
                                        Account Balance
                                    </Typography>
                                    <Typography variant="h4" color="text.secondary">
                                        {formatCurrency(accountBalance?.balance || 0)}
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </StyledCard>
                    <Box display="flex" flexDirection="column">
                        <PurpleButton variant="contained" startIcon={<SendIcon/>} onClick={() => setOpen(true)}>Send
                            Funds</PurpleButton>
                        <PurpleButton variant="contained" startIcon={<AddCardIcon/>} onClick={handleAddFunds}>Add
                            Funds</PurpleButton>
                    </Box>
                </Box>
                <Typography variant="h5" gutterBottom>
                    Recent Transactions
                </Typography>
                <Grid container spacing={3}>
                    {transactions.map((transaction, index) => (
                        <Grid item xs={12} key={index}>
                            <StyledCard>
                                <CardContent>
                                    <Typography variant="h6">{transaction.description}</Typography>
                                    <Typography color="textSecondary">
                                        Amount: {formatCurrency(transaction.amount)} -
                                        Date: {transaction.date ? format(new Date(transaction.date), 'yyyy-MM-dd HH:mm:ss') : 'Invalid Date'}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Status: {transaction.transactionStatus}
                                    </Typography>
                                </CardContent>
                            </StyledCard>
                        </Grid>
                    ))}
                </Grid>
                <Dialog open={open} onClose={handleClose}>
                    <DialogTitle>Send Funds</DialogTitle>
                    <DialogContent>
                        {errorMessage && (
                            <Typography color="error" gutterBottom>
                                {errorMessage}
                            </Typography>
                        )}
                        <Autocomplete
                            freeSolo
                            options={suggestions}
                            onInputChange={(event, newInputValue) => {
                                handleInputChange({target: {name: 'userNameTag', value: newInputValue}});
                            }}
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    margin="dense"
                                    name="userNameTag"
                                    label="Recipient Name Tag"
                                    type="text"
                                    fullWidth
                                    value={transactionDetails.userNameTag}
                                    onChange={handleInputChange}
                                    InputProps={{
                                        ...params.InputProps,
                                        endAdornment: (
                                            <>
                                                {searchLoading ? <CircularProgress color="inherit" size={20}/> : null}
                                                {params.InputProps.endAdornment}
                                            </>
                                        ),
                                    }}
                                />
                            )}
                        />
                        <TextField
                            margin="dense"
                            name="amount"
                            label="Amount"
                            type="number"
                            fullWidth
                            value={transactionDetails.amount}
                            onChange={handleInputChange}
                        />
                        <TextField
                            margin="dense"
                            name="description"
                            label="Description"
                            type="text"
                            fullWidth
                            value={transactionDetails.description}
                            onChange={handleInputChange}
                        />
                        <TextField
                            margin="dense"
                            name="currency"
                            label="Currency"
                            select
                            fullWidth
                            value={transactionDetails.currency}
                            onChange={handleInputChange}
                        >
                            <MenuItem value="USD">USD</MenuItem>
                            <MenuItem value="BGN">BGN</MenuItem>
                            <MenuItem value="EUR">EUR</MenuItem>
                            <MenuItem value="GBP">GBP</MenuItem>
                            <MenuItem value="JPY">JPY</MenuItem>
                        </TextField>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose} color="secondary">
                            Cancel
                        </Button>
                        <PurpleButton onClick={handleSendFunds} variant="contained">
                            Send
                        </PurpleButton>
                    </DialogActions>
                </Dialog>
                <Dialog open={openAddFunds} onClose={handleAddFundsClose}>
                    <DialogTitle>Add Funds</DialogTitle>
                    <DialogContent>
                        {errorMessage && (
                            <Typography color="error" gutterBottom>
                                {errorMessage}
                            </Typography>
                        )}
                        <Box display="flex" alignItems="center" mb={2}>
                            <CreditCardIcon sx={{fontSize: 40, marginRight: 2, color: '#663399'}}/>
                            <Typography variant="h5" component="div">
                                Enter Card Details
                            </Typography>
                        </Box>
                        <TextField
                            margin="dense"
                            name="cardNumber"
                            label="Card Number"
                            type="text"
                            fullWidth
                            value={addFundsDetails.cardNumber}
                            onChange={handleAddFundsInputChange}
                        />
                        <TextField
                            margin="dense"
                            name="cardOwner"
                            label="Card Owner"
                            type="text"
                            fullWidth
                            value={addFundsDetails.cardOwner}
                            onChange={handleAddFundsInputChange}
                        />
                        <Box display="flex" justifyContent="space-between">
                            <TextField
                                margin="dense"
                                name="expiryDate"
                                label="Expiry Date (MM/YY)"
                                type="text"
                                fullWidth
                                value={addFundsDetails.expiryDate}
                                onChange={handleAddFundsInputChange}
                                sx={{marginRight: 1}}
                            />
                            <TextField
                                margin="dense"
                                name="securityCode"
                                label="CVV"
                                type="text"
                                fullWidth
                                value={addFundsDetails.securityCode}
                                onChange={handleAddFundsInputChange}
                                sx={{marginLeft: 1}}
                            />
                        </Box>
                        <TextField
                            margin="dense"
                            name="amount"
                            label="Amount"
                            type="number"
                            fullWidth
                            value={addFundsDetails.amount}
                            onChange={handleAddFundsInputChange}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleAddFundsClose} color="secondary">
                            Cancel
                        </Button>
                        <PurpleButton onClick={handleAddFundsSubmit} variant="contained">
                            Add Funds
                        </PurpleButton>
                    </DialogActions>
                </Dialog>
            </Container>
        </ThemeProvider>
    );
};

export default MainPage;
