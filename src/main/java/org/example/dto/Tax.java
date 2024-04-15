package org.example.dto;

import java.math.BigDecimal;

public class Tax {
    //Tax information
    private String stateAbbreviation;
    private String stateName;
    private BigDecimal taxRate;

//    public Tax(String stateAbbreviation) {
//        StateAbbreviation = stateAbbreviation;
//    }

    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public String toString() {
        return "Tax{" +
                "stateAbbreviation='" + stateAbbreviation + '\'' +
                ", stateName='" + stateName + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}
