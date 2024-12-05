package com.forestfire.app.nodes;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeRepository extends MongoRepository<Node, String> {
  void deleteByForestId(String id);
  Node findByMacAddress(String macAddress);
  List<Node> findByForestId(String forestId);
  List<Node> findByLastReadingBefore(Date threshold);
}