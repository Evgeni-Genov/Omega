import {toast, ToastOptions} from 'react-toastify';

interface ToastConfig extends ToastOptions {
    message: string;
}

export const useToast = () => {
    const showSuccess = (config: ToastConfig) => {
        toast.success(config.message, {
            position: 'top-right',
            autoClose: 3000,
            ...config
        });
    };

    const showError = (config: ToastConfig) => {
        toast.error(config.message, {
            position: 'top-right',
            autoClose: 5000,
            ...config
        });
    };

    const showWarning = (config: ToastConfig) => {
        toast.warning(config.message, {
            position: 'top-right',
            autoClose: 4000,
            ...config
        });
    };

    return {
        showSuccess,
        showError,
        showWarning
    };
};