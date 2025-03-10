package br.com.vendasxier.repository;

import br.com.vendasxier.model.Cliente;
import br.com.vendasxier.model.ClienteVIP;
import br.com.vendasxier.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    // Metodo para inserir novo cliente.
    public void cadastrarCliente(Cliente cliente){
        String sql = "INSERT INTO clientes (nome, endereco, is_vip) VALUES (?,?,?)";

        // Try-with-resources: fecha automaticamente conexão e statement
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEndereco());
            ps.setBoolean(3, cliente instanceof ClienteVIP);
            ps.executeUpdate();

            System.out.println("Cliente cadastrado com sucesso: " + cliente.getNome());
        }catch (SQLException e){
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    // Metodo para atualizar o cliente existente
    public void atualizarCliente(Cliente cliente){
        String sql = "UPDATE clientes SET endereco = ? WHERE nome = ?";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEndereco());
            int rowsUpdate = ps.executeUpdate();

            if(rowsUpdate > 0){
                System.out.println("Cliente atualizado com sucesso!");
            }else {
                System.out.println("Nenhum cliente encontrado com esse nome.");
            }
        }catch (SQLException e){
            System.err.println("Erro ao tentar atualizar cliente: " + e.getMessage());
        }
    }

    //Metodo para excluir cliente
    public void excluirCliente(String nome) {
        String sql = "DELETE FROM clientes WHERE nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            int rowsDeleted = ps.executeUpdate();

            if(rowsDeleted > 0) {
                System.out.println("Cliente excluído com sucesso!");
            } else {
                System.out.println("Nenhum cliente encontrado com esse nome.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    // Metodo para buscar todos clientes usando Stream
    public List<Cliente> buscarTodosClientes() {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT * FROM clientes";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getString("nome"),
                        rs.getString("endereco")
                ));
            }

            // Usando Stream para processamento paralelo opcional
            clientes.stream().parallel()
                    .forEach(c -> System.out.println(c.getNome() + " - " + c.getEndereco()));

        } catch (SQLException e) {
            System.err.println("Erro ao buscar clientes: " + e.getMessage());
        }
        return clientes;
    }

    // Metodo adicional para buscar por nome usando Optional
    public Optional<Cliente> buscarClientePorNome(String nome) {
        String sql = "SELECT * FROM clientes WHERE nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return Optional.of(new Cliente(
                        rs.getString("nome"),
                        rs.getString("endereco")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
        }
        return Optional.empty();
    }
}
