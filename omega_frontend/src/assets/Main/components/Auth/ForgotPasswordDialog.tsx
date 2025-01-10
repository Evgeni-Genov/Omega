import {useToast} from "../../hooks/useToast.ts";
import api from "../../Config/api.ts";
import {BaseDialog} from "../../common/BaseDialog.tsx";
import React from "react";

interface ForgotPasswordDialogProps {
    isOpen: boolean;
    onClose: () => void;
}

export const ForgotPasswordDialog: React.FC<ForgotPasswordDialogProps> = ({
                                                                              isOpen,
                                                                              onClose
                                                                          }) => {
    const [email, setEmail] = React.useState('');
    const [isLoading, setIsLoading] = React.useState(false);
    const toast = useToast();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await api.post(`/api/reset-password?email=${email}`);
            toast.showSuccess({message: 'Password reset link has been sent to your email.'});
            onClose();
        } catch (error) {
            toast.showError({message: 'Failed to send password reset link. Please try again.'});
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <BaseDialog
            isOpen={isOpen}
            onClose={onClose}
            title="Reset Password"
        >
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">
                        Email Address
                    </label>
                    <input
                        type="email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="mt-1 block w-full border-gray-300 rounded-md shadow-sm
                   focus:ring-purple-500 focus:border-purple-500 sm:text-sm"
                    />
                </div>

                <button
                    type="submit"
                    disabled={isLoading || !email}
                    className="w-full flex justify-center py-2 px-4 border border-transparent
                 rounded-md shadow-sm text-sm font-medium text-white bg-purple-600
                 hover:bg-purple-700 focus:outline-none focus:ring-2
                 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50"
                >
                    {isLoading ? 'Sending...' : 'Send Reset Link'}
                </button>

                <p className="mt-2 text-sm text-gray-500">
                    You will receive an email with instructions to reset your password.
                </p>
            </form>
        </BaseDialog>
    );
};