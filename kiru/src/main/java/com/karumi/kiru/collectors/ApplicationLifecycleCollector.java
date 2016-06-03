/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.karumi.kiru.collectors;

import android.app.Activity;
import android.app.Application;
import com.codahale.metrics.MetricRegistry;
import com.karumi.kiru.android.EmptyActivityLifecycleCallback;

abstract class ApplicationLifecycleCollector implements Collector {

  private final Application application;

  ApplicationLifecycleCollector(Application application) {
    this.application = application;
  }

  @Override public void initialize(MetricRegistry registry) {
    application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallback() {
      @Override public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        if (activity.isTaskRoot()) {
          onApplicationResumed();
        }
      }

      @Override public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        if (activity.isTaskRoot()) {
          onApplicationPaused();
        }
      }
    });
  }

  protected abstract void onApplicationResumed();

  protected abstract void onApplicationPaused();
}
