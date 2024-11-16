package com.forestfire.app.nodes;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeRepository extends MongoRepository<Node, String> {
  List<Node> findByForestId(String forestId);
}