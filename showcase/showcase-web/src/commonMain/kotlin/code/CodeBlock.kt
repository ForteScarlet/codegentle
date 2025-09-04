package code

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme
import love.forte.codegentle.showcase_web.generated.resources.*
import org.jetbrains.compose.resources.Font

private val lightSyntaxTheme = SyntaxTheme(
    key = "custom-light",   // 主题名称
    code = 0x24292E,       // 深灰色 - 默认代码颜色
    keyword = 0x6F42C1,     // 深紫色 - 关键字（if, for, class等）
    string = 0x032F62,      // 深蓝色 - 字符串
    literal = 0xE36209,     // 橙色 - 数字和字面量
    comment = 0x6A737D,     // 灰色 - 注释
    metadata = 0x005CC5,    // 蓝色 - 元数据和注解
    multilineComment = 0x6A737D, // 灰色 - 多行注释
    punctuation = 0x24292E, // 深灰色 - 标点符号
    mark = 0xB08800         // 深金色 - 高亮标记
)

/**
 * A composable that displays code with optional line numbers and syntax highlighting.
 * Supports hover effects and synchronization for side-by-side comparison.
 *
 * @param code The code content to display
 * @param language The programming language for syntax highlighting (e.g., "kt", "java", "js")
 * @param showLineNumbers Whether to show line numbers (default: true)
 * @param hoverEffectEnabled Whether to enable hover effects on code lines (default: false)
 * @param hoverSyncState Optional state for synchronizing hover effects across multiple CodeBlocks
 * @param modifier Modifier for styling the CodeBlock
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CodeBlock(
    code: String,
    language: String? = null,
    showLineNumbers: Boolean = true,
    hoverEffectEnabled: Boolean = false,
    hoverSyncState: CodeBlockHoverState? = null,
    modifier: Modifier = Modifier
) {
    val fontFamily = FontFamily(
        Font(Res.font.JetBrainsMono_Medium, FontWeight.Medium),
        Font(Res.font.JetBrainsMono_Bold, FontWeight.Bold),
        Font(Res.font.JetBrainsMono_Thin, FontWeight.Thin),
        Font(Res.font.JetBrainsMono_Light, FontWeight.Light),
        Font(Res.font.JetBrainsMono_ExtraBold, FontWeight.ExtraBold),
        Font(Res.font.JetBrainsMono_ExtraLight, FontWeight.ExtraLight),
    )

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Line numbers (optional)
            if (showLineNumbers) {
                Box(
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                ) {
                    LineNumbers(
                        content = code,
                        hoverEffectEnabled = hoverEffectEnabled,
                        hoverSyncState = hoverSyncState,
                        fontFamily = fontFamily
                    )
                }

                // Divider between line numbers and code
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }

            // Code content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
                    .verticalScroll(verticalScrollState)
            ) {
                CodeContent(
                    content = code,
                    language = language,
                    hoverEffectEnabled = hoverEffectEnabled,
                    hoverSyncState = hoverSyncState,
                    fontFamily = fontFamily
                )
            }
        }

        // Vertical scrollbar
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(verticalScrollState)
        )

        // Horizontal scrollbar
        HorizontalScrollbar(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            adapter = rememberScrollbarAdapter(horizontalScrollState)
        )
    }
}

/**
 * State class for synchronizing hover effects across multiple CodeBlocks
 */
@Stable
class CodeBlockHoverState {
    private var _hoveredLine = mutableStateOf<Int?>(null)
    val hoveredLine: State<Int?> = _hoveredLine

    fun setHoveredLine(lineNumber: Int?) {
        _hoveredLine.value = lineNumber
    }
}

/**
 * Creates and remembers a CodeBlockHoverState for synchronizing hover effects
 */
@Composable
fun rememberCodeBlockHoverState(): CodeBlockHoverState {
    return remember { CodeBlockHoverState() }
}

/**
 * Line numbers component with optional hover effects
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LineNumbers(
    content: String,
    hoverEffectEnabled: Boolean,
    hoverSyncState: CodeBlockHoverState?,
    fontFamily: FontFamily
) {
    val lines = content.split('\n')
    val maxLineNumber = lines.size
    val lineNumberWidth = maxLineNumber.toString().length
    val hoveredLine by (hoverSyncState?.hoveredLine ?: mutableStateOf(null))

    val textStyle = MaterialTheme.typography.bodySmall.copy(
        fontFamily = fontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    Column(
        modifier = Modifier.padding(end = 8.dp)
    ) {
        lines.forEachIndexed { index, _ ->
            val lineNumber = index + 1
            val isHovered = hoverEffectEnabled && hoveredLine == lineNumber
            
            Box(
                modifier = Modifier
                    .height(24.dp)
                    // .fillMaxWidth()
                    .background(
                        if (isHovered) 
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else 
                            Color.Transparent
                    )
                    .hoverable(
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .onPointerEvent(PointerEventType.Enter) {
                        if (hoverEffectEnabled) {
                            hoverSyncState?.setHoveredLine(lineNumber)
                        }
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        if (hoverEffectEnabled) {
                            hoverSyncState?.setHoveredLine(null)
                        }
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = lineNumber.toString().padStart(lineNumberWidth),
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
    }
}

/**
 * Code content component with syntax highlighting and hover effects
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CodeContent(
    content: String,
    language: String?,
    hoverEffectEnabled: Boolean,
    hoverSyncState: CodeBlockHoverState?,
    fontFamily: FontFamily
) {
    val hoveredLine by (hoverSyncState?.hoveredLine ?: mutableStateOf(null))

    val textStyle = MaterialTheme.typography.bodySmall.copy(
        fontFamily = fontFamily, // FontFamily.Monospace,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    val syntaxLanguage: SyntaxLanguage = remember(language) {
        when (language?.lowercase()) {
            "java" -> SyntaxLanguage.JAVA
            "kt", "kotlin", "kts" -> SyntaxLanguage.KOTLIN
            "js", "javascript" -> SyntaxLanguage.JAVASCRIPT
            "sh", "shell", "bash" -> SyntaxLanguage.SHELL
            else -> SyntaxLanguage.DEFAULT
        }
    }

    val contentStringLines: MutableList<AnnotatedString> = remember(content) {
        buildList {
            AnnotatedString(content).splitLines { add(it) }
        }.toMutableStateList()
    }

    // Apply syntax highlighting
    LaunchedEffect(content, language) {
        val contentCodeWithReplacedLineChar = content.replace(Regex("\r\n|\r|\n"), "\n")
        val highlights = Highlights.Builder()
            .language(syntaxLanguage)
            .code(contentCodeWithReplacedLineChar)
            .theme(lightSyntaxTheme)
            .build()

        val annotatedContent = buildAnnotatedString {
            append(contentCodeWithReplacedLineChar)
            for (highlight in highlights.getHighlights()) {
                val location = highlight.location
                when (highlight) {
                    is ColorHighlight -> {
                        val rgb: Int = highlight.rgb
                        val color = Color(
                            red = rgb shr 16 and 0xFF,
                            green = rgb shr 8 and 0xFF,
                            blue = rgb and 0xFF,
                        )
                        if (location.start <= location.end && location.start < length) {
                            val end = minOf(location.end, length)
                            addStyle(SpanStyle(color = color), location.start, end)
                        }
                    }
                    is BoldHighlight -> {
                        if (location.start <= location.end && location.start < length) {
                            val end = minOf(location.end, length)
                            addStyle(SpanStyle(fontWeight = FontWeight.Bold), location.start, end)
                        }
                    }
                }
            }
        }

        var i = 0
        annotatedContent.splitLines { line ->
            if (i < contentStringLines.size) {
                contentStringLines[i] = line
            }
            i++
        }
    }

    SelectionContainer {
        Column(Modifier.fillMaxWidth()) {
            for ((index, line) in contentStringLines.withIndex()) {
                val lineNumber = index + 1
                val isHovered = hoverEffectEnabled && hoveredLine == lineNumber

                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth()
                        .background(
                            if (isHovered)
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            else
                                Color.Transparent
                        )
                        .hoverable(
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .onPointerEvent(PointerEventType.Enter) {
                            if (hoverEffectEnabled) {
                                hoverSyncState?.setHoveredLine(lineNumber)
                            }
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            if (hoverEffectEnabled) {
                                hoverSyncState?.setHoveredLine(null)
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = line.ifEmpty { AnnotatedString(" ") },
                        style = textStyle,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Extension function to split AnnotatedString into lines
 */
private inline fun AnnotatedString.splitLines(onLine: (AnnotatedString) -> Unit) {
    var nextStart = 0
    var nextEnd = indexOf('\n')
    while (nextEnd >= 0) {
        val line = subSequence(nextStart, nextEnd)
        onLine(line)
        nextStart = nextEnd + 1
        nextEnd = indexOf('\n', nextStart)
    }

    if (nextStart < length) {
        val line = subSequence(nextStart, length)
        onLine(line)
    }
}

internal expect suspend fun Clipboard.setText(text: String)
