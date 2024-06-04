import React, {useEffect, useRef, useState} from 'react';
import axiosInstance from '../../../AxiosConfiguration.ts';
import {useNavigate, useParams} from 'react-router-dom';
import {
    Box,
    Button,
    CircularProgress,
    Container,
    IconButton,
    Menu,
    MenuItem,
    TextField,
    Typography
} from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import './UserProfile.css';
import TwoFactorAuthentication from "./TwoFactorAuthentication.tsx";

const UserProfile = () => {
    const {userId} = useParams();
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [visibleSection, setVisibleSection] = useState('personal');
    const [anchorEl, setAnchorEl] = useState(null);
    const [formState, setFormState] = useState({});
    const [editMode, setEditMode] = useState(false);
    const formRef = useRef(null);

    useEffect(() => {
        const fetchUserData = async () => {
            if (!userId) {
                console.error('User ID not found.');
                return;
            }

            try {
                const token = localStorage.getItem('TOKEN');
                const response = await axiosInstance.get(`/user/user/${userId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const data = response.data;
                setUserData(data);
                setFormState(data);  // Initialize form state with fetched data
            } catch (error) {
                console.error('Failed to fetch user data:', error);
            }
        };

        fetchUserData();
    }, [userId]);

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

    const handleSectionChange = (section) => {
        setVisibleSection(section);
    };

    const handleMenuClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = (action) => {
        setAnchorEl(null);
        if (action === 'main') {
            navigate('/main-page'); // Navigate to the main page
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('TOKEN');
            const response = await axiosInstance.patch('/user/update/profile', formState, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            setUserData(response.data);
            setEditMode(false);
        } catch (error) {
            console.error('Failed to update user data:', error);
        }
    };

    const enableEditMode = () => {
        setEditMode(true);
    };

    const textFieldStyles = {
        '& label.Mui-focused': {
            color: 'var(--primary-color)',
        },
        '& .MuiInput-underline:after': {
            borderBottomColor: 'var(--primary-color)',
        },
        '& .MuiOutlinedInput-root': {
            '& fieldset': {
                borderColor: 'var(--primary-color)',
            },
            '&:hover fieldset': {
                borderColor: 'var(--primary-color)',
            },
            '&.Mui-focused fieldset': {
                borderColor: 'var(--primary-color)',
            },
        },
    };

    return (
        <Container maxWidth="md" className="user-profile">
            <Box display="flex" justifyContent="space-between" alignItems="center" mt={2} mb={2}>
                <Box display="flex" justifyContent="center" alignItems="center" width="100%">
                    <Typography variant="h4" align="center">
                        Welcome, {userData.nameTag}!
                    </Typography>
                </Box>
                <IconButton
                    onClick={handleMenuClick}
                    sx={{
                        color: 'var(--primary-color)',
                        '&:hover': {
                            color: 'var(--hover-color)',
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
                        Main Page
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
                        Personal Information
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
                        Security Data
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
                        Logout
                    </MenuItem>
                </Menu>
            </Box>
            <Box className={`user-details ${visibleSection === 'personal' ? 'visible' : 'hidden'}`}>
                <Typography variant="h5">Your Personal Information</Typography>
                <form onSubmit={handleSubmit} ref={formRef}>
                    {editMode ? (
                        <>
                            <TextField
                                label="Name Tag"
                                name="nameTag"
                                value={formState.nameTag || ''}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                                sx={textFieldStyles}
                            />
                            <TextField
                                label="First Name"
                                name="firstName"
                                value={formState.firstName || ''}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                                sx={textFieldStyles}
                            />
                            <TextField
                                label="Last Name"
                                name="lastName"
                                value={formState.lastName || ''}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                                sx={textFieldStyles}
                            />
                            <TextField
                                label="Country of Birth"
                                name="countryOfBirth"
                                value={formState.countryOfBirth || ''}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                                sx={textFieldStyles}
                            />
                            <TextField
                                label="Town of Birth"
                                name="townOfBirth"
                                value={formState.townOfBirth || ''}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                                sx={textFieldStyles}
                            />
                            <TextField
                                label="Address"
                                name="address"
                                value={formState.address || ''}
                                onChange={handleInputChange}
                                fullWidth
                                margin="normal"
                                sx={textFieldStyles}
                            />
                            <Button
                                type="submit"
                                variant="contained"
                                sx={{
                                    backgroundColor: 'var(--primary-color)',
                                    color: 'white',
                                    '&:hover': {
                                        backgroundColor: 'var(--hover-color)',
                                    },
                                    marginRight: '1rem',
                                }}
                            >
                                Save Changes
                            </Button>
                            <Button
                                onClick={() => setEditMode(false)}
                                variant="contained"
                                sx={{
                                    backgroundColor: 'var(--primary-color)',
                                    color: 'white',
                                    '&:hover': {
                                        backgroundColor: 'var(--hover-color)',
                                    },
                                }}
                            >
                                Cancel
                            </Button>
                        </>
                    ) : (
                        <Box onClick={enableEditMode} sx={{cursor: 'pointer'}}>
                            <Typography>Name Tag: {userData.nameTag}</Typography>
                            <Typography>First Name: {userData.firstName}</Typography>
                            <Typography>Last Name: {userData.lastName}</Typography>
                            <Typography>Country of Birth: {userData.countryOfBirth}</Typography>
                            <Typography>Town of Birth: {userData.townOfBirth}</Typography>
                            <Typography>Address: {userData.address}</Typography>
                        </Box>
                    )}
                </form>
            </Box>
            <Box className={`user-details ${visibleSection === 'security' ? 'visible' : 'hidden'}`}>
                <Typography variant="h5">Your Security Data</Typography>
                <Typography>Username: {userData.username}</Typography>
                <Typography>Password: ******</Typography>
                <Typography>Email: {userData.email}</Typography>
                <Typography>Phone Number: {userData.phoneNumber}</Typography>
                <Box display="flex" alignItems="center">
                    <Typography variant="body1">Two-Factor Authentication</Typography>
                    <TwoFactorAuthentication userId={userId}
                                             twoFactorAuthentication={userData.twoFactorAuthentication}/>
                </Box>
            </Box>
        </Container>
    );
};

export default UserProfile;
