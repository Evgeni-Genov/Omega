import {useEffect, useRef, useState} from 'react';
import axiosInstance from '../Config/api.ts';
import {useNavigate, useParams} from 'react-router-dom';
import {
    Alert,
    Avatar,
    Box,
    Button,
    Card,
    CardContent,
    CircularProgress,
    Container,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    Input,
    InputLabel,
    Menu,
    MenuItem,
    Snackbar,
    TextField,
    Typography,
} from '@mui/material';
import {styled} from '@mui/material/styles';
import SettingsIcon from '@mui/icons-material/Settings';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import LogoutIcon from '@mui/icons-material/Logout';
import HomeIcon from '@mui/icons-material/Home';
import InfoIcon from '@mui/icons-material/Info';
import SecurityIcon from '@mui/icons-material/Security';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css'; // Importing the default styles
import './UserProfile.css';
import TwoFactorAuthentication from "../Login/TwoFactorAuthentication.tsx";
import {useAvatar} from "../Util/AvatarUtil.tsx";

const CustomButton = styled(Button)(({theme}) => ({
    backgroundColor: '#663399',
    color: 'white',
    '&:hover': {
        backgroundColor: '#552286',
    },
}));

const EditButton = styled(IconButton)(({theme}) => ({
    marginLeft: theme.spacing(1),
    '&:hover': {
        color: '#552286',
    },
}));

const CustomTextField = styled(TextField)(({theme}) => ({
    '& label.Mui-focused': {
        color: '#663399',
    },
    '& .MuiInput-underline:after': {
        borderBottomColor: '#663399',
    },
    '& .MuiOutlinedInput-root': {
        '& fieldset': {
            borderColor: '#663399',
        },
        '&:hover fieldset': {
            borderColor: '#663399',
        },
        '&.Mui-focused fieldset': {
            borderColor: '#663399',
        },
    },
}));

const CustomDialog = styled(Dialog)(({theme}) => ({
    '& .MuiDialog-paper': {
        width: '100%',
        maxWidth: '600px',
    },
}));

const CustomPhoneInput = styled(PhoneInput)(({theme}) => ({
    '& .react-tel-input .form-control': {
        borderColor: '#663399',
        '&:focus': {
            borderColor: '#552286',
            boxShadow: '0 0 5px rgba(102, 51, 153, 0.5)',
        },
    },
    '& .react-tel-input .flag-dropdown': {
        borderColor: '#663399',
    },
    '& .react-tel-input .country-list': {
        backgroundColor: '#fff',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
        borderColor: '#663399',
        borderRadius: '4px',
        '& .country': {
            padding: '10px 15px',
        },
        '& .country:hover, & .country.highlight': {
            backgroundColor: 'rgba(102, 51, 153, 0.1)',
        },
    },
}));

const UserProfile = () => {
    const {userId} = useParams();
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [visibleSection, setVisibleSection] = useState('personal');
    const [anchorEl, setAnchorEl] = useState(null);
    const [formState, setFormState] = useState({});
    const [editMode, setEditMode] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState('success');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const [editField, setEditField] = useState(null);
    const formRef = useRef(null);
    const {data: avatarUrl, isLoading: avatarLoading, error: avatarError} = useAvatar(userId);

    useEffect(() => {
        const fetchUserData = async () => {
            if (!userId) {
                console.error('User ID not found.');
                return;
            }

            try {
                const token = localStorage.getItem('TOKEN');
                const response = await axiosInstance.get(`/api/user/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const data = response.data;
                setUserData(data);
                setFormState(data);  // Initialize form state with fetched data
            } catch (error: any) {
                console.error('Failed to fetch user data:', error);
                if (error.response.status === 401) {
                    navigate('/signin');
                }
            }
        };

        fetchUserData();
    }, [userId, navigate]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (formRef.current && !formRef.current.contains(event.target)) {
                setEditMode(false);
            }
        };

        if (editMode) {
            document.addEventListener('mousedown', handleClickOutside);
        } else {
            document.removeEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [editMode]);

    if (!userData) {
        return <Container maxWidth="md"><CircularProgress/></Container>;
    }

    const handleSectionChange = async (section: any) => {
        if (section !== visibleSection) {
            setVisibleSection(section);
            try {
                const token = localStorage.getItem('TOKEN');
                const response = await axiosInstance.get(`/api/user/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                setUserData(response.data);
            } catch (error: any) {
                console.error('Failed to fetch user data:', error);
                if (error.response.status === 401) {
                    navigate('/signin');
                }
            }
        }
    };

    const handleMenuClick = (event: any) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = (action: any) => {
        setAnchorEl(null);
        if (action === 'main') {
            navigate('/main-page');
        } else if (action === 'personal') {
            handleSectionChange('personal');
        } else if (action === 'security') {
            handleSectionChange('security');
        } else if (action === 'logout') {
            handleLogout();
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('TOKEN');
        localStorage.removeItem('REFRESH_TOKEN');
        navigate('/');
    };

    const handleInputChange = (e) => {
        const {name, value} = e.target;
        setFormState({
            ...formState,
            [name]: value,
            id: userId
        });
    };

    const handleSubmit = async (e: any) => {
        e.preventDefault();

        // Check for empty fields that previously had values
        const hasEmptyFields = Object.keys(formState).some(
            key => userData[key] && !formState[key]
        );

        if (hasEmptyFields) {
            setSnackbarMessage('Previous information that we already saved, cannot be saved to empty!');
            setSnackbarSeverity('error');
            setSnackbarOpen(true);
            return;
        }

        const isChanged = Object.keys(formState).some(key => formState[key] !== userData[key]);

        if (!isChanged) {
            setSnackbarMessage('No changes have been made');
            setSnackbarSeverity('error');
            setSnackbarOpen(true);
            return;
        }

        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.patch('/api/update/profile', formState, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            setUserData(prevState => ({
                ...prevState,
                ...response.data
            }));
            setEditMode(false);
            setSnackbarMessage('Changes saved successfully!');
            setSnackbarSeverity('success');
            setSnackbarOpen(true);
        } catch (error: any) {
            console.error('Failed to update user data:', error);
            if (error.response.status === 401) {
                navigate('/signin');
            } else if (error.response && error.response.data) {
                setSnackbarMessage(error.response.data.message);
                setSnackbarSeverity('error');
                setSnackbarOpen(true);
            }
        }
    };

    const enableEditMode = () => {
        setEditMode(true);
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const handleAvatarClick = () => {
        setDialogOpen(true);
    };

    const handleDialogClose = () => {
        setDialogOpen(false);
    };

    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleAvatarUpload = async () => {
        if (!selectedFile) return;

        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('userId', userId);

        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.post('/api/upload-avatar', formData, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'multipart/form-data',
                },
            });
            setUserData(prevState => ({
                ...prevState,
                avatar: response.data.avatar
            }));
            setSnackbarMessage('Avatar uploaded successfully!');
            setSnackbarSeverity('success');
            setSnackbarOpen(true);
        } catch (error: any) {
            console.error('Failed to upload avatar:', error);
            if (error.response && error.response.status === 417) {
                setSnackbarMessage('File size exceeds the maximum allowed limit!');
            } else if (error.response && error.response.data) {
                setSnackbarMessage(error.response.data.message);
            } else {
                setSnackbarMessage('Failed to upload avatar');
            }
            setSnackbarSeverity('error');
            setSnackbarOpen(true);
        } finally {
            setDialogOpen(false);
            setSelectedFile(null);
        }
    };

    const handleEditField = (field: any) => {
        setEditField(field);
        setDialogOpen(true);
    };

    const handleFieldUpdate = async () => {
        let endpoint;
        let payload = {id: userId};

        switch (editField) {
            case 'email':
                endpoint = '/api/update/email';
                payload.newEmail = formState.newEmail;
                payload.email = userData.email;
                break;
            case 'password':
                endpoint = '/api/update/password';
                payload.password = formState.password;
                payload.newPassword = formState.newPassword;
                payload.confirmNewPassword = formState.confirmNewPassword;
                break;
            case 'phoneNumber':
                endpoint = '/api/update/number';
                payload.newPhoneNumber = formState.newPhoneNumber.startsWith('+') ? formState.newPhoneNumber : `+${formState.newPhoneNumber}`;
                payload.phoneNumber = formState.phoneNumber;
                break;
            default:
                return;
        }

        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.put(endpoint, payload, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            setUserData(prevState => ({
                ...prevState,
                ...response.data
            }));
            setFormState(prevState => ({
                ...prevState,
                phoneNumber: response.data.phoneNumber,
                newPhoneNumber: ''
            }));
            setSnackbarMessage(`${editField.charAt(0).toUpperCase() + editField.slice(1)} updated successfully!`);
            setSnackbarSeverity('success');
            setSnackbarOpen(true);
        } catch (error: any) {
            console.error(`Failed to update ${editField}:`, error);
            if (error.response && error.response.data) {
                setSnackbarMessage(error.response.data.message);
            } else {
                setSnackbarMessage(`Failed to update ${editField}`);
            }
            setSnackbarSeverity('error');
            setSnackbarOpen(true);
        } finally {
            setDialogOpen(false);
            setEditField(null);
        }
    };

    return (
        <Container maxWidth="md" className="user-profile" sx={{paddingTop: '20px'}}>
            <Card sx={{
                padding: '20px',
                boxShadow: 3,
                borderRadius: '15px',
                backgroundColor: '#f5f5f5',
                marginBottom: '20px'
            }}>
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Box display="flex" alignItems="center">
                        <Avatar
                            alt={userData.nameTag}
                            sx={{bgcolor: '#663399', width: 80, height: 80, marginRight: '20px', cursor: 'pointer'}}
                            onClick={handleAvatarClick}
                            src={avatarUrl}
                            imgProps={{
                                onError: (e) => {
                                    e.target.src = '';
                                }
                            }}
                        >
                            {!userData.avatar && userData.nameTag?.charAt(0).toUpperCase()}
                        </Avatar>
                        <Typography variant="h4" sx={{fontWeight: 'bold'}}>
                            Welcome, {userData.nameTag}!
                        </Typography>
                    </Box>
                    <IconButton
                        onClick={handleMenuClick}
                        sx={{
                            color: 'var(--primary-color)',
                            '&:hover': {
                                color: '#552286',
                            }
                        }}
                    >
                        <SettingsIcon/>
                    </IconButton>
                    <Menu
                        anchorEl={anchorEl}
                        open={Boolean(anchorEl)}
                        onClose={() => handleMenuClose(null)}
                    >
                        <MenuItem
                            onClick={() => handleMenuClose('main')}
                            sx={{
                                '&:hover': {
                                    backgroundColor: 'var(--hover-color)',
                                    color: 'white',
                                }
                            }}
                        >
                            <HomeIcon sx={{marginRight: '10px'}}/> Main Page
                        </MenuItem>
                        <MenuItem
                            onClick={() => handleMenuClose('personal')}
                            sx={{
                                '&:hover': {
                                    backgroundColor: 'var(--hover-color)',
                                    color: 'white',
                                }
                            }}
                        >
                            <InfoIcon sx={{marginRight: '10px'}}/> Personal Information
                        </MenuItem>
                        <MenuItem
                            onClick={() => handleMenuClose('security')}
                            sx={{
                                '&:hover': {
                                    backgroundColor: 'var(--hover-color)',
                                    color: 'white',
                                }
                            }}
                        >
                            <SecurityIcon sx={{marginRight: '10px'}}/> Security Data
                        </MenuItem>
                        <MenuItem
                            onClick={() => handleMenuClose('logout')}
                            sx={{
                                '&:hover': {
                                    backgroundColor: 'var(--hover-color)',
                                    color: 'white',
                                }
                            }}
                        >
                            <LogoutIcon sx={{marginRight: '10px'}}/> Logout
                        </MenuItem>
                    </Menu>
                </Box>
            </Card>
            <Box>
                <Card variant="outlined" sx={{
                    marginBottom: '20px',
                    padding: '20px',
                    boxShadow: 3,
                    borderRadius: '15px',
                    backgroundColor: '#ffffff'
                }}>
                    <CardContent>
                        <Box className={`user-details ${visibleSection === 'personal' ? 'visible' : 'hidden'}`}>
                            <Typography variant="h5" sx={{marginBottom: '10px'}}>Your Personal Information</Typography>
                            <form onSubmit={handleSubmit} ref={formRef}>
                                {editMode ? (
                                    <>
                                        <CustomTextField
                                            label="Name Tag"
                                            name="nameTag"
                                            value={formState.nameTag || ''}
                                            onChange={handleInputChange}
                                            fullWidth
                                            margin="normal"
                                            sx={{marginBottom: '10px'}}
                                        />
                                        <CustomTextField
                                            label="First Name"
                                            name="firstName"
                                            value={formState.firstName || ''}
                                            onChange={handleInputChange}
                                            fullWidth
                                            margin="normal"
                                            sx={{marginBottom: '10px'}}
                                        />
                                        <CustomTextField
                                            label="Last Name"
                                            name="lastName"
                                            value={formState.lastName || ''}
                                            onChange={handleInputChange}
                                            fullWidth
                                            margin="normal"
                                            sx={{marginBottom: '10px'}}
                                        />
                                        <CustomTextField
                                            label="Country of Birth"
                                            name="countryOfBirth"
                                            value={formState.countryOfBirth || ''}
                                            onChange={handleInputChange}
                                            fullWidth
                                            margin="normal"
                                            sx={{marginBottom: '10px'}}
                                        />
                                        <CustomTextField
                                            label="Town of Birth"
                                            name="townOfBirth"
                                            value={formState.townOfBirth || ''}
                                            onChange={handleInputChange}
                                            fullWidth
                                            margin="normal"
                                            sx={{marginBottom: '10px'}}
                                        />
                                        <CustomTextField
                                            label="Address"
                                            name="address"
                                            value={formState.address || ''}
                                            onChange={handleInputChange}
                                            fullWidth
                                            margin="normal"
                                            sx={{marginBottom: '10px'}}
                                        />
                                        <Box display="flex" justifyContent="flex-end" mt={2}>
                                            <CustomButton
                                                type="submit"
                                                variant="contained"
                                                startIcon={<SaveIcon/>}
                                                sx={{
                                                    marginRight: '1rem',
                                                }}
                                            >
                                                Save Changes
                                            </CustomButton>
                                            <CustomButton
                                                onClick={() => setEditMode(false)}
                                                variant="contained"
                                                startIcon={<CancelIcon/>}
                                            >
                                                Cancel
                                            </CustomButton>
                                        </Box>
                                    </>
                                ) : (
                                    <Box onClick={enableEditMode} sx={{cursor: 'pointer'}}>
                                        <Typography sx={{marginBottom: '10px'}}>Name
                                            Tag: {userData.nameTag}</Typography>
                                        <Typography sx={{marginBottom: '10px'}}>First
                                            Name: {userData.firstName}</Typography>
                                        <Typography sx={{marginBottom: '10px'}}>Last
                                            Name: {userData.lastName}</Typography>
                                        <Typography sx={{marginBottom: '10px'}}>Country of
                                            Birth: {userData.countryOfBirth}</Typography>
                                        <Typography sx={{marginBottom: '10px'}}>Town of
                                            Birth: {userData.townOfBirth}</Typography>
                                        <Typography sx={{marginBottom: '10px'}}>Address: {userData.address}</Typography>
                                        <CustomButton
                                            startIcon={<EditIcon/>}
                                            sx={{
                                                marginTop: '10px',
                                            }}
                                        >
                                            Edit
                                        </CustomButton>
                                    </Box>
                                )}
                            </form>
                        </Box>
                    </CardContent>
                </Card>
                <Card variant="outlined" sx={{
                    marginBottom: '20px',
                    padding: '20px',
                    boxShadow: 3,
                    borderRadius: '15px',
                    backgroundColor: '#ffffff'
                }}>
                    <CardContent>
                        <Box className={`user-details ${visibleSection === 'security' ? 'visible' : 'hidden'}`}>
                            <Typography variant="h5" sx={{marginBottom: '10px'}}>Your Security Data</Typography>
                            <Typography sx={{marginBottom: '10px'}}>Username: {userData.username}</Typography>
                            <Box display="flex" alignItems="center">
                                <Typography sx={{marginBottom: '10px'}}>Password: ******</Typography>
                                <EditButton onClick={() => handleEditField('password')}><EditIcon/></EditButton>
                            </Box>
                            <Box display="flex" alignItems="center">
                                <Typography sx={{marginBottom: '10px'}}>Email: {userData.email}</Typography>
                                <EditButton onClick={() => handleEditField('email')}><EditIcon/></EditButton>
                            </Box>
                            <Box display="flex" alignItems="center">
                                <Typography sx={{marginBottom: '10px'}}>Phone
                                    Number: {userData.phoneNumber}</Typography>
                                <EditButton onClick={() => handleEditField('phoneNumber')}><EditIcon/></EditButton>
                            </Box>
                            <Box display="flex" alignItems="center" sx={{marginBottom: '10px'}}>
                                <Typography variant="body1" sx={{marginRight: '10px'}}>Two-Factor
                                    Authentication</Typography>
                                <TwoFactorAuthentication
                                    userId={userId}
                                    email={userData.email} // Pass email prop
                                    twoFactorAuthentication={userData.twoFactorAuthentication}
                                />
                            </Box>
                        </Box>
                    </CardContent>
                </Card>
            </Box>
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={2000}
                onClose={handleSnackbarClose}
                anchorOrigin={{vertical: 'top', horizontal: 'center'}}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{width: '100%'}}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
            <CustomDialog open={dialogOpen} onClose={handleDialogClose}>
                <DialogTitle>
                    {editField ? (
                        <Typography variant="h6" align="center" gutterBottom>
                            {editField === 'phoneNumber' ? 'Edit Phone Number' : `Edit ${editField.charAt(0).toUpperCase() + editField.slice(1)}`}
                        </Typography>
                    ) : null}
                </DialogTitle>
                <DialogContent>
                    {editField ? (
                        <Box display="flex" flexDirection="column" alignItems="center">
                            {editField === 'password' ? (
                                <>
                                    <CustomTextField
                                        id="edit-password"
                                        type="password"
                                        name="password"
                                        label="Current Password"
                                        fullWidth
                                        margin="normal"
                                        onChange={handleInputChange}
                                    />
                                    <CustomTextField
                                        id="edit-new-password"
                                        type="password"
                                        name="newPassword"
                                        label="New Password"
                                        fullWidth
                                        margin="normal"
                                        onChange={handleInputChange}
                                    />
                                    <CustomTextField
                                        id="edit-confirm-new-password"
                                        type="password"
                                        name="confirmNewPassword"
                                        label="Confirm New Password"
                                        fullWidth
                                        margin="normal"
                                        onChange={handleInputChange}
                                    />
                                </>
                            ) : editField === 'phoneNumber' ? (
                                <>
                                    {userData.phoneNumber && (
                                        <>
                                            <InputLabel>Current Phone Number</InputLabel>
                                            <CustomPhoneInput
                                                id="current-phone-number"
                                                name="phoneNumber"
                                                value={formState.phoneNumber || ''}
                                                onChange={(value) => setFormState((prevState) => ({
                                                    ...prevState,
                                                    phoneNumber: value
                                                }))}
                                                inputProps={{
                                                    name: 'phone',
                                                    required: true,
                                                    autoFocus: true,
                                                }}
                                                containerStyle={{marginBottom: '10px'}}
                                                inputStyle={{width: '100%'}}
                                            />
                                        </>
                                    )}
                                    <InputLabel>New Phone Number</InputLabel>
                                    <CustomPhoneInput
                                        id="new-phone-number"
                                        name="newPhoneNumber"
                                        value={formState.newPhoneNumber || ''}
                                        onChange={(value) => setFormState((prevState) => ({
                                            ...prevState,
                                            newPhoneNumber: value
                                        }))}
                                        inputProps={{
                                            name: 'newPhoneNumber',
                                            required: true,
                                        }}
                                        containerStyle={{marginBottom: '10px'}}
                                        inputStyle={{width: '100%'}}
                                    />
                                </>
                            ) : (
                                <CustomTextField
                                    id={`edit-${editField}`}
                                    type="text"
                                    name={`new${editField.charAt(0).toUpperCase() + editField.slice(1)}`}
                                    label={`New ${editField.charAt(0).toUpperCase() + editField.slice(1)}`}
                                    fullWidth
                                    margin="normal"
                                    onChange={handleInputChange}
                                />
                            )}
                        </Box>
                    ) : (
                        <Box display="flex" flexDirection="column" alignItems="center">
                            <InputLabel htmlFor="avatar-upload" sx={{marginBottom: '10px'}}>Choose a file</InputLabel>
                            <Input
                                id="avatar-upload"
                                type="file"
                                onChange={handleFileChange}
                                fullWidth
                            />
                        </Box>
                    )}
                </DialogContent>
                <DialogActions>
                    <CustomButton onClick={handleDialogClose} color="primary">
                        Cancel
                    </CustomButton>
                    {editField ? (
                        <CustomButton onClick={handleFieldUpdate} color="primary" variant="contained">
                            Save
                        </CustomButton>
                    ) : (
                        <CustomButton onClick={handleAvatarUpload} color="primary" variant="contained">
                            Upload
                        </CustomButton>
                    )}
                </DialogActions>
            </CustomDialog>
        </Container>
    );
};

export default UserProfile;
