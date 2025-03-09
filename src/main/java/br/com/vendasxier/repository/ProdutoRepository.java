package br.com.vendasxier.repository;

import br.com.vendasxier.model.Produto;
import br.com.vendasxier.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ProdutoRepository {

    public List<Produto> getAllProdutos() {
        List<Produto> produtos = new ArrayList<>();

        try (Connection coon = DatabaseConfig.getConnection();
             Statement stmt = coon.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM produtos")) {
            while (rs.next()) {
                Produto p = new Produto(
                        rs.getString("nome"),
                        rs.getString("codigo"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco")
                );
                produtos.add(p);
                //Formatação correta para exibição
                System.out.printf("Código: %s | Nome: %-20s | Preço: R$%.2f%n",
                        p.getCodigo(), p.getNome(), p.getPreco());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtos;
    }

    // Adicione este metodo no ProdutoRepository
    public void atualizarEstoque(String codigo, int quantidade) {
        String sql = "UPDATE produtos SET quantidade = quantidade + ? WHERE codigo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantidade);
            ps.setString(2, codigo);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Produto> buscarPorCodigo(String codigo){
        String sql = "SELECT * FROM produtos WHERE codigo = ?";

        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return Optional.of(new Produto(
                        rs.getString("nome"),
                        rs.getString("codigo"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
