import React, { useState, useCallback, useEffect } from "react";
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

  

  // useEffect(() => {
  //   ApiService.countRules().then((res) => {setRuleCount(res.data.length) 
  //                                         setRuleNames(res.data)})
  // },[])

  // useEffect(() => {

  //   console.log(ruleNames)

  //   let tempRules = [...rules];
  //   let ruleCounter = 0;

  //   ruleNames.map((rule) => {
  //     var tempRuleName
  //     if(rule.includes("/")){
  //       tempRuleName = rule.replaceAt(rule.indexOf("/"), "∕")
  //     }else{
  //       tempRuleName = rule
  //     }
      
  //     console.log(tempRuleName)
  //     ApiService.downloadRule(
  //       tempRuleName,
  //       {
  //         onDownloadProgress: (progressEvent) => {
  //           let completed = Math.round(
  //             (progressEvent.loaded / progressEvent.total) * 100
  //           );
  //         },
  //       }
  //     ).then((res) => {
  //       tempRules.push((res.data))
  //       console.log(JSON.stringify(res.data))
  //     }).then(() => {
  //       setRules(tempRules);
  //       ruleCounter++;
  //     }).then(() => {
  //       if(ruleCounter == ruleCount){
  //         setRulesLoaded(true);
  //       }
  //     })
  //   })


    
  // }, [ruleCount])

  const classes = useStyles();

  console.log(rules);

  const handleNavigateToGenerator = () => {
    navigate("/generator/generate-rule" , {state:{ruleNames:ruleNames , rules: rules}})
  }

  return (
    <Container style={{ width: "90vw", maxWidth: "85vw" }}>
      <div className={classes.toolbar} />
      <Typography inline className={classes.title} variant="h2">
        Rule Manager 
      </Typography>
      <Typography style={{fontSize: "30px"}} inline><Button className={classes.button} onClick={handleNavigateToGenerator}><div style={{ transform: "translateY(2px)" }}>Create New Rule</div></Button></Typography>
      <br />
      <br />

     { rulesLoaded ? 
     <>
      {rules.map((rule) => (
        <RuleObject rule={rule} />
      ))}
      </>

      :

      <></>}

      

    </Container>
    
  );
};

export default Generator;
