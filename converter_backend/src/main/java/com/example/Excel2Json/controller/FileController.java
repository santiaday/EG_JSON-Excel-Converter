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

    @PostMapping("/json/uploadFile/convert-to-excel")
    public void uploadJSONToExcel(@RequestParam("file") MultipartFile file, @RequestParam("fileName")String fileName, @RequestParam("fileKey")String fileKey, @RequestParam("targetFile")String targetExtension) {
        String sourceExtension = fileName.substring(fileName.lastIndexOf('.'));
        EG_JSON_to_Excel(file , fileName, fileKey, targetExtension, sourceExtension);

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
                for(int j=0; j<row.getPhysicalNumberOfCells();j++) {
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

    private ObjectMapper mapper = new ObjectMapper();

    public void EG_JSON_to_Excel(MultipartFile file, String fileName, String fileKey, String targetFileExtension, String sourceExtension) {

        File srcFile = new File("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\" + fileName);
        System.out.println(sourceExtension);

        if(sourceExtension.equals(".json")){
            System.out.println("they do equal");
        }
        try {
            if (!sourceExtension.equals(".json")) {
                throw new IllegalArgumentException("The source file should be .json file only");
            } else {
                Workbook workbook = null;

                //Creating workbook object based on target file format
                if (targetFileExtension.equals(".xls")) {
                    workbook = new HSSFWorkbook();
                } else if (targetFileExtension.equals(".xlsx")) {
                    workbook = new XSSFWorkbook();
                } else {
                    throw new IllegalArgumentException("The target file extension should be .xls or .xlsx only");
                }

                //Reading the json file
                ObjectNode jsonData = (ObjectNode) mapper.readTree(srcFile);

                //Iterating over the each sheets
                Iterator<String> sheetItr = jsonData.fieldNames();
                while (sheetItr.hasNext()) {

                    // create the workbook sheet
                    String sheetName = sheetItr.next();
                    Sheet sheet = workbook.createSheet(sheetName);

                    ArrayNode sheetData = (ArrayNode) jsonData.get(sheetName);
                    ArrayList<String> headers = new ArrayList<String>();

                    //Creating cell style for header to make it bold
                    CellStyle headerStyle = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setBold(true);
                    headerStyle.setFont(font);

                    //creating the header into the sheet
                    Row header = sheet.createRow(0);
                    Iterator<String> it = sheetData.get(0).fieldNames();
                    int headerIdx = 0;
                    while (it.hasNext()) {
                        String headerName = it.next();
                        headers.add(headerName);
                        Cell cell=header.createCell(headerIdx++);
                        cell.setCellValue(headerName);
                        //apply the bold style to headers
                        cell.setCellStyle(headerStyle);
                    }

                    //Iterating over the each row data and writing into the sheet
                    for (int i = 0; i < sheetData.size(); i++) {
                        ObjectNode rowData = (ObjectNode) sheetData.get(i);
                        Row row = sheet.createRow(i + 1);
                        for (int j = 0; j < headers.size(); j++) {
                            String value = rowData.get(headers.get(j)).asText();
                            row.createCell(j).setCellValue(value);
                        }
                    }

                    /*
                     * automatic adjust data in column using autoSizeColumn, autoSizeColumn should
                     * be made after populating the data into the excel. Calling before populating
                     * data will not have any effect.
                     */
                    for (int i = 0; i < headers.size(); i++) {
                        sheet.autoSizeColumn(i);
                    }

                }

                //creating a target file
                String filename = srcFile.getName();
                filename = filename.substring(0, filename.lastIndexOf(".")) + targetFileExtension;
                FileWriter targetFile = new FileWriter(String.format("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\%s" , filename));

                // write the workbook into target file
                FileOutputStream fos = new FileOutputStream(String.valueOf(targetFile));
                workbook.write(fos);

                //close the workbook and fos
                workbook.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EG_write_to_JSON(List<JSONObject> dataList, String name, String fileKey, int counter) {
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
