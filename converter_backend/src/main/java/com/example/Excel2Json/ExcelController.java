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
		EG_excel_to_multiple_JSON(data, name);
		return "Success";
	}

	@PostMapping("excel/singleJSON")
	public String EG_convertExcelToSingleJSON(@RequestParam("data") MultipartFile data, @RequestParam("name") String name) {
		EG_excel_to_single_JSON(data,name);
		return "Success";
	}

	private int counter = 1;
	private boolean multiple = false;
	public void EG_excel_to_multiple_JSON(MultipartFile data, String name) {
//		File directory = new File(String.format("C:\\Users\\saaday\\Documents\\%s" , name));
//		if(!directory.exists()){
//			directory.mkdirs();
//		}
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

	public void EG_excel_to_single_JSON(MultipartFile data, String name) {
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
	}

	public void EG_write_to_JSON(List<JSONObject> dataList, String name) {
		Gson gson = new Gson();
		try {
			FileWriter file = new FileWriter(String.format("C:\\Users\\saaday\\Documents\\JSON_dump\\%s.json", multiple ? name + "-" + counter : name));
			file.write(gson.toJson(dataList));
			file.close();
			counter++;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}
