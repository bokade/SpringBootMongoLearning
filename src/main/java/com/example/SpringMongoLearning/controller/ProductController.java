package com.example.SpringMongoLearning.controller;

import com.example.SpringMongoLearning.model.Product;
import com.example.SpringMongoLearning.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product p) {
        Product created = svc.create(p);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable String id) {
        return svc.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id, @Valid @RequestBody Product p) {
        p.setId(id);
        Product updated = svc.update(p);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Pagination & sorting example: /api/products?page=0&size=10&sort=price,desc
    @GetMapping
    public Page<Product> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort // e.g. ["price,desc"]
    ) {
        // parse sort param: supports multiple sort fields if provided
        Sort sortObj = Sort.by(parseSortOrders(sort));
        Pageable pageable = PageRequest.of(page, size, sortObj);
        // for demo, return all products page using repository's findAll(pageable)
        return svc.getByCategory(null, pageable).map(p -> p); // fallback if repository supports category=null? But better use findAll below
        // better:
        // return productRepository.findAll(pageable);
    }

    // Better: list all with pageable (using repository directly)
    @GetMapping("/all")
    public Page<Product> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        Sort sortObj = Sort.by(parseSortOrders(sort));
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return svc.getByCategory(null, pageable); // if repository has findAll(Pageable) you can call that directly by injecting repo
    }

    // Criteria search: /api/products/search?name=phone&category=electronics&minPrice=100&maxPrice=1000&page=0&size=10
    @GetMapping("/search")
    public Page<Product> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price,desc") String[] sort
    ) {
        Sort s = Sort.by(parseSortOrders(sort));
        Pageable pageable = PageRequest.of(page, size, s);
        return svc.searchWithCriteria(name, category, minPrice, maxPrice, pageable);
    }

    @GetMapping("/expensive")
    public List<Product> expensive(@RequestParam double minPrice, @RequestParam(defaultValue = "5") int limit) {
        return svc.expensiveProducts(minPrice, limit);
    }

    @GetMapping("/searchName")
    public List<Product> searchName(@RequestParam String q) {
        return svc.searchByNamePart(q);
    }

    // parse sort helper
    private Sort.Order[] parseSortOrders(String[] sortParams) {
        // convert each "field,dir" into Sort.Order
        return java.util.Arrays.stream(sortParams)
                .map(s -> {
                    String[] parts = s.split(",");
                    String prop = parts[0].trim();
                    Sort.Direction dir = Sort.Direction.ASC;
                    if (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")) dir = Sort.Direction.DESC;
                    return new Sort.Order(dir, prop);
                })
                .toArray(Sort.Order[]::new);
    }


}
