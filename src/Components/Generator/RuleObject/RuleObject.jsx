import React, { useState, useCallback, useEffect } from "react";
import { Container, Typography, Button } from "@material-ui/core";
import { FilePicker } from "react-file-picker";
import Dropzone, { useDropzone } from "react-dropzone";
import { Link, useNavigate } from "react-router-dom";
import { IoArrowDownOutline , IoMdAddCircle } from "react-icons/io5"
import useStyles from "./ruleObjectStyles";
import { MdOutlineExpandMore, MdOutlineExpandLess } from 'react-icons/md';

const RuleObject = ({ rule }) => {


  var JSONPretty = require('react-json-pretty');
  const classes = useStyles();

  const [ruleExpanded , setRuleExpanded] = useState(false);

  const expandRule = () => {
    if(!ruleExpanded){
        setRuleExpanded(true)
    }else{
        setRuleExpanded(false)
    }
  }

  return (
    <Container style={{ width: "90vw", maxWidth: "85vw"}}>
    <div style={{marginBottom: "25px"}}>

    {!ruleExpanded ? 
    <Typography  onClick={expandRule} variant="h5" style={{userSelect: "none" , cursor: "pointer", width: "500px"}}>Rule - {rule.metadata.id} <MdOutlineExpandMore style={{width: "20px", transform: "translateY(7px)", marginLeft: "10px"}} className={classes.arrowDown}/></Typography> : 
    <>
    <Typography  onClick={expandRule} variant="h5" style={{userSelect: "none" , cursor: "pointer", width: "500px"}}>Rule - {rule.metadata.id} <MdOutlineExpandLess style={{width: "20px", transform: "translateY(7px)", marginLeft: "10px"}} className={classes.arrowDown}/></Typography>
    </>
    }
    
    


    {!ruleExpanded ? 
    <></> : 
    <>
    
    <JSONPretty id="json-pretty" data={JSON.stringify(rule)}></JSONPretty>
    <Button className={classes.altButton}><div style={{ transform: "translateY(2px)" }}>Edit</div></Button>
    </>
    }
        
        
        </div>
        </Container>
  )
}

export default RuleObject