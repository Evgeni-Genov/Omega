import {BudgetDetails} from "../types/Budget.ts";
import api from "../Config/api.ts";

export const budgetService = {
    getBudget: async (userId: string): Promise<BudgetDetails> => {
        const {data} = await api.get(`/api/budgets/${userId}`);
        return data;
    },

    getRemainingBudget: async (userId: string): Promise<number> => {
        const {data} = await api.get(`/api/budgets-remaining/${userId}`);
        return data;
    },

    getTotalSpent: async (userId: string): Promise<number> => {
        const {data} = await api.get(`/api/budgets/${userId}/total-spent`);
        return data;
    },

    createBudget: async (budget: BudgetDetails): Promise<BudgetDetails> => {
        const {data} = await api.post('/api/budgets', budget);
        return data;
    },

    updateBudget: async (budget: BudgetDetails): Promise<BudgetDetails> => {
        const {data} = await api.patch(`/api/budgets/${budget.id}`, budget);
        return data;
    },

    deleteBudget: async (budgetId: number): Promise<void> => {
        await api.delete(`/api/budgets/${budgetId}`);
    }
};