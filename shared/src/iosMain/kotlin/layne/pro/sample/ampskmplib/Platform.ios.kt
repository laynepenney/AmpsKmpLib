package layne.pro.sample.ampskmplib

import platform.UIKit.UIDevice
import cocoapods.EmbraceIO.EMBWebViewCaptureServiceOptions
//import Otel.

(Otel)

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()