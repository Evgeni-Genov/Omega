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

export const handleApiError = (error: any): ApiError => {
    if (error.response) {
        return new ApiError(
            error.response.status,
            error.response.data.message || 'An error occurred',
            error.response.data
        );
    }
    return new ApiError(500, 'Network error occurred');
};