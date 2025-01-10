import {AxiosError} from 'axios';

export class ApiError extends Error {
    constructor(
        public status: number,
        message: string,
        public data?: any
    ) {
        super(message);
        this.name = 'ApiError';
    }
}

export interface ErrorResponse {
    message: string;
    validationErrors?: Record<string, string>;
}

export const handleApiError = (error: unknown): ApiError => {
    if (error instanceof AxiosError && error.response) {
        const response = error.response.data as ErrorResponse;
        return new ApiError(
            error.response.status,
            response.message || 'An error occurred',
            response.validationErrors
        );
    }

    return new ApiError(
        500,
        error instanceof Error ? error.message : 'An unexpected error occurred'
    );
};
