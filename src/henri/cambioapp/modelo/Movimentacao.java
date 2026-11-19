package henri.cambioapp.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Movimentacao {
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime dataHora;
    private final String tipo;
    private final double valor;
    private final double taxa;
    private final String moeda;
    private final String contaRelacionada;
    private final String descricao;
    private final int casasDecimais;

    public Movimentacao(String tipo, double valor, double taxa, String moeda,
                        String contaRelacionada, String descricao, int casasDecimais) {
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.valor = valor;
        this.taxa = taxa;
        this.moeda = moeda;
        this.contaRelacionada = (contaRelacionada == null || contaRelacionada.isBlank()) ? "N/A" : contaRelacionada;
        this.descricao = descricao;
        this.casasDecimais = casasDecimais;
    }

    public Movimentacao(String tipo, double valor, double taxa, String moeda, String descricao, int casasDecimais) {
        this(tipo, valor, taxa, moeda, "N/A", descricao, casasDecimais);
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public double getTaxa() {
        return taxa;
    }

    public String getMoeda() {
        return moeda;
    }

    public String getContaRelacionada() {
        return contaRelacionada;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        String formatoNumero = "%." + casasDecimais + "f";
        String valorFmt = String.format(Locale.US, formatoNumero, valor);
        String taxaFmt = String.format(Locale.US, formatoNumero, taxa);
        String infoConta = "N/A".equals(contaRelacionada) ? "" : " | Conta Rel.: " + contaRelacionada;

        return String.format(Locale.US, "[%s] %-22s | Valor: %s %s | Taxa: %s %s%s | %s",
                dataHora.format(FORMATADOR_DATA), tipo, valorFmt, moeda, taxaFmt, moeda, infoConta, descricao);
    }
}
