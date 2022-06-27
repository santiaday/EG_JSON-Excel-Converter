import React, { useState, useCallback, useEffect , Component} from "react";
import useStyles from "./generatorStyles";
import { Container, Typography, Button } from "@material-ui/core";
import { FilePicker } from "react-file-picker";
import Dropzone, { useDropzone } from "react-dropzone";
import { Link, useNavigate } from "react-router-dom";
import ApiService from "../../http-common";
import RuleObject from "./RuleObject/RuleObject";
import { IoArrowDownOutline } from "react-icons/io5"
import { IoMdAddCircle } from "react-icons/io"



const Generator = ({ rules, ruleNames, rulesLoaded, ruleCount
}) => {

  const navigate = useNavigate();

  String.prototype.replaceAt = function(index, replacement) {
    return this.substring(0, index) + replacement + this.substring(index + replacement.length);
}



  const classes = useStyles();

  console.log(rules);

  const handleNavigateToGenerator = () => {
    navigate("/generator/generate-rule" , {state:{ruleNames:ruleNames , rules: rules}})
  }

  const handleEditRule = (rule) => {
    navigate("/generator/edit-rule" , {state: {rule: rule}})
  }

  return (
    <Container style={{ width: "90vw", maxWidth: "85vw" }}>
      <div className={classes.toolbar} />
      <Typography inline className={classes.title} variant="h2">
        Rule Manager 
      </Typography>
      <Typography inline variant="h5">
        Don't see your recently added rule? Try refreshing the page.
      </Typography>
      <br/>
      <Typography style={{fontSize: "30px"}} inline><Button className={classes.button} onClick={handleNavigateToGenerator}><div style={{ transform: "translateY(2px)" }}>Create New Rule</div></Button><Button className={classes.buttonGreen} onClick={handleNavigateToGenerator}><div style={{ transform: "translateY(2px)" }}>Edit In Excel</div></Button></Typography>
      <br />
      <br />

     { rulesLoaded ? 
     <>
      {rules.map((rule) => (
        <RuleObject rule={rule} handleEditRule={handleEditRule} />
      ))}
      </>

      :

      <></>}

      

    </Container>
    
  );
};

export default Generator;
