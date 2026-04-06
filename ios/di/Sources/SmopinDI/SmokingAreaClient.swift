import Dependencies
import FirebaseCore
import Foundation

public struct SmokingAreaClient {
    public var configureFirebaseIfNeeded: @Sendable () -> Void
    public var getSmokingAreaNameList: @Sendable () async throws -> [String]

    public init(
        configureFirebaseIfNeeded: @escaping @Sendable () -> Void,
        getSmokingAreaNameList: @escaping @Sendable () async throws -> [String]
    ) {
        self.configureFirebaseIfNeeded = configureFirebaseIfNeeded
        self.getSmokingAreaNameList = getSmokingAreaNameList
    }
}

extension SmokingAreaClient: DependencyKey {
    public static let liveValue = Self(
        configureFirebaseIfNeeded: {
            if FirebaseApp.app() == nil {
                FirebaseApp.configure()
            }
        },
        getSmokingAreaNameList: {
#if canImport(SharedDatabaseFirestore)
            let repository = SmokingAreaRepositoryFactoryKt.createSmokingAreaRepositoryForIos()
            let smokingAreaList = try await repository.getSmokingAreaList()
            return smokingAreaList.map(\.name)
#else
            return ["KMP Framework not linked"]
#endif
        }
    )

    public static let testValue = Self(
        configureFirebaseIfNeeded: {},
        getSmokingAreaNameList: { [] }
    )
}

public extension DependencyValues {
    var smokingAreaClient: SmokingAreaClient {
        get { self[SmokingAreaClient.self] }
        set { self[SmokingAreaClient.self] = newValue }
    }
}
