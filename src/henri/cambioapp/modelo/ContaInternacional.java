package henri.cambioapp.modelo;

import henri.cambioapp.exception.SaldoInsuficienteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public abstract class ContaInternacional {
    private final String numeroConta;
    private final String titular;
    private final String moeda;
    protected double saldo;
    private final List<Movimentacao> historico;

    public ContaInternacional(String numeroConta, String titular, String moeda, double saldoInicial) {
        if (numeroConta == null || numeroConta.isBlank()) {
            throw new IllegalArgumentException("O número da conta é obrigatório.");
        }
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("O nome do titular é obrigatório.");
        }
        if (moeda == null || moeda.isBlank()) {
            throw new IllegalArgumentException("A moeda da conta é obrigatória.");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }

        this.numeroConta = numeroConta.trim();
        this.titular = titular.trim();
        this.moeda = moeda.trim().toUpperCase();
        this.saldo = saldoInicial;
        this.historico = new ArrayList<>();

        registrarMovimentacao(new Movimentacao(
                "ABERTURA", saldoInicial, 0.0, this.moeda,
                "Saldo inicial: " + getSaldoFormatado(), getCasasDecimais()
        ));
    }

    public abstract double calcularTaxaOperacao(double valor);
    public abstract String getTipoConta();
    public abstract boolean permiteCrypto();

    public boolean ehCrypto() {
        return false;
    }

    protected int getCasasDecimais() {
        return 2;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        this.saldo += valor;
        registrarMovimentacao(new Movimentacao(
                "DEPOSITO", valor, 0.0, moeda,
                "Depósito em conta | Saldo atual: " + getSaldoFormatado(), getCasasDecimais()
        ));
    }

    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }
        double taxa = calcularTaxaOperacao(valor);
        double totalDebito = valor + taxa;

        if (totalDebito > this.saldo) {
            throw new SaldoInsuficienteException(
                String.format(Locale.US,
                    "Saldo insuficiente na conta %s. Necessário: %." + getCasasDecimais() + "f %s (inclui taxa), Disponível: %s",
                    numeroConta, totalDebito, moeda, getSaldoFormatado())
            );
        }

        this.saldo -= totalDebito;
        registrarMovimentacao(new Movimentacao(
                "SAQUE", valor, taxa, moeda,
                "Saque realizado | Saldo atual: " + getSaldoFormatado(), getCasasDecimais()
        ));
    }

    public double debitarTransferencia(double valor, String contaDestino) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }
        double taxa = calcularTaxaOperacao(valor);
        double totalDebito = valor + taxa;

        if (totalDebito > this.saldo) {
            throw new SaldoInsuficienteException(
                String.format(Locale.US,
                    "Saldo insuficiente na conta %s para transferir. Necessário: %." + getCasasDecimais() + "f %s (inclui taxa), Disponível: %s",
                    numeroConta, totalDebito, moeda, getSaldoFormatado())
            );
        }

        this.saldo -= totalDebito;
        registrarMovimentacao(new Movimentacao(
                "TRANSFERENCIA_ENVIADA", valor, taxa, moeda, contaDestino,
                "Envio internacional | Saldo atual: " + getSaldoFormatado(), getCasasDecimais()
        ));
        return taxa;
    }

    public void creditarTransferencia(double valor, String contaOrigem) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor creditado na transferência deve ser maior que zero.");
        }
        this.saldo += valor;
        registrarMovimentacao(new Movimentacao(
                "TRANSFERENCIA_RECEBIDA", valor, 0.0, moeda, contaOrigem,
                "Recebimento internacional | Saldo atual: " + getSaldoFormatado(), getCasasDecimais()
        ));
    }

    public void estornarTransferencia(double valor, double taxaEstornada, String contaDestino) {
        double totalEstorno = valor + taxaEstornada;
        this.saldo += totalEstorno;
        registrarMovimentacao(new Movimentacao(
                "ESTORNO", valor, taxaEstornada, moeda, contaDestino,
                "Estorno automático por falha na transferência | Saldo atual: " + getSaldoFormatado(), getCasasDecimais()
        ));
    }

    protected void registrarMovimentacao(Movimentacao movimentacao) {
        this.historico.add(movimentacao);
    }

    public String getSaldoFormatado() {
        return String.format(Locale.US, "%." + getCasasDecimais() + "f %s", saldo, moeda);
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getTitular() {
        return titular;
    }

    public String getMoeda() {
        return moeda;
    }

    public double getSaldo() {
        return saldo;
    }

    public List<Movimentacao> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    @Override
    public String toString() {
        return String.format("[%s] Conta: %s | Titular: %s | Saldo: %s",
                getTipoConta(), numeroConta, titular, getSaldoFormatado());
    }
}