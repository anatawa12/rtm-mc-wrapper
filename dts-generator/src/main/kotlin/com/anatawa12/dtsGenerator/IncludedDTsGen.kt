package com.anatawa12.dtsGenerator

import org.objectweb.asm.Opcodes


object IncludedDTsGen {
    fun generate(include: String, args: GenProcessArgs) = buildSrc {
        args.header?.let { header ->
            append(header)
            appendln()
        }
        appendln(include)
        appendln()
        generateClassesInPackage(args, args.classes.rootPackage, this)
        appendln()
    }

    private fun generateClassesInPackage(args: GenProcessArgs, thePackage: ThePackage, builder: SrcBuilder): SrcBuilder = builder.apply {
        val children = thePackage.children.entries
                .filter { GenUtil.elementFilter(it.value) }
                .sortedWith(compareBy<Map.Entry<String, TheElement>> { it.value.type }
                        .thenBy { it.key })

        children@for ((_, child) in children) {
            when (child) {
                is ThePackage -> {
                    generateClassesInPackage(args, child, builder)
                }
                is TheClass -> {
                    if (child.accessExternally.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    generateClass(args, child, this)
                }
                is TheMethods -> error("method cannot be child of package")
                is TheField -> error("field cannot be child of package")
                TheDuplicated -> {
                    // nop
                }
            }
        }
    }

    private fun generateClass(args: GenProcessArgs, theClass: TheClass, builder: SrcBuilder) = builder.apply {
        val simpleName = theClass.name.substringAfterLast('/').substringAfterLast('.').substringAfterLast('$')

        // constructor class
        append("type ").append(simpleName)
                .append(" = ").append(tsValItfOrPrimitive(theClass.name))
                .appendln(";")
    }

    private fun tsValItfOrPrimitive(name: String) = "Packages." + name.replace('/', '.')

}
