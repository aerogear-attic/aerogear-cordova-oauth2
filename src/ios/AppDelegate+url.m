//
//  AppDelegate+url.m
//  HelloCordova
//
//  Created by Erik Jan de Wit on 25/11/14.
//
//

#import "AppDelegate+url.h"
#import "AeroGearHttp.h"
#import <objc/runtime.h>

@implementation AppDelegate (url)

// its dangerous to override a method from within a category.
// Instead we will use method swizzling. we set this up in the load call.
+ (void)load
{
    Method original, swizzled;
    
    original = class_getInstanceMethod(self, @selector(application:openURL:sourceApplication:annotation:));
    swizzled = class_getInstanceMethod(self, @selector(swizzled_application:openURL:sourceApplication:annotation:));
    method_exchangeImplementations(original, swizzled);
}

- (BOOL)swizzled_application:(UIApplication*)application openURL:(NSURL*)url sourceApplication:(NSString*)sourceApplication annotation:(id)annotation
{
    NSNotification *notification = [NSNotification notificationWithName:@"AGAppLaunchedWithURLNotification" object:nil userInfo:[NSDictionary dictionaryWithObject:url forKey:UIApplicationLaunchOptionsURLKey]];
    [[NSNotificationCenter defaultCenter] postNotification:notification];
    return [self swizzled_application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
}
@end