interface TransactionErrorProps {
    error: Error;
    resetErrorBoundary: () => void;
}

export const TransactionErrorFallback: React.FC<TransactionErrorProps> = ({
                                                                              error,
                                                                              resetErrorBoundary
                                                                          }) => (
    <div className="p-4 bg-red-50 rounded-lg">
        <h3 className="text-red-800 font-medium mb-2">Transaction Error</h3>
        <p className="text-red-600 mb-4">{error.message}</p>
        <button
            onClick={resetErrorBoundary}
            className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
        >
            Try Again
        </button>
    </div>
);