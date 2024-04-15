package org.example;

import org.example.UI.FlooringMasteryView;
import org.example.UI.UserIO;
import org.example.UI.UserIOConsoleImpl;
import org.example.controller.FlooringMasteryController;
import org.example.dao.*;
import org.example.dto.Tax;
import org.example.service.FlooringMasteryServiceLayer;
import org.example.service.FlooringMasteryServiceLayerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
public class App {
    public static void main(String[] args) throws Exception {
        UserIO myIO = new UserIOConsoleImpl();
        FlooringMasteryView myView = new FlooringMasteryView(myIO);
        OrderDao myOrderDao = new OrderDaoImpl();
        ProductDao myProductDao = new ProductDaoImpl();
        TaxDao myTaxDao = new TaxDaoImpl();
        FlooringMasteryServiceLayer service = new FlooringMasteryServiceLayerImpl(myOrderDao, myProductDao, myTaxDao);

        FlooringMasteryController controller = new FlooringMasteryController(myView, service);

        ConfigurableApplicationContext ctx = SpringApplication.run(App.class);

        //Run the run command inside the controller
        //Do not use the CommandLineRunner
        ctx.getBean("flooringMasteryController", FlooringMasteryController.class).run();
    }
}