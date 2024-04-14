package io.opentelemetry.example.javagent;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class Controller {

  private static final Logger LOGGER = LogManager.getLogger(Controller.class);
  private final AttributeKey<String> ATTR_METHOD = AttributeKey.stringKey("method");

  private final Random random = new Random();
  private final Tracer tracer;
  private final LongHistogram doWorkHistogram;

  @Autowired
  Controller(OpenTelemetry openTelemetry) {
    tracer = openTelemetry.getTracer(Application.class.getName());
    Meter meter = openTelemetry.getMeter(Application.class.getName());
    doWorkHistogram = meter.histogramBuilder("do-work").ofLongs().build();
  }

  @GetMapping("/ping")
  public String ping() throws InterruptedException, IOException {
    int sleepTime = random.nextInt(200);
    doWork(sleepTime);
    doWorkHistogram.record(sleepTime, Attributes.of(ATTR_METHOD, "ping"));
    return "pong";
  }

  private void doWork(int sleepTime) throws InterruptedException, IOException {
    Span span = tracer.spanBuilder("doWork").startSpan();
    try (Scope ignored = span.makeCurrent()) {
      Thread.sleep(sleepTime);
      LOGGER.info("A sample log message!");

      // URL url = new URL("http://172.21.6.8:13000");
      // String url = System.getenv("MY_URL");
      URL url = new URL(System.getenv("WZH_URL"));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int responseCode = connection.getResponseCode();
      LOGGER.info("HTTP GET response code: " + responseCode);
      connection.disconnect();

    } finally {
      span.end();
    }
  }
}
