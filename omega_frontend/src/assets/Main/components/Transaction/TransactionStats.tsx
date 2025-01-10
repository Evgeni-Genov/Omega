import React from 'react';

import {LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer} from 'recharts';
import {useTransaction} from "../../hooks/useTransactions.ts";
import {Transaction} from "../../types/Transaction.ts";
import {formatCurrency} from "../../Main/Utils/Formatters.ts";


interface TransactionStatsProps {
    userId: string;
}

export const TransactionStats: React.FC<TransactionStatsProps> = ({userId}) => {
    const {transactions} = useTransaction();

    const monthlyData = React.useMemo(() => {
        const monthlyStats = new Map<string, {
            month: string,
            income: number,
            expenses: number,
            balance: number
        }>();

        transactions.forEach(transaction => {
            const date = new Date(transaction.createdDate);
            const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

            if (!monthlyStats.has(monthKey)) {
                monthlyStats.set(monthKey, {
                    month: monthKey,
                    income: 0,
                    expenses: 0,
                    balance: 0
                });
            }

            const stats = monthlyStats.get(monthKey)!;
            if (transaction.isExpense) {
                stats.expenses += transaction.amount;
            } else {
                stats.income += transaction.amount;
            }
            stats.balance = stats.income - stats.expenses;
        });

        return Array.from(monthlyStats.values()).sort((a, b) => a.month.localeCompare(b.month));
    }, [transactions]);

    const getTransactionCategories = (transactions: Transaction[]) => {
        return transactions.reduce((categories, transaction) => {
            const category = transaction.description.split(' ')[0].toLowerCase();
            if (!categories[category]) {
                categories[category] = 0;
            }
            categories[category] += transaction.amount;
            return categories;
        }, {} as Record<string, number>);
    };

    const categories = React.useMemo(() =>
        getTransactionCategories(transactions), [transactions]);

    return (
        <div className="space-y-6">
            <div className="bg-white p-6 rounded-lg shadow-sm">
                <h3 className="text-lg font-medium mb-4">Monthly Transaction Overview</h3>
                <div className="h-80">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart data={monthlyData}>
                            <CartesianGrid strokeDasharray="3 3"/>
                            <XAxis dataKey="month"/>
                            <YAxis/>
                            <Tooltip
                                formatter={(value: number) => formatCurrency(value)}
                                labelFormatter={(label) => `Month: ${label}`}
                            />
                            <Line
                                type="monotone"
                                dataKey="income"
                                stroke="#10B981"
                                name="Income"
                            />
                            <Line
                                type="monotone"
                                dataKey="expenses"
                                stroke="#EF4444"
                                name="Expenses"
                            />
                            <Line
                                type="monotone"
                                dataKey="balance"
                                stroke="#6366F1"
                                name="Balance"
                            />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-sm">
                <h3 className="text-lg font-medium mb-4">Top Categories</h3>
                <div className="grid grid-cols-2 gap-4">
                    {Object.entries(categories)
                        .sort(([, a], [, b]) => b - a)
                        .slice(0, 6)
                        .map(([category, amount]) => (
                            <div
                                key={category}
                                className="flex justify-between items-center p-3 bg-gray-50 rounded-lg"
                            >
                                <span className="capitalize">{category}</span>
                                <span className="font-medium">{formatCurrency(amount)}</span>
                            </div>
                        ))
                    }
                </div>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-sm">
                <h3 className="text-lg font-medium mb-4">Transaction Metrics</h3>
                <div className="grid grid-cols-3 gap-4">
                    <div className="p-4 bg-gray-50 rounded-lg">
                        <p className="text-sm text-gray-500">Average Transaction</p>
                        <p className="text-xl font-medium">
                            {formatCurrency(
                                transactions.reduce((sum, t) => sum + t.amount, 0) / transactions.length || 0
                            )}
                        </p>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                        <p className="text-sm text-gray-500">Largest Transaction</p>
                        <p className="text-xl font-medium">
                            {formatCurrency(
                                Math.max(...transactions.map(t => t.amount), 0)
                            )}
                        </p>
                    </div>
                    <div className="p-4 bg-gray-50 rounded-lg">
                        <p className="text-sm text-gray-500">Total Transactions</p>
                        <p className="text-xl font-medium">{transactions.length}</p>
                    </div>
                </div>
            </div>
        </div>
    );
};