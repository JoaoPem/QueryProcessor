package com.github.JoaoPem.QueryProcessor.validators;

import com.github.JoaoPem.QueryProcessor.schema.Schema;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SqlValidator {

    private final Schema schema;

    public List<String> validateSemantics(String sqlInput) {

        List<String> errors = new ArrayList<>();
        if (sqlInput == null || sqlInput.isBlank()) {
            errors.add("SQL input is empty.");
            return errors;
        }

        // --- Normalização básica ---
        String sql = sqlInput.toUpperCase().replaceAll(";", "").trim();

        // --- Extrair tabelas e aliases ---
        Map<String, String> aliasToTable = extractTableAliases(sql);

        // --- Validar existência das tabelas ---
        for (String table : aliasToTable.values()) {
            if (!schema.getTables().contains(table)) {
                errors.add("Unknown table: " + table);
            }
        }

        if (aliasToTable.isEmpty()) {
            errors.add("No valid table found in query.");
            return errors;
        }

        // --- Validar SELECT ---
        validateSelectColumns(sql, aliasToTable, errors);

        // --- Validar WHERE ---
        validateWhereClause(sql, aliasToTable, errors);

        return errors.isEmpty() ? Collections.emptyList() : errors;
    }

    private Map<String, String> extractTableAliases(String sql) {
        Map<String, String> aliasMap = new HashMap<>();
        Pattern pattern = Pattern.compile("(FROM|JOIN)\\s+([A-Z0-9_]+)(?:\\s+([A-Z0-9_]+))?");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String table = matcher.group(2).trim();
            String alias = matcher.group(3) != null ? matcher.group(3).trim() : table;
            aliasMap.put(alias, table);
        }
        return aliasMap;
    }

    private void validateSelectColumns(String sql, Map<String, String> aliasToTable, List<String> errors) {
        List<String> columns = extractSelectColumns(sql);
        if (columns.isEmpty()) {
            errors.add("No columns specified after SELECT.");
            return;
        }

        for (String col : columns) {
            String cleanCol = col.trim();

            if (cleanCol.equals("*")) continue;

            cleanCol = cleanCol.replaceAll("[^A-Z0-9_.]", "");
            if (cleanCol.contains(".")) {
                String[] parts = cleanCol.split("\\.");
                String alias = parts[0];
                String column = parts[1];
                String table = aliasToTable.get(alias);
                if (table == null) errors.add("Unknown alias in SELECT: " + alias);
                else if (!schema.getColumns(table).contains(column))
                    errors.add("Unknown column in table " + table + ": " + column);
            } else {
                String firstTable = aliasToTable.values().iterator().next();
                if (!schema.getColumns(firstTable).contains(cleanCol))
                    errors.add("Unknown column in table " + firstTable + ": " + cleanCol);
            }
        }
    }

    private List<String> extractSelectColumns(String sql) {
        Matcher matcher = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            String columnsPart = matcher.group(1).trim();
            if (columnsPart.isEmpty()) return Collections.emptyList();
            return Arrays.asList(columnsPart.split("\\s*,\\s*"));
        }
        return Collections.emptyList();
    }

    private void validateWhereClause(String sql, Map<String, String> aliasToTable, List<String> errors) {
        int whereIndex = sql.indexOf("WHERE");
        if (whereIndex == -1) return; // não há WHERE

        String afterWhere = sql.substring(whereIndex + 5).trim();
        if (afterWhere.isEmpty()) {
            errors.add("WHERE clause is empty.");
            return;
        }

        afterWhere = afterWhere.split("\\b(GROUP|ORDER|LIMIT)\\b")[0].trim();

        // --- Apenas AND é aceito ---
        String[] parts = afterWhere.split("\\s+AND\\s+");
        for (String part : parts) {
            validateCondition(part.trim(), aliasToTable, errors);
        }
    }

    private void validateCondition(String condition, Map<String, String> aliasToTable, List<String> errors) {
        if (!condition.matches(".+\\s*(=|>|<|>=|<=|<>)\\s*.+")) {
            errors.add("Invalid condition in WHERE: " + condition);
            return;
        }

        String col = condition.split("\\s*(=|>|<|>=|<=|<>)\\s*")[0].trim();
        String table;
        String cleanCol;

        if (col.contains(".")) {
            String[] parts = col.split("\\.");
            String alias = parts[0];
            cleanCol = parts[1];
            table = aliasToTable.get(alias);
            if (table == null) {
                errors.add("Unknown alias in WHERE: " + alias);
                return;
            }
        } else {
            cleanCol = col;
            table = aliasToTable.values().iterator().next();
        }

        if (!schema.getColumns(table).contains(cleanCol)) {
            errors.add("Unknown column in WHERE: " + col);
        }
    }
}
