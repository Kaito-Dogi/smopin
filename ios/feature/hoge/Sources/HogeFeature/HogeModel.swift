import Dependencies
import Foundation
import Observation
import SmopinDI

@MainActor
@Observable
public final class HogeModel {
    public private(set) var smokingAreaNameListText: String = "Loading..."

    @ObservationIgnored
    @Dependency(\.smokingAreaClient) private var smokingAreaClient

    public init() {}

    public func onAppear() async {
        smokingAreaClient.configureFirebaseIfNeeded()

        do {
            let smokingAreaNameList = try await smokingAreaClient.getSmokingAreaNameList()
            smokingAreaNameListText = smokingAreaNameList.isEmpty
                ? "No smoking area"
                : smokingAreaNameList.joined(separator: "\n")
        } catch {
            smokingAreaNameListText = "Failed to fetch smoking area"
        }
    }
}
