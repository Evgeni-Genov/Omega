import React from 'react'
import {useNavigate} from 'react-router-dom'
import './Home.css'

const Home = (props) => {
    const {loggedIn, email} = props
    const navigate = useNavigate()

    const onSignInClick = () => {
        navigate("/login")
    }

    const onSignUpClick = () => {
        navigate("/registration")
    }

    return (
        <div className="mainContainer">
            <div className={'titleContainer'}>
                Welcome to Omega!
            </div>

            <input
                className={'inputButton'}
                type="button"
                onClick={onSignInClick}
                value={loggedIn ? 'Log out' : 'Log in'}
            />
            {loggedIn ? <div>Your email address is {email}</div> : <div/>}

            <input
                className={'smallButton'}
                type="button"
                onClick={onSignUpClick}
                value="Sign up"
            />
        </div>
    )
}

export default Home