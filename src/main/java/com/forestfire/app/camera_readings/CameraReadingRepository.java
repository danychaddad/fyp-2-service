package com.forestfire.app.camera_readings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CameraReadingRepository extends MongoRepository<CameraReading, String> {
  List<CameraReading> findCameraReadingsByNodeId(String nodeId);
  Optional<CameraReading> findTopByNodeIdOrderByTimestampDesc(String nodeId);
}