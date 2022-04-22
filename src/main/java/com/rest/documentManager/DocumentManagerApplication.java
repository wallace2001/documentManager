package com.rest.documentManager;

import com.rest.documentManager.entity.Profile;
import com.rest.documentManager.repository.ProfileRepository;
import com.rest.documentManager.repository.UserRepository;
import com.rest.documentManager.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DocumentManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentManagerApplication.class, args);
	}
}
