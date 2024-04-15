package org.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Order {
    //Order information
    private int orderNumber;
    private String customerName;
    private BigDecimal area;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal total;
    private BigDecimal totalTax;
    private LocalDate orderDate;
    //Product and tax objects for better organization(encapsulating all the relevant details inside the order class)
    private Tax tax;
    private Product product;

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(BigDecimal materialCost) {
        this.materialCost = materialCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", customerName='" + customerName + '\'' +
                ", area=" + area +
                ", materialCost=" + materialCost +
                ", laborCost=" + laborCost +
                ", total=" + total +
                ", totalTax=" + totalTax +
                ", orderDate=" + orderDate +
                ", tax=" + tax +
                ", product=" + product +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderNumber == order.orderNumber && Objects.equals(customerName, order.customerName) && Objects.equals(area, order.area) && Objects.equals(materialCost, order.materialCost) && Objects.equals(laborCost, order.laborCost) && Objects.equals(total, order.total) && Objects.equals(totalTax, order.totalTax) && Objects.equals(orderDate, order.orderDate) && Objects.equals(tax, order.tax) && Objects.equals(product, order.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, customerName, area, materialCost, laborCost, total, totalTax, orderDate, tax, product);
    }
}
