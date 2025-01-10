import React from 'react';
import {TransactionDialog} from './TransactionDialog.tsx';
import {AddFundsForm} from '../AddFundsForm.tsx';

interface AddFundsDialogProps {
    isOpen: boolean;
    onClose: () => void;
    userId: string;
    onSuccess?: () => void;
    onError?: (error: Error) => void;
}

export const AddFundsDialog: React.FC<AddFundsDialogProps> = ({
                                                                  isOpen,
                                                                  onClose,
                                                                  userId,
                                                                  onSuccess,
                                                                  onError
                                                              }) => (
    <TransactionDialog
        isOpen={isOpen}
        onClose={onClose}
        title="Add Funds"
    >
        <AddFundsForm
            userId={userId}
            onSuccess={() => {
                onSuccess?.();
                onClose();
            }}
            onError={onError}
        />
    </TransactionDialog>
);