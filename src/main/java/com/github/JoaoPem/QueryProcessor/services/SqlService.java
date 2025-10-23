package com.github.JoaoPem.QueryProcessor.services;

import com.github.JoaoPem.QueryProcessor.converters.SqlToRelationalAlgebra;
import lombok.RequiredArgsConstructor;
import com.github.JoaoPem.QueryProcessor.parsers.SqlParser;
import com.github.JoaoPem.QueryProcessor.validators.SqlValidator;
import java.util.*;

@RequiredArgsConstructor
public class SqlService {

    private final SqlParser sqlParser;
    private final SqlValidator validator;
    private final SqlToRelationalAlgebra sqlToRelationalAlgebra; // novo

    public Map<String, List<String>> validate(String sql) {
        Map<String, List<String>> results = new LinkedHashMap<>();

        List<String> syntaxErrors = sqlParser.validateSyntax(sql);
        if (!syntaxErrors.isEmpty()) results.put("Syntax Errors", syntaxErrors);

        List<String> semanticErrors = validator.validateSemantics(sql);
        if (!semanticErrors.isEmpty()) results.put("Semantic Errors", semanticErrors);


        if (syntaxErrors.isEmpty() && semanticErrors.isEmpty()) {
            String algebra = sqlToRelationalAlgebra.convert(sql);
            results.put("Relational Algebra", List.of(algebra));
        }

        return results.isEmpty() ? Map.of() : results;
    }
}