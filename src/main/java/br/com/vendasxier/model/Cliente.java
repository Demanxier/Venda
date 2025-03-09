package br.com.vendasxier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Cliente {
    private String nome;
    private String endereco;

    // Encapsulamento: campos privados com métodos públicos de acesso
    // Lombok gera getters/setters automaticamente
}
