import React from 'react';
import {Dialog, Transition} from '@headlessui/react';
import {X} from 'lucide-react';

interface BaseDialogProps {
    isOpen: boolean;
    onClose: () => void;
    title: string;
    children: React.ReactNode;
}

export const BaseDialog: React.FC<BaseDialogProps> = ({
                                                          isOpen,
                                                          onClose,
                                                          title,
                                                          children
                                                      }) => (
    <Transition show={isOpen} as={React.Fragment}>
        <Dialog onClose={onClose} className="relative z-50">
            <Transition.Child
                enter="ease-out duration-300"
                enterFrom="opacity-0"
                enterTo="opacity-100"
                leave="ease-in duration-200"
                leaveFrom="opacity-100"
                leaveTo="opacity-0"
            >
                <div className="fixed inset-0 bg-black/30"/>
            </Transition.Child>

            <div className="fixed inset-0 flex items-center justify-center p-4">
                <Transition.Child
                    as={Dialog.Panel}
                    enter="ease-out duration-300"
                    enterFrom="opacity-0 scale-95"
                    enterTo="opacity-100 scale-100"
                    leave="ease-in duration-200"
                    leaveFrom="opacity-100 scale-100"
                    leaveTo="opacity-0 scale-95"
                    className="w-full max-w-md transform overflow-hidden rounded-2xl bg-white p-6 text-left align-middle shadow-xl transition-all"
                >
                    <div className="flex justify-between items-center mb-4">
                        <Dialog.Title className="text-lg font-medium text-gray-900">
                            {title}
                        </Dialog.Title>
                        <button
                            onClick={onClose}
                            className="text-gray-400 hover:text-gray-500"
                        >
                            <X className="h-5 w-5"/>
                        </button>
                    </div>
                    {children}
                </Transition.Child>
            </div>
        </Dialog>
    </Transition>
);