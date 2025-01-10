import React from 'react';
import {useAccountMutations} from '../../hooks/useAccountMutations';
import {useToast} from '../../hooks/useToast';
import {CreditCardDetails} from "../../types/Payment.ts";

interface AddFundsFormProps {
    userId: string;
    onSuccess?: () => void;
    onError?: (error: Error) => void;
}

interface FormErrors {
    cardNumber?: string;
    cardOwner?: string;
    expiryDate?: string;
    securityCode?: string;
    amount?: string;
}

export const AddFundsForm: React.FC<AddFundsFormProps> = ({
                                                              userId,
                                                              onSuccess,
                                                              onError
                                                          }) => {
    const {addFunds} = useAccountMutations(userId);
    const toast = useToast();
    const [formData, setFormData] = React.useState<CreditCardDetails>({
        userId: Number(userId),
        cardNumber: '',
        cardOwner: '',
        expiryDate: '',
        securityCode: '',
        amount: ''
    });
    const [errors, setErrors] = React.useState<FormErrors>({});

    const formatCardNumber = (value: string): string => {
        const digits = value.replace(/\s/g, '');
        const groups = digits.match(/.{1,4}/g) || [];
        return groups.join(' ');
    };

    const formatExpiryDate = (value: string): string => {
        const digits = value.replace(/\D/g, '');
        if (digits.length >= 2) {
            return `${digits.slice(0, 2)}/${digits.slice(2, 4)}`;
        }
        return digits;
    };

    const validateForm = (): boolean => {
        const newErrors: FormErrors = {};

        if (!formData.cardNumber.replace(/\s/g, '').match(/^\d{16}$/)) {
            newErrors.cardNumber = 'Invalid card number';
        }

        if (!formData.cardOwner.trim()) {
            newErrors.cardOwner = 'Card owner name is required';
        }

        const [month, year] = formData.expiryDate.split('/');
        if (!month || !year || !/^\d{2}\/\d{2}$/.test(formData.expiryDate)) {
            newErrors.expiryDate = 'Invalid expiry date';
        }

        if (!formData.securityCode.match(/^\d{3}$/)) {
            newErrors.securityCode = 'Invalid security code';
        }

        if (!formData.amount || Number(formData.amount) <= 0) {
            newErrors.amount = 'Amount must be greater than 0';
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
            await addFunds.mutateAsync(formData);
            toast.showSuccess({message: 'Funds added successfully!'});
            onSuccess?.();
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Failed to add funds';
            toast.showError({message: errorMessage});
            onError?.(error instanceof Error ? error : new Error(errorMessage));
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <input
                    type="text"
                    name="cardNumber"
                    placeholder="Card Number"
                    value={formData.cardNumber}
                    onChange={e => setFormData(prev => ({
                        ...prev,
                        cardNumber: formatCardNumber(e.target.value)
                    }))}
                    maxLength={19}
                    className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                     ${errors.cardNumber ? 'border-red-500' : 'border-gray-300'}`}
                />
                {errors.cardNumber && (
                    <p className="mt-1 text-sm text-red-600">{errors.cardNumber}</p>
                )}
            </div>

            <div>
                <input
                    type="text"
                    name="cardOwner"
                    placeholder="Card Owner"
                    value={formData.cardOwner}
                    onChange={e => setFormData(prev => ({...prev, cardOwner: e.target.value}))}
                    className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                     ${errors.cardOwner ? 'border-red-500' : 'border-gray-300'}`}
                />
                {errors.cardOwner && (
                    <p className="mt-1 text-sm text-red-600">{errors.cardOwner}</p>
                )}
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div>
                    <input
                        type="text"
                        name="expiryDate"
                        placeholder="MM/YY"
                        value={formData.expiryDate}
                        onChange={e => setFormData(prev => ({
                            ...prev,
                            expiryDate: formatExpiryDate(e.target.value)
                        }))}
                        maxLength={5}
                        className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                       ${errors.expiryDate ? 'border-red-500' : 'border-gray-300'}`}
                    />
                    {errors.expiryDate && (
                        <p className="mt-1 text-sm text-red-600">{errors.expiryDate}</p>
                    )}
                </div>

                <div>
                    <input
                        type="password"
                        name="securityCode"
                        placeholder="CVV"
                        value={formData.securityCode}
                        onChange={e => setFormData(prev => ({...prev, securityCode: e.target.value}))}
                        maxLength={3}
                        className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                       ${errors.securityCode ? 'border-red-500' : 'border-gray-300'}`}
                    />
                    {errors.securityCode && (
                        <p className="mt-1 text-sm text-red-600">{errors.securityCode}</p>
                    )}
                </div>
            </div>

            <div>
                <input
                    type="number"
                    name="amount"
                    placeholder="Amount"
                    value={formData.amount}
                    onChange={e => setFormData(prev => ({...prev, amount: e.target.value}))}
                    className={`w-full p-2 border rounded focus:ring-2 focus:ring-purple-500 focus:border-transparent
                     ${errors.amount ? 'border-red-500' : 'border-gray-300'}`}
                />
                {errors.amount && (
                    <p className="mt-1 text-sm text-red-600">{errors.amount}</p>
                )}
            </div>

            <button
                type="submit"
                disabled={addFunds.isLoading}
                className="w-full bg-purple-600 text-white p-2 rounded hover:bg-purple-700
                 disabled:opacity-50 disabled:cursor-not-allowed"
            >
                {addFunds.isLoading ? 'Processing...' : 'Add Funds'}
            </button>
        </form>
    );
};