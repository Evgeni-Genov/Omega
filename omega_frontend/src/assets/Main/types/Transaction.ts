export enum TransactionStatus {
    PENDING = 'PENDING',
    PROCESSING = 'PROCESSING',
    SUCCESSFUL = 'SUCCESSFUL',
    FAILED = 'FAILED',
    CANCELLED = 'CANCELLED'
}

export enum TransactionType {
    PURCHASE = 'PURCHASE',
    TRANSFER = 'TRANSFER',
    DEPOSIT = 'DEPOSIT'
}

export interface Transaction {
    id: number;
    senderId: number;
    recipientId: number;
    senderNameTag?: string;
    recipientNameTag?: string;
    amount: number;
    description: string;
    currency: Currency;
    transactionStatus: TransactionStatus;
    transactionType: TransactionType;
    isExpense: boolean;
    createdDate: string;
}

export interface TransactionRequest {
    recipientNameTag: string;
    amount: number;
    description: string;
    currency: Currency;
    requestId?: number;
}

export interface TransactionReport {
    startDate: string;
    endDate: string;
    totalSpent: number;
    totalReceived: number;
    transactions: Transaction[];
}

export interface TransactionFilters {
    startDate?: string;
    endDate?: string;
    type?: TransactionType;
    status?: TransactionStatus;
    currency?: Currency;
    minAmount?: number;
    maxAmount?: number;
}

export enum Currency {
    USD = 'USD',
    BGN = 'BGN',
    EUR = 'EUR',
    GBP = 'GBP',
    JPY = 'JPY'
}
