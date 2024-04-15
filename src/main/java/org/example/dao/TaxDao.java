package org.example.dao;

import org.example.dto.Tax;

import java.util.List;

public interface TaxDao {
    public List<Tax> getAllTaxes();
    public Tax getTax(String stateAbbreviation);

}
