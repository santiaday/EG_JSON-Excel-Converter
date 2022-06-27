import React, { useState, useCallback, useEffect } from "react";
import useStyles from "./ruleUpdateConfirmationPopupStyles";
import { Link, useNavigate } from "react-router-dom";
import {
  Container,
  FormControl,
  Typography,
  Select,
  MenuItem,
  FormHelperText,
  Button,
} from "@material-ui/core";
import "./styles.css";
import { BsArrowRight } from "react-icons/bs";
import Divider from '@mui/material/Divider';

const RuleUpdateConfirmationPopup = ({
rules, ruleTitle, newRule }) => {

  const[existingRuleIndex , setExistingRuleIndex] = useState(-1)

  useEffect(() => {
    rules.map((rule) => {
      console.log(Object.keys(rule)[0])
      console.log(ruleTitle)
      let counter = 0;
  
      if(Object.keys(rule)[0] === ruleTitle){
        setExistingRuleIndex(counter)
      }else{
        counter++
      }
    })
  }, [])

  

  var JSONPretty = require('react-json-pretty');
  const navigate = useNavigate();

  const handleNavigate = () => {
    navigate("/");
  };

  function intersperse(arr, sep) {
    if (arr.length === 0) {
      return [];
    }

    return arr.slice(1).reduce(
      function(xs, x, i) {
        return xs.concat([sep, x]);
      },
      [arr[0]]
    );
  }

  const classes = useStyles();

  return (
    <Container style={{ width: "90vw", maxWidth: "85vw" }}>
      <div className={classes.toolbar} style={{ marginBottom: "30px" }} />
      <div className="popup-box">
        <div className="box">
        <br className={"unselectable"} />
          <Typography variant="h3" style={{ marginBottom: "20px" }}>
            Confirm Rule Update?
          </Typography>
          <Typography variant="h5" style={{fontWeight: "500"}}>
            A rule with the name "{ruleTitle}" already exists. Would you like to update the rule storage system or just <br />download the created rule (no changes to rule storage system)?
          </Typography>
          <br className={"unselectable"} />
          <div style={{width: "100%" , minHeight: "1300px"}}>
          <div style={{float: "left"}} >
          <Typography variant="h5" style={{ textDecoration: "underline" , textAlign: "left"}}>
            Existing Rule:
          </Typography>
          <JSONPretty style={{textAlign: "left", width: "50%"}} id="json-pretty" data={JSON.stringify(rules[existingRuleIndex])}></JSONPretty>
          </div>
          <div>
          <Typography variant="h5" style={{ textDecoration: "underline" , textAlign: "left"}}>
            Updated Rule:
          </Typography>
          <JSONPretty style={{textAlign: "left", width: "50%"}} id="json-pretty" data={JSON.stringify(newRule)}></JSONPretty>
          </div>
          </div>
          <br className={"unselectable"} />

          <Button className={classes.button}>
            <span style={{ transform: "translateY(2px)" }}>Save Changes</span>
          </Button>
          <Button className={classes.altButton}>
            <span style={{ transform: "translateY(2px)" }}>Revert Changes</span>
          </Button>
          
        </div>

      </div>
    </Container>
  );
}

export default RuleUpdateConfirmationPopup;
