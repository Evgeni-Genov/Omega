import React from 'react';
import {useAccountMutations} from '../../hooks/useAccountMutations';
import {useToast} from '../../hooks/useToast';
import {Currency, TransactionRequest} from "../../types/Transaction.ts";

interface SendFundsFormProps {
    userId: string;
    onSuccess?: () => void;
    onError?: (error: Error) => void;
}

interface FormErrors {
    recipientNameTag?: string;
    amount?: string;
    description?: string;
}

export const SendFundsForm: React.FC<SendFundsFormProps> = ({
                                                                userId,
                                                                onSuccess,
                                                                onError
                                                            }) => {
    const {sendFunds} = useAccountMutations(userId);
    const toast = useToast();
    const [formData, setFormData] = React.useState<TransactionRequest>({
        recipientNameTag: '',
        amount: 0,
        description: '',
        currency: Currency.USD
    });
    const [errors, setErrors] = React.useState<FormErrors>({});

    const validateForm = (): boolean => {
        const newErrors: FormErrors = {};

        if (!formData.recipientNameTag.trim()) {
            newErrors.recipientNameTag = 'Recipient is required';
        }

        if (formData.amount <= 0) {
            newErrors.amount = 'Amount must be greater than 0';
        }

        if (!formData.description.trim()) {
            newErrors.description = 'Description is required';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        try {
            await sendFunds.mutateAsync(formData);
            toast.showSuccess({message: 'Funds sent successfully!'});
            onSuccess?.();
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Failed to send funds';
            toast.showError({message: errorMessage});
            onError?.(error instanceof Error ? error : new Error(errorMessage));
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <input
                    type="text"
                    name="recipientNameTag"
                    placeholder="Recipient Username"
                    value={formData.recipientNameTag}
                    onChange={e => setFormData(prev => ({...prev, recipientNameTag: e.target.value}))}
                    className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                     ${errors.recipientNameTag ? 'border-red-500' : 'border-gray-300'}`}
                />
                {errors.recipientNameTag && (
                    <p className="mt-1 text-sm text-red-600">{errors.recipientNameTag}</p>
                )}
            </div>

            <div>
                <input
                    type="number"
                    name="amount"
                    placeholder="Amount"
                    value={formData.amount || ''}
                    onChange={e => setFormData(prev => ({...prev, amount: Number(e.target.value)}))}
                    className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                     ${errors.amount ? 'border-red-500' : 'border-gray-300'}`}
                />
                {errors.amount && (
                    <p className="mt-1 text-sm text-red-600">{errors.amount}</p>
                )}
            </div>

            <div>
                <input
                    type="text"
                    name="description"
                    placeholder="Description"
                    value={formData.description}
                    onChange={e => setFormData(prev => ({...prev, description: e.target.value}))}
                    className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                     ${errors.description ? 'border-red-500' : 'border-gray-300'}`}
                />
                {errors.description && (
                    <p className="mt-1 text-sm text-red-600">{errors.description}</p>
                )}
            </div>

            <button
                type="submit"
                disabled={sendFunds.isLoading}
                className="w-full bg-purple-600 text-white p-2 rounded hover:bg-purple-700
                 disabled:opacity-50 disabled:cursor-not-allowed"
            >
                {sendFunds.isLoading ? 'Sending...' : 'Send Funds'}
            </button>
        </form>
    );
};