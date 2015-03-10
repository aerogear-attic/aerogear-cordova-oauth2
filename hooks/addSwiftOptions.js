var fs = require("fs");
 
module.exports = function (context) {
	var projectRoot = context.opts.projectRoot;
	var xcconfigPath = projectRoot + "/platforms/ios/cordova/build.xcconfig";
 
	var pluginDir = context.opts.plugin.dir;
	var srcDir = pluginDir + "/src/ios/";
 
	var swiftOptions = [""];
	swiftOptions.push("IPHONEOS_DEPLOYMENT_TARGET = 7.0");
	swiftOptions.push("SWIFT_OBJC_BRIDGING_HEADER = " + srcDir + "Bridging-Header.h");
    swiftOptions.push("LD_RUNPATH_SEARCH_PATHS = $(inherited) @executable_path/Frameworks");
 
	fs.appendFileSync(xcconfigPath, swiftOptions.join('\n'));
}