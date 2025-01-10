import {useMutation, useQueryClient} from 'react-query';
import api from "../Config/api.ts";
import {CreditCardDetails} from "../types/Payment.ts";
import {TransactionRequest} from "../types/Transaction.ts";

export function useAccountMutations(userId: string) {
    const queryClient = useQueryClient();

    const addFunds = useMutation(
        (details: CreditCardDetails) =>
            api.post('/api/transaction/add-funds', details),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['accountBalance', userId]);
                queryClient.invalidateQueries(['transactions', userId]);
            }
        }
    );

    const sendFunds = useMutation(
        (transaction: TransactionRequest) =>
            api.post('/api/transaction/send-funds', transaction),
        {
            onSuccess: () => {
                queryClient.invalidateQueries(['accountBalance', userId]);
                queryClient.invalidateQueries(['transactions', userId]);
            }
        }
    );

    return {addFunds, sendFunds};
}