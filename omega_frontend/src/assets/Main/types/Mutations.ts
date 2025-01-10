import {UseMutationResult} from 'react-query';
import {Transaction, TransactionRequest} from "./Transaction.ts";
import {CreditCardDetails} from "./Payment.ts";
import {AccountBalance} from "./Account.ts";

export interface AccountMutations {
    addFunds: UseMutationResult<
        Transaction,
        Error,
        CreditCardDetails,
        { previousBalance?: AccountBalance }
    >;

    sendFunds: UseMutationResult<
        Transaction,
        Error,
        TransactionRequest,
        { previousBalance?: AccountBalance }
    >;
}