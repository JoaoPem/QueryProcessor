package com.github.JoaoPem.QueryProcessor;

import com.github.JoaoPem.QueryProcessor.converters.SqlToRelationalAlgebra;
import com.github.JoaoPem.QueryProcessor.parsers.SqlParser;
import com.github.JoaoPem.QueryProcessor.schema.Schema;
import com.github.JoaoPem.QueryProcessor.services.SqlService;
import com.github.JoaoPem.QueryProcessor.validators.SqlValidator;
import com.github.JoaoPem.QueryProcessor.views.QueryProcessorUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class QueryProcessorApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(QueryProcessorApplication.class);
		app.setHeadless(false);
		app.run(args);

		// Criar schema
		Schema schema = new Schema();

		// Criar parser, validator e conversor de álgebra relacional
		SqlParser parser = new SqlParser();
		SqlValidator validator = new SqlValidator(schema);
		SqlToRelationalAlgebra converter = new SqlToRelationalAlgebra();

		// Criar service com conversão
		SqlService service = new SqlService(parser, validator, converter);

		// Inicializar UI
		SwingUtilities.invokeLater(() -> new QueryProcessorUI(service));
	}
}
