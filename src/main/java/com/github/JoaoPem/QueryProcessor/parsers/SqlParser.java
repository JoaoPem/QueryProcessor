package com.github.JoaoPem.QueryProcessor.parsers;

import java.util.*;

public class SqlParser {

    private static final List<String> ALLOWED_KEYWORDS = Arrays.asList(
            "SELECT", "FROM", "WHERE", "JOIN", "ON", "AND"
    );
    private static final List<String> ALLOWED_OPERATORS = Arrays.asList(
            "=", ">", "<", "<=", ">=", "<>", "(", ")", ",", "*"
    );

    public List<String> validateSyntax(String sqlInput) {
        if (sqlInput == null || sqlInput.isBlank()) {
            return List.of("SQL input is empty.");
        }

        String normalizedSQL = sqlInput.replaceAll("\\s+", " ").trim().toUpperCase();
        normalizedSQL = normalizedSQL.replaceAll(";$", "");

        String[] tokens = normalizedSQL.split("(?<=\\s)|(?=\\s)|(?=[,()])|(?<=[,()])");

        List<String> errors = new ArrayList<>();
        for (String token : tokens) {
            if (token == null) continue;
            token = token.trim();
            if (token.isEmpty()) continue;

            if (!ALLOWED_KEYWORDS.contains(token)
                    && !ALLOWED_OPERATORS.contains(token)
                    && !token.matches("[A-Z0-9_.]+")) {
                errors.add("Invalid token: " + token);
            }
        }

        return errors.isEmpty() ? Collections.emptyList() : errors;
    }
}