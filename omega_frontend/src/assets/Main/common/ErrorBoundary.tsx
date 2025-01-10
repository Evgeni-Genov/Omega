import React, {Component, ErrorInfo} from 'react';

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
                <div className="p-4 bg-red-50 rounded-lg">
                    <h2 className="text-red-800 font-medium">Something went wrong</h2>
                    <p className="text-red-600">{this.state.error?.message}</p>
                </div>
            );
        }

        return this.props.children;
    }
}