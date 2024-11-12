package com.forestfire.app.sensors;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SensorReadingRepository extends MongoRepository<SensorReading, String> {

    @Query("{id:'?0'}")
    SensorReading findSensorReadingById(String id);

    List<SensorReading> findSensorReadingsByNodeId(String nodeId);
}
