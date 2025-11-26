package com.example.SpringMongoLearning.repository;

import com.example.SpringMongoLearning.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;

@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    public ProductCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Product> searchByCriteria(String name, String category, Double minPrice, Double maxPrice, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (name != null && !name.isBlank()) {
            criteria = criteria.and("name").regex(".*" + name + ".*", "i");
        }
        if (category != null && !category.isBlank()) {
            criteria = criteria.and("category").is(category);
        }
        if (minPrice != null) {
            criteria = (criteria.getCriteriaObject().isEmpty() ? Criteria.where("price").gte(minPrice) : criteria.and("price").gte(minPrice));
        }
        if (maxPrice != null) {
            criteria = (criteria.getCriteriaObject().isEmpty() ? Criteria.where("price").lte(maxPrice) : criteria.and("price").lte(maxPrice));
        }

        Query query = new Query();
        if (!criteria.getCriteriaObject().isEmpty()) {
            query.addCriteria(criteria);
        }

        long total = mongoTemplate.count(query, Product.class);
        // apply paging and sorting from pageable
        query.with(pageable);
        List<Product> list = mongoTemplate.find(query, Product.class);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<Product> findExpensiveProducts(double minPrice, int limit) {
        Query q = new Query();
        q.addCriteria(Criteria.where("price").gte(minPrice));
        q.limit(limit);
        q.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Order.desc("price")));
        return mongoTemplate.find(q, Product.class);
    }
}