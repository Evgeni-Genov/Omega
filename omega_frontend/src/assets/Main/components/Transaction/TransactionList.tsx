import React from 'react';
import {useTransactions} from '../../hooks/useTransactions';
import {LoadingFallback} from '../Error/LoadingFallback';
import {EmptyState} from '../Error/EmptyState';
import {useToast} from '../../hooks/useToast';
import {Transaction} from "../../types/Transaction.ts";
import {formatCurrency} from "../../Main/Utils/Formatters.ts";

interface TransactionListProps {
    userId: string;
}

export const TransactionList: React.FC<TransactionListProps> = ({userId}) => {
    const toast = useToast();
    const {
        data,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage,
        isLoading,
        error
    } = useTransactions(userId);

    React.useEffect(() => {
        if (error) {
            toast.showError({message: 'Failed to load transactions'});
        }
    }, [error, toast]);

    if (isLoading) return <LoadingFallback/>;

    if (!data?.pages[0]?.length) {
        return (
            <EmptyState
                title="No transactions yet"
                message="When you make transactions, they will appear here."
            />
        );
    }

    const renderTransaction = (transaction: Transaction) => (
        <div key={transaction.id} className="flex justify-between items-center p-4 border-b">
            <div>
                <p className="font-medium">{transaction.description}</p>
                <p className="text-sm text-gray-500">
                    {transaction.isExpense ? 'Sent to' : 'Received from'}: {
                    transaction.isExpense ? transaction.recipientNameTag : transaction.senderNameTag
                }
                </p>
            </div>
            <div className={`font-medium ${transaction.isExpense ? 'text-red-600' : 'text-green-600'}`}>
                {transaction.isExpense ? '-' : '+'}{formatCurrency(transaction.amount)}
            </div>
        </div>
    );

    return (
        <div className="bg-white rounded-lg shadow overflow-hidden">
            <h2 className="p-4 text-lg font-medium border-b">Transaction History</h2>
            <div className="divide-y">
                {data.pages.map((page, i) => (
                    <React.Fragment key={i}>
                        {page.map(renderTransaction)}
                    </React.Fragment>
                ))}
            </div>
            {hasNextPage && (
                <button
                    onClick={() => fetchNextPage()}
                    disabled={isFetchingNextPage}
                    className="w-full p-4 text-purple-600 hover:bg-purple-50 disabled:opacity-50"
                >
                    {isFetchingNextPage ? 'Loading...' : 'Load More'}
                </button>
            )}
        </div>
    );
};