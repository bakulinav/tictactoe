package dev.bakulin.ticktacktoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class TicktacktoeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicktacktoeApplication.class, args);
	}

}
