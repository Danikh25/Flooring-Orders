package org.example.service;

import org.example.dto.Order;
import org.example.dto.Product;
import org.example.dto.Tax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FlooringMasteryServiceLayer {


    // ---------List Order
    public List<Order> getAllOrders();
    public List<Order> getAllOrdersDate(LocalDate date) throws FlooringMasteryNoOrdersFoundException;

    //----------Create and Update Orders

    public LocalDate checkDateIsInFuture(LocalDate orderDate) throws FlooringMasteryDataValidationException;
    public void validateCustomerName(String customerNameInput) throws FlooringMasteryDataValidationException;
    public void checkStateIfAvailable(String stateAbbreviationInput) throws FlooringMasteryDataValidationException;
    public List <Product> getAllProducts();
    public void checkProductTypeIfAvailable(String productTypeInput) throws FlooringMasteryDataValidationException;
    public Product getProductType(String productType) throws FlooringMasteryDataValidationException;
    public Order createOrder(String verifyOrder, String customerNameInput, String stateAbbreviationInput, String productTypeInput,
                             BigDecimal taxRate, BigDecimal areaInput, BigDecimal costPerSquareFoot, BigDecimal laborCostPerSquareFoot, BigDecimal materialCost, BigDecimal laborCost, BigDecimal totalTax, BigDecimal total, LocalDate orderDateInput) throws FlooringMasteryDataValidationException;

    //-----------Update an order

    public Order updateOrderCalculations(Order updatedOrder) throws FlooringMasteryDataValidationException;
    public Order editOrder(String editedConfirmation, Order editedOrder) throws FlooringMasteryNoOrdersFoundException;
    public Order updateOrderCustomerName(String updatedCustomerName, Order orderToEdit);
    public Order updateOrderState(String updatedStateAbbreviation, Order orderToEdit);
    public Order updateOrderProductType(String updatedProductType, Order orderToEdit);
    public Order updateOrderArea(BigDecimal updatedArea, Order orderToEdit);

    //-----------Remove order

    public Order deleteOrder(String deleteConfirmation, LocalDate date, int orderNumber) throws FlooringMasteryNoOrdersFoundException;
    public Order getOrderId(LocalDate date, int orderNumber) throws FlooringMasteryNoOrdersFoundException;


    // ------------  ALL calculations
    void checkArea (BigDecimal areaInput) throws FlooringMasteryDataValidationException;
    BigDecimal calculateMaterialCost(BigDecimal area, BigDecimal costPerSquareFoot) throws FlooringMasteryDataValidationException;
    BigDecimal calculateLaborCost(BigDecimal area, BigDecimal laborCostPerSquareFoot) throws FlooringMasteryDataValidationException;
    BigDecimal calculateTotalTax(BigDecimal materialCost, BigDecimal laborCost,BigDecimal taxRate) throws FlooringMasteryDataValidationException;
    BigDecimal calculateTotal(BigDecimal materialCost, BigDecimal laborCost, BigDecimal tax) throws FlooringMasteryDataValidationException;

    public Tax getTax(String stateAbbreviation);
    public List<Tax> getAllTaxes();
}
