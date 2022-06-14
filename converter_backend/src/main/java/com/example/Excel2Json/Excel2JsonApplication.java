package com.example.Excel2Json;

import com.example.Excel2Json.property.FileStorageProperties;
import com.github.opendevl.JFlat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;


class Helper extends TimerTask {
	public void run() {
		File dir = new File("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				System.out.println(child.getName());
				System.out.println(System.currentTimeMillis());
				long lastModified = child.lastModified();
				long epochTime = System.currentTimeMillis();
				if((epochTime - lastModified) > 600000){
					Path path = Paths.get(String.format("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\%s", child.getName()));
					try {
						Files.delete(path);
						System.out.println("Old files have been purged");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} else {

		}
	}
}

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})

public class Excel2JsonApplication {
	public static void main(String[] args) throws Exception {


		SpringApplication.run(Excel2JsonApplication.class, args);

		Timer timer = new Timer();
		TimerTask task = new Helper();

		timer.schedule(task , 1, 600000);

//		String str = new String(Files.readAllBytes(Paths.get("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\bb98f6b9-aea0-4be9-81fb-15193e5cdb34converted-Book1 - Copy (2) - Copy.json")));
//
//		JFlat flatMe = new JFlat(str);
//
////get the 2D representation of JSON document
//		List<Object[]> json2csv = flatMe.json2Sheet().getJsonAsSheet();
//
////write the 2D representation in csv format
//		flatMe.headerSeparator().write2csv("C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\bb98f6b9-aea0-4be9-81fb-15193e5cdb34converted-Book1 - Copy (2) - Copy.csv");
//
//		String csvFileAddress = "C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\bb98f6b9-aea0-4be9-81fb-15193e5cdb34converted-Book1 - Copy (2) - Copy.csv"; //csv file address
//		String xlsxFileAddress = "C:\\Users\\saaday\\Documents\\EG_excel_JSON_converter\\converter_backend\\springBootUploads\\bb98f6b9-aea0-4be9-81fb-15193e5cdb34converted-Book1 - Copy (2) - Copy.xlsx"; //xlsx file address
//		XSSFWorkbook workBook = new XSSFWorkbook();
//		XSSFSheet sheet = workBook.createSheet("sheet1");
//		String currentLine=null;
//		int RowNum=0;
//		BufferedReader br = new BufferedReader(new FileReader(csvFileAddress));
//		while ((currentLine = br.readLine()) != null) {
//			String strArr[] = currentLine.split(",");
//			RowNum++;
//			XSSFRow currentRow=sheet.createRow(RowNum);
//			for(int i=0;i<strArr.length;i++){
//				currentRow.createCell(i).setCellValue(strArr[i]);
//			}
//		}
//
//		FileOutputStream fileOutputStream =  new FileOutputStream(xlsxFileAddress);
//		workBook.write(fileOutputStream);
//		fileOutputStream.close();
//		System.out.println("Done");
	}


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/greeting-javaconfig").allowedOrigins("http://localhost:8080");
			}
		};
	}




}