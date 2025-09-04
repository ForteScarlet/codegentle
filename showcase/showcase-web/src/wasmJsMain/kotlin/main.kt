import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.dom.clear

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // codegentle-showcase-root

    val showcase = document.getElementById("codegentle-showcase-root")?.also { it.clear() }
        ?: document.createElement("div").also {
            it.id = "codegentle-showcase-root"
            document.body!!.appendChild(it)
        }

    ComposeViewport(showcase) {
        App()
    }
}
