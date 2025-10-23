package com.github.JoaoPem.QueryProcessor;

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

		// Injetar schema no validator
		SqlValidator validator = new SqlValidator(schema);

		// Criar parser e service
		SqlParser parser = new SqlParser();
		SqlService service = new SqlService(parser, validator);

		// Inicializar UI
		SwingUtilities.invokeLater(() -> new QueryProcessorUI(service));
	}
}
