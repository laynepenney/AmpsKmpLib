// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "Otel",
    platforms: [
        .macOS(.v12),
        .iOS(.v13),
        .tvOS(.v13),
        .watchOS(.v6)
    ],
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "Otel",
            targets: ["Otel"]),
    ],
    dependencies: [
        .package(url: "https://github.com/open-telemetry/opentelemetry-swift", .upToNextMajor(from: "1.14.0"))
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .target(
            name: "Otel",
            dependencies: [
                .product(name: "OpenTelemetryApi", package: "opentelemetry-swift"),
                .product(name: "OpenTelemetrySdk", package: "opentelemetry-swift"),
            ]),
        .testTarget(
            name: "OtelTests",
            dependencies: ["Otel"]
        ),
    ]
)
