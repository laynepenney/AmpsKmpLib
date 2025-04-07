package layne.pro.sample.ampskmplib

import platform.UIKit.UIDevice
import cocoapods.EmbraceIO.EMBWebViewCaptureServiceOptions
import kotlinx.cinterop.ExperimentalForeignApi
import otel.Otel

public class IOSPlatform: Platform {
    @OptIn(ExperimentalForeignApi::class)
    override val name: String
        get() {
            val name = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
            return "${name} and otel:${Otel.hello()}"
        }
}

public actual fun getPlatform(): Platform = IOSPlatform()