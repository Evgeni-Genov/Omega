import {useInfiniteQuery} from 'react-query';
import {ApiError, handleApiError} from "../utils/errorHandler.ts";
import {TransactionContext} from "../contexts/TransactionContext.tsx";
import api from "../Config/api.ts";
import React from "react";

export const useTransactions = (userId: string, pageSize = 10) => {
    return useInfiniteQuery(
        ['transactions', userId],
        async ({pageParam = 0}) => {
            try {
                const {data} = await api.get(`/api/transaction/${userId}`, {
                    params: {page: pageParam, size: pageSize}
                });
                return data;
            } catch (error) {
                throw handleApiError(error);
            }
        },
        {
            getNextPageParam: (lastPage, pages) =>
                lastPage.length === pageSize ? pages.length : undefined,
            staleTime: 30000,
            retry: (failureCount, error) => {
                if (error instanceof ApiError && error.status === 404) {
                    return false;
                }
                return failureCount < 3;
            }
        }
    );
};

export const useTransaction = () => {
    const context = React.useContext(TransactionContext);

    if (!context) {
        throw new Error('useTransaction must be used within TransactionProvider');
    }

    return context;
};

