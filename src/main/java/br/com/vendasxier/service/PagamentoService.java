package br.com.vendasxier.service;

import br.com.vendasxier.model.FormaPagamento;

public class PagamentoService {

    public void processarPagamento(FormaPagamento forma){
        switch (forma){
            case PIX -> System.out.println("Processamento PIX...");
            case DINHEIRO -> System.out.println("Processamento Dinheiro...");
            // Switch expressions (Java 14 +)
            case CARTAO_CREDITO -> System.out.println("Processamento Cartao Credito...");
            case CARTAO_DEBITO ->  System.out.println("Processamento Cartao Debito...");
        }
        // Records (Java 16+): Poderíamos usar para DTOs
        // Ex: record PagamentoResponse(String status, LocalDate data)
    }
}
