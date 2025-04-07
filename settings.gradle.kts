import java.net.URI

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


pluginManagement {
    includeBuild("build-logic")
//    includeBuild("../swift-klib-plugin")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven { url = File(rootDir, "repo").toURI() }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = File(rootDir, "repo").toURI() }
    }
}

rootProject.name = "amps-kmp-lib"
include(":shared")