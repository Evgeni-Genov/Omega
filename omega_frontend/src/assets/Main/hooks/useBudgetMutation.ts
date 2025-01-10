import {useMutation, useQueryClient} from 'react-query';
import {BudgetDetails} from "../types/Budget.ts";
import api from "../Config/api.ts";

export function useBudgetMutations(userId: string) {
    const queryClient = useQueryClient();

    const createBudget = useMutation(
        (budget: BudgetDetails) => api.post('/api/budgets', budget),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['budget', userId]);
                queryClient.invalidateQueries(['budgetSummary', userId]);
            }
        }
    );

    const updateBudget = useMutation(
        (budget: BudgetDetails) => api.patch(`/api/budgets/${budget.id}`, budget),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['budget', userId]);
                queryClient.invalidateQueries(['budgetSummary', userId]);
            }
        }
    );

    const deleteBudget = useMutation(
        (budgetId: number) => api.delete(`/api/budgets/${budgetId}`),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['budget', userId]);
                queryClient.invalidateQueries(['budgetSummary', userId]);
            }
        }
    );

    return {createBudget, updateBudget, deleteBudget};
}