package com.github.JoaoPem.QueryProcessor.services;

import lombok.RequiredArgsConstructor;
import com.github.JoaoPem.QueryProcessor.parsers.SqlParser;
import com.github.JoaoPem.QueryProcessor.validators.SqlValidator;
import java.util.*;

@RequiredArgsConstructor
public class SqlService {

    private final SqlParser sqlParser;
    private final SqlValidator validator;

    public Map<String, List<String>> validate(String sql) {
        Map<String, List<String>> errors = new LinkedHashMap<>();

        List<String> syntaxErrors = sqlParser.validateSyntax(sql);
        if (!syntaxErrors.isEmpty()) errors.put("Syntax Errors", syntaxErrors);

        List<String> semanticErrors = validator.validateSemantics(sql);
        if (!semanticErrors.isEmpty()) errors.put("Semantic Errors", semanticErrors);

        return errors.isEmpty() ? Map.of() : errors;

    }
}