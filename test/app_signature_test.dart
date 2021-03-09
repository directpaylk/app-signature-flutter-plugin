import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:app_signature/app_signature.dart';

void main() {
  const MethodChannel channel = MethodChannel('app_signature');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getSignature', () async {
    expect(await AppSignature.platformVersion, '42');
  });
}
