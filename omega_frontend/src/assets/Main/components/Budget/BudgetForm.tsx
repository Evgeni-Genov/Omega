import React from 'react';
import {BudgetDetails} from "../../types/Budget.ts";
import {useBudgetMutations} from "../../hooks/useBudgetMutation.ts";

interface BudgetFormProps {
    userId: string;
    onSuccess?: () => void;
    initialData?: BudgetDetails;
}

export const BudgetForm: React.FC<BudgetFormProps> = ({userId, onSuccess, initialData}) => {
    const {createBudget, updateBudget} = useBudgetMutations(userId);
    const [formData, setFormData] = React.useState<BudgetDetails>(initialData || {
        userId: Number(userId),
        budget: 0,
        startDate: '',
        endDate: ''
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (initialData?.id) {
                await updateBudget.mutateAsync(formData);
            } else {
                await createBudget.mutateAsync(formData);
            }
            onSuccess?.();
        } catch (error) {
            console.error('Failed to save budget:', error);
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'budget' ? Number(value) : value
        }));
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            <div>
                <label className="block text-sm font-medium text-gray-700">Budget Amount</label>
                <input
                    type="number"
                    name="budget"
                    value={formData.budget}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                    required
                />
            </div>
            <div>
                <label className="block text-sm font-medium text-gray-700">Start Date</label>
                <input
                    type="date"
                    name="startDate"
                    value={formData.startDate}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                    required
                />
            </div>
            <div>
                <label className="block text-sm font-medium text-gray-700">End Date</label>
                <input
                    type="date"
                    name="endDate"
                    value={formData.endDate}
                    onChange={handleChange}
                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-purple-500 focus:ring-purple-500"
                    required
                />
            </div>
            <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
            >
                {initialData?.id ? 'Update Budget' : 'Create Budget'}
            </button>
        </form>
    );
};