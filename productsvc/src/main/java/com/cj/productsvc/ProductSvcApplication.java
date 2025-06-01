package com.cj.productsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductSvcApplication.class, args);
	}

}
