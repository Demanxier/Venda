package br.com.vendasxier.repository;

import br.com.vendasxier.model.*;
import br.com.vendasxier.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendaRepository {
    // Cadastrar nova venda com transação
    public void cadastrarVenda(Venda venda) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // 1. Insere a venda principal
            String sqlVenda = "INSERT INTO vendas (cliente_nome, forma_pagamento, total, data) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                psVenda.setString(1, venda.getCliente().getNome());
                psVenda.setString(2, venda.getFormaPagamento().name());
                psVenda.setDouble(3, venda.getTotal());
                psVenda.setTimestamp(4, Timestamp.valueOf(venda.getData()));
                psVenda.executeUpdate();

                // Obtém o ID gerado
                try (ResultSet rs = psVenda.getGeneratedKeys()) {
                    if (rs.next()) {
                        int vendaId = rs.getInt(1);

                        // 2. Insere os itens da venda
                        String sqlItem = "INSERT INTO itens_venda (venda_id, produto_codigo, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                            for (ItemVenda item : venda.getItens()) {
                                psItem.setInt(1, vendaId);
                                psItem.setString(2, item.getProduto().getCodigo());
                                psItem.setInt(3, item.getQuantidade());
                                psItem.setDouble(4, item.getProduto().getPreco());
                                psItem.addBatch();
                            }
                            psItem.executeBatch();
                        }
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao cadastrar venda: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Buscar vendas por cliente com JOIN
    public List<Venda> buscarVendasPorCliente(String clienteNome) {
        List<Venda> vendas = new ArrayList<>();
        String sql = """
            SELECT v.*, iv.*, p.nome as produto_nome 
            FROM vendas v
            JOIN itens_venda iv ON v.id = iv.venda_id
            JOIN produtos p ON iv.produto_codigo = p.codigo
            WHERE v.cliente_nome = ?""";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clienteNome);
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Venda> vendaMap = new HashMap<>();

                while (rs.next()) {
                    int vendaId = rs.getInt("id");

                    if (!vendaMap.containsKey(vendaId)) {
                        Venda venda = new Venda(
                                new Cliente(rs.getString("cliente_nome"), ""),
                                new ArrayList<>(),
                                FormaPagamento.valueOf(rs.getString("forma_pagamento")),
                                rs.getDouble("total"),
                                rs.getTimestamp("data").toLocalDateTime()
                        );
                        vendaMap.put(vendaId, venda);
                    }

                    ItemVenda item = new ItemVenda(
                            new Produto(
                                    rs.getString("produto_nome"),
                                    rs.getString("produto_codigo"),
                                    0, // Quantidade não é relevante aqui
                                    rs.getDouble("preco_unitario")
                            ),
                            rs.getInt("quantidade")
                    );
                    vendaMap.get(vendaId).getItens().add(item);
                }
                vendas.addAll(vendaMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vendas;
    }
}
