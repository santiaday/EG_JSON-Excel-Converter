import React, { useState, useCallback, useEffect } from 'react'
import useStyles from './settingsStyles';
import { Container, FormControl, Typography, Select, MenuItem, FormHelperText } from '@material-ui/core';
import { FilePicker } from 'react-file-picker';
import Dropzone, { useDropzone } from "react-dropzone";
import { Link, useNavigate } from 'react-router-dom';

const Settings = ({ settings , setSettings, originFile, targetFile, multipleFileOutput, setOriginFile, setTargetFile, setMultipleFileOutput }) => {

  const navigate = useNavigate();



  const handleNavigate = () => {
    navigate('/' , {state: {settings: settings , originFile: originFile , targetFile: targetFile, multipleFileOutput: multipleFileOutput}})
  }

  const classes = useStyles();
  useEffect(() => {
    if(originFile == ""){
    setOriginFile([".xlsx" , ".xls"])
    }

    if(targetFile === ""){
    setTargetFile(".json")
    }

    if(multipleFileOutput === ""){
    setMultipleFileOutput("0")
    }

  }, [])

  useEffect(() => {
    setSettings([{originFile: originFile},
      {targetFile: targetFile},
      {multipleFileOutput: multipleFileOutput}])

      console.log("SETTINGS RERENDERED")

  }, [originFile, targetFile, multipleFileOutput])

  return (
    <Container style={{width:"90vw", maxWidth:"85vw"}}>
        <div className={classes.toolbar} />
        <FormControl variant="outlined" fullwidth >
            <Typography className={classes.title} variant="h2">Settings</Typography>
            <br />
            <br />
            <br />
            <Typography className={classes.subtitle} variant="h4">Original File Format </Typography>
            <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={originFile}
                onChange={(e) => {setOriginFile(e.target.value.split(","))}}
                >
          <MenuItem value={".xlsx,.xls"}>.xlsx/.xls</MenuItem>
          <MenuItem value={".json"}>.json</MenuItem>
          </Select>
          <FormHelperText style={{marginLeft:"0"}}>Default: .xlsx</FormHelperText>
          </FormControl>
            <br />
            <br />
            <FormControl variant="outlined" fullwidth >
            <Typography className={classes.subtitle} variant="h4">Target File Format </Typography>
            <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={targetFile}
                onChange={(e) => {{setTargetFile(e.target.value)}}}
                >
          <MenuItem value={".xlsx,.xls"}>.xlsx/.xls</MenuItem>
          <MenuItem value={".json"}>.json</MenuItem>
          </Select>
          <FormHelperText style={{marginLeft:"0"}}>Default: JSON</FormHelperText>
            </FormControl>
            <br />
            <br />
            <FormControl variant="outlined" fullwidth >
            <Typography className={classes.subtitle} variant="h4">Create JSON File For Every Row? </Typography>
            <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={multipleFileOutput}
                onChange={(e) => {{setMultipleFileOutput(e.target.value)}}}
                >
          <MenuItem value={"0"}>No</MenuItem>
          <MenuItem value={"1"}>Yes</MenuItem>
          </Select>
          <FormHelperText style={{marginLeft:"0"}}>Default: No</FormHelperText>
          </FormControl>
            <br />
            <br />
            <br />
            <div className={classes.link} style={{marginLeft: "0"}}><Typography variant="h4" style={{width: "15%"}} onClick={handleNavigate}>Back</Typography></div>
        
    </Container>
  )
}

export default Settings