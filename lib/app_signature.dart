
import 'dart:async';

import 'package:flutter/services.dart';

class AppSignature {
  static const MethodChannel _channel =
      const MethodChannel('app_signature');

  static Future<String> get getSignature async {
    final String version = await _channel.invokeMethod('getSignature');
    return version;
  }
}
