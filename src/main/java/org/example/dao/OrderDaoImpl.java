package org.example.dao;

import org.example.dto.Order;
import org.example.dto.Product;
import org.example.dto.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
@Component
public class OrderDaoImpl implements OrderDao{
    @Autowired
    private JdbcTemplate jdbc;
    //Getting order by Id (orderNumber), will be used for Updating/removing an order
    @Override
    public Order getOrderId(LocalDate date, int orderNumber) {
        return jdbc.queryForObject("SELECT * FROM Orders WHERE orderDate = ? AND orderNumber = ?", new OrderMapper(), date, orderNumber);
    }
    //List orders, will be used in exporting everything
    @Override
    public List<Order> getAllOrders() {
        return jdbc.query("SELECT * FROM Orders", new OrderMapper());
    }
    //Getting all the orders for a certain date, will be used to list orders
    @Override
    public List<Order> getAllOrdersDate(LocalDate date) {
        return jdbc.query("SELECT * FROM Orders WHERE orderDate = ?", new OrderMapper(), date);
    }
    //Adding a new order
    @Override
    public Order createOrder(Order order) {
        jdbc.update("INSERT INTO Orders (orderNumber, customerName, stateAbbreviation, productType, TaxRate, area, costPerSquareFoot, " +
                    "laborCostPerSquareFoot, materialCost, laborCost, totalTax, total, orderDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", order.getOrderNumber(),
                    order.getCustomerName(), order.getTax().getStateAbbreviation(), order.getProduct().getProductType(), order.getTax().getTaxRate(), order.getArea(), order.getProduct().getCostPerSquareFoot(),
                    order.getProduct().getLaborCostPerSquareFoot(), order.getMaterialCost(), order.getLaborCost(), order.getTotalTax(), order.getTotal(), order.getOrderDate());
        return order;
    }
    //updating an existing order
    @Override
    public Order updateOrder(Order order) {
        jdbc.update("UPDATE Orders SET customerName = ?, stateAbbreviation = ?, productType = ?, TaxRate = ?, area = ?, costPerSquareFoot = ?, " +
                "laborCostPerSquareFoot = ?, materialCost = ?, laborCost = ?, totalTax = ?, total = ?, orderDate = ? WHERE orderNumber = ?", order.getCustomerName(),
                order.getTax().getStateAbbreviation(), order.getProduct().getProductType(), order.getTax().getTaxRate(), order.getArea(), order.getProduct().getCostPerSquareFoot(),
                order.getProduct().getLaborCostPerSquareFoot(), order.getMaterialCost(), order.getLaborCost(), order.getTotalTax(), order.getTotal(), order.getOrderDate(), order.getOrderNumber());
        return order;
    }
    //Deleting an existing order
    @Override
    public Order deleteOrder(LocalDate date, int orderNumber) {
        jdbc.update("DELETE FROM Orders WHERE orderDate = ? AND orderNumber = ?", date, orderNumber);
        return null;
    }
    private static final class OrderMapper implements RowMapper<Order> {
        //The JDBC template collects these Order objects into a List, which is then returned by the query() or queryForObject() method
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrderNumber(rs.getInt("orderNumber"));
            order.setCustomerName(rs.getString("customerName"));
            order.setArea(rs.getBigDecimal("area"));
            order.setMaterialCost(rs.getBigDecimal("materialCost"));
            order.setLaborCost(rs.getBigDecimal("laborCost"));
            order.setTotal(rs.getBigDecimal("total"));
            order.setTotalTax(rs.getBigDecimal("totalTax"));
            order.setOrderDate(rs.getDate("orderDate").toLocalDate());

            //Create a Tax object to hold data for the current row
            Tax tax = new Tax();
            tax.setStateAbbreviation(rs.getString("stateAbbreviation"));
            tax.setTaxRate(rs.getBigDecimal("taxRate"));
            order.setTax(tax);

            //Create a Product object to hold data for the current row
            Product product = new Product();
            product.setProductType(rs.getString("productType"));
            product.setCostPerSquareFoot(rs.getBigDecimal("costPerSquareFoot"));
            product.setLaborCostPerSquareFoot(rs.getBigDecimal("laborCostPerSquareFoot"));
            order.setProduct(product);

            //After all the properties of our objects are set we can return the Order object
            return order;
        }
    }
}
