package com.example.SpringMongoLearning.repository;

import com.example.SpringMongoLearning.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    // Derived query - find by category with paging
    Page<Product> findByCategory(String category, Pageable pageable);

    // find by name containing (search)
    List<Product> findByNameContainingIgnoreCase(String namePart);

    // Custom derived query for price less than
    List<Product> findByPriceLessThan(double price);
}