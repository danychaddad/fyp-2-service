package com.forestfire.app.forests;

import java.awt.geom.Point2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import com.forestfire.app.forests.vertex.Vertex;
import com.forestfire.app.nodes.Node;
import com.forestfire.app.nodes.NodeRepository;
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
    private final NodeRepository nodeRepository;

    public ForestController(ForestRepository forestRepository, NodeRepository nodeRepository) {
        this.forestRepository = forestRepository;
        this.nodeRepository = nodeRepository;
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
        populateForestWithNodes(forest, 0.002f);
        return ResponseEntity.status(HttpStatus.CREATED).body(forest);
    }

    private void populateForestWithNodes(Forest forest, double minDistance) {
        if (forest.getVerticesOfForest().isEmpty())
            return;
        int i = 0;
        List<Point2D.Double> points = distributePoints(forest.getVerticesOfForest(), minDistance);
        for (Point2D.Double point : points) {
            if (i > 500)
                break;
            Node n = Node.builder()
                    .latitude((float) point.y)
                    .longitude((float) point.x)
                    .forestId(forest.getId())
                    .macAddress(forest.getId() + "mac" + i++)
                    .build();
            nodeRepository.save(n);
        }
    }

    public static List<Point2D.Double> distributePoints(List<Vertex> forestVertices, double minDistance) {
        List<Point2D.Double> points = new ArrayList<>();

        // Get bounding box
        double minLongitude = Double.MAX_VALUE, minLatitude = Double.MAX_VALUE;
        double maxLongitude = Double.MIN_VALUE, maxLatitude = Double.MIN_VALUE;

        for (Vertex vertex : forestVertices) {
            minLongitude = Math.min(minLongitude, vertex.getLongitude());
            minLatitude = Math.min(minLatitude, vertex.getLatitude());
            maxLongitude = Math.max(maxLongitude, vertex.getLongitude());
            maxLatitude = Math.max(maxLatitude, vertex.getLatitude());
        }

        // Create a polygon from the vertices
        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(forestVertices.getFirst().getLongitude(), forestVertices.getFirst().getLatitude());
        for (int i = 1; i < forestVertices.size(); i++) {
            polygon.lineTo(forestVertices.get(i).getLongitude(), forestVertices.get(i).getLatitude());
        }
        polygon.closePath();

        // Distribute points
        for (double longitude = minLongitude; longitude <= maxLongitude; longitude += minDistance) {
            for (double latitude = minLatitude; latitude <= maxLatitude; latitude += minDistance) {
                Point2D.Double candidatePoint = new Point2D.Double(longitude, latitude);

                if (polygon.contains(candidatePoint)) {
                    if (isFarEnough(points, candidatePoint, minDistance)) {
                        points.add(candidatePoint);
                    }
                }
            }
        }

        return points;
    }

    private static boolean isFarEnough(List<Point2D.Double> points, Point2D.Double candidate, double minDistance) {
        for (Point2D.Double point : points) {
            if (point.distance(candidate) < minDistance) {
                return false;
            }
        }
        return true;
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
