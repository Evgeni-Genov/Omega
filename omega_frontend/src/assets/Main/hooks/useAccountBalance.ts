import {useQuery} from 'react-query';
import {AccountBalance} from "../types/Account.ts";
import api from "../Config/api.ts";

export function useAccountBalance(userId: string) {
    return useQuery<AccountBalance>(
        ['accountBalance', userId],
        async () => {
            const {data} = await api.get(`/api/account-balance/user/${userId}`);
            return data[0];
        },
        {
            staleTime: 30000,
            refetchOnWindowFocus: false,
            retry: 2
        }
    );
}