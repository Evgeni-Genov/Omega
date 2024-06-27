import React, {useEffect, useState} from 'react';
import axiosInstance from '../Config/AxiosConfiguration.ts'; // Ensure the correct path to axiosInstance
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Switch,
    TextField,
    Typography
} from '@mui/material';
import {styled} from '@mui/material/styles';

const PurpleSwitch = styled(Switch)(() => ({
    '& .MuiSwitch-switchBase.Mui-checked': {
        color: 'rebeccapurple',
        '&:hover': {
            backgroundColor: 'rgba(102, 51, 153, 0.08)',
        },
    },
    '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
        backgroundColor: 'rebeccapurple',
    },
}));

const TwoFactorAuthentication = ({userId, email, twoFactorAuthentication = false}) => {
    const [enabled, setEnabled] = useState(twoFactorAuthentication);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [open, setOpen] = useState(false);
    const [qrCode, setQrCode] = useState('');
    const [verificationCode, setVerificationCode] = useState('');

    useEffect(() => {
        setEnabled(twoFactorAuthentication);
    }, [twoFactorAuthentication]);

    const handleToggle = async () => {
        if (!enabled) {
            setOpen(true);
        } else {
            await updateTwoFactorAuthentication(false);
        }
    };

    const handleClose = () => {
        setOpen(false);
    };

    const updateTwoFactorAuthentication = async (enable) => {
        setLoading(true);
        setError('');
        try {
            const response = await axiosInstance.post('/user/update-2fa', {
                id: userId,
                twoFactorAuthentication: enable
            });
            console.log('API call made, response:', response);

            setEnabled(enable);
            console.log('2FA status updated to:', enable);
        } catch (e) {
            console.error('Error while processing request:', e);
            setError('An error occurred while processing your request.');
        } finally {
            setLoading(false);
            console.log('Loading state set to false');
        }
    };

    const generateQrCode = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await axiosInstance.get('/google-authenticator/generate-qr-code', {
                params: {account: email, issuer: 'Omega'},
                responseType: 'arraybuffer'
            });
            const blob = new Blob([response.data], {type: 'image/png'});
            const url = URL.createObjectURL(blob);
            setQrCode(url);
            setLoading(false);
        } catch (e) {
            console.error('Error while generating QR code:', e);
            setError('An error occurred while generating the QR code.');
            setLoading(false);
        }
    };

    const handleEnable = async () => {
        try {
            const response = await axiosInstance.post('/google-authenticator/verify-code', {
                id: userId,
                twoFactorAuthCode: verificationCode
            });
            console.log('Verification response:', response);
            if (response.status === 200) {
                await updateTwoFactorAuthentication(true);
                handleClose();
            }
        } catch (e) {
            if (e.response && e.response.status === 400) {
                setError('Invalid verification code!');
            } else {
                setError('An error occurred while verifying the code.');
            }
        }
    };

    return (
        <Box display="flex" alignItems="center">
            <PurpleSwitch
                checked={enabled}
                onChange={handleToggle}
                color="primary"
                disabled={loading}
            />
            <Typography variant="body2" style={{marginLeft: '10px', color: 'rebeccapurple'}}>
                {enabled ? 'On' : 'Off'}
            </Typography>
            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Enable Two-Factor Authentication</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        To enable two-factor authentication, you need to generate a QR code and scan it using your
                        authenticator app.
                    </DialogContentText>
                    {qrCode && (
                        <Box display="flex" justifyContent="center">
                            <img src={qrCode} alt="QR Code" style={{marginTop: '10px'}}/>
                        </Box>
                    )}
                    {!qrCode && (
                        <Box display="flex" justifyContent="center" sx={{mt: 2}}>
                            <Button
                                onClick={generateQrCode}
                                variant="contained"
                                sx={{
                                    backgroundColor: 'rebeccapurple',
                                    '&:hover': {backgroundColor: 'rgba(102, 51, 153, 0.8)'}
                                }}
                            >
                                Generate QR Code
                            </Button>
                        </Box>
                    )}
                    {qrCode && (
                        <TextField
                            label="Enter 6-digit code"
                            variant="outlined"
                            fullWidth
                            margin="normal"
                            value={verificationCode}
                            onChange={(e) => setVerificationCode(e.target.value)}
                            sx={{
                                '& label.Mui-focused': {
                                    color: 'rebeccapurple',
                                },
                                '& .MuiInput-underline:after': {
                                    borderBottomColor: 'rebeccapurple',
                                },
                                '& .MuiOutlinedInput-root': {
                                    '& fieldset': {
                                        borderColor: 'rebeccapurple',
                                    },
                                    '&:hover fieldset': {
                                        borderColor: 'rebeccapurple',
                                    },
                                    '&.Mui-focused fieldset': {
                                        borderColor: 'rebeccapurple',
                                    },
                                },
                            }}
                        />
                    )}
                    {error && (
                        <Typography variant="body2" color="error">
                            {error}
                        </Typography>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="secondary">
                        Cancel
                    </Button>
                    <Button
                        onClick={handleEnable}
                        variant="contained"
                        sx={{backgroundColor: 'rebeccapurple', '&:hover': {backgroundColor: 'rgba(102, 51, 153, 0.8)'}}}
                        disabled={!qrCode || !verificationCode}
                    >
                        Enable
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default TwoFactorAuthentication;
