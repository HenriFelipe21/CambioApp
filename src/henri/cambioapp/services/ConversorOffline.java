package henri.cambioapp.services;

import henri.cambioapp.exception.CambioException;
import java.util.HashMap;
import java.util.Map;

public class ConversorOffline implements ConversorMoeda {
    private final Map<String, Double> taxasFixas;

    public ConversorOffline() {
        this.taxasFixas = new HashMap<>();
        taxasFixas.put("USD-BRL", 5.50);
        taxasFixas.put("BRL-USD", 1.0 / 5.50);
        taxasFixas.put("EUR-BRL", 6.00);
        taxasFixas.put("BRL-EUR", 1.0 / 6.00);
        taxasFixas.put("EUR-USD", 1.09);
        taxasFixas.put("USD-EUR", 0.917);
        taxasFixas.put("BTC-BRL", 363000.00);
        taxasFixas.put("BRL-BTC", 1.0 / 363000.00);
        taxasFixas.put("BTC-USD", 66000.00);
        taxasFixas.put("USD-BTC", 1.0 / 66000.00);
        taxasFixas.put("BTC-EUR", 60500.00);
        taxasFixas.put("EUR-BTC", 1.0 / 60500.00);
        taxasFixas.put("ETH-BRL", 11000.00);
        taxasFixas.put("BRL-ETH", 1.0 / 11000.00);
        taxasFixas.put("ETH-USD", 2000.00);
        taxasFixas.put("USD-ETH", 1.0 / 2000.00);
    }

    @Override
    public double converter(double valor, String moedaOrigem, String moedaDestino) throws CambioException {
        if (valor <= 0) {
            throw new CambioException("O valor para conversão deve ser maior que zero.");
        }

        String origem = moedaOrigem.trim().toUpperCase();
        String destino = moedaDestino.trim().toUpperCase();

        if (origem.equals(destino)) {
            return valor;
        }

        String par = origem + "-" + destino;
        Double taxa = taxasFixas.get(par);
        if (taxa == null) {
            throw new CambioException("Par de moedas não cadastrado no modo offline: " + par);
        }

        return valor * taxa;
    }

    @Override
    public String getNomeProvedor() {
        return "Conversor Offline (Taxas Fixas)";
    }
}