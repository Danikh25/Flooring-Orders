package org.example.UI;

import org.example.dto.Order;
import org.example.dto.Product;
import org.example.dto.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Component
public class FlooringMasteryView {
    @Autowired
    private UserIO io;

    public FlooringMasteryView(UserIO io) {
        this.io = io;
    }

    public int displayMenu(){
        io.print("   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("      * <<Flooring Program>>");
        io.print("      * 1. Display Orders");
        io.print("      * 2. Add an Order");
        io.print("      * 3. Edit an Order");
        io.print("      * 4. Remove an Order");
        io.print("      * 5. Export All Data");
        io.print("      * 6. Quit");
        io.print("   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

        return io.readInt("Select one of the above options", 1, 6);
    }


    //                        Views to display list of orders
    public LocalDate getOrderDateList(){
        io.print("");
        return io.readDate("Please enter the date of the order you want to look for in the format YYYY-MM-DD");
    }
    public void displayOrderListBanner(LocalDate orderDate) {
        io.print("");
        io.print("========= all Orders for " + orderDate + " =========");
    }

    public void displayAllOrders(List<Order> allOrders){
        io.print("");
        if (allOrders == null){
            io.print("No Orders Found in the system for this date");
        } else {
            String headings = getString();
            io.print(headings);

            io.print("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            for (Order currentOrder : allOrders){

                String orderInfo = String.format(
                        "%16s  %16s  %16s  %16s  %16s  %16s  %16s  %16s  %16s  %16s  %16s",
                        currentOrder.getCustomerName(),
                        currentOrder.getTax().getStateAbbreviation(),
                        currentOrder.getTax().getTaxRate().toString(),
                        currentOrder.getProduct().getProductType(),
                        currentOrder.getArea().toString(),
                        currentOrder.getProduct().getCostPerSquareFoot().toString(),
                        currentOrder.getProduct().getLaborCostPerSquareFoot().toString(),
                        currentOrder.getMaterialCost().toString(),
                        currentOrder.getLaborCost().toString(),
                        currentOrder.getTotalTax().toString(),
                        currentOrder.getTotal().toString());
                io.print(orderInfo);
            }
            io.print("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            io.readString("Please hit enter to continue");
        }
    }

    private static String getString() {
        String format = "%-16s  %-16s  %-16s  %-16s  %16s  %16s  %16s  %16s  %16s  %16s  %16s";
        return String.format(format,
                "Customer name",
                "State",
                "Tax rate",
                "Product",
                "Area",
                "Cost/sqft($)",
                "Labor cost/sqft($)",
                "Material cost($)",
                "Labor cost($)",
                "Tax($)",
                "Order Total($)");
    }


    //                                 View for creating an Order

    public LocalDate getOrderDateCreate(){
        return io.readDate("Please enter a date in the format YYYY-MM-DD");
    }
    public String getCustomerName() {
        return io.readString("Enter full name: ");
    }

    //Getting the state abbreviation by converting list into stream and uses lambda to display in desired way
    public String displayAvailableStateAbbreviation(List<Tax> taxList) {
            io.print("       === Available in these States ===");
            io.print("");
            taxList.stream().map(currentTax -> String.format("               %s  %s",
                    currentTax.getStateAbbreviation(),
                    currentTax.getStateName())).forEachOrdered(taxInfo -> {
                io.print(taxInfo);
            });
            io.print("");
            return io.readString("Enter your State Abbreviation: ");
    }
    //Get product type by converting list into stream and uses lambda to display in desired way
    public String displayAvailableProducts(List<Product> productList){
        io.print("         === Available products ===");
        io.print("Product type    CostPerSquareFoot   LaborCostPerSquareFoot ");
        productList.stream().map(currentProduct -> String.format("%-10s :      %7s          %7s",
                currentProduct.getProductType(),
                currentProduct.getCostPerSquareFoot().toString(),
                currentProduct.getLaborCostPerSquareFoot().toString())).forEachOrdered(productInfo -> {
            io.print(productInfo);
        });
        return io.readString("Which product type would you like? Please select from the list above");
    }
    public BigDecimal getArea() {
        return io.readBigDecimal("Please enter the area you required in sqft.");
    }
    //Displaying order summary and ask the user if they want to create it
    public String displayNewOrderSummary(LocalDate orderDateInput,String customerNameInput, String stateAbbreviationInput, String productTypeInput, BigDecimal areaInput, BigDecimal materialCost, BigDecimal laborCost, BigDecimal totalTax, BigDecimal total){
        io.print("========= New Order Summary =========");
        io.print("Order date:         " + orderDateInput);
        io.print("Customer Name:      " + customerNameInput);
        io.print("State Abbreviation: " + stateAbbreviationInput);
        io.print("Product:            " + productTypeInput);
        io.print("Area required:      " + areaInput + " sqft");
        io.print("Material cost:      $"+ materialCost);
        io.print("Labor cost:         $"+ laborCost);
        io.print("Total Tax:          $"+ totalTax);
        io.print("---------------------------------");
        io.print("Total:              $" + total);

        return io.readString("Do you want to place the order? (Y/N).");
    }
    public void displayCreateSuccessBanner(String verifyOrder){
        if (verifyOrder.equalsIgnoreCase("y")) {
            io.print("=== Order successfully created ===");
        } else{
            io.readString("Please press enter to continue.");
        }
    }
    //Overloading the displayCreateSuccessBanner method so it accepts either string ot Order object
    public void displayCreateSuccessBanner(Order newOrder){
        //if new order is null, user did not want to place their order
        if (newOrder==null){
            //do nothing, return to main menu
            io.readString("Please hit enter to continue to main menu.");
        } else {
            //If new order is not null
            io.print("=== Order Successfully Created ===");
        }
    }

    //                               View for updating an order

    public LocalDate getOrderDateUpdate(){
        return io.readDate("What is the date of the order you want to edit(YYYY-MM-DD)?");
    }
    public int getOrderNumberUpdate(){
        return io.readInt("What is the order number of the order you want to edit? ");
    }

    public String getEditCustomerName(Order updatedOrder){
        return io.readString("Enter the new customer name, name on file:(" + updatedOrder.getCustomerName()+"):");
    }

    public String getEditState (Order updatedOrder){
        return io.readString("Enter the new state name, state on file:(" + updatedOrder.getTax().getStateAbbreviation()+"):");
    }

    public String getEditProductType(Order updatedOrder){
        return io.readString("Enter new product type, product on file:(" + updatedOrder.getProduct().getProductType()+"):");
    }

    public String getEditArea(Order updatedOrder){
        return io.readString("Enter new the area, area on file:(" + updatedOrder.getArea()+"):");
    }
    public String displayUpdatedOrderSummary(LocalDate orderDateInput, Order updatedOrder){
        io.print("========= Updated Order Summary =========");
        io.print("Order date:         " + orderDateInput);
        io.print("Customer Name:      " + updatedOrder.getCustomerName());
        io.print("State Abbreviation: " + updatedOrder.getTax().getStateAbbreviation());
        io.print("Product:            " + updatedOrder.getProduct().getProductType());
        io.print("Area required:      " + updatedOrder.getArea().toString() + " sqft");
        io.print("Material cost:      $"+ updatedOrder.getMaterialCost().toString());
        io.print("Labor cost:         $"+ updatedOrder.getLaborCost());
        io.print("Tax:                $" + updatedOrder.getTotalTax().toString());
        io.print("---------------------------------");
        io.print("Total:              $" + updatedOrder.getTotal().toString());

        return io.readString("Do you want to make changes to this order? (Y/N).");
    }
    public void displayUpdatedSucessBanner(Order updatedOrder){
        if (updatedOrder==null) {
            //do nothing, return to main menu
            io.readString("Please hit enter to continue to main menu.");
        } else {
            //If edited order is not null
            io.print("=== Order Succesfully Edited ===");
        }
    }

    //                               View for removing an order

    public LocalDate getOrderDateRemove() {
        return io.readDate("What is the date of the order you want to remove?(YYYY-MM-DD)");
    }

    public int getOrderNumberRemove(){
        return io.readInt("What is the order number of the order you want to remove? ");
    }

    public String displayRemovedOrderSummary(LocalDate orderDateInput, Order order){
        io.print("========= Deleted Order Summary =========");
        io.print("Order date:         " + orderDateInput);
        io.print("Customer Name:      " + order.getCustomerName());
        io.print("State Abbreviation: " + order.getTax().getStateAbbreviation());
        io.print("Product:            " + order.getProduct().getProductType());
        io.print("Area required:      " + order.getArea().toString() + " sqft");
        io.print("Material cost:      $"+ order.getMaterialCost().toString());
        io.print("Labor cost:         $"+ order.getLaborCost());
        io.print("Tax:                $"+ order.getTotalTax().toString());
        io.print("---------------------------------");
        io.print("Total:              $" + order.getTotal().toString());

        return io.readString("Do you want to remove this order? (Y/N).");
    }
    //Display success banner from removing an order
    public void displayRemoveSuccessBanner(Order removedOrder){
        if (removedOrder==null) {
            io.print("=== Order Successfully Removed ===");
            io.readString("Please hit enter to continue to main menu.");
        }
    }


    //Rest of the views

    public void displayUnknownCommandMsg() {
        io.print("Unknown Command!");
    }

    public void displayExitMessage() {
        io.print("Goodbye!");
    }

    public void displayErrorMsg(String errorMsg) {
        io.print(errorMsg);
    }
    public void displayBanner(String action) {
        io.print("=== " + action.toUpperCase() + " ===");
    }
}
