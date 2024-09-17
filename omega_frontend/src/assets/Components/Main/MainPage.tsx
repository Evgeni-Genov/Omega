import React, {useEffect, useState} from 'react';
import {Box, Container} from '@mui/material';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import axiosInstance from "../Config/AxiosConfiguration.ts";
import {useNavigate} from "react-router-dom";
import {AccountBalance} from "@mui/icons-material";

const MainPage = () => {
    const [accountBalance, setAccountBalance] = useState<number>(0);
    const [loading, setLoading] = useState(true);
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
                const accountResponse = await axiosInstance.get(`/api/account-balance/user/${userId}`);
                const accountData = accountResponse.data.length > 0 ? accountResponse.data[0] : { balance: 0 };
                setAccountBalance(accountData.balance);
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

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="md">
                <AccountBalance balance={accountBalance} />
                {/*<TransactionList transactions={transactions} />*/}
                {/*<SendFundsDialog />*/}
                {/*<AddFundsDialog />*/}
                {/*<BudgetDialog />*/}
                {/*<ReportDialog />*/}
                {/*<TransactionDetailsDialog />*/}
            </Container>
        </ThemeProvider>
    );
};

export default MainPage;