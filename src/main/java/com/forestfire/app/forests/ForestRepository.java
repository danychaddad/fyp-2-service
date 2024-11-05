package com.forestfire.app.forests;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForestRepository extends MongoRepository<Forest, String> {
}
