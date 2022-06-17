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


const Generator = ({
}) => {

  const [rules, setRules] = useState([]);
  const [ruleCount, setRuleCount] = useState([]);
  const [rulesLoaded , setRulesLoaded] = useState(false);

  

  useEffect(() => {
    ApiService.countRules().then((res) => setRuleCount(res.data))
  },[])

  useEffect(() => {

    let tempRules = [...rules];
    let ruleCounter = 0;

    for(let i = 1; i <= ruleCount; i++){
      ApiService.downloadRule(
        "rule_" + i + ".0.json",
        {
          onDownloadProgress: (progressEvent) => {
            let completed = Math.round(
              (progressEvent.loaded / progressEvent.total) * 100
            );
          },
        }
      ).then((res) => {
        tempRules.push((res.data))
        console.log(JSON.stringify(res.data))
      }).then(() => {
        setRules(tempRules);
        ruleCounter++;
      }).then(() => {
        if(ruleCounter == ruleCount){
          console.log("They are the same")
          setRulesLoaded(true);
        }
      })
    }


    
  }, [ruleCount])

  const classes = useStyles();

  console.log(rules);

  return (
    <Container style={{ width: "90vw", maxWidth: "85vw" }}>
      <div className={classes.toolbar} />
      <Typography inline className={classes.title} variant="h2">
        Rule Manager 
      </Typography>
      <Typography style={{fontSize: "30px"}} inline><Button className={classes.button}><div style={{ transform: "translateY(2px)" }}>Create New Rule</div></Button></Typography>
      <br />
      <br />

     { rulesLoaded ? 
     <>
      {rules.sort((a , b) => (a.rId > b.rId) ? 1 : -1).map((rule) => (
        <RuleObject rule={rule} />
      ))}
      </>

      :

      <></>}

      

    </Container>
    
  );
};

export default Generator;
