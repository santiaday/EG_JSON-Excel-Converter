package com.example.Excel2Json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@RestController
public class ExcelController {

	@PostMapping("excel/multipleJSON")
	public String EG_convertExcelToMultipleJSON(@RequestParam("data") MultipartFile data, @RequestParam("name") String name) {
		return "Success";
	}

	@PostMapping("excel/singleJSON")
	public String EG_convertExcelToSingleJSON(@RequestParam("data") MultipartFile data, @RequestParam("name") String name) {
		return "Success";
	}




	
}
