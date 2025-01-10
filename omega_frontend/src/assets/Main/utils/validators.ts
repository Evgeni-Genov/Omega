import {TransactionRequest} from "../types/Transaction.ts";
import {CreditCardDetails} from "../types/Payment.ts";

export const validateTransactionRequest = (request: TransactionRequest): string[] => {
    const errors: string[] = [];

    if (!request.recipientNameTag) {
        errors.push('Recipient is required');
    }

    if (!request.amount || request.amount <= 0) {
        errors.push('Amount must be greater than zero');
    }

    if (!request.description) {
        errors.push('Description is required');
    }

    return errors;
};

export const validateCreditCard = (card: CreditCardDetails): string[] => {
    const errors: string[] = [];

    // Card number validation (Luhn algorithm)
    const isValidCardNumber = (number: string): boolean => {
        const digits = number.replace(/\D/g, '');
        let sum = 0;
        let isEven = false;

        for (let i = digits.length - 1; i >= 0; i--) {
            let digit = parseInt(digits[i]);
            if (isEven) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            isEven = !isEven;
        }

        return sum % 10 === 0;
    };

    if (!isValidCardNumber(card.cardNumber)) {
        errors.push('Invalid card number');
    }

    // Expiry date validation
    const [month, year] = card.expiryDate.split('/');
    const now = new Date();
    const expiry = new Date(2000 + parseInt(year), parseInt(month) - 1);

    if (expiry < now) {
        errors.push('Card has expired');
    }

    // CVV validation
    if (!/^\d{3,4}$/.test(card.securityCode)) {
        errors.push('Invalid security code');
    }

    if (!card.cardOwner || card.cardOwner.length < 3) {
        errors.push('Invalid card owner name');
    }

    return errors;
};