package br.com.vendasxier;

import br.com.vendasxier.model.*;
import br.com.vendasxier.repository.ProdutoRepository;
import br.com.vendasxier.service.ClienteService;
import br.com.vendasxier.service.VendaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProdutoRepository produtoRepository = new ProdutoRepository();
        ClienteService clienteService = new ClienteService();
        VendaService vendaService = new VendaService();

        while (true) {
            System.out.println("1. Listar produtos");
            System.out.println("2. Cadastrar cliente");
            System.out.println("3. Realizar venda");
            System.out.println("4. Buscar Vendas");
            System.out.print("Escolha: ");

            int opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    System.out.println("\n=== Lista de Produtos ===");
                    List<Produto> produtos = produtoRepository.getAllProdutos();
                    if(produtos.isEmpty()) {
                        System.out.println("Nenhum produto cadastrado.");
                    }
                    break;
                case 2:
                    scanner.nextLine();
                    System.out.println("Nome do Cliente: ");
                    String nome = scanner.nextLine();

                    System.out.println("Endereco do cliente: ");
                    String endereco = scanner.nextLine();

                    System.out.println("É cliente VIP? (s/n): ");
                    String respostaVIP = scanner.nextLine().trim();

                    try {
                        Cliente cliente;
                        if (respostaVIP.equalsIgnoreCase("s")){
                            cliente = new ClienteVIP(nome, endereco);
                        }else {
                            cliente = new Cliente(nome, endereco);
                        }
                        clienteService.cadastrarCliente(cliente);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Erro: " + e.getMessage());
                    }
                    break;
                case 3:
                    scanner.nextLine();
                    System.out.println("Nome do cliente: ");
                    String nomeCliente = scanner.nextLine();
                    Optional<Cliente> clienteOpt = clienteService.buscarClientePorNome(nomeCliente);
                    if (clienteOpt.isEmpty()) {
                        System.out.println("Cliente não encontrado");
                        return;
                    }
                    List<ItemVenda> itens = new ArrayList<>();
                    boolean continuar = true;

                    while (continuar) {
                        System.out.print("Código do produto: ");
                        String codigo = scanner.nextLine();

                        Optional<Produto> produtoOpt = produtoRepository.buscarPorCodigo(codigo);
                        if (produtoOpt.isEmpty()) {
                            System.out.println("Produto não encontrado");
                            continue;
                        }
                        System.out.println("Quantidade: ");
                        int quantidade = scanner.nextInt();
                        scanner.nextLine(); //Limpa buffer

                        itens.add(new ItemVenda(produtoOpt.get(), quantidade));

                        System.out.println("Adicionar outro produto? (s/n): ");
                        continuar = scanner.nextLine().equalsIgnoreCase("s");
                    }
                    System.out.print("Forma de pagamento (DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX): ");
                    FormaPagamento forma = FormaPagamento.valueOf(scanner.nextLine().toUpperCase());

                    Venda venda = new Venda(clienteOpt.get(), itens, forma, 0, null);
                    double total = venda.getItens().stream()
                                    .mapToDouble(item -> item.getQuantidade() * item.getProduto().getPreco())
                                    .sum();
                    if (venda.getCliente() instanceof ClienteVIP){
                        double desconto = total * ((ClienteVIP) venda.getCliente()).getDesconto();
                        total -= desconto;
                    }
                    venda.setTotal(total);

                    vendaService.processarVenda(venda);
                    System.out.println("Venda realizada.");
                    System.out.println("Total: R$" + venda.getTotal());
                    if (venda.getCliente() instanceof ClienteVIP){
                        double valorOriginal = venda.getTotal() / 0.90;
                        System.out.println("Parabéns, você é VIP! 10% de desconto aplicado.");
                        System.out.printf("Valor original: R$ %.2f%n", valorOriginal);
                        System.out.printf("Valor final: R$ %.2f%n", venda.getTotal());
                    }
                    System.out.println("___________");
                    break;
                case 4:
                    scanner.nextLine();
                    System.out.print("Nome do cliente: ");
                    String nomeC = scanner.nextLine();

                    List<Venda> vendas = vendaService.buscarVendasPorCliente(nomeC);
                    if (vendas.isEmpty()) {
                        System.out.println("Nenhuma venda encontrada!");
                        return;
                    }

                    vendas.forEach(v -> {
                        System.out.println("\nData: " + v.getData());
                        System.out.println("Total: R$ " + v.getTotal());
                        System.out.println("Forma de Pagamento: " + v.getFormaPagamento());
                        System.out.println("Produtos:");

                        v.getItens().forEach(item -> {
                            System.out.printf("- %s: %d x R$ %.2f%n",
                                    item.getProduto().getNome(),
                                    item.getQuantidade(),
                                    item.getProduto().getPreco());
                        });
                    });
                    break;
            }
        }
    }
}
