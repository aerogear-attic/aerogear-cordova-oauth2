Xcode
=====

When you open the xcode project the changes made to it with the `build.xconfig` file will not be reflected. To make the project build again make the following changes:

1. change `deployment target` to 7.0 or above
![deployment target](ios_step_1.png "deployment target")

2. add `[Project Name]/plugins/org.jboss.aerogear.cordova.oauth2/src/ios/Bridging-Header.h` to Objective-c Bridging Header under the Swift Compiler - Code Generation options
![bridging header](ios_step_2.png "bridging header")

3. set `Runpath Search Paths` to "$(inherited) @executable_path/Frameworks"
![search path](ios_step_3.png "search path")

