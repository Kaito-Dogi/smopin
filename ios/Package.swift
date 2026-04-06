// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "Smopin",
    platforms: [
        .iOS(.v18),
    ],
    products: [
        .library(
            name: "AppFeature",
            targets: ["AppFeature"]
        ),
        .library(
            name: "HogeFeature",
            targets: ["HogeFeature"]
        ),
        .library(
            name: "SmopinDI",
            targets: ["SmopinDI"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/pointfreeco/swift-dependencies", from: "1.9.2"),
        .package(url: "https://github.com/firebase/firebase-ios-sdk", from: "12.4.0"),
    ],
    targets: [
        .target(
            name: "AppFeature",
            dependencies: [
                "HogeFeature",
            ],
            path: "app/Sources/AppFeature"
        ),
        .target(
            name: "HogeFeature",
            dependencies: [
                "SmopinDI",
                .product(name: "Dependencies", package: "swift-dependencies"),
            ],
            path: "feature/hoge/Sources/HogeFeature"
        ),
        .target(
            name: "SmopinDI",
            dependencies: [
                .product(name: "Dependencies", package: "swift-dependencies"),
                .product(name: "FirebaseCore", package: "firebase-ios-sdk"),
            ],
            path: "di/Sources/SmopinDI"
        ),
    ]
)
