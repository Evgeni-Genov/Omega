import React from 'react';
import {TransactionStatus, TransactionType} from "../../types/Transaction.ts";

interface TransactionFilterProps {
    onFilterChange: (filters: TransactionFilters) => void;
}

interface TransactionFilters {
    dateRange: 'all' | 'today' | 'week' | 'month' | 'custom';
    startDate?: Date;
    endDate?: Date;
    type?: TransactionType;
    status?: TransactionStatus;
    minAmount?: number;
    maxAmount?: number;
}

export const TransactionFilter: React.FC<TransactionFilterProps> = ({onFilterChange}) => {
    const [filters, setFilters] = React.useState<TransactionFilters>({
        dateRange: 'all'
    });

    const handleFilterChange = (key: keyof TransactionFilters, value: any) => {
        const newFilters = {...filters, [key]: value};
        setFilters(newFilters);
        onFilterChange(newFilters);
    };

    return (
        <div className="bg-white p-4 rounded-lg shadow-sm space-y-4">
            <div className="grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Date Range
                    </label>
                    <select
                        value={filters.dateRange}
                        onChange={(e) => handleFilterChange('dateRange', e.target.value)}
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                    >
                        <option value="all">All Time</option>
                        <option value="today">Today</option>
                        <option value="week">This Week</option>
                        <option value="month">This Month</option>
                        <option value="custom">Custom Range</option>
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Transaction Type
                    </label>
                    <select
                        value={filters.type || ''}
                        onChange={(e) => handleFilterChange('type', e.target.value || undefined)}
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                    >
                        <option value="">All Types</option>
                        {Object.values(TransactionType).map(type => (
                            <option key={type} value={type}>{type}</option>
                        ))}
                    </select>
                </div>

                {filters.dateRange === 'custom' && (
                    <div className="col-span-2 grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Start Date
                            </label>
                            <input
                                type="date"
                                value={filters.startDate?.toISOString().split('T')[0] || ''}
                                onChange={(e) => handleFilterChange('startDate', new Date(e.target.value))}
                                className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                End Date
                            </label>
                            <input
                                type="date"
                                value={filters.endDate?.toISOString().split('T')[0] || ''}
                                onChange={(e) => handleFilterChange('endDate', new Date(e.target.value))}
                                className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                            />
                        </div>
                    </div>
                )}

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Status
                    </label>
                    <select
                        value={filters.status || ''}
                        onChange={(e) => handleFilterChange('status', e.target.value || undefined)}
                        className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                    >
                        <option value="">All Statuses</option>
                        {Object.values(TransactionStatus).map(status => (
                            <option key={status} value={status}>{status}</option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Amount Range
                    </label>
                    <div className="grid grid-cols-2 gap-2">
                        <input
                            type="number"
                            placeholder="Min"
                            value={filters.minAmount || ''}
                            onChange={(e) => handleFilterChange('minAmount', e.target.value ? Number(e.target.value) : undefined)}
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                        />
                        <input
                            type="number"
                            placeholder="Max"
                            value={filters.maxAmount || ''}
                            onChange={(e) => handleFilterChange('maxAmount', e.target.value ? Number(e.target.value) : undefined)}
                            className="w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                        />
                    </div>
                </div>
            </div>
        </div>
    );
};