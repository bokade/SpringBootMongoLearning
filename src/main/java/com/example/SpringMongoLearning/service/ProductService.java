package com.example.SpringMongoLearning.service;

import com.example.SpringMongoLearning.model.Product;
import com.example.SpringMongoLearning.repository.ProductRepository;
import com.example.SpringMongoLearning.repository.ProductCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;

    public ProductService(ProductRepository productRepository, ProductCustomRepository productCustomRepository) {
        this.productRepository = productRepository;
        this.productCustomRepository = productCustomRepository;
    }

    // CRUD using repository
    public Product create(Product p) {
        p.setId(null); // ensure create
        return productRepository.save(p);
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    public Product update(Product p) {
        if (p.getId() == null) throw new IllegalArgumentException("id required for update");
        return productRepository.save(p);
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }

    public Page<Product> getByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> searchWithCriteria(String name, String category, Double minPrice, Double maxPrice, Pageable pageable) {
        return productCustomRepository.searchByCriteria(name, category, minPrice, maxPrice, pageable);
    }

    public List<Product> expensiveProducts(double minPrice, int limit) {
        return productCustomRepository.findExpensiveProducts(minPrice, limit);
    }

    public List<Product> searchByNamePart(String part) {
        return productRepository.findByNameContainingIgnoreCase(part);
    }
}