package com.ecommerce.flashsales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@Configuration
@EnableDiscoveryClient
@ImportResource({"memcached.xml"})
public class InventoryManagerApplication {

	/*** rate limiter setting ***/
    private static double maxBudget = 100.0;
    private static double fillRatePerMs = 100.0;	
    public static RateLimiter rateLimiter = new RateLimiter(maxBudget, fillRatePerMs);
    
	public static void main(String[] args) {
		SpringApplication.run(InventoryManagerApplication.class, args);
	}
}
