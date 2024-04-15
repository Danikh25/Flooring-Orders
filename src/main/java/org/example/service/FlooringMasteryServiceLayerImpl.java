package org.example.service;

import org.example.dao.OrderDao;
import org.example.dao.ProductDao;
import org.example.dao.TaxDao;
import org.example.dto.Order;
import org.example.dto.Product;
import org.example.dto.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**The service layer is responsible for the business logic of an application.
 * It sits between the DAOs and the controller.
 */
@Component
public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer{
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private TaxDao taxDao;

    public FlooringMasteryServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }

    //                                     List Orders
    @Override
    public List<Order> getAllOrdersDate(LocalDate date) throws FlooringMasteryNoOrdersFoundException {
        List<Order> orders = orderDao.getAllOrdersDate(date);
        if (orders == null | orders.isEmpty()) { //Check if empty list also not just null
            throw new FlooringMasteryNoOrdersFoundException("\nError: Could not list orders");
        }
        return orders;
    }
    public List<Order> getAllOrders(){
        return orderDao.getAllOrders();
    }

    //                                     Create Orders

    //Comparing the date enter with the date now
    @Override
    public LocalDate checkDateIsInFuture(LocalDate dateInput) throws FlooringMasteryDataValidationException {
        LocalDate currentDate = LocalDate.now();
        if (!dateInput.isAfter(currentDate)){
            throw new FlooringMasteryDataValidationException("Error: Date must be in the future.");
        }
        return dateInput;
    }
    //Validating if the name is limited to [a-z][0-9] and not blank
    @Override
    public void validateCustomerName(String customerNameInput) throws FlooringMasteryDataValidationException {
        if(!customerNameInput.matches("^[A-Za-z0-9., ]+$") || customerNameInput.isBlank()){
            throw new FlooringMasteryDataValidationException("Error: Invalid name format");
        }
    }
    //Check if the state abbr enter is in the table
    @Override
    public void checkStateIfAvailable(String stateAbbreviationInput) throws FlooringMasteryDataValidationException {
        List<Tax> taxesList = getAllTaxes();
        String stateAbbreviation = null;
        //Retrieve all the state abbreviation from database and then iterate over the list
        for (Tax tax : taxesList){
            if (tax.getStateAbbreviation().equalsIgnoreCase(stateAbbreviationInput)){
                //set the stateAbbreviation if it matches the input
                stateAbbreviation = tax.getStateAbbreviation();
                if (stateAbbreviation != null){
                    break;
                }
            }
        }
        //If it doesnt match display error and go back to the main menu
        if(stateAbbreviation == null){
            throw new FlooringMasteryDataValidationException("Error: We do not sell to " + stateAbbreviationInput + " currently.");
        }

        if(stateAbbreviationInput.length() != 2){
            throw new FlooringMasteryDataValidationException("Only 2 characters are necessary for state abbreviation.");
        }
    }
    //List of all the products in the database
    @Override
    public List<Product> getAllProducts() {
        return productDao.getAllProducts();
    }
    //Checking if the product is in the products table
    @Override
    public void checkProductTypeIfAvailable(String productTypeInput) throws FlooringMasteryDataValidationException {
        List<Product> productList = getAllProducts();
        String productType = null;

        for(Product product : productList){
            //set the productType if it matches the input
            if (product.getProductType().equalsIgnoreCase(productTypeInput)){
                productType = product.getProductType();
            }
        }
        //If it doesnt match display error and go back to the main menu
        if (productType == null){
            throw new FlooringMasteryDataValidationException("Error: The product " + productTypeInput + " is currently unavailable");
        }
    }
    //Get the productType from the database
    @Override
    public Product getProductType(String productType) throws FlooringMasteryDataValidationException {
        return productDao.getProductType(productType);
    }

    // CreateOrder() impl from dao
    @Override
    public Order createOrder(String verifyOrder, String customerNameInput, String stateAbbreviationInput, String productTypeInput,
                             BigDecimal taxRate, BigDecimal areaInput, BigDecimal costPerSquareFoot, BigDecimal laborCostPerSquareFoot, BigDecimal materialCost, BigDecimal laborCost, BigDecimal totalTax, BigDecimal total, LocalDate orderDateInput) throws FlooringMasteryDataValidationException {
        //Create an Order, tax and product objects and if confirmed by user set all the respective fields
            Order newOrder = null;
            if (verifyOrder.equalsIgnoreCase("Y")) {
                newOrder = new Order();
                newOrder.setCustomerName(customerNameInput);

                Tax taxObject = new Tax();
                taxObject.setStateAbbreviation(stateAbbreviationInput);
                taxObject.setTaxRate(taxRate);
                newOrder.setTax(taxObject);

                Product productObject = new Product();
                productObject.setProductType(productTypeInput);
                productObject.setCostPerSquareFoot(costPerSquareFoot);
                productObject.setLaborCostPerSquareFoot(laborCostPerSquareFoot);
                newOrder.setProduct(productObject);

                newOrder.setArea(areaInput);
                newOrder.setMaterialCost(materialCost);
                newOrder.setLaborCost(laborCost);
                newOrder.setTotalTax(totalTax);
                newOrder.setTotal(total);
                newOrder.setOrderDate(orderDateInput);
                //Call dao to create the order in database and then return it
                return orderDao.createOrder(newOrder);
            } else {
                throw new FlooringMasteryDataValidationException("\n Order not created. User chose not to save order");
            }
    }
    //                                               Update Order
    @Override
    public Order updateOrderCalculations(Order orderToEdit) throws FlooringMasteryDataValidationException {
        //get the updated state, product type and area and use them to obtain and update the other values.
        //if the values haven't actually been updated, they will just return the same old value.
        BigDecimal updatedTaxRate = null;
        BigDecimal updatedCostPerSquareFoot = null;
        BigDecimal updatedLaborCostPerSquareFoot = null;

        //if the state is not null, then get the new tax rate
        String updatedStateAbbreviation = orderToEdit.getTax().getStateAbbreviation();
        if (updatedStateAbbreviation != null) {
            Tax updatedTaxObj = taxDao.getTax(updatedStateAbbreviation);
            updatedTaxRate = updatedTaxObj.getTaxRate();
            orderToEdit.setTax(updatedTaxObj); // set the Tax object to the Order
        }
        //if the productType is not null, then get the new productType
        String updatedProductType = orderToEdit.getProduct().getProductType();
        if (updatedProductType != null) {
            Product updatedProduct = productDao.getProductType(updatedProductType);
            updatedCostPerSquareFoot = updatedProduct.getCostPerSquareFoot();
            updatedLaborCostPerSquareFoot = updatedProduct.getLaborCostPerSquareFoot();
            orderToEdit.setProduct(updatedProduct); // set the Product object to the Order
        }

        BigDecimal updatedArea = orderToEdit.getArea();
        //Get the updated calculated values
        BigDecimal updatedMaterialCost = this.calculateMaterialCost(updatedArea, updatedCostPerSquareFoot);
        BigDecimal updatedLaborCost = this.calculateLaborCost(updatedArea, updatedLaborCostPerSquareFoot);
        BigDecimal updatedTax = this.calculateTotalTax(updatedMaterialCost, updatedLaborCost, updatedTaxRate);
        BigDecimal updatedTotal = this.calculateTotal(updatedMaterialCost, updatedLaborCost, updatedTax);
        orderToEdit.setMaterialCost(updatedMaterialCost);
        orderToEdit.setLaborCost(updatedLaborCost);
        orderToEdit.setTotal(updatedTotal);

        //Update the orders calculated values
        return orderToEdit;
    }

    @Override
    public Order editOrder(String editedConfirmation, Order updatedOrder) throws FlooringMasteryNoOrdersFoundException {
        if (editedConfirmation.equalsIgnoreCase("Y")) {
            return orderDao.updateOrder(updatedOrder);
        } else throw new FlooringMasteryNoOrdersFoundException("Order not updated. User chose not to update order");
    }
    //Update customer name if needed
    @Override
    public Order updateOrderCustomerName(String updatedCustomerName, Order orderToEdit) {
        if (updatedCustomerName != null) {
            orderToEdit.setCustomerName(updatedCustomerName);
        }
        return orderToEdit;
    }
    //Update state abbr if needed
    @Override
    public Order updateOrderState(String updatedStateAbbreviation, Order orderToEdit) {
        if (updatedStateAbbreviation != null) {
            Tax updatedTaxObj = taxDao.getTax(updatedStateAbbreviation);
            orderToEdit.setTax(updatedTaxObj);
        }
        return orderToEdit;
    }
    //Update product type if needed
    @Override
    public Order updateOrderProductType(String updatedProductType, Order orderToEdit) {
        if (updatedProductType != null) {
            Product updatedProduct = productDao.getProductType(updatedProductType);
            orderToEdit.setProduct(updatedProduct);
        }
        return orderToEdit;
    }
    //Update order area if needed
    @Override
    public Order updateOrderArea(BigDecimal updatedArea, Order orderToEdit) {
        if (updatedArea != null) {
            orderToEdit.setArea(updatedArea);
        }
        return orderToEdit;
    }


    //                                         Delete Orders
    @Override
    public Order deleteOrder(String deleteConfirmation, LocalDate date, int orderNumber) throws FlooringMasteryNoOrdersFoundException {
        if (deleteConfirmation.equalsIgnoreCase("Y")) {
            return orderDao.deleteOrder(date, orderNumber);
        } else throw new FlooringMasteryNoOrdersFoundException("Order was not deleted");

    }

    @Override
    public Order getOrderId(LocalDate date, int orderNumber) throws FlooringMasteryNoOrdersFoundException {
        //throw a custom exception that indicates the order was not found
        try {
            return orderDao.getOrderId(date, orderNumber);
        } catch (EmptyResultDataAccessException e){
            throw new FlooringMasteryNoOrdersFoundException("No orders found with the order date and number provided");
        }

    }

    //                                       All Calculations logic
    @Override
    public void checkArea(BigDecimal areaInput) throws FlooringMasteryDataValidationException {
        if (areaInput.compareTo(new BigDecimal("100"))<0) {
            throw new FlooringMasteryDataValidationException(
                    "ERROR: the area is below the minimum order of 100 sqft");
        }
    }
    //MaterialCost = (Area * CostPerSquareFoot) and setting 2 decimal places after calculations
    @Override
    public BigDecimal calculateMaterialCost(BigDecimal area, BigDecimal costPerSquareFoot) throws FlooringMasteryDataValidationException {
        return area.multiply(costPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
    }
    //LaborCost = (Area * LaborCostPerSquareFoot)
    @Override
    public BigDecimal calculateLaborCost(BigDecimal area, BigDecimal laborCostPerSquareFoot) throws FlooringMasteryDataValidationException {
        return area.multiply(laborCostPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
    }
    //Tax = (MaterialCost + LaborCost) * (TaxRate/100)
    @Override
    public BigDecimal calculateTotalTax(BigDecimal materialCost, BigDecimal laborCost, BigDecimal taxRate) throws FlooringMasteryDataValidationException {
        return (materialCost.add(laborCost)).multiply(taxRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
    }
    //Total = (MaterialCost + LaborCost + Tax)
    @Override
    public BigDecimal calculateTotal(BigDecimal materialCost, BigDecimal laborCost, BigDecimal tax) throws FlooringMasteryDataValidationException {
        return materialCost.add(laborCost).add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Tax getTax(String stateAbbreviation) {
        return taxDao.getTax(stateAbbreviation);
    }

    @Override
    public List<Tax> getAllTaxes() {
        return taxDao.getAllTaxes();
    }
}
