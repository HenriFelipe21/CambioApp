package henri.cambioapp.modelo;

import java.util.Locale;
import java.util.Set;

public class ContaPremium extends ContaInternacional {
    private static final Set<String> MOEDAS_FIAT = Set.of("BRL", "USD", "EUR");

    public ContaPremium(String numeroConta, String titular, String moeda, double saldoInicial) {
        super(numeroConta, titular, moeda, saldoInicial);
        if (!MOEDAS_FIAT.contains(getMoeda())) {
            throw new IllegalArgumentException(
                "Conta Premium deve ser aberta em moeda fiduciária (BRL, USD, EUR)."
            );
        }
    }

    @Override
    public double calcularTaxaOperacao(double valor) {
        return 0.0;
    }

    @Override
    public boolean permiteCrypto() {
        return true; // Permite transferir fundos para/de contas Crypto
    }

    public void aplicarRendimento(double percentual) {
        if (percentual <= 0) {
            throw new IllegalArgumentException("O percentual de rendimento deve ser maior que zero.");
        }
        if (this.saldo > 0) {
            double ganho = this.saldo * (percentual / 100.0);
            this.saldo += ganho;
            registrarMovimentacao(new Movimentacao(
                    "RENDIMENTO", ganho, 0.0, getMoeda(),
                    String.format(Locale.US, "Rendimento Premium (+%.2f%%) | Saldo atual: %s", percentual, getSaldoFormatado()),
                    getCasasDecimais()
            ));
        }
    }

    @Override
    public String getTipoConta() {
        return "PREMIUM";
    }
}