/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.metricnames;

import android.content.Context;
import android.content.SharedPreferences;
import com.flowup.BuildConfig;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class)
public class UUIDGeneratorTest {

  private static final String UUID_KEY = "uuid";
  private static final String UUID_SHARED_PREFS_NAME = "uuid_shared_prefs_name";

  @After public void tearDown() {
    clearGeneratorSharedPreferences();
  }

  @Test public void savesANonEmptyAndNotNullStringOnTheSharedPreferencesOnGettingIt() {
    UUIDGenerator generator = givenAGenerator();

    String uuid = generator.getUUID();

    assertNotNull(uuid);
    assertFalse(uuid.isEmpty());
  }

  @Test public void returnsTheGeneratedAndStoredUUIDAfterGettingIt() {
    UUIDGenerator generator = givenAGenerator();

    generator.getUUID();
    String storedUUID = getUUIDGeneratorSharedPreferences().getString(UUID_KEY, "");

    assertEquals(storedUUID, generator.getUUID());
  }

  @Test public void returnsTheSameUUIDEvenIfITryToGetItTwice() {
    UUIDGenerator generator = givenAGenerator();

    String firstUUIDCall = generator.getUUID();
    String secondUUIDCall = generator.getUUID();

    assertEquals(firstUUIDCall, secondUUIDCall);
  }

  @Test public void returnsTheSameUUIDEvenIfTheGeneratorInstancesAreDifferent() {
    UUIDGenerator generator1 = givenAGenerator();
    UUIDGenerator generator2 = givenAGenerator();

    String firstUUIDCall = generator1.getUUID();
    String secondUUIDCall = generator2.getUUID();

    assertEquals(firstUUIDCall, secondUUIDCall);
  }

  private void clearGeneratorSharedPreferences() {
    SharedPreferences sharedPreferences = getUUIDGeneratorSharedPreferences();
    sharedPreferences.edit().clear().commit();
  }

  private SharedPreferences getUUIDGeneratorSharedPreferences() {
    return RuntimeEnvironment.application.getSharedPreferences(UUID_SHARED_PREFS_NAME,
        Context.MODE_PRIVATE);
  }

  private UUIDGenerator givenAGenerator() {
    return new UUIDGenerator(RuntimeEnvironment.application);
  }
}