package code

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

internal actual suspend fun Clipboard.setText(text: String) {
    setClipEntry(ClipEntry(text))
}
