package henri.cambioapp.modelo;

import java.util.Set;

public class ContaCrypto extends ContaInternacional {
    private final String redeBlockchain;
    private static final double TAXA_REDE_GAS = 0.01; // 1,0% de taxa de rede
    private static final Set<String> MOEDAS_FIAT = Set.of("BRL", "USD", "EUR");

    public ContaCrypto(String numeroConta, String titular, String cryptoMoeda, double saldoInicial, String redeBlockchain) {
        super(numeroConta, titular, cryptoMoeda, saldoInicial);
        if (MOEDAS_FIAT.contains(getMoeda())) {
            throw new IllegalArgumentException(
                "Conta Crypto não aceita moedas fiduciárias (BRL, USD, EUR). Informe um criptoativo (ex: BTC, ETH)."
            );
        }
        if (redeBlockchain == null || redeBlockchain.isBlank()) {
            throw new IllegalArgumentException("A rede blockchain deve ser informada (ex: Bitcoin Mainnet).");
        }
        this.redeBlockchain = redeBlockchain.trim();
    }

    @Override
    public double calcularTaxaOperacao(double valor) {
        return valor * TAXA_REDE_GAS;
    }

    @Override
    public boolean permiteCrypto() {
        return true;
    }

    @Override
    public boolean ehCrypto() {
        return true;
    }

    @Override
    protected int getCasasDecimais() {
        return 8;
    }

    public String getRedeBlockchain() {
        return redeBlockchain;
    }

    @Override
    public String getTipoConta() {
        return "CRYPTO (" + redeBlockchain + ")";
    }
}