package com.github.JoaoPem.QueryProcessor.converters;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlToRelationalAlgebra {

    public String convert(String sql) {
        if (sql == null || sql.isBlank()) return "";

        String normalizedSQL = sql.toUpperCase().replaceAll(";", "").trim();

        List<String> selectColumns = extractSelectColumns(normalizedSQL);

        Map<String, String> aliasToTable = extractTableAliases(normalizedSQL);
        List<String> joinClauses = extractJoinConditions(normalizedSQL);

        String whereClause = extractWhereClause(normalizedSQL);

        StringBuilder algebra = new StringBuilder();

        // Projeção
        algebra.append("π[");
        algebra.append(String.join(", ", selectColumns));
        algebra.append("](");

        // Se houver junções
        if (!joinClauses.isEmpty()) {
            List<String> tables = new ArrayList<>();
            for (Map.Entry<String, String> entry : aliasToTable.entrySet()) {
                tables.add(entry.getValue());
            }

            String joinExpr = tables.get(0);
            for (int i = 1; i < tables.size(); i++) {
                joinExpr = joinExpr + " ⋈ " + tables.get(i) + " ON " + joinClauses.get(i-1);
            }

            // Se houver WHERE
            if (whereClause != null) {
                algebra.append("σ[").append(whereClause).append("](").append(joinExpr).append(")");
            } else {
                algebra.append(joinExpr);
            }
        } else {
            // Apenas uma tabela
            String table = aliasToTable.values().iterator().next();
            if (whereClause != null) {
                algebra.append("σ[").append(whereClause).append("](").append(table).append(")");
            } else {
                algebra.append(table);
            }
        }

        algebra.append(")");

        return algebra.toString();
    }

    private List<String> extractSelectColumns(String sql) {
        Matcher matcher = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            String[] cols = matcher.group(1).trim().split("\\s*,\\s*");
            return Arrays.asList(cols);
        }
        return List.of("*");
    }

    private Map<String, String> extractTableAliases(String sql) {
        Map<String, String> aliasMap = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(FROM|JOIN)\\s+([A-Z0-9_]+)(?:\\s+([A-Z0-9_]+))?");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String table = matcher.group(2).trim();
            String alias = matcher.group(3) != null ? matcher.group(3).trim() : table;
            aliasMap.put(alias, table);
        }
        return aliasMap;
    }

    private List<String> extractJoinConditions(String sql) {
        List<String> joins = new ArrayList<>();
        Pattern pattern = Pattern.compile("JOIN\\s+[A-Z0-9_]+\\s+[A-Z0-9_]+\\s+ON\\s+(.*?)\\s+(JOIN|WHERE|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql + " "); // adiciona espaço para pegar o último join
        while (matcher.find()) {
            joins.add(matcher.group(1).trim());
        }
        return joins;
    }

    private String extractWhereClause(String sql) {
        Pattern pattern = Pattern.compile("WHERE\\s+(.*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
