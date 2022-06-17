import React, { useEffect, useState } from 'react'
import { AppBar, Toolbar, IconButton, Badge, MenuItem, Menu, Typography, Grid } from '@material-ui/core';
import useStyles from './headerStyles';
import { Link, useNavigate } from "react-router-dom";

const Header = () => {

  const classes = useStyles();

  const[page, setPage] = useState(0);

  const navigate = useNavigate();

  const handleNavigateFileConverter = () => {
    if(page != 1){
      navigate("/")
    }
  }

  const handleNavigateRuleGenerator = () => {
    if(page != 2){
      navigate("/generator")
    }
  }

  useEffect(() => {
    if((window.location.pathname) === "/"){
      setPage(1)
    }else if((window.location.pathname) === "/generator"){
      setPage(2)
    }
  })

  return (
    <AppBar style={{ height: '164px'}} className={classes.appBar} color="#000099" >

        <Typography inline style={{maxHeight: "130px"}}>
            <img src={'expedia-group-logo-white.svg'} alt="EG_Logo_White" className={classes.logo}/>
        </Typography>
        <Typography inline style={{display: "inline"}}>

          {page == 1 ? <><span className={classes.headerLinks} style={{textDecoration: "underline 2px", textUnderlineOffset: "5px",}} onClick={handleNavigateFileConverter}>File Converter</span>
          <span className={classes.headerLinks} onClick={handleNavigateRuleGenerator}>Rule Manager</span></>
          
            : <><span className={classes.headerLinks} onClick={handleNavigateFileConverter}>File Converter</span>
          <span className={classes.headerLinks} style={{textDecoration: "underline 2px", textUnderlineOffset: "5px",}} onClick={handleNavigateRuleGenerator}>Rule Manager</span></>}

        </Typography>

    </AppBar>
  )
}

export default Header