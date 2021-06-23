package com.shirley.demo_REST_api;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
	List<Product> findByNameLike(String productName);
	List<Product> findByNameLikeIgnoreCase(String name, Sort sort);
	boolean existsByName(String productName);
}
