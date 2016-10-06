/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup;

import android.app.Application;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.flowup.collectors.Collector;
import com.flowup.collectors.Collectors;
import com.readytalk.metrics.StatsDReporter;
import java.util.concurrent.TimeUnit;

public class FlowUp {

  private final Application application;
  private static MetricRegistry registry;

  public static FlowUp with(Application application) {
    return new FlowUp(application);
  }

  FlowUp(Application application) {
    if (application == null) {
      throw new IllegalArgumentException(
          "The application instance used to initialize FlowUp can not be null.");
    }
    this.application = application;
  }

  public void start() {
    if (hasBeenInitialized()) {
      return;
    }
    initializeMetrics();
    Thread initializationThread = new Thread(new Runnable() {
      @Override public void run() {
        initializeReporters();
      }
    });
    initializationThread.start();
    initializeForegroundCollectors();
    initializeHttpCollectors();
  }

  private void initializeMetrics() {
    registry = new MetricRegistry();
  }

  private boolean hasBeenInitialized() {
    return registry != null;
  }

  private void initializeReporters() {
    initializeConsoleReporter();
    initializeFlowUpReporter();
  }

  private void initializeConsoleReporter() {
    ConsoleReporter.forRegistry(registry).build().start(10, TimeUnit.SECONDS);
  }

  private void initializeFlowUpReporter() {
    String host = application.getString(R.string.host);
    int port = application.getResources().getInteger(R.integer.port);
    StatsDReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .filter(MetricFilter.ALL)
        .build(host, port)
        .start(10, TimeUnit.SECONDS);
  }

  private void initializeForegroundCollectors() {
    initializeFPSCollector();
    initializeFrameTimeCollector();
  }

  private void initializeFPSCollector() {
    Collector fpsCollector = Collectors.getFPSCollector(application);
    fpsCollector.initialize(registry);
  }

  private void initializeFrameTimeCollector() {
    Collector frameTimeCollector = Collectors.getFrameTimeCollector(application);
    frameTimeCollector.initialize(registry);
  }

  private void initializeHttpCollectors() {
    Collector httpBytesDownloadedCollector =
        Collectors.getHttpBytesDownloadedCollector(application);
    httpBytesDownloadedCollector.initialize(registry);
    Collector httpBytesUploadedCollector = Collectors.getHttpBytesUploadedCollector(application);
    httpBytesUploadedCollector.initialize(registry);
  }
}