import {useQuery, useMutation, UseQueryResult, UseMutationResult} from 'react-query';
import {AccountBalance} from "../types/Account.ts";
import {Transaction} from "../types/Transaction.ts";
import {accountService} from "../services/AccountService.ts";

//TODO
interface UseAccountResult {
    balance: UseQueryResult<AccountBalance>;
    transactions: UseQueryResult<Transaction[]>;
    addFunds: UseMutationResult;
    sendFunds: UseMutationResult;
}

export const useAccount = (userId: string): UseAccountResult => {
    const balance = useQuery(['balance', userId], () =>
        accountService.getBalance(userId)
    );

    const transactions = useQuery(['transactions', userId], () =>
        accountService.getTransactions(userId)
    );

    const addFunds = useMutation(accountService.addFunds);
    const sendFunds = useMutation(accountService.sendFunds);

    return {
        balance,
        transactions,
        addFunds,
        sendFunds
    };
};