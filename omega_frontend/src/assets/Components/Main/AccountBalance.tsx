import React from 'react';
import {Box, Card, CardContent, Typography} from '@mui/material';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import {styled} from '@mui/material/styles';
import {formatCurrency} from "./Utils/Formatters.ts";

const StyledCard = styled(Card)(({theme}) => ({
    backgroundColor: '#f5f5f5',
    borderRadius: theme.spacing(2),
    boxShadow: theme.shadows[3],
    padding: theme.spacing(2),
}));

interface AccountBalanceProps {
    balance: number;
}

const AccountBalance: React.FC<AccountBalanceProps> = ({balance}) => {
    return (
        <StyledCard>
            <CardContent>
                <Box display="flex" alignItems="center">
                    <AccountBalanceIcon sx={{fontSize: 40, marginRight: 2, color: '#663399'}}/>
                    <Box>
                        <Typography variant="h5" component="div">
                            Account Balance
                        </Typography>
                        <Typography variant="h4" color="text.secondary">
                            {formatCurrency(balance)}
                        </Typography>
                    </Box>
                </Box>
            </CardContent>
        </StyledCard>
    );
};

export default AccountBalance;