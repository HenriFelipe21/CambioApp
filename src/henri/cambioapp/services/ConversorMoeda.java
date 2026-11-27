package henri.cambioapp.services;

import henri.cambioapp.exception.CambioException;

public interface ConversorMoeda {
    double converter(double valor, String moedaOrigem, String moedaDestino) throws CambioException;
    String getNomeProvedor();
}