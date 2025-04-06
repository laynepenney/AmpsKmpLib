enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("../swift-klib-plugin")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AmpsKMPLib"
include(":shared")