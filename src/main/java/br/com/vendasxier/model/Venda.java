package br.com.vendasxier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venda {
    private Cliente cliente;
    private List<ItemVenda> itens;
    private FormaPagamento formaPagamento;
    private double total;
    private LocalDateTime data;
}
