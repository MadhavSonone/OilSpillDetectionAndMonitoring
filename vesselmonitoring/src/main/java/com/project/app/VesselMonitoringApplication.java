package com.project.app;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import com.project.app.service.UserService;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling 
@SpringBootApplication
@EnableCassandraRepositories(basePackages = "com.project.app.repository")
public class VesselMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(VesselMonitoringApplication.class, args);
	}
	
    @Bean
    ApplicationRunner init(UserService userService) {
        return args -> {
            userService.createAdminUser(); 
        };

    }
};
