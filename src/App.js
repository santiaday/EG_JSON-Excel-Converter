import logo from './logo.svg';
import './App.css';
import React, { useEffect, useState } from "react";
import Header from './Components/Header/Header';
import { createTheme, ThemeProvider } from '@material-ui/core'
import Body from './Components/Body/Body';
import Settings from './Components/Settings/Settings';
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom';

const theme = createTheme({
  typography: {
    fontFamily: [
      'Gothic A1', 
      'sans-serif'
    ].join(','),
  },
});



function App() {
  const[settings, setSettings] = useState([{ originFile: [""] },
                                        { targetFile: "" },
                                        { multipleFileOutput: "" }]);

  const [originFile, setOriginFile] = useState([""])
  const [targetFile, setTargetFile] = useState("");
  const [multipleFileOutput, setMultipleFileOutput] = useState("");
  const [files, setFiles] = useState([]);
  const [percentages, setPercentages] = useState([]);
  const [counters, setCounters] = useState([]);
  const [firstRender , setFirstRender] = useState(1);



  useEffect(() => {
    
        setSettings([
        {originFile: [".xlsx", ".xls"]},
        {targetFile: ".json"},
        {multipleFileOutput: "0"}
        ]

    )
    
  }, [])

  
  
  return (
    <>
    <ThemeProvider theme={theme}>
      <Header />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Body settings={settings} setSettings={setSettings} files={files} setFiles={setFiles} percentages={percentages} setPercentages={setPercentages}
                                      counters={counters} setCounters={setCounters}/>} />

            <Route path="/settings" element={<Settings settings={settings} setSettings={setSettings} originFile={originFile}
                                              setOriginFile={setOriginFile} targetFile={targetFile} setTargetFile={setTargetFile}
                                              multipleFileOutput={multipleFileOutput} setMultipleFileOutput={setMultipleFileOutput} 
                                              files={files} firstRender={firstRender} setFirstRender={setFirstRender}/>} />
          </Routes>
        </BrowserRouter>
      
    </ThemeProvider>
    </>
  );
}

export default App;
