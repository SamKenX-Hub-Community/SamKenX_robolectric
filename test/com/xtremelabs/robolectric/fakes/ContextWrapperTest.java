package com.xtremelabs.robolectric.fakes;

import android.app.Activity;
import android.app.Application;
import android.appwidget.AppWidgetProvider;
import android.content.*;
import com.xtremelabs.robolectric.DogfoodRobolectricTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.util.Transcript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(DogfoodRobolectricTestRunner.class)
public class ContextWrapperTest {
    public Transcript transcript;
    private ContextWrapper contextWrapper;

    @Before public void setUp() throws Exception {
        DogfoodRobolectricTestRunner.addGenericProxies();

        Robolectric.application = new Application();

        transcript = new Transcript();
        contextWrapper = new ContextWrapper(new Activity());
    }

    @Test
    public void registerReceiver_shouldRegisterForAllIntentFilterActions() throws Exception {
        IntentFilter intentFilter = new IntentFilter("foo");
        intentFilter.addAction("baz");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                transcript.add("notified of " + intent.getAction());
            }
        };
        contextWrapper.registerReceiver(receiver, intentFilter);

        contextWrapper.sendBroadcast(new Intent("foo"));
        transcript.assertEventsSoFar("notified of foo");

        contextWrapper.sendBroadcast(new Intent("womp"));
        transcript.assertNoEventsSoFar();

        contextWrapper.sendBroadcast(new Intent("baz"));
        transcript.assertEventsSoFar("notified of baz");

        contextWrapper.unregisterReceiver(receiver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unregisterReceiver_shouldThrowExceptionWhenReceiverIsNotRegistered() throws Exception {
        contextWrapper.unregisterReceiver(new AppWidgetProvider());
    }

    @Test
    public void shouldReturnSameApplicationEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getApplication(), sameInstance(activity.getApplication()));

        assertThat(activity.getApplication(), sameInstance(new Activity().getApplication()));
    }

    @Test
    public void shouldReturnSameApplicationContextEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getApplicationContext(), sameInstance(activity.getApplicationContext()));

        assertThat(activity.getApplicationContext(), sameInstance(new Activity().getApplicationContext()));
    }

    @Test
    public void shouldReturnSameContentResolverEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getContentResolver(), sameInstance(activity.getContentResolver()));

        assertThat(activity.getContentResolver(), sameInstance(new Activity().getContentResolver()));
    }

    @Test
    public void shouldReturnSameLocationManagerEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getSystemService(Context.LOCATION_SERVICE), sameInstance(activity.getSystemService(Context.LOCATION_SERVICE)));

        assertThat(activity.getSystemService(Context.LOCATION_SERVICE), sameInstance(new Activity().getSystemService(Context.LOCATION_SERVICE)));
    }

    @Test
    public void shouldReturnSameWifiManagerEveryTime() throws Exception {
        Activity activity = new Activity();
        assertThat(activity.getSystemService(Context.WIFI_SERVICE), sameInstance(activity.getSystemService(Context.WIFI_SERVICE)));

        assertThat(activity.getSystemService(Context.WIFI_SERVICE), sameInstance(new Activity().getSystemService(Context.WIFI_SERVICE)));
    }
}