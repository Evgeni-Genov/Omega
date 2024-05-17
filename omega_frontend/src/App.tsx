import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Home from './assets/Components/Home/Home.tsx'
import Login from './assets/Components/Login/Login.tsx'
import UserList from './assets/Components/UserList.tsx'
import Registration from './assets/Components/Registration/Registration.tsx'
import UserProfile from './assets/Components/Login/UserProfile.tsx'
import './App.css'
import {useState} from 'react'

function App() {
    const [loggedIn, setLoggedIn] = useState(false)
    const [email, setEmail] = useState('')

    return (
        <div className="App">
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Home email={email} loggedIn={loggedIn} setLoggedIn={setLoggedIn}/>}/>
                    <Route path="/login" element={<Login setLoggedIn={setLoggedIn} setEmail={setEmail}/>}/>
                    <Route path="/registration" element={<Registration/>}/>
                    <Route path="/user-profile" element={<UserProfile/>}/>
                    <Route path="/users" element={<UserList/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default App