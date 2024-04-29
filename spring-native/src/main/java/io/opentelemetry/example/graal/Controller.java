package io.opentelemetry.example.graal;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@Component
public class Controller {

  @GetMapping("/ping")
  public String ping() {
    // Make a request to the URL and get the response
    String response = makeRequest();
    return response;
  }

  @WithSpan
  private String makeRequest() {
    // Implement the logic to make a request to the given URL
    // and return the response
    // You can use libraries like HttpClient or OkHttp to make the request
    StringBuilder response = new StringBuilder();
    // Here's an example using HttpClient:
    try {
      URI uri = new URI(System.getenv("WZH_URL"));
      URL url = uri.toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      // int responseCode = connection.getResponseCode();
      // LOGGER.info("HTTP GET response code: " + responseCode);
      
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
      } catch (IOException e) {
        // Handle exception
      }
      connection.disconnect();
    } catch (URISyntaxException | IOException e) {
      // Handle exceptions
    }
    return response.toString();

  }

}