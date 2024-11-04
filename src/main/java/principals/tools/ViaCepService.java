package principals.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.json.JSONObject;
import response.CepInfo;

public class ViaCepService {
    public CepInfo buscarCep(String cep) throws IOException {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String jsonResponse = reader.lines().collect(Collectors.joining());
            JSONObject jsonObject = new JSONObject(jsonResponse);

            return new CepInfo(
                    jsonObject.optString("cep"),
                    jsonObject.optString("logradouro"),
                    jsonObject.optString("complemento"),
                    jsonObject.optString("bairro"),
                    jsonObject.optString("localidade"),
                    jsonObject.optString("uf"),
                    jsonObject.optString("unidade"),
                    jsonObject.optString("estado"),
                    jsonObject.optString("ibge"),
                    jsonObject.optString("gia"),
                    jsonObject.optString("ddd"),
                    jsonObject.optString("siafi")
            );
        }
    }
}

