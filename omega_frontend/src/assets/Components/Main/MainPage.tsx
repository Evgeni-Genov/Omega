import React, {useEffect, useState} from 'react';
import {
    Alert,
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
    Snackbar,
    TextField,
    Typography
} from '@mui/material';
import axiosInstance from "../Config/AxiosConfiguration.ts";
import {debounce} from 'lodash';
import {format, parseISO} from 'date-fns';
import SendIcon from '@mui/icons-material/Send';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AddCardIcon from '@mui/icons-material/AddCard';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import SettingsIcon from '@mui/icons-material/Settings';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddIcon from '@mui/icons-material/Add';
import {useNavigate} from 'react-router-dom';
import {styled} from '@mui/material/styles';

const PurpleButton = styled(Button)(({theme}) => ({
    backgroundColor: 'rebeccapurple',
    '&:hover': {
        backgroundColor: 'rgba(102, 51, 153, 0.8)',
    },
    color: 'white',
    margin: theme.spacing(1),
}));

const StyledCard = styled(Card)(({theme}) => ({
    backgroundColor: '#f5f5f5',
    borderRadius: theme.spacing(2),
    boxShadow: theme.shadows[3],
    padding: theme.spacing(2),
}));

const TransactionCard = styled(Card)(({theme}) => ({
    display: 'flex',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
    borderRadius: theme.spacing(1),
    boxShadow: theme.shadows[1],
    padding: theme.spacing(2),
    marginBottom: theme.spacing(1),
}));

const PositiveAmount = styled(Typography)(({theme}) => ({
    color: 'green',
}));

const NegativeAmount = styled(Typography)(({theme}) => ({
    color: 'red',
}));

const CustomTextField = styled(TextField)(({theme}) => ({
    '& label.Mui-focused': {
        color: 'rebeccapurple',
    },
    '& .MuiInput-underline:after': {
        borderBottomColor: 'rebeccapurple',
    },
    '& .MuiOutlinedInput-root': {
        '& fieldset': {
            borderColor: 'rebeccapurple',
        },
        '&:hover fieldset': {
            borderColor: 'rebeccapurple',
        },
        '&.Mui-focused fieldset': {
            borderColor: 'rebeccapurple',
        },
    },
}));

interface Transaction {
    id: number;
    description: string;
    amount: number;
    createdDate: string;
    userNameTag: string;
    currency: string;
    transactionStatus: string;
    transactionType: string;
    senderId: number;
    recipientId: number;
    isExpense: boolean;
}

interface AccountBalance {
    balance: number;
}

const formatDate = (dateString: string) => {
    try {
        if (!isNaN(parseFloat(dateString))) {
            const date = new Date(parseFloat(dateString) * 1000);
            return format(date, 'yyyy-MM-dd HH:mm:ss');
        }
        return format(parseISO(dateString), 'yyyy-MM-dd HH:mm:ss');
    } catch (error) {
        return 'Invalid Date';
    }
};

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
        rawExpiryDate: '',
        expiryDate: '',
        securityCode: '',
        amount: '',
    });
    const [suggestions, setSuggestions] = useState<string[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [addFundsSuccessMessage, setAddFundsSuccessMessage] = useState(false);
    const [sendFundsSuccessMessage, setSendFundsSuccessMessage] = useState(false);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchAccountData = async () => {
            setLoading(true);
            try {
                const token = localStorage.getItem('TOKEN');
                if (!token) {
                    navigate('/signin');
                    return;
                }
                const userId = localStorage.getItem('USER_ID');
                if (!userId) {
                    throw new Error('User ID not found');
                }
                const accountResponse = await axiosInstance.get(`/account-balance/account-balances/user/${userId}`);
                const transactionsResponse = await axiosInstance.get(`/transaction/all-transactions/${userId}`);
                const accountData = accountResponse.data.length > 0 ? accountResponse.data[0] : {balance: 0};
                setAccountBalance(accountData);
                setTransactions(transactionsResponse.data);
            } catch (error) {
                console.error('Failed to fetch account data:', error);
                localStorage.removeItem('TOKEN');
                localStorage.removeItem('REFRESH_TOKEN');
                navigate('/signin');
            } finally {
                setLoading(false);
            }
        };

        fetchAccountData();
    }, [navigate]);

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

        if (name === 'cardNumber') {
            let formattedValue = value.replace(/\s/g, ''); // Remove existing spaces

            if (formattedValue.length > 16) {
                formattedValue = formattedValue.slice(0, 16); // Restrict to 16 digits
            }

            // Add spaces every 4 digits
            formattedValue = formattedValue.replace(/(\d{4})(?=\d)/g, '$1 ');

            setAddFundsDetails({
                ...addFundsDetails,
                cardNumber: formattedValue,
            });
        } else if (name === 'expiryDate') {
            let formattedValue = value.replace(/[^0-9]/g, ''); // Remove non-numeric characters

            if (formattedValue.length <= 2) {
                formattedValue = formattedValue;
            } else if (formattedValue.length <= 4) {
                formattedValue = `${formattedValue.slice(0, 2)}/${formattedValue.slice(2)}`;
            } else {
                formattedValue = `${formattedValue.slice(0, 2)}/${formattedValue.slice(2, 4)}`;
            }

            setAddFundsDetails({
                ...addFundsDetails,
                rawExpiryDate: formattedValue,
            });

            if (formattedValue.length === 5) {
                const [month, year] = formattedValue.split('/');
                const formattedDate = `20${year}-${month}-01`; // yyyy-MM-dd
                setAddFundsDetails(prevState => ({
                    ...prevState,
                    expiryDate: formattedDate,
                }));
            }
        } else {
            setAddFundsDetails({
                ...addFundsDetails,
                [name]: value,
            });
        }
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
            const userId = parseInt(localStorage.getItem('USER_ID') || '', 10);
            const response = await axiosInstance.post('/transaction/send-funds', {
                ...transactionDetails,
                senderId: userId,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Transaction successful:', response.data);

            setAccountBalance(prevBalance => {
                if (prevBalance) {
                    return {balance: prevBalance.balance - parseFloat(transactionDetails.amount)};
                }
                return prevBalance;
            });

            setTransactions(prevTransactions => [
                {
                    ...response.data,
                    date: new Date().toISOString(),  // Ensuring the date is correctly formatted
                    amount: -Math.abs(parseFloat(transactionDetails.amount)), // Ensure the amount is negative
                    senderId: userId,
                },
                ...prevTransactions
            ]);

            handleClose();
            setSendFundsSuccessMessage(true); // Show success message for sending funds
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
            const userId = parseInt(localStorage.getItem('USER_ID') || '', 10);
            const response = await axiosInstance.post('/transaction/add-funds', {
                cardNumber: addFundsDetails.cardNumber.replaceAll(" ", ""),
                cardOwner: addFundsDetails.cardOwner,
                expiryDate: addFundsDetails.expiryDate,
                securityCode: addFundsDetails.securityCode,
                amount: addFundsDetails.amount,
                userId: userId,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Add funds successful:', response.data);

            setAccountBalance(prevBalance => {
                if (prevBalance) {
                    return {balance: prevBalance.balance + parseFloat(addFundsDetails.amount)};
                }
                return prevBalance;
            });

            setTransactions(prevTransactions => [
                {
                    ...response.data,
                    date: new Date().toISOString(), // Ensure the date is set correctly
                    amount: Math.abs(parseFloat(addFundsDetails.amount)), // Ensure the amount is positive
                    recipientId: userId,
                    isExpense: false,
                    transactionType: 'DEPOSIT'
                },
                ...prevTransactions
            ]);

            handleAddFundsClose();
            setAddFundsSuccessMessage(true); // Show success message for adding funds
        } catch (error) {
            console.error('Failed to add funds:', error);
            if (error.response && error.response.status === 400) {
                const errorData = error.response.data;
                if (errorData.validationErrors) {
                    setErrorMessage(`Validation Error: ${Object.values(errorData.validationErrors).join(', ')}`);
                } else {
                    setErrorMessage(errorData.message || 'Bad Request');
                }
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
            navigate(`/user-profile/${localStorage.getItem('USER_ID')}`);
        } else if (action === 'logout') {
            localStorage.removeItem('TOKEN');
            localStorage.removeItem('USER_ID');
            navigate('/signin');
        }
    };

    const handleSnackbarClose = () => {
        setAddFundsSuccessMessage(false);
        setSendFundsSuccessMessage(false);
    };

    const renderCardIcon = (cardNumber: string) => {
        if (cardNumber.startsWith('4')) {
            return (
                <img src="https://upload.wikimedia.org/wikipedia/commons/5/5e/Visa_Inc._logo.svg" alt="Visa"
                     style={{width: '50px', marginRight: '10px'}}/>
            );
        } else if (cardNumber.startsWith('5')) {
            return (
                <img src="https://upload.wikimedia.org/wikipedia/commons/a/a4/Mastercard_2019_logo.svg" alt="Mastercard"
                     style={{width: '50px', marginRight: '10px'}}/>
            );
        } else {
            return null;
        }
    };

    const getCardType = (cardNumber: string) => {
        if (cardNumber.startsWith('4')) {
            return 'Visa';
        } else if (cardNumber.startsWith('5')) {
            return 'Mastercard';
        } else {
            return '';
        }
    };

    const renderTransactionIcon = (transaction: Transaction) => {
        if (transaction.transactionType === 'DEPOSIT') {
            return <AddIcon sx={{color: 'green'}}/>;
        } else if (transaction.isExpense) {
            return <ArrowForwardIcon sx={{color: 'red'}}/>;
        } else {
            return <ArrowBackIcon sx={{color: 'green'}}/>;
        }
    };

    if (loading) {
        return <Container maxWidth="md"><CircularProgress/></Container>;
    }

    return (
        <Container component="main" maxWidth="md"
                   sx={{backgroundColor: 'white', borderRadius: 2, boxShadow: 3, padding: 3, mt: 8}}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4">Welcome to Your Dashboard</Typography>
                <IconButton onClick={handleMenuClick} sx={{color: '#663399'}}>
                    <SettingsIcon/>
                </IconButton>
                <Menu
                    anchorEl={anchorEl}
                    open={Boolean(anchorEl)}
                    onClose={() => handleMenuClose(null)}
                >
                    <MenuItem
                        sx={{
                            '&:hover': {backgroundColor: 'rebeccapurple', color: 'white'},
                            backgroundColor: 'white',
                            color: 'black',
                            '&.Mui-focusVisible': {
                                backgroundColor: 'rebeccapurple',
                                color: 'white',
                            }
                        }}
                        onClick={() => handleMenuClose('profile')}
                    >
                        Profile
                    </MenuItem>
                    <MenuItem
                        sx={{
                            '&:hover': {backgroundColor: 'rebeccapurple', color: 'white'},
                            backgroundColor: 'white',
                            color: 'black',
                            '&.Mui-focusVisible': {
                                backgroundColor: 'rebeccapurple',
                                color: 'white',
                            }
                        }}
                        onClick={() => handleMenuClose('logout')}
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
                        <TransactionCard>
                            <Box display="flex" alignItems="center" mr={2}>
                                {renderTransactionIcon(transaction)}
                            </Box>
                            <Box>
                                <Typography variant="h6">
                                    {transaction.description}
                                </Typography>
                                <Typography component="div" color="textSecondary">
                                    Amount: {transaction.isExpense ? (
                                    <NegativeAmount
                                        component="span">{formatCurrency(Math.abs(transaction.amount))}</NegativeAmount>
                                ) : (
                                    <PositiveAmount
                                        component="span">{formatCurrency(transaction.amount)}</PositiveAmount>
                                )}
                                </Typography>
                                <Typography color="textSecondary">
                                    Date: {transaction.createdDate ? formatDate(transaction.createdDate) : 'Invalid Date'}
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Status: {transaction.transactionStatus}
                                </Typography>
                            </Box>
                        </TransactionCard>
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
                            <CustomTextField
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
                    <CustomTextField
                        margin="dense"
                        name="amount"
                        label="Amount"
                        type="number"
                        fullWidth
                        value={transactionDetails.amount}
                        onChange={handleInputChange}
                    />
                    <CustomTextField
                        margin="dense"
                        name="description"
                        label="Description"
                        type="text"
                        fullWidth
                        value={transactionDetails.description}
                        onChange={handleInputChange}
                    />
                    <CustomTextField
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
                    </CustomTextField>
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
                    <Box display="flex" alignItems="center" mb={2}>
                        {renderCardIcon(addFundsDetails.cardNumber)}
                        <Typography variant="body1" component="div">
                            {getCardType(addFundsDetails.cardNumber)}
                        </Typography>
                    </Box>
                    <CustomTextField
                        margin="dense"
                        name="cardNumber"
                        label="Card Number"
                        type="text"
                        fullWidth
                        value={addFundsDetails.cardNumber}
                        onChange={handleAddFundsInputChange}
                    />
                    <CustomTextField
                        margin="dense"
                        name="cardOwner"
                        label="Card Owner"
                        type="text"
                        fullWidth
                        value={addFundsDetails.cardOwner}
                        onChange={handleAddFundsInputChange}
                    />
                    <Box display="flex" justifyContent="space-between">
                        <CustomTextField
                            margin="dense"
                            name="expiryDate"
                            label="Expiry Date (MM/YYYY)"
                            type="text"
                            fullWidth
                            value={addFundsDetails.rawExpiryDate}
                            onChange={handleAddFundsInputChange}
                            sx={{marginRight: 1}}
                        />
                        <CustomTextField
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
                    <CustomTextField
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
                        Add
                    </PurpleButton>
                </DialogActions>
            </Dialog>
            <Snackbar
                open={addFundsSuccessMessage}
                autoHideDuration={2000}
                onClose={handleSnackbarClose}
                anchorOrigin={{vertical: 'top', horizontal: 'center'}}
            >
                <Alert onClose={handleSnackbarClose} severity="success" sx={{width: '100%'}}>
                    Successfully added funds!
                </Alert>
            </Snackbar>
            <Snackbar
                open={sendFundsSuccessMessage}
                autoHideDuration={2000}
                onClose={handleSnackbarClose}
                anchorOrigin={{vertical: 'top', horizontal: 'center'}}
            >
                <Alert onClose={handleSnackbarClose} severity="success" sx={{width: '100%'}}>
                    Successfully sent funds!
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default MainPage;
