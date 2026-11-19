package henri.cambioapp.modelo;

import java.util.Set;

public class ContaPadrao extends ContaInternacional {
    private static final double PERCENTUAL_TAXA = 0.025; // 2,5% de tarifa administrativa
    private static final Set<String> MOEDAS_FIAT = Set.of("BRL", "USD", "EUR");

    public ContaPadrao(String numeroConta, String titular, String moeda, double saldoInicial) {
        super(numeroConta, titular, moeda, saldoInicial);
        if (!MOEDAS_FIAT.contains(getMoeda())) {
            throw new IllegalArgumentException(
                "Conta Padrão aceita apenas moedas fiduciárias (BRL, USD, EUR)."
            );
        }
    }

    @Override
    public double calcularTaxaOperacao(double valor) {
        return valor * PERCENTUAL_TAXA;
    }

    @Override
    public boolean permiteCrypto() {
        return false; // Bloqueia operações envolvendo criptoativos
    }

    @Override
    public String getTipoConta() {
        return "PADRAO";
    }
}