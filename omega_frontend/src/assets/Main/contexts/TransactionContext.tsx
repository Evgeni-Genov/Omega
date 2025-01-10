import React from 'react';
import {useQuery, useQueryClient} from 'react-query';
import {useToast} from '../hooks/useToast';
import api from "../Config/api.ts";
import {Currency, Transaction} from "../types/Transaction.ts";

interface TransactionContextType {
    transactions: Transaction[];
    pendingTransactions: Transaction[];
    isLoading: boolean;
    error: Error | null;
    handleSendFunds: (recipientTag: string, amount: number, description: string) => Promise<void>;
    handleAddFunds: (amount: number) => Promise<void>;
    refetchTransactions: () => Promise<void>;
}

export const TransactionContext = React.createContext<TransactionContextType | null>(null);

interface TransactionProviderProps {
    userId: string;
    children: React.ReactNode;
}

export const TransactionProvider: React.FC<TransactionProviderProps> = ({
                                                                            userId,
                                                                            children
                                                                        }) => {
    const queryClient = useQueryClient();
    const toast = useToast();

    const {
        data: transactions = [],
        isLoading,
        error,
        refetch
    } = useQuery(
        ['transactions', userId],
        async () => {
            const {data} = await api.get(`/api/transaction/${userId}`);
            return data;
        },
        {
            staleTime: 30000, // Consider data fresh for 30 seconds
            cacheTime: 5 * 60 * 1000, // Cache for 5 minutes
            retry: 2,
            onError: (error: Error) => {
                toast.showError({message: 'Failed to load transactions'});
            }
        }
    );

    const pendingTransactions = React.useMemo(() =>
            transactions.filter(t =>
                t.transactionStatus === 'PENDING' && t.recipientId === Number(userId)
            ),
        [transactions, userId]
    );

    const handleSendFunds = async (recipientTag: string, amount: number, description: string) => {
        try {
            await api.post('/api/transaction/send-funds', {
                recipientNameTag: recipientTag,
                amount,
                description,
                currency: Currency.USD
            });

            await queryClient.invalidateQueries(['transactions', userId]);
            await queryClient.invalidateQueries(['accountBalance', userId]);

        } catch (error) {
            throw new Error(error instanceof Error ? error.message : 'Failed to send funds');
        }
    };

    const handleAddFunds = async (amount: number) => {
        try {
            await api.post('/api/transaction/add-funds', {
                userId,
                amount,
                currency: Currency.USD
            });

            await queryClient.invalidateQueries(['transactions', userId]);
            await queryClient.invalidateQueries(['accountBalance', userId]);

        } catch (error) {
            throw new Error(error instanceof Error ? error.message : 'Failed to add funds');
        }
    };

    const refetchTransactions = async () => {
        try {
            await refetch();
        } catch (error) {
            toast.showError({message: 'Failed to refresh transactions'});
        }
    };

    return (
        <TransactionContext.Provider
            value={{
                transactions,
                pendingTransactions,
                isLoading,
                error,
                handleSendFunds,
                handleAddFunds,
                refetchTransactions
            }}
        >
            {children}
        </TransactionContext.Provider>
    );
};