import React, { useState, useCallback, useEffect } from 'react'
import useStyles from './UploadedFileStyles';
import { Container, Typography, Button } from '@material-ui/core';
import ProgressBar from "@ramonak/react-progress-bar";
import { CircularProgressbar , buildStyles } from 'react-circular-progressbar';
import './styles.css'
import { BsFillArrowUpSquareFill , BsArrowDownSquareFill} from 'react-icons/bs'
import ConfirmUpdatePopup from '../ConfirmUpdatePopup/ConfirmUpdatePopup';

const UploadedFile = ({file, files, handleRemoveFile, handleFileDownload , donwloadPercentage, handleSingleFileUpload, percentages , handleFileUpload, confirmSingleUpdatePopup, setConfirmSingleUpdatePopup}) => {
    const classes = useStyles();
    const [confirmUpdatePopup , setConfirmUpdatePopup ] = useState(0)

    let index = 0;
    let counter = 0;
    percentages.map((percentFile) => {
      if(percentFile.file == file.key){
        index = counter;
      }

      counter++;
    })

    const handlePopup = (fileKey) => {
      setConfirmSingleUpdatePopup(1)
    }
 

    const [windowDimenion, detectHW] = useState({
      winWidth: window.innerWidth,
      winHeight: window.innerHeight,
    })
  
    const detectSize = () => {
      detectHW({
        winWidth: window.innerWidth,
        winHeight: window.innerHeight,
      })
    }
  
    
    useEffect(() => {
      window.addEventListener('resize', detectSize)

  
      return () => {
        window.removeEventListener('resize', detectSize)
      }
    }, [windowDimenion])
 
  return (

    windowDimenion.winWidth > 900 ? 

      <Typography variant="h6" style={{minWidth: "320px", maxWidth: "1000px" , marginTop: "20px", marginBottom: "20px"}}> <Typography variant="h7" style={{marginRight: "20px", display: "inline-block", marginBottom: "10px", maxWidth: "30vw", overflow: "hidden" , textOverflow: "ellipsis"}}>{file.name}</Typography>
              
              

              <Button style={{maxWidth: '30px', maxHeight: '20px', minWidth: '30px', minHeight: '20px', 
                              color: "#00099", fontWeight: "900" ,background: "white",
                                float: "right" ,display: "inline", marginBottom: "10px"}} onClick={handleRemoveFile(file.key)} disableRipple>X</Button>

             {percentages[index].percentage == 100 ?  <Button disableRipple className={classes.altButton} style={{float: "right", marginRight: "30px", fontWeight: "800",
             fontSize: "13px" , cursor: "pointer", transform:"translateY(-4px)", height:"25px" , background: "#57deb7" , border: "1px solid #57deb7"}} onClick={() => {handleFileDownload(file)}}>
               <Typography variant="h7" style={{transform: "translateY(2px)"}}>Download</Typography></Button>

             : 
             percentages[index].percentage == 0 ? <Button disableRipple className={classes.button} style={{float: "right", marginRight: "30px", 
                                      fontWeight: "800", fontSize: "13px" , cursor: "pointer", transform:"translateY(-4px)", height:"25px"}} 
                                      onClick={() => {handlePopup(file.key)}}><Typography variant="h7" style={{transform: "translateY(2px)"}}>Convert</Typography></Button>
             
             : <Typography style={{display: "inline-block", marginRight: "30px", minWidth: "320px" , maxWidth: "450px", float: "right", marginBottom: "20px"}}><ProgressBar style="minWidth: 320px; maxWidth: 450px; display: inline-block;" completed={percentages[index].percentage} customLabel="Converting..." 
                            className="wrapper"
                            barContainerClassName="container"
                            completedClassName="barCompleted"
                            labelClassName="label"/></Typography>}
                            {confirmSingleUpdatePopup == 1 ? <><ConfirmUpdatePopup handleSingleFileUpload={handleSingleFileUpload} fileKey={file.key} multipleFiles={0} /></> : <></>}
             </Typography>
             
      
      : 
      <Typography variant="h6" style={{minWidth: "320px", maxWidth: "900px" , marginTop: "20px", marginBottom: "20px"}}> <Typography variant="h7" style={{marginRight: "20px", display: "inline-block", marginBottom: "10px", float: "left", maxWidth: "40vw", overflow: "hidden" , textOverflow: "ellipsis"}}>{file.name}</Typography>
              
              

              <Button style={{maxWidth: '30px', maxHeight: '20px', minWidth: '30px', minHeight: '20px', 
                              color: "#00099", fontWeight: "900" ,background: "white",
                                float: "right" ,display: "inline", marginBottom: "10px"}} onClick={handleRemoveFile(file.key)} disableRipple>X</Button>

             {percentages[index].percentage == 100 ?  <Button disableRipple style={{float: "right", marginRight: "30px", 
                                      fontSize: "27px" , cursor: "pointer", transform:"translateY(-2px)", height:"25px" , transform: "translateY(10px)" , color: "#57deb7"}} 
                                      onClick={() => {{handleFileDownload(file)}}}><Typography variant="h7"><BsArrowDownSquareFill style={{width: "50px"}}/></Typography></Button>

             : 
             percentages[index].percentage == 0 ? <Button disableRipple style={{float: "right", marginRight: "30px", 
                                      fontSize: "27px" , cursor: "pointer", transform:"translateY(-2px)", height:"25px" , transform: "translateY(10px)" , color: "#000099"}} 
                                      onClick={() => {handlePopup(file.key)}}><Typography variant="h7"><BsFillArrowUpSquareFill style={{width: "50px"}}/></Typography></Button>
             
             : <Typography style={{display: "inline-block", marginRight: "30px", minWidth: "30px" , maxWidth: "30px", minHeight: "30px" , maxHeight: "30px" , 
                                  float: "right", marginBottom: "20px"}}><CircularProgressbar value={percentages[index].percentage} styles={buildStyles({
                                                    rotation: 0.25,
                                                    strokeLinecap: 'butt',
                                                    textSize: '16px',
                                                    pathTransitionDuration: 0.0001,
                                                    pathColor: '#000099',
                                                    trailColor: 'rgba(128,128,128, 0.1)',
                                                    backgroundColor: 'white',
                                                  })}/></Typography>
                                                  }
                                                  {confirmSingleUpdatePopup == 1 ? <><ConfirmUpdatePopup handleSingleFileUpload={handleSingleFileUpload} fileKey={file.key} multipleFiles={0} /></> : <></>}
            <br></br>
            <br></br>
             </Typography>

             
  

    
  )
}

export default UploadedFile