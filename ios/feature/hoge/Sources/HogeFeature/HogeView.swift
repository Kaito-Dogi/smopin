import SwiftUI

public struct HogeView: View {
    @State private var model = HogeModel()

    public init() {}

    public var body: some View {
        Text(model.smokingAreaNameListText)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .padding()
            .task {
                await model.onAppear()
            }
    }
}

#Preview {
    HogeView()
}
