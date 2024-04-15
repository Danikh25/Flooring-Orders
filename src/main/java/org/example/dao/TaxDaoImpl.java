package org.example.dao;

import org.example.dto.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component
public class TaxDaoImpl implements TaxDao{
    @Autowired
    JdbcTemplate jdbc;

    @Override
    public List<Tax> getAllTaxes() {
        return jdbc.query("SELECT * FROM Tax", new TaxMapper());
    }

    @Override
    public Tax getTax(String stateAbbreviation) {
        return jdbc.queryForObject("SELECT * FROM Tax WHERE stateAbbreviation = ?", new TaxMapper(), stateAbbreviation);
    }

    private static final class TaxMapper implements RowMapper<Tax>{

        @Override
        public Tax mapRow(ResultSet rs, int index) throws SQLException {
            Tax tax = new Tax();
            tax.setStateAbbreviation(rs.getString("stateAbbreviation"));
            tax.setStateName(rs.getString("stateName"));
            tax.setTaxRate(rs.getBigDecimal("taxRate"));
            return tax;
        }
    }

}
