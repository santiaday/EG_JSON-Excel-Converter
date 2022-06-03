import React from 'react'
import { AppBar, Toolbar, IconButton, Badge, MenuItem, Menu, Typography, Grid } from '@material-ui/core';
import useStyles from './headerStyles';

const Header = () => {

  const classes = useStyles();

  return (
    <AppBar style={{ height: '164px'}} position="fixed" className={classes.appBar} color="#000099" >

        <Typography inline>
            <img src={'expedia-group-logo-white.svg'} alt="EG_Logo_White" className={classes.logo}/>
        </Typography>

    </AppBar>
  )
}

export default Header