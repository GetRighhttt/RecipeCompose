import SwiftUI

/// Native lifecycle owner for iOS. Application content is rendered by the
/// Compose Multiplatform controller hosted in `ContentView`.
@main
struct RecipeComposeApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
