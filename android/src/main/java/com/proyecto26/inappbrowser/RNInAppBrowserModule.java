package com.proyecto26.inappbrowser;

import android.app.Activity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RNInAppBrowserModule extends ReactContextBaseJavaModule {
  private final RNInAppBrowser inAppBrowser;
  private final ReactApplicationContext reactContext;

  public RNInAppBrowserModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.inAppBrowser = new RNInAppBrowser();
  }

  @Override
  public String getName() {
    return "RNInAppBrowser";
  }

  @ReactMethod
  public void open(final ReadableMap options, final Promise promise) {
    final Activity activity = getCurrentActivity();
    inAppBrowser.open(this.reactContext, options, promise, activity);
    registerEventBus();
  }

  @ReactMethod
  public void close() {
    inAppBrowser.close();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe
  public void onEvent(ChromeTabsDismissedEvent event) {
    EventBus.getDefault().unregister(this);
    WritableMap params = Arguments.createMap();
    this.reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("onClose", params);
  }

  private void registerEventBus() {
    if (!EventBus.getDefault().isRegistered(this)) {
       EventBus.getDefault().register(this);
    }
  }
}