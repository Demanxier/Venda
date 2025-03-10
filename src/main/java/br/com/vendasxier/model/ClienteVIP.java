package br.com.vendasxier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteVIP extends Cliente{
    private static final double DESCONTO_VIP = 0.10;

    public ClienteVIP(String nome, String endereco) {
        super(nome, endereco);
    }

    public double getDesconto(){
        return DESCONTO_VIP;
    }

    @Override
    public String toString() {
        return super.toString() + "(VIP)";
    }
}
