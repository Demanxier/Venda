package br.com.vendasxier.service;

import br.com.vendasxier.model.Produto;
import br.com.vendasxier.model.Venda;
import br.com.vendasxier.repository.ProdutoRepository;
import br.com.vendasxier.repository.VendaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VendaService {
    private final VendaRepository vendaRepo = new VendaRepository();
    private final ProdutoRepository produtoRepo = new ProdutoRepository();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    // Processamento assíncrono de vendas
    public void processarVenda(Venda venda) {
        executor.submit(() -> {
            try {
                validarEstoque(venda);
                calcularTotal(venda);
                venda.setData(LocalDateTime.now());
                vendaRepo.cadastrarVenda(venda);
                atualizarEstoque(venda);
            } catch (Exception e) {
                System.err.println("Erro na venda: " + e.getMessage());
            }
        });
    }
    private void validarEstoque(Venda venda) {
        venda.getItens().forEach(item -> {
            Optional<Produto> produtoOpt = produtoRepo.buscarPorCodigo(item.getProduto().getCodigo());
            Produto produto = produtoOpt.orElseThrow(() ->
                    new RuntimeException("Produto não encontrado: " + item.getProduto().getCodigo()));

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para: " + produto.getNome());
            }
        });
    }

    private void calcularTotal(Venda venda) {
        double total = venda.getItens().stream()
                .mapToDouble(item -> item.getQuantidade() * item.getProduto().getPreco())
                .sum();
        venda.setTotal(total);
    }
    private void atualizarEstoque(Venda venda) {
        venda.getItens().forEach(item -> {
            produtoRepo.atualizarEstoque(
                    item.getProduto().getCodigo(),
                    -item.getQuantidade()
            );
        });
    }
    public List<Venda> buscarVendasPorCliente(String clienteNome) {
        return vendaRepo.buscarVendasPorCliente(clienteNome);
    }

}
