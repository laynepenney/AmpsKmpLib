import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.jvm.optionals.getOrNull


plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.android.library)
//    alias(libs.plugins.swiftklib)
    id("io.github.ttypic.swiftklib")
    id("pro-layne-amps-kmp-publish")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    val versionCatalog: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    jvmToolchain {
        val javaVersion = versionCatalog.findVersion("java").getOrNull()?.requiredVersion
            ?: throw GradleException("Version 'java' is not specified in the version catalog")
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    explicitApi()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {

        it.compilations.getByName("main") {
            cinterops {
                create("Otel") {

                }
            }
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "2.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "shared"
            isStatic = true
        }

        pod("EmbraceIO") {
            version = "6.8.4"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "layne.pro.sample.ampskmplib"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

swiftklib {
    create("Otel") {
        path = file("../Otel")
        packageName("otel")
    }
}


/*

    private fun buildSwift(xcodeVersion: Int): SwiftBuildResult {
        val sourceFilePathReplacements = mapOf(
            buildDir().absolutePath to pathProperty.get().absolutePath
        )
        val extraArgs = if (xcodeVersion >= 15 && compileTarget in SDKLESS_TARGETS) {
            additionalSysrootArgs()
        } else {
            emptyList()
        }
        val args = generateBuildArgs() + extraArgs

        logger.info("-- Running swift build --")
        logger.info("Working directory: $swiftBuildDir")
        logger.info("xcrun ${args.joinToString(" ")}")

        execOperations.exec {
            it.executable = "xcrun"
            it.workingDir = swiftBuildDir
            it.args = args
            it.standardOutput = StringReplacingOutputStream(
                delegate = System.out,
                replacements = sourceFilePathReplacements
            )
            it.errorOutput = StringReplacingOutputStream(
                delegate = System.err,
                replacements = sourceFilePathReplacements
            )
        }

        val releaseBuildPath = File(swiftBuildDir, ".build/${compileTarget.arch()}-apple-macosx/release")

        return SwiftBuildResult(
            libPath = File(releaseBuildPath, "lib${cinteropName}.a"),
            headerPath = File(releaseBuildPath, "$cinteropName.build/$cinteropName-Swift.h")
        )
    }

    private fun generateBuildArgs(): List<String> {
        val sdkPath = readSdkPath()
        val baseArgs = "swift build --arch ${compileTarget.arch()} -c release".split(" ")

        val xcrunArgs = listOf(
            "-sdk",
            sdkPath,
            "-target",
            compileTarget.asSwiftcTarget(compileTarget.operatingSystem()),
        ).asSwiftcArgs()

        return baseArgs + xcrunArgs
    }

    /** Workaround for bug in toolchain where the sdk path (via `swiftc -sdk` flag) is not propagated to clang. */
    private fun additionalSysrootArgs(): List<String> =
        listOf(
            "-isysroot",
            readSdkPath(),
        ).asCcArgs()

    private fun List<String>.asSwiftcArgs() = asBuildToolArgs("swiftc")
    private fun List<String>.asCcArgs() = asBuildToolArgs("cc")

    private fun List<String>.asBuildToolArgs(tool: String): List<String> {
        return this.flatMap {
            listOf("-X$tool", it)
        }
    }

    private fun readSdkPath(): String {
        val stdout = ByteArrayOutputStream()

        execOperations.exec {
            it.executable = "xcrun"
            it.args = listOf(
                "--sdk",
                compileTarget.os(),
                "--show-sdk-path",
            )
            it.standardOutput = stdout
        }

        return stdout.toString().trim()
    }

    private fun readXcodeMajorVersion(): Int {
        val stdout = ByteArrayOutputStream()

        execOperations.exec {
            it.executable = "xcodebuild"
            it.args = listOf("-version")
            it.standardOutput = stdout
        }

        val output = stdout.toString().trim()
        val (_, majorVersion) = "Xcode (\\d+)\\..*".toRegex().find(output)?.groupValues
            ?: throw IllegalStateException("Can't find Xcode")

        return majorVersion.toInt()
    }

    private fun readXcodePath(): String {
        val stdout = ByteArrayOutputStream()

        execOperations.exec {
            it.executable = "xcode-select"
            it.args = listOf("--print-path")
            it.standardOutput = stdout
        }

        return stdout.toString().trim()
    }

    /**
     * Generates Def-file for Kotlin/Native Cinterop
     *
     * Note: adds lib-file md5 hash to library in order to automatically
     * invalidate connected cinterop task
     */
    private fun createDefFile(libPath: File, headerPath: File, packageName: String, xcodeVersion: Int) {
        val xcodePath = readXcodePath()

        val linkerPlatformVersion =
            if (xcodeVersion >= 15) compileTarget.linkerPlatformVersionName()
            else compileTarget.linkerMinOsVersionName()

        val modulePath = headerPath.parentFile.absolutePath

        val basicLinkerOpts = listOf(
            "-L/usr/lib/swift",
            "-$linkerPlatformVersion",
            "${minOs(compileTarget)}.0",
            "${minOs(compileTarget)}.0",
            "-L${xcodePath}/Toolchains/XcodeDefault.xctoolchain/usr/lib/swift/${compileTarget.os()}"
        )

        val linkerOpts = basicLinkerOpts.joinToString(" ")

        val content = """
            package = $packageName
            language = Objective-C
            modules = $cinteropName

            # md5 ${libPath.md5()}
            staticLibraries = ${libPath.name}
            libraryPaths = "${libPath.parentFile.absolutePath}"

            compilerOpts = -fmodules -I"$modulePath"
            linkerOpts = $linkerOpts
        """.trimIndent()

        logger.info("--- Generated cinterop def file for $cinteropName ---")
        logger.info("--- cinterop def ---")
        logger.info(content)
        logger.info("---/ cinterop def /---")

        defFile.writeText(content)
    }

    private fun CompileTarget.operatingSystem(): String =
        when (this) {
            CompileTarget.iosX64, CompileTarget.iosArm64, CompileTarget.iosSimulatorArm64 -> "ios$minIos"
            CompileTarget.watchosX64, CompileTarget.watchosArm64, CompileTarget.watchosSimulatorArm64 -> "watchos$minWatchos"
            CompileTarget.tvosX64, CompileTarget.tvosArm64, CompileTarget.tvosSimulatorArm64 -> "tvos$minTvos"
            CompileTarget.macosX64, CompileTarget.macosArm64 -> "macosx$minMacos"
        }

    private fun minOs(compileTarget: CompileTarget): Int =
        when (compileTarget) {
            CompileTarget.iosX64, CompileTarget.iosArm64, CompileTarget.iosSimulatorArm64 -> minIos
            CompileTarget.watchosX64, CompileTarget.watchosArm64, CompileTarget.watchosSimulatorArm64 -> minWatchos
            CompileTarget.tvosX64, CompileTarget.tvosArm64, CompileTarget.tvosSimulatorArm64 -> minTvos
            CompileTarget.macosX64, CompileTarget.macosArm64 -> minMacos
        }
}

 */