package com.jvli.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.jvli.project.config.WebSiteProperties;

@SpringBootApplication
@MapperScan(basePackages = "com.jvli.project.mapper")
@EnableCaching
public class JvliRabbitmqApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JvliRabbitmqApplication.class, args);
		System.out.println("Hello world, keep coding!");
	}
	

}
