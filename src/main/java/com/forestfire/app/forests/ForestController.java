package com.forestfire.app.forests;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/forests")
@Slf4j
public class ForestController {

    private final ForestRepository forestRepository;

    public ForestController(ForestRepository forestRepository) {
        this.forestRepository = forestRepository;
    }

    @GetMapping
    public List<Forest> getAllForests() {
        return forestRepository.findAll();
    }

    @GetMapping("/{id}")
    public Forest getForestById(@PathVariable String id) {
        return forestRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Forest> addForest(@RequestBody Forest forest) {
        log.info("Adding forest with name: {} to the list", forest.getName());
        forestRepository.save(forest);
        return ResponseEntity.status(HttpStatus.CREATED).body(forest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Forest> updateForest(@PathVariable String id, @RequestBody Forest forest) {
        log.info("Updating forest with id: {}", id);
        forest.setId(id);
        forestRepository.save(forest);
        return ResponseEntity.status(HttpStatus.OK).body(forest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteForest(@PathVariable String id) {
        log.info("Deleting forest with id: {}", id);
        forestRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted forest with id: " + id);
    }
}
