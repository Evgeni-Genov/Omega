import React from 'react';
import {Printer, X} from 'lucide-react';
import {Transaction} from "../../types/Transaction.ts";
import {TransactionDialog} from "./TransactionDialogs/TransactionDialog.tsx";
import {formatDate} from "date-fns";
import {formatCurrency} from "../../Main/Utils/Formatters.ts";


interface TransactionPrintPreviewProps {
    isOpen: boolean;
    onClose: () => void;
    transactions: Transaction[];
    dateRange?: {
        startDate: string;
        endDate: string;
    };
}

export const TransactionPrintPreview: React.FC<TransactionPrintPreviewProps> = ({
                                                                                    isOpen,
                                                                                    onClose,
                                                                                    transactions,
                                                                                    dateRange
                                                                                }) => {
    const handlePrint = () => {
        window.print();
    };

    const calculateTotals = () => {
        return transactions.reduce(
            (acc, transaction) => {
                if (transaction.isExpense) {
                    acc.expenses += transaction.amount;
                } else {
                    acc.income += transaction.amount;
                }
                return acc;
            },
            {income: 0, expenses: 0}
        );
    };

    const totals = React.useMemo(calculateTotals, [transactions]);

    return (
        <TransactionDialog
            isOpen={isOpen}
            onClose={onClose}
            title="Transaction Preview"
        >
            <div className="space-y-6 print:p-8">
                {/* Header */}
                <div className="flex justify-between items-center print:mb-8">
                    <div>
                        <h2 className="text-2xl font-bold text-gray-900">Transaction Report</h2>
                        {dateRange && (
                            <p className="text-sm text-gray-500">
                                {formatDate(dateRange.startDate)} - {formatDate(dateRange.endDate)}
                            </p>
                        )}
                    </div>
                    <button
                        onClick={handlePrint}
                        className="inline-flex items-center px-4 py-2 bg-purple-600
                     text-white rounded-md hover:bg-purple-700 print:hidden"
                    >
                        <Printer className="h-4 w-4 mr-2"/>
                        Print
                    </button>
                </div>

                {/* Summary */}
                <div className="grid grid-cols-3 gap-4 print:mb-8">
                    <div className="bg-gray-50 p-4 rounded-lg">
                        <p className="text-sm text-gray-500">Total Income</p>
                        <p className="text-lg font-semibold text-green-600">
                            {formatCurrency(totals.income)}
                        </p>
                    </div>
                    <div className="bg-gray-50 p-4 rounded-lg">
                        <p className="text-sm text-gray-500">Total Expenses</p>
                        <p className="text-lg font-semibold text-red-600">
                            {formatCurrency(totals.expenses)}
                        </p>
                    </div>
                    <div className="bg-gray-50 p-4 rounded-lg">
                        <p className="text-sm text-gray-500">Net Balance</p>
                        <p className="text-lg font-semibold">
                            {formatCurrency(totals.income - totals.expenses)}
                        </p>
                    </div>
                </div>

                {/* Transactions Table */}
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Date
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Description
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Type
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Amount
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Status
                            </th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {transactions.map((transaction) => (
                            <tr key={transaction.id}>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {formatDate(transaction.createdDate)}
                                </td>
                                <td className="px-6 py-4 text-sm text-gray-900">
                                    {transaction.description}
                                </td>
                                <td className="px-6 py-4 text-sm text-gray-500">
                                    {transaction.transactionType}
                                </td>
                                <td className={`px-6 py-4 text-sm ${
                                    transaction.isExpense ? 'text-red-600' : 'text-green-600'
                                }`}>
                                    {transaction.isExpense ? '-' : '+'}{formatCurrency(transaction.amount)}
                                </td>
                                <td className="px-6 py-4 text-sm">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                      ${transaction.transactionStatus === 'SUCCESSFUL' ? 'bg-green-100 text-green-800' :
                        transaction.transactionStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-red-100 text-red-800'}`}>
                      {transaction.transactionStatus}
                    </span>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

                {/* Print Styles */}
                <style>{`
          @media print {
            @page { size: auto; margin: 20mm; }
            body { print-color-adjust: exact; -webkit-print-color-adjust: exact; }
            .print\\:hidden { display: none !important; }
            .print\\:p-8 { padding: 2rem !important; }
            .print\\:mb-8 { margin-bottom: 2rem !important; }
          }
        `}</style>
            </div>
        </TransactionDialog>
    );
};