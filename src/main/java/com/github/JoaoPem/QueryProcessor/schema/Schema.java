package com.github.JoaoPem.QueryProcessor.schema;

import java.util.*;

public class Schema {
    private final Map<String, List<String>> tables = new HashMap<>();

    public Schema() {
        tables.put("PRODUCT", Arrays.asList("IDPRODUCT", "NAME", "PRICE", "QUANTITY"));
        tables.put("CATEGORY", Arrays.asList("IDCATEGORY", "DESCRIPTION"));
        tables.put("CUSTOMER", Arrays.asList("IDCUSTOMER", "NAME", "EMAIL"));
    }

    public List<String> getColumns(String table) {
        return tables.getOrDefault(table.toUpperCase(), Collections.emptyList());
    }

    public Set<String> getTables() {
        return tables.keySet();
    }
}
