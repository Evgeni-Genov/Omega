import React from 'react';
import {AlertCircle, RefreshCw} from 'lucide-react';

interface TransactionErrorProps {
    error: Error;
    resetErrorBoundary: () => void;
}

export const TransactionErrorFallback: React.FC<TransactionErrorProps> = ({
                                                                              error,
                                                                              resetErrorBoundary
                                                                          }) => (
    <div className="p-6 bg-red-50 rounded-lg">
        <div className="flex items-center mb-4">
            <AlertCircle className="h-6 w-6 text-red-600 mr-2"/>
            <h3 className="text-lg font-medium text-red-800">Transaction Error</h3>
        </div>
        <p className="text-red-600 mb-4">{error.message}</p>
        <button
            onClick={resetErrorBoundary}
            className="inline-flex items-center px-4 py-2 border border-transparent
                 text-sm font-medium rounded-md text-white bg-red-600
                 hover:bg-red-700 focus:outline-none focus:ring-2
                 focus:ring-offset-2 focus:ring-red-500"
        >
            <RefreshCw className="h-4 w-4 mr-2"/>
            Try Again
        </button>
    </div>
);