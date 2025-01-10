import {useQuery, useMutation} from 'react-query';
import {budgetService} from "../services/BudgetService.ts";

export const useBudget = (userId: string) => {
    const budget = useQuery(['budget', userId], () =>
        budgetService.getBudget(userId)
    );

    const createBudget = useMutation(budgetService.createBudget);
    const updateBudget = useMutation(budgetService.updateBudget);

    return {
        budget,
        createBudget,
        updateBudget
    };
};