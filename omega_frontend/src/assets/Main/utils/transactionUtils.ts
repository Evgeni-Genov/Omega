import {Currency, Transaction, TransactionStatus} from "../types/Transaction.ts";


export const formatCurrency = (amount: number, currency: Currency = Currency.USD): string => {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(amount);
};

export const calculateTransactionTotal = (transactions: Transaction[]): number => {
    return transactions.reduce((total, transaction) => {
        if (transaction.transactionStatus === TransactionStatus.SUCCESSFUL) {
            return total + (transaction.isExpense ? -transaction.amount : transaction.amount);
        }
        return total;
    }, 0);
};

export const filterTransactions = (
    transactions: Transaction[],
    filters: {
        startDate?: Date;
        endDate?: Date;
        status?: TransactionStatus;
        type?: 'incoming' | 'outgoing' | 'all';
    }
) => {
    return transactions.filter(transaction => {
        const date = new Date(transaction.createdDate);

        if (filters.startDate && date < filters.startDate) return false;
        if (filters.endDate && date > filters.endDate) return false;
        if (filters.status && transaction.transactionStatus !== filters.status) return false;
        if (filters.type === 'incoming' && transaction.isExpense) return false;
        if (filters.type === 'outgoing' && !transaction.isExpense) return false;

        return true;
    });
};

export const groupTransactionsByDate = (transactions: Transaction[]) => {
    const grouped = transactions.reduce((groups, transaction) => {
        const date = new Date(transaction.createdDate).toLocaleDateString();
        if (!groups[date]) {
            groups[date] = [];
        }
        groups[date].push(transaction);
        return groups;
    }, {} as Record<string, Transaction[]>);

    return Object.entries(grouped).sort(([dateA], [dateB]) =>
        new Date(dateB).getTime() - new Date(dateA).getTime()
    );
};

export const validateTransaction = (
    amount: number,
    balance: number,
    type: 'send' | 'request'
): string | null => {
    if (amount <= 0) {
        return 'Amount must be greater than zero';
    }

    if (type === 'send' && amount > balance) {
        return 'Insufficient funds';
    }

    return null;
};

export const generateTransactionReference = (): string => {
    return `TXN-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};