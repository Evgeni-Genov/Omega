import React from 'react';

import {Download, FileText, Loader} from 'lucide-react';
import {formatDate} from "date-fns";
import api from "../../Config/api.ts";

interface TransactionExportProps {
    userId: string;
}

export const TransactionExport: React.FC<TransactionExportProps> = ({userId}) => {
    const [isLoading, setIsLoading] = React.useState(false);
    const [dateRange, setDateRange] = React.useState({
        startDate: '',
        endDate: ''
    });

    const handleExport = async (format: 'pdf' | 'csv') => {
        try {
            setIsLoading(true);
            const response = await api.get(
                `/api/transactions-report`, {
                    params: {
                        startDate: dateRange.startDate,
                        endDate: dateRange.endDate
                    },
                    responseType: 'blob'
                });

            // Create download link
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `transactions-${format}-${formatDate(new Date())}.${format}`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Export failed:', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="bg-white p-6 rounded-lg shadow-sm">
            <div className="space-y-4">
                <h3 className="text-lg font-medium">Export Transactions</h3>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Start Date
                        </label>
                        <input
                            type="date"
                            value={dateRange.startDate}
                            onChange={(e) => setDateRange(prev => ({
                                ...prev,
                                startDate: e.target.value
                            }))}
                            className="w-full rounded-md border-gray-300 shadow-sm
                       focus:border-purple-500 focus:ring-purple-500"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            End Date
                        </label>
                        <input
                            type="date"
                            value={dateRange.endDate}
                            onChange={(e) => setDateRange(prev => ({
                                ...prev,
                                endDate: e.target.value
                            }))}
                            className="w-full rounded-md border-gray-300 shadow-sm
                       focus:border-purple-500 focus:ring-purple-500"
                        />
                    </div>
                </div>

                <div className="flex space-x-4">
                    <button
                        onClick={() => handleExport('pdf')}
                        disabled={isLoading || !dateRange.startDate || !dateRange.endDate}
                        className="inline-flex items-center px-4 py-2 border border-transparent
                     text-sm font-medium rounded-md text-white bg-purple-600
                     hover:bg-purple-700 focus:outline-none focus:ring-2
                     focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50
                     disabled:cursor-not-allowed"
                    >
                        {isLoading ? (
                            <Loader className="h-4 w-4 mr-2 animate-spin"/>
                        ) : (
                            <FileText className="h-4 w-4 mr-2"/>
                        )}
                        Export as PDF
                    </button>

                    <button
                        onClick={() => handleExport('csv')}
                        disabled={isLoading || !dateRange.startDate || !dateRange.endDate}
                        className="inline-flex items-center px-4 py-2 border border-purple-600
                     text-sm font-medium rounded-md text-purple-600 bg-white
                     hover:bg-purple-50 focus:outline-none focus:ring-2
                     focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50
                     disabled:cursor-not-allowed"
                    >
                        {isLoading ? (
                            <Loader className="h-4 w-4 mr-2 animate-spin"/>
                        ) : (
                            <Download className="h-4 w-4 mr-2"/>
                        )}
                        Export as CSV
                    </button>
                </div>

                <p className="text-sm text-gray-500">
                    Export your transactions for the selected date range in PDF or CSV format.
                    The exported file will include transaction details, amounts, and dates.
                </p>
            </div>
        </div>
    );
};