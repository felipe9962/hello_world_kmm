import SwiftUI
import generic_model

struct ContentView: View {
	let greet = Greeting().greeting()

	var body: some View {
		Text(greet)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}