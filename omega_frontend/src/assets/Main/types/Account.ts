import {Currency, Transaction} from "./Transaction.ts";
import {BudgetDetails} from "./Budget.ts";

export interface AccountBalance {
    id: number;
    userId: number;
    currency: Currency;
    balance: number;
}

export interface AccountSummary {
    balance: AccountBalance;
    pendingTransactions: number;
    recentTransactions: Transaction[];
    monthlySpending: number;
    monthlyDeposits: number;
    budgetStatus?: BudgetDetails;
}

