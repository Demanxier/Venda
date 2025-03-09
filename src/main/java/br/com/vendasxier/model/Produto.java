package br.com.vendasxier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Produto {
    private String nome;
    private String codigo;
    private int quantidade;
    private double preco;

    // Encapsulamento: campos privados com métodos públicos de acesso
    // Lombok gera getters/setters automaticamente
    // Na classe Produto.java
    @Override
    public String toString() {
        return String.format("Código: %s | Nome: %s | Preço: R$%.2f | Estoque: %d",
                codigo, nome, preco, quantidade);
    }
}
