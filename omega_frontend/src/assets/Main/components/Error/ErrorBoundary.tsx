import React, {Component, ErrorInfo} from 'react';
import {Alert} from '@/components/ui/alert';

interface Props {
    children: React.ReactNode;
    fallback?: React.ReactNode;
}

interface State {
    hasError: boolean;
    error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
    state: State = {hasError: false};

    static getDerivedStateFromError(error: Error): State {
        return {hasError: true, error};
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error('Error caught by boundary:', error, errorInfo);
    }

    render() {
        if (this.state.hasError) {
            return this.props.fallback || (
                <Alert variant="destructive">
                    <h2 className="font-medium mb-2">Something went wrong</h2>
                    <p className="text-sm">{this.state.error?.message}</p>
                </Alert>
            );
        }

        return this.props.children;
    }
}