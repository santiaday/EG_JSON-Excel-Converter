import React, { useState, useCallback, useEffect } from "react";
import useStyles from "./generatorStyles";
import { Container, Typography, Button } from "@material-ui/core";
import { FilePicker } from "react-file-picker";
import Dropzone, { useDropzone } from "react-dropzone";
import { Link, useNavigate } from "react-router-dom";
import ApiService from "../../http-common";


const Generator = ({
}) => {

  const [rules, setRules] = useState([]);
  const [ruleCount, setRuleCount] = useState([]);

  useEffect(() => {
    ApiService.countRules().then((res) => setRuleCount(res.data))
  })

  useEffect(() => {

    let tempRules = [...rules];

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
        tempRules.push(JSON.stringify(res.data))
        console.log(JSON.stringify(res.data));
      })
    }


    setRules(tempRules);
  },)

  const classes = useStyles();

  return (
    <Container style={{ width: "90vw", maxWidth: "85vw" }}>
      <div className={classes.toolbar} />
      <Typography className={classes.title} variant="h2">
        Custom Rule Generator
      </Typography>
      <br />
      <br />
      {rules.map((rule) => (
        <Typography>{rule.toString()}</Typography>
      ))}

    </Container>
    
  );
};

export default Generator;
