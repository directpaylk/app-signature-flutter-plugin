package com.example.app_signature;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Debug;
import android.provider.Settings;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AppSignaturePlugin */
public class AppSignaturePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "app_signature");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getSignature")) {
      result.success(getAppSignature(activity.getApplicationContext()));
    }else if(call.method.equals("isDebuggerAttached")) {
      result.success(isDebugOn(activity.getApplicationContext()) || isDebuggerAttached() || isDebuggable(activity.getApplicationContext()) );
    } else {
      result.notImplemented();
    }
  }
  public static boolean isDebuggable(Context context){
   
    return ((context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);

  }
  private boolean isDebugOn(Context context){
    if(Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1) {
      return true;
    } 
    return false;
  }
  private boolean isDebuggerAttached(){
    return Debug.isDebuggerConnected();
  }
  private String getAppSignature(Context context) {

    String hash = "";
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      File file = new File(info.applicationInfo.sourceDir);
      hash = calculateMD5(file);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return hash;
    
    }
  private String calculateMD5(File file) {

    MessageDigest digest;

    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      return null;
    }

    InputStream is;
    try {
      is = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      return null;
    }

    byte[] buffer = new byte[8192];
    int read;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      String output = bigInt.toString(16);
      output = String.format("%32s", output).replace(' ', '0');
      return output;
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for MD5", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }
  }
  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}
