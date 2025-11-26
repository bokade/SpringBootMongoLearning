package com.example.SpringMongoLearning.repository;

import com.example.SpringMongoLearning.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductCustomRepository {
    Page<Product> searchByCriteria(String name, String category, Double minPrice, Double maxPrice, Pageable pageable);
    List<Product> findExpensiveProducts(double minPrice, int limit);
}