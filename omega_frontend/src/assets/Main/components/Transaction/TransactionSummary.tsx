import React from 'react';
import {useAccountBalance} from '../../hooks/useAccountBalance';
import {ArrowUpCircle, ArrowDownCircle, Wallet} from 'lucide-react';
import {useTransaction} from "../../hooks/useTransactions.ts";
import {formatCurrency} from "../../Main/Utils/Formatters.ts";

interface TransactionSummaryProps {
    userId: string;
}

export const TransactionSummary: React.FC<TransactionSummaryProps> = ({userId}) => {
    const {data: balance} = useAccountBalance(userId);
    const {transactions} = useTransaction();

    const calculateMonthlyStats = () => {
        const now = new Date();
        const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);

        return transactions.reduce((stats, transaction) => {
            const transactionDate = new Date(transaction.createdDate);
            if (transactionDate >= monthStart) {
                if (transaction.isExpense) {
                    stats.expenses += transaction.amount;
                } else {
                    stats.income += transaction.amount;
                }
            }
            return stats;
        }, {income: 0, expenses: 0});
    };

    const monthlyStats = React.useMemo(calculateMonthlyStats, [transactions]);

    return (
        <div className="grid grid-cols-3 gap-4">
            <div className="bg-white p-4 rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                    <Wallet className="h-8 w-8 text-purple-600"/>
                    <div>
                        <p className="text-sm text-gray-500">Current Balance</p>
                        <p className="text-xl font-semibold">{formatCurrency(balance?.balance || 0)}</p>
                    </div>
                </div>
            </div>

            <div className="bg-white p-4 rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                    <ArrowUpCircle className="h-8 w-8 text-green-600"/>
                    <div>
                        <p className="text-sm text-gray-500">Monthly Income</p>
                        <p className="text-xl font-semibold text-green-600">
                            {formatCurrency(monthlyStats.income)}
                        </p>
                    </div>
                </div>
            </div>

            <div className="bg-white p-4 rounded-lg shadow-sm">
                <div className="flex items-center space-x-3">
                    <ArrowDownCircle className="h-8 w-8 text-red-600"/>
                    <div>
                        <p className="text-sm text-gray-500">Monthly Expenses</p>
                        <p className="text-xl font-semibold text-red-600">
                            {formatCurrency(monthlyStats.expenses)}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};