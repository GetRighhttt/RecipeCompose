import RecipeComposeShared
import SwiftUI
import UIKit

/// The only bridge required to place a shared Compose UIViewController inside
/// SwiftUI. All onboarding layout, state, theme, and resources remain Kotlin.
private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(
        _ uiViewController: UIViewController,
        context: Context
    ) {
        // Shared state drives updates; the native host owns no UI state.
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            // Let Compose react to the keyboard without placing content under
            // the status bar or home indicator.
            .ignoresSafeArea(.keyboard)
    }
}
