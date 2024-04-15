DROP DATABASE IF EXISTS flooringTest;
CREATE DATABASE flooringtest;

USE flooringTest;

CREATE TABLE Product (
    productType VARCHAR(255) PRIMARY KEY,
    costPerSquareFoot DECIMAL(10, 2),
    laborCostPerSquareFoot DECIMAL(10, 2)
);

CREATE TABLE Tax (
    stateAbbreviation VARCHAR(2) PRIMARY KEY,
    stateName VARCHAR(255),
    taxRate DECIMAL(5, 2)
);


CREATE TABLE Orders (
    orderNumber INT AUTO_INCREMENT PRIMARY KEY,
    customerName VARCHAR(255),
    stateAbbreviation VARCHAR(2),
    productType VARCHAR(255),
    TaxRate DECIMAL(5, 2),
    area DECIMAL(10, 2),
    costPerSquareFoot DECIMAL(10, 2),
    laborCostPerSquareFoot DECIMAL(10, 2),
    materialCost DECIMAL(10, 2),
    laborCost DECIMAL(10, 2),
    totalTax DECIMAL(10, 2),
    total DECIMAL(10, 2),
    orderDate DATE,
	FOREIGN KEY (stateAbbreviation) REFERENCES Tax(stateAbbreviation),
    FOREIGN KEY (productType) REFERENCES Product(productType)
);