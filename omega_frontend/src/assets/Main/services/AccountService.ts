import {AxiosResponse} from 'axios';
import {AccountBalance} from "../types/Account.ts";
import api from "../Config/api.ts";
import {Transaction} from "../types/Transaction.ts";
import {CreditCardDetails} from "../types/Payment.ts";

export const accountService = {
    getBalance: async (userId: string): Promise<AccountBalance> => {
        const response: AxiosResponse<AccountBalance> = await api.get(`/api/account-balance/user/${userId}`);
        return response.data;
    },

    getTransactions: async (userId: string): Promise<Transaction[]> => {
        const response: AxiosResponse<Transaction[]> = await api.get(`/api/transaction/${userId}`);
        return response.data;
    },

    addFunds: async (data: CreditCardDetails): Promise<Transaction> => {
        const response: AxiosResponse<Transaction> = await api.post('/api/transaction/add-funds', data);
        return response.data;
    },

    sendFunds: async (data: Transaction): Promise<Transaction> => {
        const response: AxiosResponse<Transaction> = await api.post('/api/transaction/send-funds', data);
        return response.data;
    }
};