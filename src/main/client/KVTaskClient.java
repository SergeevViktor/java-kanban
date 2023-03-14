package main.client;

import main.util.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class KVTaskClient {
    private final String url;
    private final String API_TOKEN;
    private final HttpClient client;

    public KVTaskClient(String url) throws ManagerSaveException {
        this.url = url;
        URI uri = URI.create(url + "/register");
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код состояни: " + response.statusCode());
            System.out.println("Ответ: " + response.body());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Код состояни: " + response.statusCode() +
                        ". Ответ: " + response.body());
            }
            API_TOKEN = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Ошибка выполнения запроса ресурса по URL-адресу " + url);
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Код состояни: " + response.statusCode() +
                        ". Ответ: " + response.body());
            }
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Ошибка выполнения запроса ресурса по URL-адресу " + url + ".\n" +
                    "Проверьте адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Код состояни: " + response.statusCode() +
                        ". Ответ: " + response.body());
            }
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Ошибка выполнения запроса ресурса по URL-адресу " + url + ".\n" +
                    "Проверьте адрес и повторите попытку.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KVTaskClient that = (KVTaskClient) o;
        return Objects.equals(url, that.url) && Objects.equals(API_TOKEN, that.API_TOKEN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, API_TOKEN);
    }
}
