package org.example.dao;

import org.example.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component
public class ProductDaoImpl implements ProductDao{
    @Autowired
    private JdbcTemplate jdbc;
    @Override
    public List<Product> getAllProducts() {
        return jdbc.query("SELECT * FROM product", new ProductMapper());
    }

    @Override
    public Product getProductType(String productType) {
        return jdbc.queryForObject("SELECT * FROM Product WHERE productType = ?", new ProductMapper(), productType);
    }

    //The purpose of a RowMapper is to map each row of data in a ResultSet to an instance of the desired object(Product)
    private static final class ProductMapper implements RowMapper<Product>{

        @Override
        public Product mapRow(ResultSet rs, int index) throws SQLException {
            Product product = new Product();
            product.setProductType(rs.getString("productType"));
            product.setCostPerSquareFoot(rs.getBigDecimal("costPerSquareFoot"));
            product.setLaborCostPerSquareFoot(rs.getBigDecimal("laborCostPerSquareFoot"));
            return product;
        }
    }
}
