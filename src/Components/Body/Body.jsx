import React, { useState, useCallback, useEffect } from 'react'
import useStyles from './bodyStyles';
import { Container, Typography, Button } from '@material-ui/core';
import { FilePicker } from 'react-file-picker';
import Dropzone, { useDropzone } from "react-dropzone";
import bodyStyles from './bodyStyles.css';
import { Link } from 'react-router-dom';
import { uuid } from 'uuidv4';
import ApiService from "../../http-common"
import httpCommon from '../../http-common';
import UploadedFile from './UploadedFiles/UploadedFile';
import { saveAs } from 'file-saver';
import * as JSZip from 'jszip';


const Body = ({settings , files, setFiles}) => {

  const classes = useStyles();

  
  
  const {getRootProps, getInputProps, isDragActive, acceptedFiles} = useDropzone({onDrop});
  const [fileRejected, setFilesRejected] = useState(false);
  const [percentages, setPercentages] = useState([]);
  const [allFilesConverted, setAllFilesConverted] = useState(0);


    const onSubmit = (e) => {
        e.preventDefault();
    };

    useEffect(() => {

      if(percentages.length == 0){
        setAllFilesConverted(0);
      }

      let counter = 0;
      let counter2 = 0; 

      if(percentages.length > 0)
      {percentages.map((file) => {
        if(file.percentage != 100){
          counter = -1;
          setAllFilesConverted(0)
        }else if(file.percentage == 100){
          counter2++;
        }
        
      })}

      if(counter2 == percentages.length && percentages.length > 0){
        counter = 2;
      }

      if(counter != -1 && counter != 0 && percentages.length > 0){
        setAllFilesConverted(1);
      }
      console.log(allFilesConverted)
    },[percentages])

    function onDrop(acceptedFiles){
      setFilesRejected(false);

      let counter = 0;

      acceptedFiles.map((file) => {
        for(let i = 0; i < settings[0].originFile.length;i++){
        if((file.name.substr(file.name.lastIndexOf('.'))) === settings[0].originFile[i]){
          files.push(file);
          counter++;
          break;
        }else if(counter < acceptedFiles.length){
          setFilesRejected(true)
        }

    }})

    acceptedFiles.map((file) => {
      file.key = uuid();
      file.uploaded = false;
      let temp = [...percentages];
      temp.push({file: file.key , percentage: 0});
      setPercentages(temp)
    })

      setFiles(files);
      console.log(files);

    }

     const handleRemoveFile = (fileKey) => () => {
       console.log("file being removed")
      let tempFiles = [...files];

      for(let i = 0; i < tempFiles.length; i++){
        if(tempFiles[i].key === fileKey){
          tempFiles.splice(i,1);
          acceptedFiles.splice(i,1);
          break;
        }
      }

      let index = 0;
      let tempPercents = [...percentages]
      percentages.map((percentFile) => {
        if(percentFile.file == fileKey){
          tempPercents.splice(index,1);
        }
        index++;
      })

      setPercentages(tempPercents)
      setFiles(tempFiles);
    }


    function intersperse(arr, sep) {
      if (arr.length === 0) {
          return [];
      }
  
      return arr.slice(1).reduce(function(xs, x, i) {
          return xs.concat([sep, x]);
      }, [arr[0]]);
  }

  const handleFileUpload = () => {
        console.log(files);


        files.map((file) => {

          let index = 0;
          let counter = 0;
          percentages.map((percentFile) => {
            if(percentFile.file == file.key){
              index = counter
            }

            counter++;
          })

          const formData = new FormData();
          formData.append('file' , file);
          formData.append('fileName', file.name);
          formData.append('fileKey' , file.key)
  
          ApiService.upload(formData , file.name, file.key ,{headers: {
                                        'content-type': 'multipart/form-data'
                                        }, onUploadProgress: progressEvent => {

                                        console.log("Loaded: " + progressEvent.loaded + "Total: " + progressEvent.total);
                                        let completed = (Math.round((progressEvent.loaded / progressEvent.total) * 100));
                                        let tempPercents = [...percentages];
                                        tempPercents[index].percentage = completed
                                        setPercentages(tempPercents)
  
          }}).then(res => {
            file.uploaded = true;
            let tempPercents = [...percentages];
            tempPercents[index].percentage = 100
            setPercentages(tempPercents)
          })
          })

  }

  const handleSingleFileUpload = (fileKey) => {

    files.map((file) => {


      let index = 0;
          let counter = 0;
          percentages.map((percentFile) => {
            if(percentFile.file == file.key){
              index = counter
            }

            counter++;
          })

      if(file.key == fileKey){
        const formData = new FormData();
      formData.append('file' , file);
      formData.append('fileName', file.name);
      formData.append('fileKey' , file.key)

      ApiService.upload(formData , {headers: {
                                    'content-type': 'multipart/form-data'
                                    }, onUploadProgress: progressEvent => {

                                    console.log("Loaded: " + progressEvent.loaded + "Total: " + progressEvent.total);
                                    let completed = (Math.round((progressEvent.loaded / progressEvent.total) * 100));
                                    let tempPercents = [...percentages];
                                    tempPercents[index].percentage = completed
                                    setPercentages(tempPercents)

      }}).then(res => {
        file.uploaded = true;
        let tempPercents = [...percentages];
        tempPercents[index].percentage = 100
        setPercentages(tempPercents)
       console.log("success");
      })
      }
    })
      

}

  const handleFileDownload = (file) => {

    console.log(settings[1]);

    if(settings[1].targetFile == ".json"){
  
        ApiService.download(file.key + "converted-" + file.name.substr(0,file.name.lastIndexOf('.')) + settings[1].targetFile , {onDownloadProgress: progressEvent => {
          console.log("Loaded: " + progressEvent.loaded + "Total: " + progressEvent.total);
          let completed = (Math.round((progressEvent.loaded / progressEvent.total) * 100)); 
          console.log('completed: ', completed)

        }}).then((response) => {
          console.log();
          const url = window.URL.createObjectURL(new Blob([JSON.stringify(response.data)])) 
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', file.name.substr(0,file.name.lastIndexOf('.')) + settings[1].targetFile); //or any other extension
          document.body.appendChild(link);
          link.click();
      }).catch(err => console.error(err));

    }
}

const handleDownloadAllFiles = () => {

  var zip = new JSZip();
  let counter = 0;
  

  files.map((file) => {
    if(settings[1].targetFile == ".json"){

      ApiService.download(file.key + "converted-" + file.name.substr(0,file.name.lastIndexOf('.')) + settings[1].targetFile , {onDownloadProgress: progressEvent => {
        console.log("Loaded: " + progressEvent.loaded + "Total: " + progressEvent.total);
        let completed = (Math.round((progressEvent.loaded / progressEvent.total) * 100)); 
        console.log('completed: ', completed)

      }}).then((response) => {
        let blob = new Blob([JSON.stringify(response.data)])
        zip.file(file.name.substr(0,file.name.lastIndexOf('.')) + settings[1].targetFile , blob)
        counter++;
        // console.log(JSON.stringify(response.data));
        // 
        // 
        // const url = window.URL.createObjectURL(new Blob([JSON.stringify(response.data)])) 
        // const link = document.createElement('a');
        // link.href = url;
        // link.setAttribute('download', file.name.substr(0,file.name.lastIndexOf('.')) + settings[1].targetFile); //or any other extension
        // document.body.appendChild(link);
        // link.click();
    }).then(() => {
      console.log("got here" + counter + "times")
      if(counter == files.length){
        zip.generateAsync({type:"blob"})
          .then(function(content) {
          saveAs(content, "converted.zip");
            });
      }
    }).catch(err => console.error(err));

  }
  })
}

const handleClearFiles = () => {
  let tempFiles = [...files];
  let tempPercents = [...percentages]

  tempFiles.splice(0, tempFiles.length);
  tempPercents.splice(0, tempPercents.length);

  setFiles(tempFiles);
  setPercentages(tempPercents);
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

<form method="get" type="file" action="#" multiple id="#" onChange={onDrop}>
    <div className={classes.dropzone} {...getRootProps()}>
      <input {...getInputProps()} />
      {
        isDragActive ?
        <div className="onDragOver" style={{height: "100%" ,  pointerEvents: "none"}}>
        <div className={classes.text} style={{transform: "translateY(100px)"}}>Drop Your Files Here</div>
        <div className={classes.subText} style={{transform: "translateY(120px)"}}>Dropped Files Will Be<br></br>Automatically Uploaded</div>
            </div> :
          <div className="onDragOut">
            <div style={{top:"50%", height: "50%" ,transform: "translateY(63%)",  pointerEvents: "none"}}>
                <label style={{textAlign: "center",}} for="files"><div className={classes.text}>Upload Your Files Here</div><br /><div className={classes.subText}>Click To Browse Or <br></br>Drag And Drop</div></label>
                <input type="file"
                       id="files"
                       style={{display:"none"}}
                       multiple/>
            </div>

            
            </div>
      }
    </div>
    <br />
    {allFilesConverted == 1 ? <Button onClick={handleDownloadAllFiles} className={classes.button}><div style={{fontWeight: "bold",  fontSize: "15px", cursor: "pointer", transform: "translateY(2px)"}}>Download All</div></Button> : 
    <Button onClick={handleFileUpload} className={classes.button}><div style={{fontWeight: "bold",  fontSize: "15px", cursor: "pointer", transform: "translateY(2px)"}}>Convert All</div></Button>
    }
    
    <Link to="/settings" className={classes.links}><div className={classes.link}>Settings</div></Link>

    {fileRejected == true ? <div className={classes.warningMessage}>Please make sure all files are of format(s): {intersperse(settings[0].originFile, ", ")}</div> : <></>}

    <div>
        <Typography variant="h3" style={{fontWeight: "bold",  fontSize: "175%", marginTop: "2%", textDecoration: "underline", display: "inline-block", marginRight: "20px"}}>Uploaded Files</Typography> <Typography variant="h3" className={classes.link} style={{fontWeight: "bold",  fontSize: "125%", marginTop: "2%",display: "inline-block", transform: "translateY(1px)"}} onClick={handleClearFiles}>Clear Files</Typography>
              {files.map((file) => (
                    <UploadedFile file={file} percentages={percentages} fileName={file.name} handleRemoveFile={handleRemoveFile} handleFileDownload={handleFileDownload} handleSingleFileUpload={handleSingleFileUpload}/>
              ))}
              </div>

    </form>
)}
    </Dropzone>
    </Container>
  )
}

export default Body