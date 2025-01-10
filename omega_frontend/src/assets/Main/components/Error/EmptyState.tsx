import React from 'react';
import {AlertCircle} from 'lucide-react';

interface EmptyStateProps {
    title: string;
    message: string;
    action?: {
        label: string;
        onClick: () => void;
    };
}

export const EmptyState: React.FC<EmptyStateProps> = ({
                                                          title,
                                                          message,
                                                          action
                                                      }) => (
    <div className="text-center py-12 px-4">
        <AlertCircle className="mx-auto h-12 w-12 text-gray-400"/>
        <h3 className="mt-2 text-lg font-medium text-gray-900">{title}</h3>
        <p className="mt-1 text-sm text-gray-500">{message}</p>
        {action && (
            <div className="mt-6">
                <button
                    onClick={action.onClick}
                    className="inline-flex items-center px-4 py-2 border border-transparent
                   text-sm font-medium rounded-md text-white bg-purple-600
                   hover:bg-purple-700 focus:outline-none focus:ring-2
                   focus:ring-offset-2 focus:ring-purple-500"
                >
                    {action.label}
                </button>
            </div>
        )}
    </div>
);