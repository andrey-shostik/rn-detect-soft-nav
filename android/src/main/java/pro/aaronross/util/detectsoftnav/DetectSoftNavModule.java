package pro.aaronross.util.detectsoftnav;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.provider.Settings;
import android.content.res.Resources;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.LifecycleEventListener;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;

public class DetectSoftNavModule extends ReactContextBaseJavaModule {
    ReactApplicationContext mContext;
    boolean _init = false;

    public DetectSoftNavModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @ReactMethod
    public void init() {
        if (_init) return;

        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View decorView = getCurrentActivity().getWindow().getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        WritableMap params = Arguments.createMap();
                        sendEvent(mContext, (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0 ? "softNavDidShow" : "softNavDidHide", params);
                    }
                });

                // flicker SYSTEM_UI_FLAG_HIDE_NAVIGATION so that isVisible calls will detect the right value
                // don't ask, it works ;)
                int uiOptions = decorView.getSystemUiVisibility();
                decorView.setSystemUiVisibility(uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                decorView.setSystemUiVisibility(uiOptions);
            }
        });
        _init = true;
    }

   @ReactMethod
      public void isVisible(final Promise promise) {
        try {
           View decorView = getCurrentActivity().getWindow().getDecorView().getRootView();
            int visibility = decorView.getSystemUiVisibility();

            promise.resolve((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0);
          } catch (Exception e) {
            promise.reject(e);
          }
   }

    @Override
    public String getName() {
        return "DetectSoftNav";
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

 @Override
     public Map<String, Object> getConstants() {
         Map<String, Object> constants = new HashMap<>();

         constants.put("hasSoftKeys", hasSoftKeys());
         constants.put("hasSoftKeysHeight", hasSoftKeysHeight());
         return constants;
     }

    private boolean hasSoftKeys() {
             boolean hasSoftwareKeys;

             Activity activity = getCurrentActivity();

             if (activity == null) {
                 return true;
             }

             WindowManager window = getCurrentActivity().getWindowManager();

             if(window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                 Display d = getCurrentActivity().getWindowManager().getDefaultDisplay();

                 DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                 d.getRealMetrics(realDisplayMetrics);

                 int realHeight = realDisplayMetrics.heightPixels;
                 int realWidth = realDisplayMetrics.widthPixels;

                 DisplayMetrics displayMetrics = new DisplayMetrics();
                 d.getMetrics(displayMetrics);

                 int displayHeight = displayMetrics.heightPixels;
                 int displayWidth = displayMetrics.widthPixels;

                 hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
             } else {
                 boolean hasMenuKey = ViewConfiguration.get(getReactApplicationContext()).hasPermanentMenuKey();
                 boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

                 hasSoftwareKeys = !hasMenuKey && !hasBackKey;
             }

             return hasSoftwareKeys;
        }

          private float hasSoftKeysHeight() {
                  float hasSoftKeysHeight;

                  Activity activity = getCurrentActivity();

                  WindowManager window = getCurrentActivity().getWindowManager();

                  if(window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                      Display d = getCurrentActivity().getWindowManager().getDefaultDisplay();

                      DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                      d.getRealMetrics(realDisplayMetrics);

                      int realHeight = realDisplayMetrics.heightPixels;
                      int realWidth = realDisplayMetrics.widthPixels;

                      DisplayMetrics displayMetrics = new DisplayMetrics();
                      d.getMetrics(displayMetrics);

                      int displayHeight = displayMetrics.heightPixels;
                      int displayWidth = displayMetrics.widthPixels;

                      hasSoftKeysHeight =  (realHeight - displayHeight) / 3;
                  } else {
                      boolean hasMenuKey = ViewConfiguration.get(getReactApplicationContext()).hasPermanentMenuKey();
                      boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

                      hasSoftKeysHeight = 0;
                  }

                  return hasSoftKeysHeight;
           }
}
