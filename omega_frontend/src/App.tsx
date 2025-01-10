import {BrowserRouter, Route, Routes} from 'react-router-dom';
import Home from './assets/Main/Home/Home.tsx';
import UserProfile from './assets/Main/Profile/UserProfile.tsx';
import './App.css';
import SignIn from "./assets/Main/Login/SignIn.tsx";
import SignUp from "./assets/Main/Registration/SignUp.tsx";
import ResetPassword from "./assets/Main/Login/ResetPassword.tsx";
import {useState} from "react";
import MainPage from "./assets/Main/Main/MainPage.tsx";

function App() {
    const [loggedIn, setLoggedIn] = useState<boolean>(false);
    const [email] = useState<string>('');

    return (
        <div className="App">
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home email={email} loggedIn={loggedIn} setLoggedIn={setLoggedIn}/>}/>
                    <Route path="/user-profile/:userId" element={<UserProfile/>}/>
                    <Route path="/main-page" element={<MainPage/>}/>
                    <Route path="/signin" element={<SignIn/>}/>
                    <Route path="/signup" element={<SignUp/>}/>
                    <Route path="/reset-password/:token" element={<ResetPassword/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;