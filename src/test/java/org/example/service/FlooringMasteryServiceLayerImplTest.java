package org.example.service;

import org.example.TestApplicationConfiguration;
import org.example.dao.*;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
public class FlooringMasteryServiceLayerImplTest {
    @Autowired
    private FlooringMasteryServiceLayer serviceDao;
    @Autowired
    private OrderDaoImpl orderDao;


    @BeforeEach
    public void setUp()  {
//        Delete all existing order before testing
//        List<Order> orders = serviceDao.getAllOrders();
//        for (Order order : orders) {
//            orderDao.deleteOrder(order.getOrderDate(), order.getOrderNumber());
//        }
    }
    private Order createTestOrder1(LocalDate testDate) {
        Order testOrder1 = new Order();

        // Create and set tax object for test order 1
        Tax tax1 = new Tax();
        tax1.setStateAbbreviation("CA");
        tax1.setTaxRate(new BigDecimal("25.00"));


        // Create and set product object for test order 1
        Product product1 = new Product();
        product1.setProductType("Carpet");
        product1.setCostPerSquareFoot(new BigDecimal("2.25"));
        product1.setLaborCostPerSquareFoot(new BigDecimal("2.10"));


        // Set properties for test order 1
        testOrder1.setCustomerName("Test Customer 1");
        testOrder1.setTax(tax1);
        testOrder1.setProduct(product1);
        testOrder1.setArea(new BigDecimal("150.00"));
        testOrder1.setMaterialCost(new BigDecimal("337.50"));
        testOrder1.setLaborCost(new BigDecimal("315.00"));
        testOrder1.setTotalTax(new BigDecimal("163.13"));
        testOrder1.setTotal(new BigDecimal("815.63"));
        testOrder1.setOrderDate(testDate);

        return testOrder1;
    }

    // Utility method to create test order 2 which will have data not valid to enter into the flooring database
    private Order createTestOrder2(LocalDate testDate) {
        Order testOrder2 = new Order();

        // Create and set tax object for test order 2
        Tax tax2 = new Tax();
        tax2.setStateAbbreviation("FL");
        tax2.setTaxRate(new BigDecimal("9.99"));


        // Create and set product object for test order 2
        Product product2 = new Product();
        product2.setProductType("Marble");
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


    //Testing application logic
    @Test
    public void testCheckDateIsInFuture() {
        LocalDate testDate = LocalDate.now();
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        LocalDate futureDate = LocalDate.of(2025, 1, 1);

        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.checkDateIsInFuture(pastDate));
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.checkDateIsInFuture(testDate));
        assertDoesNotThrow(() -> serviceDao.checkDateIsInFuture(futureDate));
    }
    @Test
    public void testValidateCustomerName() {
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.validateCustomerName(""));
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.validateCustomerName(" "));
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.validateCustomerName("David#1"));

        assertDoesNotThrow(() -> serviceDao.validateCustomerName("David12"));
        assertDoesNotThrow(() -> serviceDao.validateCustomerName("David, INC"));
    }
    @Test
    public void testCheckStateIfAvailable() {
        LocalDate testDate = LocalDate.now();
        Order testOrder1 = createTestOrder1(testDate);
        assertDoesNotThrow(() -> serviceDao.checkStateIfAvailable(testOrder1.getTax().getStateAbbreviation()));

        Order testOrder2 = createTestOrder2(testDate);
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.checkStateIfAvailable(testOrder2.getTax().getStateAbbreviation()));
    }
    @Test
    public void testCheckProductTypeIfAvailable() {
        assertDoesNotThrow(() -> serviceDao.checkProductTypeIfAvailable("Tile"));
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.checkProductTypeIfAvailable("Marble"));
    }
    //Test calculation logic
    @Test
    public void testCheckArea() {
        assertDoesNotThrow(() -> serviceDao.checkArea(new BigDecimal("150")));
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.checkArea(new BigDecimal("50")));
    }
    @Test
    public void testCalculateMaterialCost() throws FlooringMasteryDataValidationException {
        BigDecimal area = new BigDecimal("200");
        BigDecimal costPerSquareFoot = new BigDecimal("3.50");
        BigDecimal expectedMaterialCost = new BigDecimal("700.00");
        assertEquals(expectedMaterialCost, serviceDao.calculateMaterialCost(area, costPerSquareFoot));
    }
    @Test
    public void testCalculateLaborCost() throws FlooringMasteryDataValidationException {
        BigDecimal area = new BigDecimal("200");
        BigDecimal laborCostPerSquareFoot = new BigDecimal("4.15");
        BigDecimal expectedLaborCost = new BigDecimal("830.00");
        assertEquals(expectedLaborCost, serviceDao.calculateLaborCost(area, laborCostPerSquareFoot));
    }
    @Test
    public void testCalculateTotalTax() throws FlooringMasteryDataValidationException {
        BigDecimal materialCost = new BigDecimal("700.00");
        BigDecimal laborCost = new BigDecimal("830.00");
        BigDecimal taxRate = new BigDecimal("4.45");
        BigDecimal expectedTotalTax = new BigDecimal("68.09");
        assertEquals(expectedTotalTax, serviceDao.calculateTotalTax(materialCost, laborCost, taxRate));
    }

    @Test
    public void testCalculateTotal() throws FlooringMasteryDataValidationException {
        BigDecimal materialCost = new BigDecimal("700.00");
        BigDecimal laborCost = new BigDecimal("830.00");
        BigDecimal tax = new BigDecimal("68.09");
        BigDecimal expectedTotal = new BigDecimal("1598.09");
        assertEquals(expectedTotal, serviceDao.calculateTotal(materialCost, laborCost, tax));
    }

    //2.Testing createOrder
    @Test
    public void testCreateOrder() {
        // Create test data
        LocalDate testDate = LocalDate.now();
        Order testOrder1 = createTestOrder1(testDate);
        Order testOrder2 = createTestOrder2(testDate);

        // Call the method to test with testOrder1 (should not throw an exception)
        assertDoesNotThrow(() -> serviceDao.createOrder("Y", testOrder1.getCustomerName(), testOrder1.getTax().getStateAbbreviation(), testOrder1.getProduct().getProductType(),
                testOrder1.getTax().getTaxRate(), testOrder1.getArea(), testOrder1.getProduct().getCostPerSquareFoot(), testOrder1.getProduct().getLaborCostPerSquareFoot(), testOrder1.getMaterialCost(), testOrder1.getLaborCost(), testOrder1.getTotalTax(), testOrder1.getTotal(), testDate));

        // Call the method to test with testOrder2 (should throw an exception)
        assertThrows(FlooringMasteryDataValidationException.class, () -> serviceDao.createOrder("N", testOrder2.getCustomerName(), testOrder2.getTax().getStateAbbreviation(), testOrder2.getProduct().getProductType(),
                testOrder2.getTax().getTaxRate(), testOrder2.getArea(), testOrder2.getProduct().getCostPerSquareFoot(), testOrder2.getProduct().getLaborCostPerSquareFoot(), testOrder2.getMaterialCost(), testOrder2.getLaborCost(), testOrder2.getTotalTax(), testOrder2.getTotal(), testDate));
    }

    //Testing updateOrder logic
    @Test
    public void testUpdateOrderCalculations() throws FlooringMasteryDataValidationException {
        // Create test data
        LocalDate testDate = LocalDate.now();
        Order testOrder1 = createTestOrder1(testDate);

        // Call the method to test
        Order result = serviceDao.updateOrderCalculations(testOrder1);

        // Verify the result
        assertNotNull(result);
        assertEquals(testOrder1.getCustomerName(), result.getCustomerName());
        assertEquals(testOrder1.getTax().getStateAbbreviation(), result.getTax().getStateAbbreviation());
        assertEquals(testOrder1.getProduct().getProductType(), result.getProduct().getProductType());

        // Continue with other assertions
        assertEquals(testOrder1.getArea(), result.getArea());
        assertEquals(testOrder1.getMaterialCost(), result.getMaterialCost());
        assertEquals(testOrder1.getLaborCost(), result.getLaborCost());
        assertEquals(testOrder1.getTotalTax(), result.getTotalTax());
        assertEquals(testOrder1.getTotal(), result.getTotal());
        assertEquals(testOrder1.getOrderDate(), result.getOrderDate());
    }

    //3.Testing updateOrder
    @Test
    public void testEditOrder() {
        // Create test data
        LocalDate testDate = LocalDate.now();
        Order testOrder1 = createTestOrder1(testDate);

        // Call the method to test with editedConfirmation as "Y" (should not throw an exception)
        assertDoesNotThrow(() -> serviceDao.editOrder("Y", testOrder1));

        // Call the method to test with editedConfirmation as "N" (should throw an exception)
        assertThrows(FlooringMasteryNoOrdersFoundException.class, () -> serviceDao.editOrder("N", testOrder1));
    }

    //4.Testing deleteOrder
    @Test
    public void testDeleteOrder() {
        // Create test data
        LocalDate testDate = LocalDate.now();
        int orderNumber = 1;

        // Call the method to test with deleteConfirmation as "Y" (should not throw an exception)
        assertDoesNotThrow(() -> serviceDao.deleteOrder("Y", testDate, orderNumber));

        // Call the method to test with deleteConfirmation as "N" (should throw an exception)
        assertThrows(FlooringMasteryNoOrdersFoundException.class, () -> serviceDao.deleteOrder("N", testDate, orderNumber));
    }
    @Test
    public void testGetOrderId() {
        // Create test data
        LocalDate testDate = LocalDate.now();
        int orderNumber = 22;

        // Call the method to test with a valid order number (should not throw an exception)
        assertDoesNotThrow(() -> serviceDao.getOrderId(testDate, orderNumber));

        // Call the method to test with an invalid order number (should throw an exception)
        assertThrows(FlooringMasteryNoOrdersFoundException.class, () -> serviceDao.getOrderId(testDate, 1));
    }

}
