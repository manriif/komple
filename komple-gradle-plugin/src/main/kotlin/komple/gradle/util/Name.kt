/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package komple.gradle.util

import java.util.*

///////////////////////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////////////////////

private val SplitRegex = Regex("""[^a-zA-Z0-9]""")

private val HardKeywords = arrayOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
    "interface", "is", "null", "object", "package", "return", "super", "this", "throw",
    "true", "try", "typealias", "typeof", "val", "var", "when", "while"
)

///////////////////////////////////////////////////////////////////////////
// Format
///////////////////////////////////////////////////////////////////////////

/**
 * Capitalizes [this] first character.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun String.capitalize() = replaceFirstChar { char ->
    if (char.isLowerCase()) {
        char.titlecase(Locale.getDefault())
    } else {
        char.toString()
    }
}

/**
 * Decapitalizes [this] first character.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun String.decapitalize() = replaceFirstChar { char ->
    if (char.isUpperCase()) {
        char.lowercase(Locale.getDefault())
    } else {
        char.toString()
    }
}

/**
 * Transforms [this] to word [separator] separated.
 */
private fun String.wordSeparated(separator: Char): String {
    val builder = StringBuilder()

    forEachIndexed { index, char ->
        when {
            char.isUpperCase() && index > 0 && get(index - 1).isLowerCase() -> {
                builder.append(separator)
                builder.append(char.lowercaseChar())
            }

            char.isWhitespace() -> builder.append(separator)
            char.isLetterOrDigit() -> builder.append(char.lowercaseChar())
            else -> builder.append(separator) // Transform other chars to separator
        }
    }

    return builder.toString()
}

/**
 * Transforms [this] to dash-case.
 */
internal fun String.dashCased(): String {
    return wordSeparated('-')
}

/**
 * Returns a valid Kotlin type name (PascalCase) from `this` [String].
 */
internal fun String.pascalCased(): String {
    var name = this

    // Preserve the initial underscore if it exists
    var prefix = ""

    if (startsWith('_')) {
        prefix = "_"
        name = name.substring(1) // Remove the initial underscore for processing
    }

    return prefix + name
        .split(SplitRegex)
        .joinToString("") { word ->
            if (word.isNotEmpty()) {
                "${word[0].uppercaseChar()}${word.substring(1)}"
            } else {
                word
            }
        }
}

/**
 * Returns a valid Kotlin property name (camelCase) from `this` [String].
 */
internal fun String.camelCased(): String {
    val words = split(SplitRegex)

    val name = if (words.size == 1) {
        words.first().lowercase()
    } else {
        words
            .mapIndexed { index, word ->
                if (index == 0) {
                    word.lowercase()
                } else {
                    word.lowercase().capitalize()
                }
            }
            .joinToString("")
    }

    return if (name in HardKeywords) {
        "`$name`"
    } else {
        name
    }
}