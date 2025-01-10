import React from 'react';

export const LoadingFallback: React.FC = () => (
    <div className="w-full h-full flex items-center justify-center p-8">
        <div className="flex flex-col items-center space-y-4">
            <div className="w-8 h-8 border-4 border-purple-500 rounded-full
                    border-t-transparent animate-spin"/>
            <p className="text-sm text-gray-500">Loading...</p>
        </div>
    </div>
);