package io.opentelemetry.example.graal;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;

@RestController
@Component
public class Controller {

  @GetMapping("/ping")
  public String ping() {
    String url = System.getenv("WZH_URL");
    // Make a request to the URL and get the response
    String response = makeRequest(url);
    return response;
  }

  @WithSpan
  private String makeRequest(String url) {
    // Implement the logic to make a request to the given URL
    // and return the response
    // You can use libraries like HttpClient or OkHttp to make the request
    // Here's an example using HttpClient:
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }

  }

}