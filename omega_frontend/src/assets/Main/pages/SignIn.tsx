import React from 'react';
import {useNavigate} from 'react-router-dom';
import {AlertCircle} from 'lucide-react';
import {useToast} from '../hooks/useToast';
import api from "../Config/api.ts";

interface SignInForm {
    username: string;
    password: string;
    rememberMe: boolean;
}

export const SignIn: React.FC = () => {
    const navigate = useNavigate();
    const toast = useToast();
    const [isLoading, setIsLoading] = React.useState(false);
    const [formData, setFormData] = React.useState<SignInForm>({
        username: '',
        password: '',
        rememberMe: false
    });
    const [showTwoFactorOptions, setShowTwoFactorOptions] = React.useState(false);
    const [selectedAuthMethod, setSelectedAuthMethod] = React.useState<'googleAuth' | 'emailCode'>('googleAuth');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const response = await api.post('/auth/signin', {
                username: formData.username,
                password: formData.password
            });

            const {id, token, refreshToken, twoFactorAuthentication} = response.data;

            if (twoFactorAuthentication) {
                if (selectedAuthMethod === 'emailCode') {
                    const emailResponse = await api.get(`/api/user/email/${formData.username}`);
                    const email = emailResponse.data;

                    await api.post('/api/email-verification-code', null, {
                        params: {email}
                    });

                    // Handle 2FA verification
                    // You would typically show a 2FA verification dialog here
                }
                setShowTwoFactorOptions(true);
            } else {
                handleSuccessfulLogin(id, token, refreshToken);
            }
        } catch (error) {
            toast.showError({
                message: error instanceof Error ? error.message : 'Failed to sign in'
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleSuccessfulLogin = (userId: string, token: string, refreshToken: string) => {
        localStorage.setItem('TOKEN', token);
        localStorage.setItem('REFRESH_TOKEN', refreshToken);
        localStorage.setItem('USER_ID', userId);

        if (formData.rememberMe) {
            localStorage.setItem('rememberedUsername', formData.username);
        } else {
            localStorage.removeItem('rememberedUsername');
        }

        navigate(`/user-profile/${userId}`);
    };

    return (
        <div className="min-h-screen bg-gray-100 flex flex-col justify-center">
            <div className="sm:mx-auto sm:w-full sm:max-w-md">
                <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
                    <div className="sm:mx-auto sm:w-full sm:max-w-md mb-6">
                        <h2 className="text-center text-3xl font-extrabold text-gray-900">
                            Sign in to your account
                        </h2>
                    </div>

                    <form className="space-y-6" onSubmit={handleSubmit}>
                        <div>
                            <label
                                htmlFor="username"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Username
                            </label>
                            <div className="mt-1">
                                <input
                                    id="username"
                                    name="username"
                                    type="text"
                                    required
                                    value={formData.username}
                                    onChange={(e) => setFormData(prev => ({
                                        ...prev,
                                        username: e.target.value
                                    }))}
                                    className="appearance-none block w-full px-3 py-2 border
                         border-gray-300 rounded-md shadow-sm placeholder-gray-400
                         focus:outline-none focus:ring-purple-500
                         focus:border-purple-500 sm:text-sm"
                                />
                            </div>
                        </div>

                        <div>
                            <label
                                htmlFor="password"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Password
                            </label>
                            <div className="mt-1">
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    value={formData.password}
                                    onChange={(e) => setFormData(prev => ({
                                        ...prev,
                                        password: e.target.value
                                    }))}
                                    className="appearance-none block w-full px-3 py-2 border
                         border-gray-300 rounded-md shadow-sm placeholder-gray-400
                         focus:outline-none focus:ring-purple-500
                         focus:border-purple-500 sm:text-sm"
                                />
                            </div>
                        </div>

                        <div className="flex items-center justify-between">
                            <div className="flex items-center">
                                <input
                                    id="remember-me"
                                    name="remember-me"
                                    type="checkbox"
                                    checked={formData.rememberMe}
                                    onChange={(e) => setFormData(prev => ({
                                        ...prev,
                                        rememberMe: e.target.checked
                                    }))}
                                    className="h-4 w-4 text-purple-600 focus:ring-purple-500
                         border-gray-300 rounded"
                                />
                                <label
                                    htmlFor="remember-me"
                                    className="ml-2 block text-sm text-gray-900"
                                >
                                    Remember me
                                </label>
                            </div>

                            <div className="text-sm">
                                <a
                                    href="#"
                                    onClick={() => navigate('/forgot-password')}
                                    className="font-medium text-purple-600 hover:text-purple-500"
                                >
                                    Forgot your password?
                                </a>
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full flex justify-center py-2 px-4 border border-transparent
                     rounded-md shadow-sm text-sm font-medium text-white
                     bg-purple-600 hover:bg-purple-700 focus:outline-none
                     focus:ring-2 focus:ring-offset-2 focus:ring-purple-500
                     disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? 'Signing in...' : 'Sign in'}
                        </button>

                        <div className="mt-6">
                            <div className="relative">
                                <div className="absolute inset-0 flex items-center">
                                    <div className="w-full border-t border-gray-300"/>
                                </div>
                                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-gray-500">
                    New to our platform?
                  </span>
                                </div>
                            </div>

                            <div className="mt-6">
                                <button
                                    type="button"
                                    onClick={() => navigate('/signup')}
                                    className="w-full flex justify-center py-2 px-4 border
                         border-purple-600 rounded-md shadow-sm text-sm
                         font-medium text-purple-600 bg-white hover:bg-purple-50"
                                >
                                    Create an account
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};