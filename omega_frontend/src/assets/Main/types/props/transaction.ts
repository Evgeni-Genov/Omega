export interface SendFundsDialogProps {
    // Controls dialog visibility
    isOpen: boolean;

    // Function to close the dialog
    onClose: () => void;

    // User ID for the current user making the transaction
    userId: string;

    // Optional callback when transaction is successful
    onSuccess?: () => void;

    // Optional callback when transaction fails
    onError?: (error: Error) => void;
}

export interface AddFundsDialogProps {
    isOpen: boolean;
    onClose: () => void;
    userId: string;
    onSuccess?: () => void;
    onError?: (error: Error) => void;
}

// Base dialog props that both SendFunds and AddFunds extend
export interface BaseTransactionDialogProps {
    isOpen: boolean;
    onClose: () => void;
    title: string;
    children: React.ReactNode;
}