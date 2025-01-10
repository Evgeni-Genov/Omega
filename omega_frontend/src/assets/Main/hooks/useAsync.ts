import {useState} from 'react';
import {useToast} from './useToast';
import {handleApiError} from "../utils/errorHandler.ts";

interface UseAsyncActionOptions {
    onSuccess?: () => void;
    onError?: (error: Error) => void;
    successMessage?: string;
}

export const useAsyncAction = (options: UseAsyncActionOptions = {}) => {
    const [isLoading, setIsLoading] = useState(false);
    const toast = useToast();

    const execute = async <T>(asyncFn: () => Promise<T>): Promise<T | undefined> => {
        setIsLoading(true);
        try {
            const result = await asyncFn();
            if (options.successMessage) {
                toast.showSuccess({message: options.successMessage});
            }
            options.onSuccess?.();
            return result;
        } catch (error) {
            const apiError = handleApiError(error);
            toast.showError({message: apiError.message});
            options.onError?.(apiError);
            return undefined;
        } finally {
            setIsLoading(false);
        }
    };

    return {execute, isLoading};
};