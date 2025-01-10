import React from 'react';
import {TransactionDialog} from './TransactionDialog.tsx';
import {SendFundsForm} from '../SendFundsForm.tsx';
import {ErrorBoundary} from '../../Error/ErrorBoundary.tsx';
import {TransactionErrorFallback} from '../../Error/TransactionErrorFallback.tsx';
import {SendFundsDialogProps} from "../../../types/props/transaction.ts";

export const SendFundsDialog: React.FC<SendFundsDialogProps> = ({
                                                                    isOpen,
                                                                    onClose,
                                                                    userId,
                                                                    onSuccess,
                                                                    onError
                                                                }) => (
    <TransactionDialog
        isOpen={isOpen}
        onClose={onClose}
        title="Send Funds"
    >
        <ErrorBoundary
            fallback={
                <TransactionErrorFallback
                    error={new Error('Failed to process transaction')}
                    resetErrorBoundary={() => window.location.reload()}
                />
            }
        >
            <SendFundsForm
                userId={userId}
                onSuccess={() => {
                    onSuccess?.();
                    onClose();
                }}
                onError={onError}
            />
        </ErrorBoundary>
    </TransactionDialog>
);