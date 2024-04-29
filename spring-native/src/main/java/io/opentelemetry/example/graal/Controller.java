package io.opentelemetry.example.graal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;

@RestController
public class Controller {

  private final Tracer tracer;

  public Controller(OpenTelemetry openTelemetry) {
    tracer = openTelemetry.getTracer("io.opentelemetry.example.graal");
  }

  @GetMapping("/ping")
  public String ping() {
    
    String url = System.getenv("WZH_URL");
    // Make a request to the URL and get the response
    String response = makeRequest(url);

    return response;
  }

  private String makeRequest(String url) {
    // Implement the logic to make a request to the given URL
    // and return the response
    // You can use libraries like HttpClient or OkHttp to make the request
    // Here's an example using HttpClient:
    Span span = tracer.spanBuilder("makeHttpRequest").setSpanKind(SpanKind.CLIENT).startSpan();
    
    HttpResponse<String> response = null;
    try (Scope ignored = span.makeCurrent()) {

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .build();
      try {
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        response = null;
      }
    } finally {
      span.end();
    }

    if (response!=null) {
      return response.body();
    }
    else {
      return null;
    }
  }

}