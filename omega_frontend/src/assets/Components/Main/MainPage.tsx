import React, {useCallback, useEffect, useRef, useState} from 'react';
import {
    Alert,
    Autocomplete,
    Avatar,
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
import {format, isSameYear, isToday, isValid, parseISO} from 'date-fns';
import SendIcon from '@mui/icons-material/Send';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AddCardIcon from '@mui/icons-material/AddCard';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import SettingsIcon from '@mui/icons-material/Settings';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddIcon from '@mui/icons-material/Add';
import {useNavigate} from 'react-router-dom';
import {createTheme, styled, ThemeProvider} from '@mui/material/styles';
import InfoIcon from "@mui/icons-material/Info";
import DescriptionIcon from '@mui/icons-material/Description';
import {ArcElement, Chart as ChartJS, Legend, Tooltip} from 'chart.js';
import {Doughnut} from 'react-chartjs-2';
import {motion} from 'framer-motion';
import {useAvatar} from "../Util/AvatarUtil.tsx";

ChartJS.register(ArcElement, Tooltip, Legend);

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

const theme = createTheme({
    components: {
        MuiPickersDay: {
            styleOverrides: {
                root: {
                    '&.Mui-selected': {
                        backgroundColor: 'rebeccapurple',
                        '&:hover': {
                            backgroundColor: 'rgba(102, 51, 153, 0.8)',
                        },
                    },
                },
            },
        },
        MuiPickersCalendarHeader: {
            styleOverrides: {
                switchHeader: {
                    '& .MuiTypography-root': {
                        color: 'rebeccapurple',
                    },
                    '& .MuiSvgIcon-root': {
                        color: 'rebeccapurple',
                    },
                },
            },
        },
        MuiPickersModal: {
            styleOverrides: {
                dialogAction: {
                    '& .MuiButton-textPrimary': {
                        color: 'rebeccapurple',
                    },
                },
            },
        },
    },
});

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
        const date = parseISO(dateString);
        if (!isValid(date)) {
            throw new Error('Invalid date');
        }
        return format(date, 'yyyy-MM-dd HH:mm:ss');
    } catch (error) {
        console.error('Error formatting date:', error);
        return 'Invalid Date';
    }
};

const formatDateWithoutTime = (dateInput: string | Array<number>) => {
    try {
        let date: Date;
        if (Array.isArray(dateInput) && dateInput.length === 3) {
            const [year, month, day] = dateInput;
            date = new Date(year, month - 1, day);
        } else if (typeof dateInput === 'string') {
            date = parseISO(dateInput);
        } else {
            throw new Error('Invalid date input');
        }

        if (!isValid(date)) {
            throw new Error('Invalid date');
        }
        return format(date, 'yyyy-MM-dd');
    } catch (error) {
        console.error('Error formatting date:', error);
        return 'Invalid Date';
    }
};

const MainPage = () => {
    const [accountBalance, setAccountBalance] = useState<AccountBalance | null>(null);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(true);
    const [openAddFunds, setOpenAddFunds] = useState(false);
    const [suggestions, setSuggestions] = useState<string[]>([]);
    const [searchLoading, setSearchLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [addFundsSuccessMessage, setAddFundsSuccessMessage] = useState(false);
    const [sendFundsSuccessMessage, setSendFundsSuccessMessage] = useState(false);
    const [requestFundsSuccessMessage, setRequestFundsSuccessMessage] = useState(false);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const navigate = useNavigate();
    const [errorColor, setErrorColor] = useState('error');
    const [createBudgetSuccessMessage, setCreateBudgetSuccessMessage] = useState(false);
    const [viewBudget, setViewBudget] = useState(false);
    const [showCreateBudget, setShowCreateBudget] = useState(false);
    const [openBudget, setOpenBudget] = useState(false);
    const [budgets, setBudgets] = useState([]);
    const [remainingBudget, setRemainingBudget] = useState<number | null>(null);
    const [totalSpent, setTotalSpent] = useState<number>(0);
    const [searchUser, setSearchUser] = useState('');
    const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null);
    const [transactionSnapshots, setTransactionSnapshots] = useState<any[]>([]);
    const [openReport, setOpenReport] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [transactionHistory, setTransactionHistory] = useState([]);
    const [openRequestFunds, setOpenRequestFunds] = useState(false);
    const [mainSearchUser, setMainSearchUser] = useState('');
    const [mainSearchSuggestions, setMainSearchSuggestions] = useState([]);
    const [sendFundsSearchUser, setSendFundsSearchUser] = useState('');
    const [sendFundsSuggestions, setSendFundsSuggestions] = useState([]);
    const [openRemoveFriendDialog, setOpenRemoveFriendDialog] = useState(false);
    const [pendingFundRequests, setPendingFundRequests] = useState([]);
    const userId = localStorage.getItem('USER_ID');
    const [isFriend, setIsFriend] = useState(false);
    const chatEndRef = useRef(null);
    const [isLoading, setIsLoading] = useState(false);
    const [sendFundsDialogState, setSendFundsDialogState] = useState({open: false, fromUserHistory: false});
    const {data: avatarUrl, isLoading: avatarLoading, error: avatarError} = useAvatar(userId);
    const [requestFundsDetails, setRequestFundsDetails] = useState({
        amount: '',
        currency: 'USD',
        description: '',
    });
    const [reportDates, setReportDates] = useState({
        startDate: '',
        endDate: ''
    });
    const [budgetDetails, setBudgetDetails] = useState({
        budget: '',
        startDate: '',
        endDate: ''
    });
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
                const accountResponse = await axiosInstance.get(`/api/account-balance/user/${userId}`);
                const transactionsResponse = await axiosInstance.get(`/api/transaction/${userId}`);
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

    useEffect(() => {
        if (selectedUser) {
            setIsFriend(selectedUser.isFriend);
        }
    }, [selectedUser]);

    useEffect(() => {
        if (chatEndRef.current) {
            chatEndRef.current.scrollIntoView({behavior: "smooth"});
        }
    }, [transactionHistory]);

    useEffect(() => {
        const fetchBudget = async () => {
            try {
                const token = localStorage.getItem('TOKEN');
                const userId = localStorage.getItem('USER_ID');
                const response = await axiosInstance.get(`/api/budgets/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setBudgets([response.data]);
            } catch (error) {
                console.error('Failed to fetch budget:', error);
            }
        };

        fetchBudget();
    }, []);

    useEffect(() => {
        if (viewBudget) {
            fetchRemainingBudget();
            fetchTotalSpent();
        }
    }, [viewBudget]);

    const fetchTransactionHistory = useCallback(async (nameTag) => {
        if (!nameTag) return;

        try {
            const token = localStorage.getItem('TOKEN');
            const currentUserId = localStorage.getItem('USER_ID');
            const response = await axiosInstance.get(`/api/transactions`, {
                params: {
                    userId: currentUserId,
                    otherUserNameTag: nameTag
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setTransactionHistory(response.data.transactions);
            setIsFriend(response.data.isFriend);
        } catch (error) {
            console.error('Failed to fetch transaction history:', error);
        }
    }, []);

    useEffect(() => {
        if (selectedUser && selectedUser.nameTag) {
            fetchTransactionHistory(selectedUser.nameTag);
        }
    }, [selectedUser, fetchTransactionHistory]);

    const fetchMainSuggestions = useCallback(
        debounce(async (query: string) => {
            setSearchLoading(true);
            try {
                const token = localStorage.getItem('TOKEN');
                const response = await axiosInstance.get(`/api/search-user/${query}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setMainSearchSuggestions(response.data);
            } catch (error) {
                console.error('Failed to fetch user suggestions:', error);
            } finally {
                setSearchLoading(false);
            }
        }, 300),
        []
    );

    useEffect(() => {
        if (selectedUser) {
            setIsFriend(selectedUser.isFriend);
        }
    }, [selectedUser]);

    useEffect(() => {
        if (chatEndRef.current) {
            chatEndRef.current.scrollIntoView({behavior: "smooth"});
        }
    }, [transactionHistory]);

    useEffect(() => {
        if (selectedUser) {
            setIsFriend(selectedUser.isFriend);
        }
    }, [selectedUser]);

    const fetchSendFundsSuggestions = useCallback(
        debounce(async (query: string) => {
            setSearchLoading(true);
            try {
                const token = localStorage.getItem('TOKEN');
                const userId = localStorage.getItem('USER_ID');
                const response = await axiosInstance.get(`/api/users/${userId}/friends/${query}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setSendFundsSuggestions(response.data);
            } catch (error) {
                console.error('Failed to fetch user suggestions:', error);
            } finally {
                setSearchLoading(false);
            }
        }, 300),
        []
    );

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setTransactionDetails({
            ...transactionDetails,
            [name]: value,
        });

        if (name === 'userNameTag') {
            if (value.length > 2) {
                fetchSendFundsSuggestions(value);
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
        setSendFundsDialogState({open: false, fromUserHistory: false});
        setErrorMessage('');
    };

    const handleAddFundsClose = () => {
        setOpenAddFunds(false);
        setErrorMessage('');
    };

    const handleSendFunds = async (transactionDetails) => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = parseInt(localStorage.getItem('USER_ID') || '', 10);
            const response = await axiosInstance.post('/api/transaction/send-funds', {
                ...transactionDetails,
                senderId: userId,
                transactionType: 'TRANSFER',
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Transaction successful:', response.data);

            setAccountBalance(prevBalance => {
                if (prevBalance) {
                    return { balance: prevBalance.balance - parseFloat(transactionDetails.amount) };
                }
                return prevBalance;
            });

            setTransactions(prevTransactions => {
                const updatedTransactions = prevTransactions.map(t =>
                    t.id === transactionDetails.requestId
                        ? { ...t, transactionStatus: 'SUCCESSFUL' }
                        : t
                );

                // Remove the pending transaction if it exists
                const pendingTransactionIndex = updatedTransactions.findIndex(
                    t => t.id === transactionDetails.requestId && t.transactionStatus === 'PENDING'
                );
                if (pendingTransactionIndex !== -1) {
                    updatedTransactions.splice(pendingTransactionIndex, 1);
                }

                if (!transactionDetails.requestId) {
                    updatedTransactions.unshift({
                        ...response.data,
                        date: new Date().toISOString(),
                        amount: -Math.abs(parseFloat(transactionDetails.amount)),
                        senderId: userId,
                    });
                }
                return updatedTransactions;
            });

            setSendFundsSuccessMessage(true);
            fetchTransactionHistory(selectedUser.nameTag);
        } catch (error) {
            console.error('Failed to send funds:', error);
            if (error.response && error.response.status === 400) {
                setErrorMessage(error.response.data.message);
                setErrorColor('error');
            } else {
                setErrorMessage('Internal Server Error. Please try again later.');
            }
        }
    };

    const handleSendFundsDialog = async () => {
        await handleSendFunds(transactionDetails);
        setSendFundsDialogState({open: false, fromUserHistory: false});
    };

    const handleAddFundsSubmit = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = parseInt(localStorage.getItem('USER_ID') || '', 10);
            const response = await axiosInstance.post('/api/transaction/add-funds', {
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
                    date: new Date().toISOString(),
                    amount: Math.abs(parseFloat(addFundsDetails.amount)),
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

    const formatCurrency = (amount: number, p0: { style: string; currency: any; currencyDisplay: string; }) => {
        return amount.toLocaleString('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        });
    };

    const optimizedHandleClick = useCallback((action) => {
        setIsLoading(true);
        setTimeout(() => {
            action();
            setIsLoading(false);
        }, 300);
    }, []);

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
        setRequestFundsSuccessMessage(false);
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

    const renderTransactionIcon = (transaction) => {
        if (transaction.transactionType === 'DEPOSIT') {
            return <AddIcon sx={{color: 'green'}}/>;
        } else if (transaction.transactionStatus === 'PENDING' && transaction.recipientId === parseInt(userId)) {
            return <ArrowForwardIcon sx={{color: 'orange'}}/>;
        } else if (transaction.isExpense) {
            return <ArrowForwardIcon sx={{color: 'red'}}/>;
        } else {
            return <ArrowBackIcon sx={{color: 'green'}}/>;
        }
    };

    if (loading) {
        return <Container maxWidth="md"><CircularProgress/></Container>;
    }

    const handleBudgetInputChange = (e) => {
        const {name, value} = e.target;
        setBudgetDetails(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleCreateBudget = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = parseInt(localStorage.getItem('USER_ID') || '', 10);
            const response = await axiosInstance.post('/api/budgets', {...budgetDetails, userId}, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.status === 200) {
                setBudgets(prevBudgets => [...prevBudgets, response.data]);
                setOpenBudget(false);
                setShowCreateBudget(false);
                setCreateBudgetSuccessMessage(true);
            } else {
                throw new Error('Failed to create budget');
            }
        } catch (error) {
            console.error('Failed to create budget:', error);
            if (error.response) {
                if (error.response.status === 400) {
                    setErrorMessage(error.response.data.message);
                } else if (error.response.status === 500) {
                    setErrorMessage('Internal Server Error. Please try again later.');
                } else {
                    setErrorMessage('An error occurred. Please try again.');
                }
            } else {
                setErrorMessage('An error occurred. Please try again.');
            }
        }
    };

    const handleDeleteBudget = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const budgetId = budgets[0]?.id;
            await axiosInstance.delete(`/api/budgets/${budgetId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setViewBudget(false);
            setBudgets([]);
        } catch (error) {
            console.error('Failed to delete budget:', error);
        }
    };

    const fetchRemainingBudget = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            const response = await axiosInstance.get(`/api/budgets-remaining/${userId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setRemainingBudget(response.data);
        } catch (error) {
            console.error('Failed to fetch remaining budget:', error);
        }
    };


    const handleViewBudget = () => {
        setViewBudget(true);
        fetchRemainingBudget();
    };

    const fetchTotalSpent = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            const response = await axiosInstance.get(`/api/budgets/${userId}/total-spent`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setTotalSpent(response.data);
        } catch (error) {
            console.error('Failed to fetch total spent amount:', error);
        }
    };

    const fetchTransactionSnapshots = async (transactionId: number) => {
        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.get(`/api/snapshots/${transactionId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setTransactionSnapshots(response.data);
        } catch (error) {
            console.error('Failed to fetch transaction snapshots:', error);
        }
    };


    const handleReportDatesChange = (e) => {
        const {name, value} = e.target;
        setReportDates(prevDates => ({
            ...prevDates,
            [name]: value
        }));
    };

    const handleDownloadReport = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.get('/api/transactions-report', {
                params: {
                    startDate: reportDates.startDate,
                    endDate: reportDates.endDate
                },
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                responseType: 'blob'
            });

            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'transaction_report.pdf');
            document.body.appendChild(link);
            link.click();
            link.remove();
            setOpenReport(false);
        } catch (error) {
            console.error('Failed to download report:', error);
            setErrorMessage('Failed to download report. Please try again.');
        }
    };

    const doughnutLabelsPlugin = {
        id: 'doughnutLabels',
        afterDraw(chart, args, options) {
            const {ctx, chartArea: {top, bottom, left, right, width, height}} = chart;
            ctx.save();

            const centerX = (left + right) / 2;
            const centerY = (top + bottom) / 2;

            chart.data.datasets.forEach((dataset, datasetIndex) => {
                chart.getDatasetMeta(datasetIndex).data.forEach((datapoint, index) => {
                    const {startAngle, endAngle, innerRadius, outerRadius} = datapoint;
                    const middleAngle = startAngle + (endAngle - startAngle) / 2;

                    const x = centerX + Math.cos(middleAngle) * (outerRadius + innerRadius) / 2;
                    const y = centerY + Math.sin(middleAngle) * (outerRadius + innerRadius) / 2;

                    ctx.font = '12px Arial';
                    ctx.fillStyle = 'black';
                    ctx.textAlign = 'center';
                    ctx.textBaseline = 'middle';

                    const value = dataset.data[index];
                    const percentage = ((value / dataset.data.reduce((a, b) => a + b)) * 100).toFixed(2) + '%';

                    ctx.fillText(`${formatCurrency(value)} (${percentage})`, x, y);
                });
            });

            ctx.restore();
        }
    };


    const handleRequestFunds = async () => {
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            const response = await axiosInstance.post('/api/transaction/request-funds', {
                ...requestFundsDetails,
                senderId: userId,
                recipientId: selectedUser.id,
            }, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            console.log('Request funds successful:', response.data);
            setOpenRequestFunds(false);
            setSelectedUser(null);
            setRequestFundsSuccessMessage(true);
            setRequestFundsSuccessMessage(true);
            fetchTransactionHistory(selectedUser.nameTag);
        } catch (error) {
            console.error('Failed to request funds:', error);
        }
    };

    const handleRequestFundsInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setRequestFundsDetails(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };


    const formatTransactionDateHistory = (dateString: string): string => {
        try {
            const date = parseISO(dateString);

            if (isToday(date)) {
                return format(date, 'HH:mm');
            } else if (isSameYear(date, new Date())) {
                return format(date, 'dd.MM');
            } else {
                return format(date, 'dd.MM.yyyy');
            }
        } catch (error) {
            console.error('Error formatting transaction date:', error);
            return 'Invalid Date';
        }
    };

    const handleAddFriend = async () => {
        setIsLoading(true);
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            await axiosInstance.post(`/api/users/${userId}/friends/${selectedUser.nameTag}`, null, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setIsFriend(true);
            setSelectedUser(prevUser => ({...prevUser, isFriend: true}));
        } catch (error) {
            console.error('Failed to add friend:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleRemoveFriend = async () => {
        setIsLoading(true);
        try {
            const token = localStorage.getItem('TOKEN');
            const userId = localStorage.getItem('USER_ID');
            await axiosInstance.delete(`/api/users/${userId}/friends/${selectedUser.nameTag}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setIsFriend(false);
            setSelectedUser(prevUser => ({...prevUser, isFriend: false}));
            setOpenRemoveFriendDialog(false);
        } catch (error) {
            console.error('Failed to remove friend:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleMainSearchSuggestionClick = (user) => {
        setSelectedUser(user);
        setIsFriend(false);
    };

    const handleCancelRequest = async (requestId) => {
        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.patch(`/api/transaction/cancel/${requestId}`, null, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            // Update the transaction in the list instead of removing it
            setTransactions(prevTransactions =>
                prevTransactions.map(transaction =>
                    transaction.id === requestId
                        ? {...transaction, ...response.data}
                        : transaction
                )
            );
            setErrorMessage(''); // Clear any existing error messages
        } catch (error) {
            console.error('Failed to cancel fund request:', error);
            setErrorMessage('Failed to cancel fund request. Please try again.');
        }
    };

    const fetchTransactionById = async (transactionId) => {
        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.get(`/api/transactions/${transactionId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            return response.data;
        } catch (error) {
            console.error('Failed to fetch transaction:', error);
            if (error.response) {
                if (error.response.status === 400) {
                    setErrorMessage(error.response.data.message);
                } else {
                    setErrorMessage('An error occurred. Please try again.');
                }
            } else {
                setErrorMessage('An error occurred. Please try again.');
            }
        }
    };

    const handleExistingFundRequest = async (request) => {
        try {
            const transaction = await fetchTransactionById(request.id);
            const transactionDetails = {
                userNameTag: transaction.senderNameTag,
                amount: transaction.amount.toString(),
                description: transaction.description || 'Fulfilling fund request',
                currency: transaction.currency,
                recipientId: transaction.senderId,
                requestId: transaction.id,
            };
            await handleSendFunds(transactionDetails);

        } catch (error) {
            console.error('Failed to handle existing fund request:', error);
            setErrorMessage('Failed to process fund request. Please try again.');
        }
    };

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="md"
                       sx={{backgroundColor: 'white', borderRadius: 2, boxShadow: 3, padding: 3, mt: 8}}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                    <Box display="flex" alignItems="center" width="70%">
                        {avatarUrl ? (
                            <Avatar src={avatarUrl} alt="User Avatar" sx={{width: 56, height: 56, marginRight: 2}}/>
                        ) : (
                            <Avatar sx={{width: 56, height: 56, marginRight: 2, bgcolor: 'rebeccapurple'}}>
                                {userId?.charAt(0).toUpperCase() || 'U'}
                            </Avatar>
                        )}
                        <Autocomplete
                            freeSolo
                            options={mainSearchSuggestions}
                            getOptionLabel={(option) => option.nameTag || ''}
                            renderOption={(props, option) => {
                                const {key, ...otherProps} = props;
                                return (
                                    <Box component="li" sx={{'& > img': {mr: 2, flexShrink: 0}}}
                                         key={key} {...otherProps}>
                                        <Avatar
                                            src={`${axiosInstance.defaults.baseURL}/api/avatar/user/nameTag/${option.nameTag}`}
                                            alt={option.nameTag}
                                            sx={{width: 32, height: 32, marginRight: 2}}
                                        />
                                        {option.nameTag}
                                    </Box>
                                );
                            }}
                            renderInput={(params) => (
                                <CustomTextField
                                    {...params}
                                    margin="dense"
                                    label="Search User"
                                    type="text"
                                    fullWidth
                                    value={mainSearchUser}
                                    onChange={(e) => {
                                        const value = e.target.value;
                                        setMainSearchUser(value);

                                        if (value.length > 2) {
                                            fetchMainSuggestions(value);
                                        } else {
                                            setMainSearchSuggestions([]);
                                        }
                                    }}
                                    InputProps={{
                                        ...params.InputProps,
                                        endAdornment: (
                                            <>
                                                {searchLoading ?
                                                    <CircularProgress color="inherit" size={20}/> : null}
                                                {params.InputProps.endAdornment}
                                            </>
                                        ),
                                    }}
                                />
                            )}
                            onChange={(event, newValue) => {
                                if (newValue && typeof newValue === 'object' && newValue.nameTag) {
                                    handleMainSearchSuggestionClick(newValue);
                                } else {
                                    setSelectedUser(null);
                                }
                            }}
                            onInputChange={(event, newInputValue) => {
                                setMainSearchUser(newInputValue);
                            }}
                            sx={{width: '50%'}}
                        />
                    </Box>
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
                        <PurpleButton variant="contained" startIcon={<SendIcon/>}
                                      onClick={() => setSendFundsDialogState({open: true, fromUserHistory: false})}>Send
                            Funds</PurpleButton>
                        <PurpleButton variant="contained" startIcon={<AddCardIcon/>} onClick={handleAddFunds}>Add
                            Funds</PurpleButton>
                        <PurpleButton variant="contained" startIcon={<AccountBalanceIcon/>}
                                      onClick={() => setOpenBudget(true)}>Budget</PurpleButton>
                        <PurpleButton variant="contained" startIcon={<DescriptionIcon/>}
                                      onClick={() => setOpenReport(true)}>Report</PurpleButton>
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
                                <Box flexGrow={1}>
                                    <Typography variant="h6">
                                        {transaction.description}
                                    </Typography>
                                    <Typography component="div" color="textSecondary">
                                        Amount: {
                                        transaction.transactionStatus === 'PENDING' && transaction.recipientId === parseInt(userId) ? (
                                            <NegativeAmount
                                                component="span">{formatCurrency(transaction.amount)}</NegativeAmount>
                                        ) : transaction.isExpense ? (
                                            <NegativeAmount
                                                component="span">{formatCurrency(Math.abs(transaction.amount))}</NegativeAmount>
                                        ) : (
                                            <PositiveAmount
                                                component="span">{formatCurrency(transaction.amount)}</PositiveAmount>
                                        )
                                    }
                                    </Typography>
                                    <Typography color="textSecondary">
                                        Date: {transaction.createdDate ? formatDate(transaction.createdDate) : 'Invalid Date'}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Status: {transaction.transactionStatus}
                                    </Typography>
                                </Box>
                                <Box display="flex" justifyContent="flex-end" alignItems="center">
                                    {transaction.transactionStatus === 'PENDING' && transaction.recipientId === parseInt(userId) ? (
                                        <>
                                            <Button
                                                onClick={() => handleCancelRequest(transaction.id)}
                                                color="secondary"
                                                variant="outlined"
                                                style={{marginRight: '8px'}}
                                            >
                                                Cancel
                                            </Button>
                                            <PurpleButton
                                                onClick={() => handleExistingFundRequest(transaction)}
                                                variant="contained"
                                            >
                                                Send Requested Funds
                                            </PurpleButton>
                                        </>
                                    ) : (
                                        <IconButton
                                            onClick={() => {
                                                setSelectedTransaction(transaction);
                                                fetchTransactionSnapshots(transaction.id);
                                            }}
                                        >
                                            <InfoIcon/>
                                        </IconButton>
                                    )}
                                </Box>
                            </TransactionCard>
                        </Grid>
                    ))}
                </Grid>
                <Dialog
                    open={sendFundsDialogState.open}
                    onClose={() => setSendFundsDialogState({open: false, fromUserHistory: false})}
                >
                    <DialogTitle>Send Funds</DialogTitle>
                    <DialogContent>
                        {errorMessage && (
                            <Typography
                                sx={{
                                    color: 'error.main',
                                    fontWeight: 'bold',
                                    bgcolor: 'error.light',
                                    p: 1,
                                    borderRadius: 1,
                                }}
                                align="center"
                                gutterBottom
                            >
                                {errorMessage}
                            </Typography>
                        )}
                        {sendFundsDialogState.fromUserHistory ? (
                            <CustomTextField
                                margin="dense"
                                name="userNameTag"
                                label="User"
                                type="text"
                                fullWidth
                                value={transactionDetails.userNameTag}
                                disabled
                            />
                        ) : (
                            <Autocomplete
                                freeSolo
                                options={sendFundsSuggestions}
                                getOptionLabel={(option) => option.nameTag}
                                renderOption={(props, option) => (
                                    <Box component="li" sx={{'& > img': {mr: 2, flexShrink: 0}}} {...props}>
                                        <Avatar
                                            src={`${axiosInstance.defaults.baseURL}/api/avatar/user/nameTag/${option.nameTag}`}
                                            alt={option.nameTag}
                                            sx={{width: 32, height: 32, marginRight: 2}}
                                        />
                                        {option.nameTag}
                                    </Box>
                                )}
                                renderInput={(params) => (
                                    <CustomTextField
                                        {...params}
                                        margin="dense"
                                        label="Search User"
                                        type="text"
                                        fullWidth
                                        value={sendFundsSearchUser}
                                        onChange={(e) => {
                                            const value = e.target.value;
                                            setSendFundsSearchUser(value);

                                            if (value.length > 2) {
                                                fetchSendFundsSuggestions(value);
                                            } else {
                                                setSendFundsSuggestions([]);
                                            }
                                        }}
                                        InputProps={{
                                            ...params.InputProps,
                                            endAdornment: (
                                                <>
                                                    {searchLoading ?
                                                        <CircularProgress color="inherit" size={20}/> : null}
                                                    {params.InputProps.endAdornment}
                                                </>
                                            ),
                                        }}
                                    />
                                )}
                                onChange={(event, newValue) => {
                                    if (newValue) {
                                        setTransactionDetails(prevDetails => ({
                                            ...prevDetails,
                                            userNameTag: newValue.nameTag
                                        }));
                                    }
                                }}
                            />
                        )}
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
                            {/*<MenuItem value="BGN">BGN</MenuItem>*/}
                            {/*<MenuItem value="EUR">EUR</MenuItem>*/}
                            {/*<MenuItem value="GBP">GBP</MenuItem>*/}
                            {/*<MenuItem value="JPY">JPY</MenuItem>*/}
                        </CustomTextField>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setSendFundsDialogState({open: false, fromUserHistory: false})}
                                color="secondary">
                            Cancel
                        </Button>
                        <PurpleButton onClick={handleSendFundsDialog} variant="contained">
                            Send
                        </PurpleButton>
                    </DialogActions>
                </Dialog>
                <Dialog open={selectedUser !== null} onClose={() => setSelectedUser(null)} maxWidth="md" fullWidth>
                    <DialogTitle
                        style={{background: 'rebeccapurple', color: 'white', textAlign: 'center', padding: '16px'}}>
                        <DialogTitle
                            style={{
                                background: 'rebeccapurple',
                                color: 'white',
                                textAlign: 'center',
                                padding: '16px'
                            }}>
                            <Box display="flex" alignItems="center" justifyContent="center">
                                <Avatar
                                    src={`${axiosInstance.defaults.baseURL}/api/avatar/user/nameTag/${selectedUser?.nameTag}`}
                                    alt={selectedUser?.nameTag}
                                    sx={{width: 64, height: 64, marginRight: 2}}
                                />
                                <Typography variant="h5"
                                            style={{fontWeight: 'bold'}}>{selectedUser?.nameTag}</Typography>
                            </Box>
                        </DialogTitle>
                    </DialogTitle>
                    <DialogContent
                        style={{height: '500px', overflowY: 'auto', background: '#f0f0f0', padding: '16px'}}>
                        <Box display="flex" flexDirection="column" alignItems="stretch" height="100%">
                            {transactionHistory.map((transaction, index) => {
                                const formattedDate = formatTransactionDateHistory(transaction.createdDate);
                                const currentUserId = parseInt(localStorage.getItem('USER_ID') || '0');
                                const isSent = transaction.senderId === currentUserId;

                                return (
                                    <motion.div
                                        key={index}
                                        initial={{opacity: 0, y: 10}}
                                        animate={{opacity: 1, y: 0}}
                                        transition={{duration: 0.2, delay: index * 0.05}}
                                    >
                                        <Box
                                            mb={1}
                                            p={1.5}
                                            borderRadius={12}
                                            maxWidth="30%" // Increased from 15% to allow for more content
                                            minWidth="15%" // Set a minimum width
                                            alignSelf={isSent ? 'flex-end' : 'flex-start'}
                                            style={{
                                                background: isSent ? 'rebeccapurple' : '#fff',
                                                color: isSent ? '#fff' : '#000',
                                                boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
                                                position: 'relative',
                                                marginLeft: isSent ? 'auto' : '0',
                                                marginRight: isSent ? '0' : 'auto',
                                                textAlign: 'center',
                                                display: 'flex',
                                                flexDirection: 'column',
                                            }}
                                        >
                                            <Typography variant="caption"
                                                        style={{fontWeight: 'bold', fontSize: '0.7rem'}}>
                                                {isSent ? 'Sent' : 'Received'}
                                            </Typography>
                                            <Typography variant="body2" style={{
                                                fontWeight: 'bold',
                                                fontSize: '0.9rem',
                                                wordBreak: 'break-word'
                                            }}>
                                                {formatCurrency(transaction.amount, {
                                                    style: 'currency',
                                                    currency: transaction.currency,
                                                    currencyDisplay: 'symbol',
                                                })}
                                            </Typography>
                                            <Typography
                                                variant="caption"
                                                style={{
                                                    fontSize: '0.75rem',
                                                    padding: '4px 0',
                                                    wordBreak: 'break-word',
                                                }}
                                            >
                                                {transaction.description}
                                            </Typography>
                                            <Typography
                                                variant="caption"
                                                style={{
                                                    opacity: 0.7,
                                                    fontSize: '0.7rem',
                                                    alignSelf: 'flex-end',
                                                    marginTop: '4px',
                                                }}
                                            >
                                                {formattedDate}
                                            </Typography>
                                            <div ref={chatEndRef}/>
                                        </Box>
                                    </motion.div>
                                );
                            })}
                        </Box>
                    </DialogContent>
                    <DialogActions style={{background: 'white', justifyContent: 'center', padding: '16px'}}>
                        <PurpleButton onClick={() => optimizedHandleClick(() => setSelectedUser(null))}
                                      variant="contained"
                                      disabled={isLoading}>
                            Close
                        </PurpleButton>
                        <PurpleButton onClick={() => {
                            setTransactionDetails(prevDetails => ({
                                ...prevDetails,
                                userNameTag: selectedUser.nameTag
                            }));
                            setSendFundsDialogState({open: true, fromUserHistory: true});
                        }} variant="contained" disabled={isLoading}>
                            Send Funds
                        </PurpleButton>
                        <PurpleButton onClick={() => optimizedHandleClick(() => {
                            setRequestFundsDetails(prevDetails => ({
                                ...prevDetails,
                                userNameTag: selectedUser.nameTag
                            }));
                            setOpenRequestFunds(true);
                        })} variant="contained" disabled={isLoading}>
                            Request Funds
                        </PurpleButton>
                        {selectedUser && (
                            <>
                                {isFriend ? (
                                    <Button
                                        onClick={() => setOpenRemoveFriendDialog(true)}
                                        variant="contained"
                                        style={{
                                            background: 'red',
                                            color: 'white',
                                            fontWeight: 'bold',
                                        }}
                                    >
                                        Remove Friend
                                    </Button>
                                ) : (
                                    <Button
                                        onClick={handleAddFriend}
                                        variant="contained"
                                        style={{
                                            background: 'green',
                                            color: 'white',
                                            fontWeight: 'bold',
                                        }}
                                    >
                                        Add Friend
                                    </Button>
                                )}
                            </>
                        )}
                    </DialogActions>
                </Dialog>
                <Dialog
                    open={openRemoveFriendDialog}
                    onClose={() => setOpenRemoveFriendDialog(false)}
                >
                    <DialogTitle>Remove Friend</DialogTitle>
                    <DialogContent>
                        <Typography>
                            Are you sure you want to remove {selectedUser?.nameTag} from your friend list?
                        </Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenRemoveFriendDialog(false)} color="secondary">
                            No
                        </Button>
                        <Button onClick={handleRemoveFriend} style={{background: 'red', color: 'white'}}>
                            Yes
                        </Button>
                    </DialogActions>
                </Dialog>

                <Dialog open={openRequestFunds} onClose={() => setOpenRequestFunds(false)}>
                    <DialogTitle>Request Funds from {selectedUser?.nameTag}</DialogTitle>
                    <DialogContent>
                        <CustomTextField
                            margin="dense"
                            name="userNameTag"
                            label="User"
                            type="text"
                            fullWidth
                            value={selectedUser?.nameTag || ''}
                            disabled
                        />
                        <CustomTextField
                            margin="dense"
                            name="amount"
                            label="Amount"
                            type="number"
                            fullWidth
                            value={requestFundsDetails.amount}
                            onChange={handleRequestFundsInputChange}
                        />
                        <CustomTextField
                            margin="dense"
                            name="description"
                            label="Description"
                            type="text"
                            fullWidth
                            value={requestFundsDetails.description}
                            onChange={handleRequestFundsInputChange}
                        />
                        <CustomTextField
                            margin="dense"
                            name="currency"
                            label="Currency"
                            select
                            fullWidth
                            value={requestFundsDetails.currency}
                            onChange={handleRequestFundsInputChange}
                        >
                            <MenuItem value="USD">USD</MenuItem>
                            <MenuItem value="BGN">BGN</MenuItem>
                            <MenuItem value="EUR">EUR</MenuItem>
                            <MenuItem value="GBP">GBP</MenuItem>
                            <MenuItem value="JPY">JPY</MenuItem>
                        </CustomTextField>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenRequestFunds(false)} color="secondary">
                            Cancel
                        </Button>
                        <PurpleButton onClick={handleRequestFunds} variant="contained">
                            Request
                        </PurpleButton>
                    </DialogActions>
                </Dialog>
                <Dialog open={openAddFunds} onClose={handleAddFundsClose}>
                    <DialogTitle>Add Funds</DialogTitle>
                    <DialogContent>
                        {errorMessage && (
                            <Typography
                                sx={{
                                    color: 'error.main',
                                    fontWeight: 'bold',
                                    bgcolor: 'error.light',
                                    p: 1,
                                    borderRadius: 1,
                                }}
                                gutterBottom
                            >
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

                <Dialog open={openBudget} onClose={() => setOpenBudget(false)} maxWidth="md" fullWidth>
                    <DialogTitle>Budget Management</DialogTitle>
                    <DialogContent>
                        {budgets.length === 0 ? (
                            <Typography variant="body1" gutterBottom>
                                No active budgets
                            </Typography>
                        ) : (
                            <>
                                <Typography variant="body1" gutterBottom>
                                    You have {budgets.length} active budget(s)
                                </Typography>
                                <Box textAlign="center" mt={2}>
                                    <PurpleButton onClick={handleViewBudget} variant="contained">
                                        View Budget
                                    </PurpleButton>
                                </Box>
                            </>
                        )}
                        {!showCreateBudget ? (
                            <Box textAlign="center" mt={2}>
                                <PurpleButton onClick={() => setShowCreateBudget(true)} variant="contained">
                                    Create New Budget
                                </PurpleButton>
                            </Box>
                        ) : (
                            <>
                                <Typography variant="h6" gutterBottom>
                                    Create New Budget
                                </Typography>
                                {/* Budget creation fields */}
                                <CustomTextField
                                    margin="dense"
                                    name="budget"
                                    label="Budget Amount"
                                    type="number"
                                    fullWidth
                                    value={budgetDetails.budget}
                                    onChange={handleBudgetInputChange}
                                />
                                <CustomTextField
                                    margin="dense"
                                    name="startDate"
                                    label="Start Date"
                                    type="date"
                                    fullWidth
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    value={budgetDetails.startDate}
                                    onChange={handleBudgetInputChange}
                                />
                                <CustomTextField
                                    margin="dense"
                                    name="endDate"
                                    label="End Date"
                                    type="date"
                                    fullWidth
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    value={budgetDetails.endDate}
                                    onChange={handleBudgetInputChange}
                                />
                            </>
                        )}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenBudget(false)} color="secondary">
                            Cancel
                        </Button>
                        {showCreateBudget && (
                            <PurpleButton onClick={handleCreateBudget} variant="contained">
                                Create Budget
                            </PurpleButton>
                        )}
                    </DialogActions>
                </Dialog>

                <Dialog open={viewBudget} onClose={() => setViewBudget(false)} maxWidth="md" fullWidth>
                    <DialogTitle style={{display: 'flex', alignItems: 'center'}}>
                        <AccountBalanceIcon style={{marginRight: '8px', color: 'rebeccapurple'}}/>
                        Active Budget
                    </DialogTitle>
                    <DialogContent>
                        {budgets.length > 0 && (
                            <>
                                <Typography variant="h6" gutterBottom
                                            style={{textAlign: 'center', marginTop: '16px'}}>
                                    {formatDateWithoutTime(budgets[0]?.startDate)} - {formatDateWithoutTime(budgets[0]?.endDate)}
                                </Typography>
                                <div style={{
                                    height: '400px',
                                    width: '100%',
                                    display: 'flex',
                                    justifyContent: 'center',
                                    alignItems: 'center'
                                }}>
                                    <Doughnut
                                        data={{
                                            labels: ['Money Spent', 'Money Left'],
                                            datasets: [{
                                                data: [totalSpent, budgets[0]?.budget - totalSpent],
                                                backgroundColor: ['#DAA520', '#663399'],  // Rebeccapurple for left, Goldenrod for spent
                                                hoverBackgroundColor: ['#DAA520', '#663399']  // Same colors for hover effect
                                            }]
                                        }}
                                        options={{
                                            responsive: true,
                                            maintainAspectRatio: false,
                                            plugins: {
                                                legend: {
                                                    position: 'bottom',
                                                },
                                                tooltip: {
                                                    callbacks: {
                                                        label: function (context) {
                                                            let label = context.label || '';
                                                            if (label) {
                                                                label += ': ';
                                                            }
                                                            if (context.parsed !== null) {
                                                                label += formatCurrency(context.parsed);
                                                            }
                                                            return label;
                                                        }
                                                    }
                                                },
                                                title: {
                                                    display: true,
                                                    text: `Budget Overview: ${formatCurrency(budgets[0]?.budget)}`,
                                                    font: {
                                                        size: 16
                                                    }
                                                },
                                                doughnutLabels: true
                                            },
                                        }}
                                        plugins={[doughnutLabelsPlugin]}
                                    />
                                </div>
                            </>
                        )}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setViewBudget(false)} color="secondary">
                            Close
                        </Button>
                        <Button onClick={handleDeleteBudget} color="error">
                            Delete
                        </Button>
                    </DialogActions>
                </Dialog>

                <Dialog open={openReport} onClose={() => setOpenReport(false)}>
                    <DialogTitle>Generate Transaction Report</DialogTitle>
                    <DialogContent>
                        <CustomTextField
                            margin="dense"
                            name="startDate"
                            label="Start Date"
                            type="date"
                            fullWidth
                            InputLabelProps={{
                                shrink: true,
                            }}
                            value={reportDates.startDate}
                            onChange={handleReportDatesChange}
                        />
                        <CustomTextField
                            margin="dense"
                            name="endDate"
                            label="End Date"
                            type="date"
                            fullWidth
                            InputLabelProps={{
                                shrink: true,
                            }}
                            value={reportDates.endDate}
                            onChange={handleReportDatesChange}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenReport(false)} color="secondary">
                            Cancel
                        </Button>
                        <PurpleButton onClick={handleDownloadReport} variant="contained">
                            Download PDF
                        </PurpleButton>
                    </DialogActions>
                </Dialog>

                <Dialog open={selectedTransaction !== null} onClose={() => setSelectedTransaction(null)}>
                    <DialogTitle>Transaction History</DialogTitle>
                    <DialogContent>
                        {transactionSnapshots.map((snapshot, index) => (
                            <Box key={index} mb={2}>
                                <Typography variant="h6">Transaction ID: {snapshot.globalId.cdoId}</Typography>
                                <Typography>Created Date: {snapshot.commitMetadata.commitDateInstant}</Typography>
                                <Typography>Author: {snapshot.commitMetadata.author}</Typography>
                                <Typography>Transaction Type: {snapshot.state.transactionType}</Typography>
                                <Typography>Amount: {snapshot.state.amount}</Typography>
                                <Typography>Currency: {snapshot.state.currency}</Typography>
                                <Typography>Status: {snapshot.state.transactionStatus}</Typography>
                            </Box>
                        ))}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setSelectedTransaction(null)} color="secondary">
                            Close
                        </Button>
                    </DialogActions>
                </Dialog>
                <Snackbar
                    open={createBudgetSuccessMessage}
                    autoHideDuration={3000}
                    onClose={() => setCreateBudgetSuccessMessage(false)}
                    anchorOrigin={{vertical: 'top', horizontal: 'center'}}
                >
                    <Alert onClose={() => setCreateBudgetSuccessMessage(false)} severity="success"
                           sx={{width: '100%'}}>
                        Successfully created budget!
                    </Alert>
                </Snackbar>
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
                <Snackbar
                    open={requestFundsSuccessMessage}
                    autoHideDuration={2000}
                    onClose={handleSnackbarClose}
                    anchorOrigin={{vertical: 'top', horizontal: 'center'}}
                >
                    <Alert onClose={handleSnackbarClose} severity="success" sx={{width: '100%'}}>
                        Successfully requested funds!
                    </Alert>
                </Snackbar>
            </Container>
        </ThemeProvider>
    );
};

export default MainPage;
