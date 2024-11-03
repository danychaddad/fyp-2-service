package com.forestfire.app.nodes;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeRepository extends MongoRepository<Node, String> {
}
