package henri.cambioapp.services;

import henri.cambioapp.exception.CambioException;
import henri.cambioapp.exception.SaldoInsuficienteException;
import henri.cambioapp.modelo.ContaInternacional;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class GerenciadorCarteiras {
    private static GerenciadorCarteiras instancia;
    private final Map<String, ContaInternacional> contas;

    private GerenciadorCarteiras() {
        this.contas = new LinkedHashMap<>();
    }

    public static synchronized GerenciadorCarteiras getInstance() {
        if (instancia == null) {
            instancia = new GerenciadorCarteiras();
        }
        return instancia;
    }

    public void cadastrarConta(ContaInternacional conta) throws CambioException {
        if (conta == null) {
            throw new CambioException("Conta inválida.");
        }
        if (contas.containsKey(conta.getNumeroConta())) {
            throw new CambioException("Já existe uma conta cadastrada com o número " + conta.getNumeroConta());
        }
        contas.put(conta.getNumeroConta(), conta);
    }

    public ContaInternacional buscarConta(String numeroConta) throws CambioException {
        if (numeroConta == null || numeroConta.isBlank()) {
            throw new CambioException("Informe o número da conta.");
        }
        ContaInternacional conta = contas.get(numeroConta.trim());
        if (conta == null) {
            throw new CambioException("Conta não encontrada: " + numeroConta);
        }
        return conta;
    }

    public Collection<ContaInternacional> listarContas() {
        return Collections.unmodifiableCollection(contas.values());
    }

    public void limparContas() {
        this.contas.clear();
    }

    public String transferir(String numOrigem, String numDestino, double valor, ConversorMoeda conversor)
            throws CambioException, SaldoInsuficienteException {

        if (conversor == null) {
            throw new CambioException("Provedor de câmbio não informado.");
        }

        ContaInternacional origem = buscarConta(numOrigem);
        ContaInternacional destino = buscarConta(numDestino);

        if (origem.getNumeroConta().equals(destino.getNumeroConta())) {
            throw new CambioException("A conta de origem e a conta de destino devem ser diferentes.");
        }

        //impede que ContaPadrao envie ou receba valores de ContaCrypto
        if ((origem.ehCrypto() || destino.ehCrypto()) && (!origem.permiteCrypto() || !destino.permiteCrypto())) {
            throw new CambioException(
                "Operação negada: contas do tipo PADRAO não possuem permissão para operar com criptoativos."
            );
        }

        if (valor <= 0) {
            throw new CambioException("O valor da transferência deve ser maior que zero.");
        }

        //consulta cotacao antes de movimentar saldo
        double valorConvertido = conversor.converter(valor, origem.getMoeda(), destino.getMoeda());

        //debita a conta de origem (valida saldo + taxa)
        double taxaCobrada = origem.debitarTransferencia(valor, destino.getNumeroConta());

        //se falhar ao creditar o destino, estorna
        try {
            destino.creditarTransferencia(valorConvertido, origem.getNumeroConta());
        } catch (RuntimeException e) {
            origem.estornarTransferencia(valor, taxaCobrada, destino.getNumeroConta());
            throw new CambioException("Falha ao creditar destino. Estorno automático efetuado na conta " + origem.getNumeroConta() + ".");
        }

        int casasDecDestino = destino.ehCrypto() ? 8 : 2;
        return String.format(Locale.US,
                "Transferência concluída via %s | Debitado: %.4f %s (Taxa: %.4f %s) -> Creditado: %." + casasDecDestino + "f %s",
                conversor.getNomeProvedor(), valor, origem.getMoeda(), taxaCobrada, origem.getMoeda(),
                valorConvertido, destino.getMoeda());
    }
}