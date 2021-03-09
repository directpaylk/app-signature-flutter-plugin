#import "AppSignaturePlugin.h"
#if __has_include(<app_signature/app_signature-Swift.h>)
#import <app_signature/app_signature-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "app_signature-Swift.h"
#endif

@implementation AppSignaturePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAppSignaturePlugin registerWithRegistrar:registrar];
}
@end
