package com.borczeangelov.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.borczeangelov.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    
}
