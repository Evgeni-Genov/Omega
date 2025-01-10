import React from 'react';
import {useToast} from '../../hooks/useToast';
import {Radio} from 'lucide-react';
import api from "../../Config/api.ts";
import {BaseDialog} from "../../common/BaseDialog.tsx";

interface TwoFactorDialogProps {
    isOpen: boolean;
    onClose: () => void;
    userId: string;
    onSuccess: (token: string, refreshToken: string) => void;
    selectedMethod: 'googleAuth' | 'emailCode';
    onMethodChange: (method: 'googleAuth' | 'emailCode') => void;
}

export const TwoFactorDialog: React.FC<TwoFactorDialogProps> = ({
                                                                    isOpen,
                                                                    onClose,
                                                                    userId,
                                                                    onSuccess,
                                                                    selectedMethod,
                                                                    onMethodChange
                                                                }) => {
    const [code, setCode] = React.useState('');
    const [isLoading, setIsLoading] = React.useState(false);
    const toast = useToast();

    const handleVerify = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const response = await api.post('/api/google-authenticator/verify-authenticator-code', {
                id: userId,
                twoFactorAuthCode: code
            });

            if (response.status === 200) {
                onSuccess(response.data.token, response.data.refreshToken);
                onClose();
            }
        } catch (error) {
            toast.showError({message: 'Invalid verification code'});
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <BaseDialog
            isOpen={isOpen}
            onClose={onClose}
            title="Two-Factor Authentication"
        >
            <div className="space-y-6">
                <div className="flex flex-col space-y-4">
                    <Radio.Group value={selectedMethod}
                                 onChange={value => onMethodChange(value as 'googleAuth' | 'emailCode')}>
                        <Radio value="googleAuth">Google Authenticator</Radio>
                        <Radio value="emailCode">Email Code</Radio>
                    </Radio.Group>
                </div>

                <form onSubmit={handleVerify} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700">
                            Enter Verification Code
                        </label>
                        <input
                            type="text"
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                            className="mt-1 block w-full border-gray-300 rounded-md shadow-sm
                     focus:ring-purple-500 focus:border-purple-500 sm:text-sm"
                            maxLength={6}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading || code.length !== 6}
                        className="w-full flex justify-center py-2 px-4 border border-transparent
                   rounded-md shadow-sm text-sm font-medium text-white bg-purple-600
                   hover:bg-purple-700 focus:outline-none focus:ring-2
                   focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50"
                    >
                        {isLoading ? 'Verifying...' : 'Verify'}
                    </button>
                </form>
            </div>
        </BaseDialog>
    );
};