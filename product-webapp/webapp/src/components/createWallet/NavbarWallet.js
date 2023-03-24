import React, {useState} from 'react'
import * as FaIcons from "react-icons/fa";
import * as AiIcons from "react-icons/ai";
import { Link } from 'react-router-dom';
import { NavbarWalletOption } from './NavbarWalletOption';
import './NavbarWallet.css';
import { IconContext } from 'react-icons';
import Logo from "../../assets/logo.png";
import PowerSettingsNewIcon from '@mui/icons-material/PowerSettingsNew';


function NavbarWallet() {
  const[sidebar, setSidebar] = useState(false)

  const showSidebar = () => setSidebar(!sidebar)

  return (
    <>
    <IconContext.Provider value={{color: '#fff'}}>
        <div className='navbar'>
            <Link to="#" className='menu-bars'>
                <FaIcons.FaBars onClick={showSidebar}/>
            </Link>
            <div className='logontitle'>
                <img className = 'logo' src={Logo} height="40vh" width="40vh" />
                <h2 className='title'>CREDWIZ</h2>
            </div>
            <PowerSettingsNewIcon/>
        </div>
        <nav className={sidebar ? 'nav-menu active' :'nav-menu'}>
            <ul className='nav-menu-items' onClick={showSidebar}>
                <li className='navbar-toggle'>
                    <Link to="#" className='menu-bars'>
                        <AiIcons.AiOutlineClose />
                    </Link>
                </li>
                {NavbarWalletOption.map((item, index) => {
                    return (
                        <li key = {index} className={item.cName}>
                            <Link to ={item.path}>
                                {item.icon}
                                <span>{item.title}</span>
                            </Link>
                        </li>
                        
                    )
                })}
            </ul>
       </nav>
       </IconContext.Provider>
    </>
  )
}

export default NavbarWallet