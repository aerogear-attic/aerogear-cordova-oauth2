import Foundation

 @objc(OAuth2Plugin) class OAuth2Plugin : CDVPlugin {
    
    func add(command: CDVInvokedUrlCommand) {
        let args = command.arguments[0] as! [String: String]
        
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
    
    func addGoogle(command: CDVInvokedUrlCommand) {
        let args = command.arguments[0] as! [String: String]
        
        var scopes: [String] = []
        if args["scopes"] != nil {
            scopes = args["scopes"]!.componentsSeparatedByString(",")
        }
        
        let googleConfig = GoogleConfig(
            clientId: args["clientId"]!,
            scopes: scopes,
            accountId: args["accountId"])
        
        AccountManager.addGoogleAccount(googleConfig)
    }
    
    func addKeycloak(command: CDVInvokedUrlCommand) {
        let args = command.arguments[0] as! [String: String]
        let keycloakConfig = KeycloakConfig(
            clientId: args["clientId"]!,
            host: args["base"]!.stringByReplacingOccurrencesOfString("/auth", withString: ""),
            realm: args["realm"])
        
        keycloakConfig.accountId = args["accountId"]
        
        AccountManager.addKeycloakAccount(keycloakConfig)
    }

    func addFacebook(command: CDVInvokedUrlCommand) {
        let args = command.arguments[0] as! [String: String]
        
        var scopes: [String] = []
        if args["scopes"] != nil {
            scopes = args["scopes"]!.componentsSeparatedByString(",")
        }
        
        let facebookConfig = FacebookConfig(
            clientId: args["clientId"]!,
            clientSecret: args["clientSecret"]!,
            scopes:scopes)
        
        facebookConfig.accountId = args["accountId"]
        
        AccountManager.addFacebookAccount(facebookConfig)
    }
    
    func requestAccess(command: CDVInvokedUrlCommand) {
        let accountId = command.arguments[0] as! String
        let module = AccountManager.getAccountByName(accountId)

        commandDelegate?.runInBackground { () -> Void  in
            if let module = module {
                module.requestAccess({ (accessToken, error ) in
                    var commandResult:CDVPluginResult
                    if let error = error {
                        commandResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAsString: error.description)
                    } else {
                        commandResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAsString: accessToken as! String)
                    }
                    self.commandDelegate?.sendPluginResult(commandResult, callbackId: command.callbackId)
                })
            }
        }
    }
}