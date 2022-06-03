import React, { useState, useCallback, useEffect } from 'react'
import useStyles from './bodyStyles';
import { Container, Typography, Button } from '@material-ui/core';
import { FilePicker } from 'react-file-picker';
import Dropzone, { useDropzone } from "react-dropzone";
import bodyStyles from './bodyStyles.css';
import { Link } from 'react-router-dom';
import { uuid } from 'uuidv4';
import UploadService from "../../services/upload-files.service"
import httpCommon from '../../http-common';

const Body = ({settings , files, setFiles}) => {

  const classes = useStyles();

  
  
  const [fileUploaded, setFileUploaded] = useState("0");
  const {getRootProps, getInputProps, isDragActive, acceptedFiles} = useDropzone({onDrop});
  const [fileRejected, setFilesRejected] = useState(false);


    const onSubmit = (e) => {
        e.preventDefault();
    };


    function onDrop(acceptedFiles){
      setFileUploaded("1");
      setFilesRejected(false);
      console.log(settings[0].originFile)
      let counter = 0;

      acceptedFiles.map((file) => {
        for(let i = 0; i < settings[0].originFile.length;i++){
        if((file.name.substr(file.name.lastIndexOf('.'))) === settings[0].originFile[i]){
          files.push(file);
          counter++;
        }else if(counter < acceptedFiles.length){
          setFilesRejected(true)
        }

    }})

    files.map((file) => {
      file.key = uuid();
    })

      setFiles(files);

    }

     const handleRemoveFile = fileKey => () => {
      let tempFiles = [...files];

      for(let i = 0; i < tempFiles.length; i++){
        if(tempFiles[i].key === fileKey){
          tempFiles.splice(i,1);
          acceptedFiles.splice(i,1);
          break;
        }
      }

      setFiles(tempFiles);
     }

    useEffect(() => {
      console.log(files);
    }, [files])


    function intersperse(arr, sep) {
      if (arr.length === 0) {
          return [];
      }
  
      return arr.slice(1).reduce(function(xs, x, i) {
          return xs.concat([sep, x]);
      }, [arr[0]]);
  }

  const handleFileUpload = () => {
    files.map((file) => {
      var data = new FormData()
      data.append('file', file)

      UploadService.upload(data);
    })
  }

  return (
    <Container style={{width:"90vw", maxWidth:"85vw"}}>
        <div className={classes.toolbar} />
        <Typography className={classes.title} variant="h2">Choose the files you want to convert</Typography>
        <br />
        <br />
        <br />
<Dropzone onDrop={onDrop}> 

{({getRootProps, getInputProps, isDragActive}) => (       

<form method="post" action="#" id="#" onSubmit={onSubmit}>
    <div className={classes.dropzone} {...getRootProps()}>
      <input {...getInputProps()} />
      {
        isDragActive ?
        <div className="onDragOver" style={{height: "100%" ,  pointerEvents: "none"}}>
        <div className={classes.text} style={{transform: "translateY(100px)"}}>Drop Your Files Here</div>
        <div className={classes.subText} style={{transform: "translateY(120px)"}}>Dropped Files Will Be<br></br>Automatically Uploaded</div>
            </div> :
          <div className="onDragOut">
            <div style={{top:"50%", height: "50%" ,transform: "translateY(70%)",  pointerEvents: "none"}}>
                <label style={{textAlign: "center",}} for="files"><div className={classes.text}>Upload Your Files Here</div><br /><div className={classes.subText}>Click To Browse Or Drag And Drop</div></label>
                <input type="file"
                       id="files"
                       style={{display:"none"}}
                       multiple/>
            </div>

            
            </div>
      }
    </div>
    <br />
    <button type="submit" className={classes.button} onClick={handleFileUpload}><div style={{fontWeight: "bold",  fontSize: "130%", cursor: "pointer"}}>Convert</div></button>
    <Link to="/settings" className={classes.links}><div className={classes.link}>Settings</div></Link>

    {fileRejected == true ? <div className={classes.warningMessage}>Please make sure all files are of format(s): {intersperse(settings[0].originFile, ", ")}</div> : <></>}

    <div>
        <div style={{fontWeight: "bold",  fontSize: "175%", marginTop: "2%"}}>Uploaded Files:</div>
              {files.map((file) => (
              <p>{file.name} 
              
              <Button style={{maxWidth: '30px', maxHeight: '20px', minWidth: '30px', minHeight: '20px', 
                              color: "#00099", fontWeight: "600" ,background: "white", transform: "translateY(1.4px)", 
                              fontSize:"75%" , marginLeft:"1%"}} onClick={handleRemoveFile(file.key)} disableRipple>x</Button> </p>
              ))}
              </div>

    </form>
)}
    </Dropzone>
    </Container>
  )
}

export default Body