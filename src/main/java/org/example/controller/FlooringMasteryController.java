package org.example.controller;

import org.example.UI.FlooringMasteryView;
import org.example.UI.UserIO;
import org.example.UI.UserIOConsoleImpl;
import org.example.dao.OrderDao;
import org.example.dao.ProductDao;
import org.example.dao.TaxDao;
import org.example.dto.Order;
import org.example.dto.Product;
import org.example.dto.Tax;
import org.example.service.FlooringMasteryDataValidationException;
import org.example.service.FlooringMasteryNoOrdersFoundException;
import org.example.service.FlooringMasteryServiceLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class FlooringMasteryController {

    @Autowired
    private FlooringMasteryView view;
    @Autowired
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasteryView view, FlooringMasteryServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run(String... args) throws Exception {
        boolean keepGoing = true;
        int menuSelection = 0;

            while (keepGoing) {
                try{
                    menuSelection = getMenuSelection();
                    //menuSelection = 1;
                    switch (menuSelection) {
                        case 1:
                            listOrders();
                            break;
                        case 2:
                            createOrder();
                            break;
                        case 3:
                            updateOrder();
                            break;
                        case 4:
                            removeOrder();
                            break;
                        case 5:
                            //exportAllData();
                            break;
                        case 6:
                            keepGoing = false;
                            break;
                        default:
                            unknownCommand();
                    }

                } catch (FlooringMasteryDataValidationException | FlooringMasteryNoOrdersFoundException e){
                        view.displayErrorMsg(e.getMessage());
                    }
                exitMessage();
            }

    }

    //Displaying the menu
    private int getMenuSelection(){
        return view.displayMenu();
    }


    private void listOrders() throws FlooringMasteryDataValidationException, FlooringMasteryNoOrdersFoundException{
        view.displayBanner(" List Order Menu");
        //boolean hasErrors = false;
        List<Order> orderList = null;

            try {
                //Get the input date
                LocalDate currentInputDate = view.getOrderDateList();
                //Check if an order exists for this date
                orderList = service.getAllOrdersDate(currentInputDate);
                view.displayOrderListBanner(currentInputDate);
                //hasErrors = false;
            } catch (DateTimeException | FlooringMasteryNoOrdersFoundException e){
                //hasErrors = true;
                view.displayErrorMsg(e.getMessage());
            }

        view.displayAllOrders(orderList);

    }
    
    private void createOrder() throws FlooringMasteryDataValidationException{
        view.displayBanner("Create Order Menu");
        boolean hasErrors = false;
        do {
            try {
                //get the input date from the user
                LocalDate orderInputDate = view.getOrderDateCreate();
                //Check if its in the future
                service.checkDateIsInFuture(orderInputDate);

                //Get the customer's name
                String customerNameInput = view.getCustomerName();
                //Validate the name format
                service.validateCustomerName(customerNameInput);

                //Get the list of Tax objects from the service layer
                List<Tax> taxList = service.getAllTaxes();
                String stateAbbreviationInput = view.displayAvailableStateAbbreviation(taxList);
                //Check if the state exists in the database
                service.checkStateIfAvailable(stateAbbreviationInput);
                Tax stateSelected = service.getTax(stateAbbreviationInput);
                BigDecimal taxRate = stateSelected.getTaxRate();

                //Get the list of Product objects from the service layer
                List<Product> availableProducts = service.getAllProducts();
                String productTypeInput = view.displayAvailableProducts(availableProducts);
                //Check if the productType exists in the database
                service.checkProductTypeIfAvailable(productTypeInput);
                Product productSelected = service.getProductType(productTypeInput);

                //Get the area input
                BigDecimal areaInput = view.getArea();
                //Check if more than 100 sqft
                service.checkArea(areaInput);

                //Calculations: MaterialCost, LaborCost, Total Tax, Total
                BigDecimal materialCost = service.calculateMaterialCost(areaInput, productSelected.getCostPerSquareFoot());
                BigDecimal laborCost = service.calculateLaborCost(areaInput, productSelected.getLaborCostPerSquareFoot());
                BigDecimal totalTax = service.calculateTotalTax(materialCost, laborCost, stateSelected.getTaxRate());
                BigDecimal total = service.calculateTotal(materialCost, laborCost, totalTax);


                //Display the order summary with (Y/N)?
                String verifyOrder = view.displayNewOrderSummary(orderInputDate, customerNameInput, stateAbbreviationInput,
                        productTypeInput, areaInput, materialCost, laborCost, totalTax, total);
                if(verifyOrder.equalsIgnoreCase("n")) {
                    view.displayBanner("Order not created. User chose not to save order");
                    return; // Return to main menu
                }
                //If confirmed create new order in database
                service.createOrder(verifyOrder, customerNameInput, stateAbbreviationInput,
                        productTypeInput, taxRate, areaInput, productSelected.getCostPerSquareFoot(), productSelected.getLaborCostPerSquareFoot(), materialCost, laborCost, totalTax, total, orderInputDate);

                view.displayCreateSuccessBanner(verifyOrder);
                hasErrors = false;

            } catch (FlooringMasteryDataValidationException e) {
                view.displayErrorMsg(e.getMessage());
                hasErrors = true;
            }
        }while (hasErrors);

    }

    private void updateOrder() throws FlooringMasteryNoOrdersFoundException, FlooringMasteryDataValidationException {
        view.displayBanner("Update Order Menu");
        boolean hasErrors = false;
        do {
            try {
                //Ask the user to the order date and number
                LocalDate orderDateInput = view.getOrderDateUpdate();
                int orderNumberInput = view.getOrderNumberUpdate();

                //Check if order exists in the database
                Order orderToEdit = service.getOrderId(orderDateInput, orderNumberInput);

                //Name edit
                String updatedCustomerName = view.getEditCustomerName(orderToEdit);
                service.validateCustomerName(updatedCustomerName);
                Order updatedOrder = service.updateOrderCustomerName(updatedCustomerName, orderToEdit);

                //State abbr edit
                String updatedStateAbbreviation = view.getEditState(orderToEdit);
                service.checkStateIfAvailable(updatedStateAbbreviation);
                updatedOrder = service.updateOrderState(updatedStateAbbreviation, orderToEdit);

                //Product type to edit
                String updatedProductType = view.getEditProductType(orderToEdit);
                service.checkProductTypeIfAvailable(updatedProductType);
                updatedOrder = service.updateOrderProductType(updatedProductType, orderToEdit);

                //Area to edit
                String updatedAreaString = view.getEditArea(orderToEdit);
                BigDecimal updatedArea = new BigDecimal(updatedAreaString);
                service.checkArea(updatedArea);
                updatedOrder = service.updateOrderArea(updatedArea, orderToEdit);

                //Update Calculations
                updatedOrder = service.updateOrderCalculations(updatedOrder);

                String verifyEdit = view.displayUpdatedOrderSummary(orderDateInput, orderToEdit);
                if (verifyEdit.equalsIgnoreCase("n")) {
                    view.displayBanner("Order not updated. User chose not to update order");
                    return;//return to main menu
                }
                //If confirmed edit the order in database
                Order editedOrder = service.editOrder(verifyEdit, updatedOrder);
                view.displayUpdatedSucessBanner(editedOrder);
                hasErrors = false;

            } catch (FlooringMasteryNoOrdersFoundException | FlooringMasteryDataValidationException e) {
                view.displayErrorMsg(e.getMessage());
                hasErrors = true;
            }
        }while (hasErrors);
    }
    private void removeOrder() throws  FlooringMasteryNoOrdersFoundException{
        view.displayBanner("Remove Order Menu");

        try {
            //Ask the user to the order date and number
            LocalDate orderDateInput = view.getOrderDateRemove();
            int orderNumberInput = view.getOrderNumberRemove();

            //Check if exists
            Order orderToRemove = service.getOrderId(orderDateInput, orderNumberInput);

            //Display the order infos
            //Ask the user if they want to remove
            String verifyRemove = view.displayRemovedOrderSummary(orderDateInput, orderToRemove);

            //If 'Y' is selected
            Order removedOrder = service.deleteOrder(verifyRemove, orderDateInput, orderNumberInput);
            view.displayRemoveSuccessBanner(removedOrder);
        }catch (FlooringMasteryNoOrdersFoundException  e){
            view.displayErrorMsg(e.getMessage());
        }
    }



    private void exitMessage() {
        view.displayExitMessage();
    }
    private void unknownCommand() {
        view.displayUnknownCommandMsg();
    }
}


