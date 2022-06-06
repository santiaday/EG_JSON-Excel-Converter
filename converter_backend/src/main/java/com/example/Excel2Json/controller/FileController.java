package com.example.Excel2Json.controller;

import com.example.Excel2Json.payload.UploadFileResponse;
import com.example.Excel2Json.service.FileStorageService;
import com.google.gson.Gson;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/excel/uploadFile/convert-to-single-json")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        String fileNameWOExt = fileName.substring(0 , fileName.lastIndexOf('.'));
        System.out.println(fileNameWOExt);

        EG_excel_to_single_JSON(file , fileNameWOExt);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }


    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
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

    private int counter = 1;
    private boolean multiple = false;
    public void EG_excel_to_multiple_JSON(MultipartFile data, String name) {
        multiple = true;
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
                EG_write_to_JSON(dataList, name);
                dataList = new ArrayList<>();
            }
            counter = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        multiple=false;
    }

    @PostMapping("/excel/uploadFile/convert-to-single-json-test")
    public String EG_excel_to_single_JSON(@RequestParam("data") MultipartFile data, String name) {
        System.out.println("GOT HEREEEE");
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
            EG_write_to_JSON(dataList,name);
            counter = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Success";
    }

    public void EG_write_to_JSON(List<JSONObject> dataList, String name) {
        Gson gson = new Gson();
        try {
            FileWriter file = new FileWriter(String.format("E:\\springBootUploads\\%s.json", multiple ? "converted-" + name + "-" + counter : "converted-" + name));
            file.write(gson.toJson(dataList));
            file.close();
            counter++;
        } catch (IOException e) {
            System.out.println("theres been an error");
            e.printStackTrace();
        }

    }

}
