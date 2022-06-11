package com.alladeson.caurisit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alladeson.caurisit.config.AppConfig;

@SpringBootApplication
@EnableConfigurationProperties({ AppConfig.class })
@EnableScheduling
@EnableAsync
public class CaurisItApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaurisItApplication.class, args);
	}

}
