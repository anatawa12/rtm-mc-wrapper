package com.anatawa12.dtsGenerator

inline fun buildSrc(builderAction: SrcBuilder.() -> Unit): String =
        SrcBuilder().apply(builderAction).toString()

class SrcBuilder : Appendable {
    private val builder = StringBuilder()

    override fun append(v: CharSequence) = apply {
        appendIndent()
        builder.append(v)
    }

    override fun append(c: Char) = apply {
        appendIndent()
        builder.append(c)
    }

    override fun append(csq: CharSequence, start: Int, end: Int) = apply {
        appendIndent()
        builder.append(csq, start, end)
    }

    fun appendln(v: String) {
        append(v)
        appendln()
    }

    fun appendln() {
        builder.appendln()
        shouldIndent = true
    }

    override fun toString(): String {
        return builder.toString()
    }

    fun indent(width: String = "    ") {
        indent += width
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun outdent() {
        indent.removeLast()
    }

    private val indent = mutableListOf<String>()
    private var shouldIndent = true
    private fun appendIndent() {
        if (shouldIndent)
            for (part in indent)
                builder.append(part)
        shouldIndent = false
    }
}
