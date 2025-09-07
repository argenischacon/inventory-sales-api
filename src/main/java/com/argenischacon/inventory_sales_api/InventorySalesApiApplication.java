package com.argenischacon.inventory_sales_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InventorySalesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventorySalesApiApplication.class, args);
	}

}
