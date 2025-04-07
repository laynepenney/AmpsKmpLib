plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
//    implementation(libs.dokka.gradle.plugin)
//    implementation(libs.animalsniffer.gradle.plugin)
}

kotlin {
    jvmToolchain(JavaLanguageVersion.of(libs.versions.java.get()).asInt())
}