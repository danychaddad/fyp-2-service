package com.forestfire.app;

import com.forestfire.app.sensors.SensorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class App {

    @Autowired
    private SensorReadingRepository sensorRepo;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
