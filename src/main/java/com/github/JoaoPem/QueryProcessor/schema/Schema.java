package com.github.JoaoPem.QueryProcessor.schema;

import java.util.*;

public class Schema {
    private final Map<String, List<String>> tables = new HashMap<>();

    public Schema() {
        tables.put("Categoria", Arrays.asList("idCategoria", "Descricao"));
        tables.put("Produto", Arrays.asList("idProduto", "Nome", "Descricao", "Preco", "QuantEstoque", "Categoria_idCategoria"));
        tables.put("TipoCliente", Arrays.asList("idTipoCliente", "Descricao"));
        tables.put("Cliente", Arrays.asList("idCliente", "Nome", "Email", "Nascimento", "Senha", "TipoCliente_idTipoCliente", "DataRegistro"));
        tables.put("TipoEndereco", Arrays.asList("idTipoEndereco", "Descricao"));
        tables.put("Endereco", Arrays.asList("idEndereco", "EnderecoPadrao", "Logradouro", "Numero", "Complemento", "Bairro", "Cidade", "UF", "CEP", "TipoEndereco_idTipoEndereco", "Cliente_idCliente"));
        tables.put("Telefone", Arrays.asList("Numero", "Cliente_idCliente"));
        tables.put("Status", Arrays.asList("idStatus", "Descricao"));
        tables.put("Pedido", Arrays.asList("idPedido", "Status_idStatus", "DataPedido", "ValorTotalPedido", "Cliente_idCliente"));
        tables.put("Pedido_has_Produto", Arrays.asList("idPedidoProduto", "PrecoUnitario"));
    }


    public List<String> getColumns(String table) {
        return tables.getOrDefault(table.toUpperCase(), Collections.emptyList());
    }

    public Set<String> getTables() {
        return tables.keySet();
    }
}
