import {Transaction} from '../../../models/transaction.types';
import {formatCurrency, formatDate} from '../../../utils/formatters';
import {BaseDialog} from "./BaseDialog.tsx";

interface TransactionDetailsDialogProps {
    isOpen: boolean;
    onClose: () => void;
    transaction: Transaction;
}

export const TransactionDetailsDialog: React.FC<TransactionDetailsDialogProps> = ({
                                                                                      isOpen,
                                                                                      onClose,
                                                                                      transaction
                                                                                  }) => (
    <BaseDialog
        isOpen={isOpen}
        onClose={onClose}
        title="Transaction Details"
    >
        <div className="space-y-4">
            <div>
                <label className="block text-sm font-medium text-gray-700">Status</label>
                <div className={`mt-1 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
          ${transaction.transactionStatus === 'SUCCESSFUL' ? 'bg-green-100 text-green-800' :
                    transaction.transactionStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-red-100 text-red-800'}`}
                >
                    {transaction.transactionStatus}
                </div>
            </div>

            <div>
                <label className="block text-sm font-medium text-gray-700">Amount</label>
                <p className={`mt-1 ${transaction.isExpense ? 'text-red-600' : 'text-green-600'}`}>
                    {transaction.isExpense ? '-' : '+'}{formatCurrency(transaction.amount)}
                </p>
            </div>

            <div>
                <label className="block text-sm font-medium text-gray-700">Description</label>
                <p className="mt-1">{transaction.description}</p>
            </div>

            <div>
                <label className="block text-sm font-medium text-gray-700">
                    {transaction.isExpense ? 'Recipient' : 'Sender'}
                </label>
                <p className="mt-1">
                    {transaction.isExpense ? transaction.recipientNameTag : transaction.senderNameTag}
                </p>
            </div>

            <div>
                <label className="block text-sm font-medium text-gray-700">Date</label>
                <p className="mt-1">{formatDate(transaction.createdDate)}</p>
            </div>
        </div>
    </BaseDialog>
);