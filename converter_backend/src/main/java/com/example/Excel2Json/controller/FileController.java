package com.example.Excel2Json.controller;

import com.example.Excel2Json.payload.UploadFileResponse;
import com.example.Excel2Json.service.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:3000")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/excel/uploadFile/convert-to-single-json")
    public void uploadFileToExcel(@RequestParam("file") MultipartFile file, @RequestParam("fileName")String fileName, @RequestParam("fileKey")String fileKey) {
        String fileNameWOExt = fileName.substring(0, fileName.lastIndexOf('.'));
        EG_excel_to_single_JSON(file, fileNameWOExt, fileKey);

    }

    @PostMapping("/excel/uploadFile/convert-to-multiple-json")
    public int uploadFileToMultipleJSON(@RequestParam("file") MultipartFile file, @RequestParam("fileName")String fileName, @RequestParam("fileKey")String fileKey) {
        String fileNameWOExt = fileName.substring(0 , fileName.lastIndexOf('.'));
        return EG_excel_to_multiple_JSON(file , fileNameWOExt, fileKey);
    }


    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        System.out.println("Downloading file...");
        Resource resource = fileStorageService.loadFileAsResource(fileName);


        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private boolean multiple = false;
    public int EG_excel_to_multiple_JSON(MultipartFile data, String name, String fileKey) {
        multiple = true;
        int counter = 1;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(0);
            List<JSONObject> dataList = new ArrayList<>();
            XSSFRow header = workSheet.getRow(0);
            for(int i=1;i<workSheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = workSheet.getRow(i);
                JSONObject rowJsonObject = new JSONObject();
                for(int j=0; j<row.getPhysicalNumberOfCells();j++) {
                    String columnName = header.getCell(j).toString();
                    String columnValue = row.getCell(j).toString();
                    rowJsonObject.put(columnName, columnValue);
                }
                dataList.add(rowJsonObject);
                EG_write_to_JSON(dataList, name, fileKey, counter);
                System.out.println(counter);
                counter++;
                dataList = new ArrayList<>();
            }
            return counter;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return 1;
    }

    public String EG_excel_to_single_JSON(@RequestParam("data") MultipartFile data, String name, String fileKey) {
        multiple=false;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(0);
            List<JSONObject> dataList = new ArrayList<>();
            XSSFRow header = workSheet.getRow(0);
            for(int i=1;i<workSheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = workSheet.getRow(i);

                JSONObject rowJsonObject = new JSONObject();
                JSONObject otherJsonObject = new JSONObject();
                for(int j=0; j<row.getPhysicalNumberOfCells();j++) {

                    if(header.getCell(j) == null || row.getCell(j) == null){
                        System.out.println("It was null");
                        continue;
                    }

                    String columnName = header.getCell(j).toString();
                    String columnValue = row.getCell(j).toString();
                    rowJsonObject.put(columnName, columnValue);
                }
                dataList.add(rowJsonObject);
            }
            EG_write_to_JSON(dataList,name, fileKey, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Success";
    }

    public void EG_write_to_JSON(List<JSONObject> dataList, String name, String fileKey, int counter) {
        Gson gson = new Gson();
        try {
            FileWriter file = new FileWriter(String.format("C:\\Users\\santi\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\springBootUploads\\%s.json",
                    multiple ? fileKey + "converted-" + name + "-" + counter : fileKey + "converted-" + name));
            file.write(gson.toJson(dataList));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
