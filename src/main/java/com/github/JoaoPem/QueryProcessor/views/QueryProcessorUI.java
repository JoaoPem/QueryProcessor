package com.github.JoaoPem.QueryProcessor.views;

import com.github.JoaoPem.QueryProcessor.services.SqlService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class QueryProcessorUI extends JFrame {

    private final JTextArea sqlInputArea = new JTextArea(5, 40);
    private final JTextArea outputArea = new JTextArea(10, 40);
    private final SqlService sqlService;

    public QueryProcessorUI(SqlService sqlService) {
        this.sqlService = sqlService;

        setTitle("Query Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter SQL Query"));
        inputPanel.add(new JScrollPane(sqlInputArea), BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Validation Results"));
        outputArea.setEditable(false);
        resultsPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton validateButton = new JButton("Validate SQL");
        buttonPanel.add(validateButton);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.SOUTH);

        validateButton.addActionListener(e -> validateSql());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void validateSql() {
        outputArea.setText("");
        String sql = sqlInputArea.getText();

        Map<String, List<String>> errors = sqlService.validate(sql);

        if (errors.isEmpty()) {
            outputArea.append("SQL is valid!");
        } else {
            errors.forEach((key, list) -> {
                outputArea.append(key + ":\n");
                list.forEach(err -> outputArea.append("  " + err + "\n"));
            });
        }
    }
}

