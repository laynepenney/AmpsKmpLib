// The Swift Programming Language
// https://docs.swift.org/swift-book

import Foundation
import OpenTelemetrySdk


@objc public class Otel : NSObject {
    @objc public static func hello() -> String {
        let sel = OpenTelemetrySdk.AggregationSelector.defaultSelector()
        return "\(String(describing: sel))"
    }
}
