package com.example.Excel2Json;

import com.example.Excel2Json.service.FilesStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class Excel2JsonApplication implements CommandLineRunner {
	@Resource
	FilesStorageService storageService;
	public static void main(String[] args) {
		SpringApplication.run(Excel2JsonApplication.class, args);
	}
	@Override
	public void run(String... arg) throws Exception {
		storageService.deleteAll();
		storageService.init();
	}

}