package henri.cambioapp.services;

import henri.cambioapp.exception.CambioException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversorAwesomeAPI implements ConversorMoeda {
    private static final String URL_BASE = "https://economia.awesomeapi.com.br/json/last/";
    private static final Pattern PADRAO_BID = Pattern.compile("\"bid\"\\s*:\\s*\"([0-9.]+)\"");
    private final HttpClient httpClient;

    public ConversorAwesomeAPI() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
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
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_BASE + par))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new CambioException("Par de moedas não disponível na AwesomeAPI (" + par + "). HTTP: " + response.statusCode());
            }

            Matcher matcher = PADRAO_BID.matcher(response.body());
            if (!matcher.find()) {
                throw new CambioException("Não foi possível interpretar a cotação retornada pela AwesomeAPI.");
            }

            double cotacao = Double.parseDouble(matcher.group(1));
            return valor * cotacao;

        } catch (IOException e) {
            throw new CambioException("Falha de conexão com a AwesomeAPI: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CambioException("Consulta à AwesomeAPI interrompida.");
        } catch (NumberFormatException e) {
            throw new CambioException("Formato numérico inválido na resposta da AwesomeAPI.");
        }
    }

    @Override
    public String getNomeProvedor() {
        return "AwesomeAPI (Câmbio em Tempo Real)";
    }
}