package org.example.dao;

import org.example.TestApplicationConfiguration;
import org.example.dto.Order;
import org.example.dto.Product;
import org.example.dto.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
public class OrderDaoImplTest {

    @Autowired
    private OrderDaoImpl testDao;
    @Autowired
    private TaxDaoImpl taxDao;
    @Autowired
    private ProductDaoImpl productDao;

    @BeforeEach
    public void setUp() {
        // Delete all existing orders
        List<Order> orders = testDao.getAllOrders();
        for (Order order : orders) {
            testDao.deleteOrder(order.getOrderDate(), order.getOrderNumber());
        }
        }

        //Here we will assume the user entered all the correct values just to test the listAllOrders
    // Utility method to create test order 1
    private Order createTestOrder1(LocalDate testDate) {
        Order testOrder1 = new Order();

        // Create and set tax object for test order 1
        Tax tax1 = new Tax();
        tax1.setStateAbbreviation("TX");
        tax1.setTaxRate(new BigDecimal("4.45"));


        // Create and set product object for test order 1
        Product product1 = new Product();
        product1.setProductType("Tile");
        product1.setCostPerSquareFoot(new BigDecimal("3.50"));
        product1.setLaborCostPerSquareFoot(new BigDecimal("4.15"));


        // Set properties for test order 1
        testOrder1.setCustomerName("Test Customer 1");
        testOrder1.setTax(tax1);
        testOrder1.setProduct(product1);
        testOrder1.setArea(new BigDecimal("100.00"));
        testOrder1.setMaterialCost(new BigDecimal("200.00"));
        testOrder1.setLaborCost(new BigDecimal("300.00"));
        testOrder1.setTotalTax(new BigDecimal("50.00"));
        testOrder1.setTotal(new BigDecimal("500.00"));
        testOrder1.setOrderDate(testDate);

        return testOrder1;
    }

    // Utility method to create test order 2
    private Order createTestOrder2(LocalDate testDate) {
        Order testOrder2 = new Order();

        // Create and set tax object for test order 2
        Tax tax2 = new Tax();
        tax2.setStateAbbreviation("WA");
        tax2.setTaxRate(new BigDecimal("9.25"));


        // Create and set product object for test order 2
        Product product2 = new Product();
        product2.setProductType("Wood");
        product2.setCostPerSquareFoot(new BigDecimal("5.15"));
        product2.setLaborCostPerSquareFoot(new BigDecimal("4.75"));


        // Set properties for test order 2
        testOrder2.setCustomerName("Test Customer 2");
        testOrder2.setTax(tax2);
        testOrder2.setProduct(product2);
        testOrder2.setArea(new BigDecimal("200.00"));
        testOrder2.setMaterialCost(new BigDecimal("400.00"));
        testOrder2.setLaborCost(new BigDecimal("600.00"));
        testOrder2.setTotalTax(new BigDecimal("100.00"));
        testOrder2.setTotal(new BigDecimal("1000.00"));
        testOrder2.setOrderDate(testDate);

        return testOrder2;
    }

    //1.Testing listOrders
        @Test
        public void testGetAllOrdersDate() {
            // Create a test date
            LocalDate testDate = LocalDate.now();

            // Create a test order 1
            Order testOrder1 = createTestOrder1(testDate);
            // Add the order to the database
            testDao.createOrder(testOrder1);

            // Create a test order 2
            Order testOrder2 = createTestOrder2(testDate);
            // Add the order to the database
            testDao.createOrder(testOrder2);

            // Retrieve all orders
            List<Order> orders = testDao.getAllOrders();

            // Assert that the list contains the correct number of orders
            assertEquals(2, orders.size());

    }
}
