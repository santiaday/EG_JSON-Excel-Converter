package com.example.Excel2Json.controller;

import com.example.Excel2Json.payload.UploadFileResponse;
import com.example.Excel2Json.service.FileStorageService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileInputStream;

@RestController
@CrossOrigin("http://localhost:3000")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileStorageService fileStorageService;

    @PostMapping("/excel/uploadFile/convert-to-single-json")
    public void uploadFileToExcel(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName, @RequestParam("fileKey") String fileKey, @RequestParam("updateStorage") boolean updateStorage) {
        String fileNameWOExt = fileName.substring(0, fileName.lastIndexOf('.'));

        try {
            System.out.println(file.getBytes());
        }catch(IOException e) {

        }

        if(updateStorage == true){
            EG_excel_to_single_JSON(file, fileNameWOExt, fileKey, true);
        }else if(updateStorage == false){
            EG_excel_to_single_JSON(file, fileNameWOExt, fileKey, false);
        }


    }

    @PostMapping("/excel/uploadFile/convert-to-multiple-json")
    public int uploadFileToMultipleJSON(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName, @RequestParam("fileKey") String fileKey, @RequestParam("updateStorage") boolean updateStorage) {
        String fileNameWOExt = fileName.substring(0, fileName.lastIndexOf('.'));

        if(updateStorage == true){
           return EG_excel_to_multiple_JSON(file, fileNameWOExt, fileKey, true);
        }else if(updateStorage == false){
            return EG_excel_to_multiple_JSON(file, fileNameWOExt, fileKey, false);
        }

        return -1;
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
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/downloadRule/{fileName:.+}")
    public ResponseEntity<Resource> downloadRule(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        System.out.println("Downloading file...");
        Resource resource = fileStorageService.loadRuleAsResource(fileName);


        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/rules/download-rule-spreadsheet")
    public ResponseEntity<Resource> downloadRuleSpreadsheet(HttpServletRequest request) {
        System.out.println("Downloading file...");
        Resource resource = fileStorageService.loadRuleAsResource("master_rules.xlsx");


        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/rules/createRule")
    public void createRule(@RequestParam("rule") MultipartFile jsonRule , @RequestParam("ruleName") String ruleName) {


        try {
            String content = new String(jsonRule.getBytes());
            System.out.println(content);
            JsonObject newRuleObject = JsonParser.parseString(content).getAsJsonObject();

            update_rules_storage(newRuleObject, ruleName, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean multiple = false;

    public int EG_excel_to_multiple_JSON(MultipartFile data, String name, String fileKey, boolean updateStorage) {
        multiple = true;
        List<JsonObject> dataList = new ArrayList<>();
        int counter = 0;

        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(0);
            int numRows = workSheet.getPhysicalNumberOfRows();

            int i = 4;
            int j = 5;

            JsonObject jsonObject = new JsonObject();


            while (j < numRows) {
                System.out.println(jsonObject);
                dataList.add(write_to_json_object(data, i, j, jsonObject, 0));
                counter++;

                if(updateStorage){
                    update_rules_storage(dataList.get(0), dataList.get(0).keySet().toArray()[0].toString(), false);
                }



                EG_write_rule_to_JSON(dataList, name, fileKey, counter);
                dataList.clear();
                jsonObject = new JsonObject();
                System.out.println(dataList);
                i = i + 3;
                j = j + 3;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return counter;
    }


    public String EG_excel_to_single_JSON(@RequestParam("data") MultipartFile data, String name, String fileKey, boolean updateStorage) {
        List<JsonObject> dataList = new ArrayList<>();
        List<JsonObject> tempDataList = new ArrayList<>();

        multiple = false;
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(0);
            int numRows = workSheet.getPhysicalNumberOfRows();

            int i = 4;
            int j = 5;

            JsonObject jsonObject = new JsonObject();
            JsonObject tempJsonObject = new JsonObject();

            while (j < numRows) {
                dataList.add(write_to_json_object(data, i, j, jsonObject, 0));
                tempDataList.add(write_to_json_object(data, i, j, tempJsonObject, 0));

                if(updateStorage){
                    update_rules_storage(tempJsonObject , tempDataList.get(0).keySet().toArray()[0].toString(), false);
                }

                tempJsonObject = new JsonObject();
                tempDataList.clear();


                i = i + 3;
                j = j + 3;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        EG_write_rule_to_JSON(dataList, name, fileKey, 0);

        return "Success";
    }

    private void update_rules_storage(JsonObject jsonObject, String ruleName, boolean newRule) {
        Gson gson = new Gson();


        if(ruleName.contains("\"")){
            ruleName = ruleName.substring(1 , ruleName.length()-1);
        }
        if(ruleName.contains("/")){
            StringBuilder sb = new StringBuilder(ruleName);
            sb.setCharAt(ruleName.indexOf("/") , '???');
            ruleName = sb.toString();
        }

        try {

            if(jsonObject.toString() != null) {

                int jIndex = 0;

                Path path = Paths.get(String.format("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\rulesStorage\\%s.json", ruleName));
                boolean exists = Files.exists(path);
                if (!exists) {
                    write_to_excel_spreadsheet(jsonObject, true);
                    FileWriter file = new FileWriter("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\rulesStorage\\" + ruleName + ".json");
                    file.write(gson.toJson(jsonObject));
                    file.close();
                } else {
                    String content = new String(Files.readAllBytes(path));
                    if (!(jsonObject.toString().equals(content))) {
                        JsonObject temp = new JsonObject();
                        temp = jsonObject;
                        write_to_excel_spreadsheet(jsonObject, false);
                        FileWriter file = new FileWriter(String.format("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\rulesStorage\\%s.json", ruleName));
                        file.write(gson.toJson(temp));
                        file.close();
                    } else {
                        System.out.println("They are equal");
                    }
                }

                jIndex++;
            }
        }catch(IOException e){

        }


    }
    @GetMapping("/rules/countRules")
    public List<String> countRules(){

        File dir = new File("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\rulesStorage");
        File[] directoryListing = dir.listFiles();

        List<String> ruleNames = new ArrayList<>();

        for(File f : directoryListing){
            if(f.getName().contains("???") && f.getName().contains(".json")){
                StringBuilder sb = new StringBuilder(f.getName());
                sb.setCharAt(f.getName().indexOf("???") , '/');
                ruleNames.add(sb.toString());
            }else if(f.getName().contains(".json")){
                ruleNames.add(f.getName());
            }


        }

        return ruleNames;
    }

    public JsonObject write_to_json_object(MultipartFile data, int headerInt, int rowInt, JsonObject jsonObject, int sheetNum) {

        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(sheetNum);
            XSSFRow header = workSheet.getRow(headerInt);


            for (int i = rowInt; i < workSheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = workSheet.getRow(i);


                if (workSheet.getRow(i) == null) {
                    break;
                }


                if (row.getCell(0).toString().contains("////")) {
                    return jsonObject;
                }


                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {


                    if (header.getCell(j) == null) {
                        continue;
                    }


                    if (row.getCell(j).toString().contains("sheet") && !(row.getCell(j).toString().contains("Arr"))) {
                        JsonObject temp = new JsonObject();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('t') + 1));
                        jsonObject.add(header.getCell(j).toString(), write_to_json_object(data, headerInt, rowInt, temp, sheetNumber - 1));
                        continue;
                    }

                    if (row.getCell(j).toString().contains("sheetArr")) {
                        JsonObject temp = new JsonObject();
                        JsonArray tempDataList = new JsonArray();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('r') + 1, row.getCell(j).toString().length()));
                        jsonObject.add(header.getCell(j).toString(), write_to_json_array(data, headerInt, rowInt, temp, sheetNumber - 1, tempDataList));
                        continue;
                    }


                    String columnName = header.getCell(j).toString();
                    String columnValue = row.getCell(j).toString();

                    if(columnValue != ""){
                        jsonObject.addProperty(columnName, columnValue);
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JsonArray write_to_json_array(MultipartFile data, int headerInt, int rowInt, JsonObject jsonObject, int sheetNum, JsonArray jsonArray) {
        try {
            XSSFWorkbook workBook = new XSSFWorkbook(data.getInputStream());
            XSSFSheet workSheet = workBook.getSheetAt(sheetNum);
            XSSFRow header = workSheet.getRow(headerInt);
            for (int i = rowInt; i < workSheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = workSheet.getRow(i);
                if (workSheet.getRow(i) == null) {
                    break;
                }

                if (row.getCell(0).toString().contains("////")) {
                    return jsonArray;
                }

                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {

                    if (header.getCell(j).toString().contains("---")) {
                        jsonArray.add(jsonObject);
                        jsonObject = new JsonObject();
                        continue;
                    }

                    if (row.getCell(j).toString().contains("sheet") && !(row.getCell(j).toString().contains("Arr"))) {
                        JsonObject temp = new JsonObject();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('t') + 1));
                        jsonObject.add(header.getCell(j).toString(), write_to_json_object(data, headerInt, rowInt, temp, sheetNumber - 1));
                        continue;
                    }

                    if (row.getCell(j).toString().contains("sheetArr")) {
                        JsonObject temp = new JsonObject();
                        JsonArray tempDataList = new JsonArray();
                        int sheetNumber = Integer.parseInt(row.getCell(j).toString().substring(row.getCell(j).toString().lastIndexOf('r') + 1));
                        jsonObject.add(header.getCell(j).toString(), write_to_json_array(data, headerInt, rowInt, temp, sheetNumber - 1, tempDataList));
                        continue;
                    }


                    String columnName = header.getCell(j).toString();
                    String columnValue = row.getCell(j).toString();


                    if(columnValue != ""){
                        jsonObject.addProperty(columnName, columnValue);
                    }
                }
                jsonArray.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }



    //METHOD FOR ADDING RULE TO EXCEL SPREADSHEET
    public void write_to_excel_spreadsheet(JsonObject jsonObject, boolean newRule){

        try{
            FileInputStream fis = new FileInputStream("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\rulesStorage\\master_rulesTESTING - Copy.xlsx");
            XSSFWorkbook workBook = new XSSFWorkbook(fis);
            fis.close();
            XSSFSheet worksheet = workBook.getSheetAt(0);
            int headerInt = worksheet.getPhysicalNumberOfRows();


            for(int i = 0; i < workBook.getNumberOfSheets() - 1; i++){
                XSSFSheet tempWorksheet = workBook.getSheetAt(i);
                tempWorksheet.copyRows(0 , 2 , headerInt, new CellCopyPolicy());
            }

            for(int i = 0; i < workBook.getNumberOfSheets() - 1; i++){

                XSSFSheet tempWorksheet = workBook.getSheetAt(i);
                XSSFRow key = tempWorksheet.getRow(headerInt + 1);
                XSSFRow value = tempWorksheet.getRow(headerInt + 2);

                if(i == 0){
                    key.getCell(0).setCellValue(jsonObject.keySet().toArray()[0].toString());
                    continue;
                }else if(i == 1){
                    continue;
                }

                if(i == 2) {
                    for (int j = 0; j < key.getPhysicalNumberOfCells(); j++) {
                        if (value.getCell(j) == null || value.getCell(j).toString() == "") {
                            System.out.println(jsonObject.getAsJsonObject(jsonObject.keySet().toArray()[0].toString()).getAsJsonObject("key").get(key.getCell(j).toString()));
                            System.out.println(key.getCell(j).toString());
                            String cellVal = jsonObject.getAsJsonObject(jsonObject.keySet().toArray()[0].toString()).getAsJsonObject("key").get(key.getCell(j).toString()).toString();
                            value.createCell(j).setCellValue(cellVal.substring(1, cellVal.length()-1));
                        }
                    }
                }
            }




            FileOutputStream out = new FileOutputStream(new File("rulesStorage\\master_rulesTESTING - Copy.xlsx"));
            workBook.write(out);
            out.close();



            System.out.println("We got here:  " + headerInt);


        } catch (Exception e){
            e.printStackTrace();
        }




    }

    public void EG_write_to_JSON(List<JsonObject> dataList, String name, String fileKey, int counter) {
        Gson gson = new Gson();
        try {
            FileWriter file = new FileWriter(String.format("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\springBootUploads\\%s.json",
                    multiple ? fileKey + "converted-" + name + "-" + counter : fileKey + "converted-" + name));
            file.write(gson.toJson(dataList));
            file.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void EG_write_rule_to_JSON(List<JsonObject> dataList, String name, String fileKey, int counter) {
        Gson gson = new Gson();


        try {


            FileWriter file = new FileWriter(String.format("C:\\Users\\saaday\\Documents\\EG_JSON-Excel-Converter\\converter_backend\\springBootUploads\\%s.json",
                    multiple ? fileKey + "converted-" + name + "-" + counter : fileKey + "converted-" + name));
            file.write(gson.toJson(dataList));
            file.close();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
