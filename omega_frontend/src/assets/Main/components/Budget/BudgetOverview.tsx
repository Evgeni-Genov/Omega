import React from 'react';
import {useQuery} from 'react-query';
import {budgetService} from "../../services/BudgetService.ts";
import {formatCurrency} from "../../Main/Utils/Formatters.ts";

interface BudgetOverviewProps {
    userId: string;
}

export const BudgetOverview: React.FC<BudgetOverviewProps> = ({userId}) => {
    const {data: budget} = useQuery(['budget', userId], () => budgetService.getBudget(userId));
    const {data: remaining} = useQuery(['remaining', userId], () => budgetService.getRemainingBudget(userId));
    const {data: spent} = useQuery(['spent', userId], () => budgetService.getTotalSpent(userId));

    if (!budget) return null;

    const percentageSpent = spent ? (spent / budget.budget) * 100 : 0;

    return (
        <div className="p-6 bg-white rounded-lg shadow">
            <h2 className="text-xl font-semibold mb-4">Budget Overview</h2>
            <div className="space-y-4">
                <div className="flex justify-between">
                    <span>Total Budget:</span>
                    <span className="font-medium">{formatCurrency(budget.budget)}</span>
                </div>
                <div className="flex justify-between">
                    <span>Spent:</span>
                    <span className="font-medium text-red-600">{formatCurrency(spent || 0)}</span>
                </div>
                <div className="flex justify-between">
                    <span>Remaining:</span>
                    <span className="font-medium text-green-600">{formatCurrency(remaining || 0)}</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                    <div
                        className="bg-purple-600 h-2.5 rounded-full"
                        style={{width: `${Math.min(percentageSpent, 100)}%`}}
                    />
                </div>
            </div>
        </div>
    );
};