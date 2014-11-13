import Foundation

 @objc(OAuth2Plugin) class OAuth2Plugin : CDVPlugin {
    var accountId: String?
    
    func add(command: CDVInvokedUrlCommand) {
        let args = command.arguments[0] as [String: String]
        
        self.accountId = args["accountId"]
        var scopes: [String] = []
        if args["scopes"] != nil {
            scopes = args["scopes"]!.componentsSeparatedByString(",")
        }
        let config = Config(base: args["base"]!,
            authzEndpoint: args["authzEndpoint"]!,
            redirectURL: args["redirectURL"]!,
            accessTokenEndpoint: args["accessTokenEndpoint"]!,
            clientId: args["clientId"]!,
            refreshTokenEndpoint: args["refreshTokenEndpoint"],
            clientSecret: args["clientSecret"],
            revokeTokenEndpoint: args["revokeTokenEndpoint"],
            scopes: scopes,
            accountId: args["accountId"])
        
        
        AccountManager.addAccount(config, moduleClass: OAuth2Module.self)
    }
    
    func requestAccess(command: CDVInvokedUrlCommand) {
        let module = AccountManager.getAccountByName(self.accountId!)
        module?.requestAccess({ (response, error ) in
            var commandResult:CDVPluginResult
            if (error != nil) {
                commandResult = CDVPluginResult(status: CDVCommandStatus_OK)
            } else {
                commandResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAsString: error?.description)
            }
            self.commandDelegate.sendPluginResult(commandResult, callbackId: command.callbackId)
        })
    }
}