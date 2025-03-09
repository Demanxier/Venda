package br.com.vendasxier.service;

import br.com.vendasxier.model.Cliente;
import br.com.vendasxier.repository.ClienteRepository;
import br.com.vendasxier.util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final ClienteRepository clienteRepository = new ClienteRepository();

    // Metodo para cadastro com validação
    public void cadastrarCliente(Cliente cliente) throws IllegalArgumentException{
        if(cliente.getNome() == null || cliente.getNome().trim().isEmpty()){
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }

        if(cliente.getEndereco() == null || cliente.getEndereco().trim().isEmpty()){
            throw new IllegalArgumentException("Endereço do cliente é obrigatório.");
        }

        Optional<Cliente> existente = clienteRepository.buscarClientePorNome(cliente.getNome());
        if(existente.isPresent()){
            throw new IllegalArgumentException("Cliente já cadastrado com este nome.");
        }

        clienteRepository.cadastrarCliente(cliente);
    }

    //Metodo para atualizar com tratamento de Optional
    public void atualizarCliente(Cliente cliente){
        Optional<Cliente> existente = clienteRepository.buscarClientePorNome(cliente.getNome());

        existente.ifPresentOrElse(
                c -> clienteRepository.atualizarCliente(cliente),
                ()-> { throw new IllegalArgumentException("Cliente não encontrado.");}
        );
    }

    // Metodo para excluir cliente
    public void excluirCliente(String nome){
        Optional<Cliente> cliente = clienteRepository.buscarClientePorNome(nome);

        cliente.ifPresentOrElse(
                c -> clienteRepository.excluirCliente(nome),
                ()->System.out.println("Cliente não encontrado.")
        );
    }

    // Metodo para listagem usando Stream
    public List<Cliente> listarTodosClientes() {
        List<Cliente> clientes = clienteRepository.buscarTodosClientes();

        if(clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            clientes.stream()
                    .map(c -> c.getNome() + " - " + c.getEndereco())
                    .forEach(System.out::println);
        }

        return clientes;
    }

    // Metodo buscar cliente por nome
    public Optional<Cliente> buscarClientePorNome(String nome){
        String sql = "SELECT * FROM clientes WHERE nome = ?";

        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return Optional.of(new Cliente(
                        rs.getString("nome"),
                        rs.getString("endereco")
                ));
            }
        }catch (SQLException e){
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
        }
        return Optional.empty();
    }
}
