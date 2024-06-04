import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Home from './assets/Components/Home/Home.tsx'
import UserList from './assets/Components/UserList.tsx'

import UserProfile from './assets/Components/Login/UserProfile.tsx'
import './App.css'
import {useState} from "react";
import SignIn from "./assets/Components/Login/SignIn.tsx";
import SignUp from "./assets/Components/Registration/SignUp.tsx";
import ResetPassword from "./assets/Components/Login/ResetPassword.tsx";
import MainPage from "./assets/Components/MainPage.tsx";

function App() {
    const [loggedIn, setLoggedIn] = useState(false)
    const [email, setEmail] = useState('')

    return (
        <div className="App">
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home email={email} loggedIn={loggedIn} setLoggedIn={setLoggedIn}/>}/>
                    <Route path="/user-profile/:userId" element={<UserProfile/>}/>
                    <Route path="/main-page" element={<MainPage/>}/>
                    <Route path="/users" element={<UserList/>}/>
                    <Route path="/signin" element={<SignIn/>}/>
                    <Route path="/signup" element={<SignUp/>}/>
                    <Route path="/reset-password/:token" element={<ResetPassword/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default App