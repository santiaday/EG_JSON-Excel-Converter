import React, { useState, useCallback, useEffect } from 'react'
import useStyles from './UploadedFileStyles';
import { Container, Typography, Button } from '@material-ui/core';
import ProgressBar from "@ramonak/react-progress-bar";

const UploadedFile = ({file, files, handleRemoveFile, handleFileDownload , donwloadPercentage, handleSingleFileUpload, percentages}) => {
    const classes = useStyles();
    

    let index = 0;
    let counter = 0;
    percentages.map((percentFile) => {
      if(percentFile.file == file.key){
        index = counter;
      }

      counter++;
    })

    console.log(percentages);
 
  return (
    <Typography variant="h6" style={{width: "500px", marginTop: "30px"}}>{file.name} 
              
              

              <Button style={{maxWidth: '30px', maxHeight: '20px', minWidth: '30px', minHeight: '20px', 
                              color: "#00099", fontWeight: "900" ,background: "white", transform: "translateY(7px)", 
                                float: "right"}} onClick={handleRemoveFile(file.key)} disableRipple>X</Button>

             {percentages[index].percentage == 100 ?  <Button disableRipple className={classes.button} style={{float: "right", marginRight: "30px", fontWeight: "800",
             fontSize: "13px" , cursor: "pointer", transform:"translateY(-2px)", height:"25px"}} onClick={() => {handleFileDownload(file)}}>
               <Typography variant="h7" style={{transform: "translateY(2px)"}}>Download</Typography></Button>

             : 
             percentages[index].percentage == 0 ? <Button disableRipple className={classes.button} style={{float: "right", marginRight: "30px", 
                                      fontWeight: "800", fontSize: "13px" , cursor: "pointer", transform:"translateY(-2px)", height:"25px"}} 
                                      onClick={() => {handleSingleFileUpload(file.key)}}><Typography variant="h7" style={{transform: "translateY(2px)"}}>Convert</Typography></Button>
             
             : <ProgressBar completed={percentages[index].percentage} customLabel="Converting..."/>}
             </Typography>
  )
}

export default UploadedFile