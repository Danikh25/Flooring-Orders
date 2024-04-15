package org.example.dao;

import org.example.dto.Product;

import java.util.List;

public interface ProductDao {
    //List that holds all the products to be added in an order
    public List<Product> getAllProducts();
    public Product getProductType(String productType);
}
