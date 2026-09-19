package com.venta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VentaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VentaApplication.class, args);
	}

}
