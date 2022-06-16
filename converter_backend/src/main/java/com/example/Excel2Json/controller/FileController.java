package com.example.Excel2Json.controller;

import com.example.Excel2Json.payload.UploadFileResponse;
import com.example.Excel2Json.service.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.sql.Array;
import java.util.*;
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
        List<JsonObject> dataList = new ArrayList<>();
        int counter = 0;

        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(0);
            int numRows = workSheet.getPhysicalNumberOfRows();

            int i = 1;
            int j = 2;

            JsonObject jsonObject = new JsonObject();

            for(int k = 0; k < workBook.getNumberOfSheets(); k++){
                System.out.println(workBook.getSheetAt(k));
            }

            while(j < numRows){
                System.out.println("Did the for loop");
                dataList.add(write_to_json_object(data, i, j, jsonObject, 0));
                counter++;
                EG_write_to_JSON(dataList, name, fileKey, counter);
                dataList.clear();
                i = i + 3;
                j = j + 3;

            }


        }catch(IOException e){
            e.printStackTrace();
        }




        return counter;
    }


    public String EG_excel_to_single_JSON(@RequestParam("data") MultipartFile data, String name, String fileKey) {
        List<JsonObject> dataList = new ArrayList<>();

        multiple=false;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(0);
            int numRows = workSheet.getPhysicalNumberOfRows();

            int i = 1;
            int j = 2;

            JsonObject jsonObject = new JsonObject();

            for(int k = 0; k < workBook.getNumberOfSheets(); k++){
                System.out.println(workBook.getSheetAt(k));
            }

            while(j < numRows){
                System.out.println("Did the for loop");
                dataList.add(write_to_json_object(data, i, j, jsonObject, 0));
                jsonObject = new JsonObject();
                i = i + 3;
                j = j + 3;

            }


        }catch(IOException e){
            e.printStackTrace();
        }


        EG_write_to_JSON(dataList, name, fileKey, 0);

        return "Success";
    }

    public JsonObject write_to_json_object (MultipartFile data , int headerInt , int rowInt, JsonObject jsonObject, int sheetNum){

        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(sheetNum);
            XSSFRow header = workSheet.getRow(headerInt);



            for(int i=rowInt;i<workSheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = workSheet.getRow(i);


                if(workSheet.getRow(i) == null){
                    break;
                }



                    if(row.getCell(0).toString().contains("////")){
                        System.out.println("Returning object..." + jsonObject);
                        return jsonObject;
                    }


                for(int j=0; j<row.getPhysicalNumberOfCells();j++) {


                    if(header.getCell(j) == null){
                        continue;
                    }


                    if(row.getCell(j).toString().contains("sheet") && !(row.getCell(j).toString().contains("Arr"))){
                        JsonObject temp = new JsonObject();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('t')+ 1 ));
                        System.out.println("THIS IS THE SHEET NUMBER: " + sheetNumber);
                        jsonObject.add(header.getCell(j).toString(), write_to_json_object(data, headerInt, rowInt, temp, sheetNumber-1));
                        continue;
                    }

                    if(row.getCell(j).toString().contains("sheetArr")){
                        JsonObject temp = new JsonObject();
                        JsonArray tempDataList = new JsonArray();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('r') + 1 , row.getCell(j).toString().length()));
                        System.out.println("THIS IS THE SHEET NUMBER: " + sheetNumber);
                        jsonObject.add(header.getCell(j).toString(), write_to_json_array(data, headerInt, rowInt, temp, sheetNumber-1, tempDataList));
                        continue;
                    }


                    String columnName = header.getCell(j).toString();
                    String columnValue = row.getCell(j).toString();
                    jsonObject.addProperty(columnName, columnValue);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public JsonArray write_to_json_array (MultipartFile data , int headerInt , int rowInt, JsonObject jsonObject, int sheetNum, JsonArray jsonArray){
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(sheetNum);
            XSSFRow header = workSheet.getRow(headerInt);
            for(int i=rowInt;i<workSheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = workSheet.getRow(i);
                if(workSheet.getRow(i) == null){
                    break;
                }

                if(row.getCell(0).toString().contains("////")){
                    System.out.println("Returning object..." + jsonObject);
                    return jsonArray;
                }

                for(int j=0; j<row.getPhysicalNumberOfCells();j++) {

                    if(header.getCell(j).toString().equals("-")){
                        jsonArray.add(jsonObject);
                        jsonObject = new JsonObject();
                        continue;
                    }

                    if(row.getCell(j).toString().contains("sheet") && !(row.getCell(j).toString().contains("Arr"))){
                        JsonObject temp = new JsonObject();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('t')+ 1 ));
                        System.out.println("THIS IS THE SHEET NUMBER: " + sheetNumber);
                        jsonObject.add(header.getCell(j).toString(), write_to_json_object(data, headerInt, rowInt, temp, sheetNumber-1));
                        continue;
                    }

                    if(row.getCell(j).toString().contains("sheetArr")){
                        JsonObject temp = new JsonObject();
                        JsonArray tempDataList = new JsonArray();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('r')+ 1));
                        System.out.println("THIS IS THE SHEET NUMBER: " + sheetNumber);
                        jsonObject.add(header.getCell(j).toString(), write_to_json_array(data, headerInt, rowInt, temp, sheetNumber-1, tempDataList));
                        continue;
                    }


                    String columnName = header.getCell(j).toString();
                    String columnValue = row.getCell(j).toString();

//                    System.out.println("ARRAY HEADER VALUE: " + header.getCell(0).toString());
//                    System.out.println("ARRAY ROW VALUE: " + row.getCell(0).toString());

                    jsonObject.addProperty(columnName, columnValue);
                }
                jsonArray.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public void EG_write_to_JSON(List<JsonObject> dataList, String name, String fileKey, int counter) {
        Gson gson = new Gson();
        try {
            FileWriter file = new FileWriter(String.format("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\%s.json",
                    multiple ? fileKey + "converted-" + name + "-" + counter : fileKey + "converted-" + name));
            file.write(gson.toJson(dataList));
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
