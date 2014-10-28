import Foundation

 @objc(OAuth2Plugin) class OAuth2Plugin : CDVPlugin {
    func getGoogleDriveFiles(command: CDVInvokedUrlCommand) {
        var pluginResult = CDVPluginResult(status: CDVCommandStatus_OK)
        commandDelegate.sendPluginResult(pluginResult, callbackId:command.callbackId)
    }
}