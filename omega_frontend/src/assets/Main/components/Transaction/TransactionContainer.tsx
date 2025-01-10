import React from 'react';
import {TransactionList} from './TransactionList';
import {SendFundsDialog} from './TransactionDialogs/SendFundsDialog.tsx';
import {AddFundsDialog} from './TransactionDialogs/AddFundsDialog.tsx';
import {useToast} from '../../hooks/useToast';
import {TransactionErrorFallback} from './TransactionErrorFallback';
import {TransactionProvider} from '../../contexts/TransactionContext';
import {SendIcon, PlusIcon} from 'lucide-react';
import {ErrorBoundary} from "../Error/ErrorBoundary.tsx";

interface TransactionContainerProps {
    userId: string;
}

export const TransactionContainer: React.FC<TransactionContainerProps> = ({userId}) => {
    const [isSendFundsOpen, setIsSendFundsOpen] = React.useState(false);
    const [isAddFundsOpen, setIsAddFundsOpen] = React.useState(false);
    const toast = useToast();

    const handleTransactionSuccess = (message: string) => {
        toast.showSuccess({message});
    };

    const handleTransactionError = (error: Error) => {
        toast.showError({message: error.message});
    };

    const handleCloseDialogs = () => {
        setIsSendFundsOpen(false);
        setIsAddFundsOpen(false);
    };

    return (
        <ErrorBoundary
            fallback={
                <TransactionErrorFallback
                    error={new Error('Failed to load transactions')}
                    resetErrorBoundary={() => window.location.reload()}
                />
            }
        >
            <TransactionProvider userId={userId}>
                <div className="space-y-6">
                    <div className="flex justify-between items-center">
                        <h1 className="text-2xl font-semibold text-gray-900">
                            Transactions
                        </h1>
                        <div className="flex space-x-4">
                            <button
                                onClick={() => setIsSendFundsOpen(true)}
                                className="inline-flex items-center px-4 py-2 text-sm font-medium
                         rounded-md text-white bg-purple-600 hover:bg-purple-700
                         focus:outline-none focus:ring-2 focus:ring-offset-2
                         focus:ring-purple-500 transition-colors"
                            >
                                <SendIcon className="h-5 w-5 mr-2"/>
                                Send Funds
                            </button>
                            <button
                                onClick={() => setIsAddFundsOpen(true)}
                                className="inline-flex items-center px-4 py-2 text-sm font-medium
                         rounded-md text-white bg-purple-600 hover:bg-purple-700
                         focus:outline-none focus:ring-2 focus:ring-offset-2
                         focus:ring-purple-500 transition-colors"
                            >
                                <PlusIcon className="h-5 w-5 mr-2"/>
                                Add Funds
                            </button>
                        </div>
                    </div>

                    <div className="bg-white rounded-lg shadow-sm overflow-hidden">
                        <TransactionList
                            userId={userId}
                        />
                    </div>

                    <SendFundsDialog
                        isOpen={isSendFundsOpen}
                        onClose={handleCloseDialogs}
                        userId={userId}
                        onSuccess={() => handleTransactionSuccess('Funds sent successfully')}
                        onError={handleTransactionError}
                    />

                    <AddFundsDialog
                        isOpen={isAddFundsOpen}
                        onClose={handleCloseDialogs}
                        userId={userId}
                        onSuccess={() => handleTransactionSuccess('Funds added successfully')}
                        onError={handleTransactionError}
                    />
                </div>
            </TransactionProvider>
        </ErrorBoundary>
    );
};