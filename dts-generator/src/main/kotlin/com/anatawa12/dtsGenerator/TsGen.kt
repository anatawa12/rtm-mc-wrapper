package com.anatawa12.dtsGenerator

import org.objectweb.asm.Opcodes


object TsGen {
    fun generate(classes: ClassesManager) = buildString {
        appendln("declare const Packages: p_")
        appendln("interface String extends i_java_lang_String {}")
        appendln()
        appendln("type t_byte = number")
        appendln("type t_char = number")
        appendln("type t_double = number")
        appendln("type t_float = number")
        appendln("type t_int = number")
        appendln("type t_long = number")
        appendln("type t_short = number")
        appendln("type t_boolean = boolean")
        appendln("type t_array<T> = Array<T>")
        appendln()
        generatePackage(classes, classes.rootPackage, this)
        appendln()
    }

    private val extraTypeMapping = mutableMapOf(
            "java/lang/Object" to "unknown",
            "java/lang/String" to "String",
            "java/lang/Byte" to "t_byte",
            "java/lang/Character" to "t_char",
            "java/lang/Double" to "t_double",
            "java/lang/Float" to "t_float",
            "java/lang/Integer" to "t_int",
            "java/lang/Long" to "t_long",
            "java/lang/Short" to "t_short",
            "java/lang/Boolean" to "t_boolean"
    )

    private const val idt = "    "

    private fun generatePackage(classes: ClassesManager, thePackage: ThePackage, builder: StringBuilder): StringBuilder = builder.apply {
        val packageItfName = thePackage.tsItfName
        val children = thePackage.children.entries
                .filter { elementFilter(it.value) }
                .sortedWith(compareBy<Map.Entry<String, TheElement>> { it.value.type }
                        .thenBy { it.key })
        appendln("interface $packageItfName {")
        children@for ((name, child) in children) {
            when (child) {
                is ThePackage -> {
                    appendln("$idt[${string(name)}]: ${child.tsItfName}")
                }
                is TheClass -> {
                    if (child.accessExternally.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    generateComment(child.comments, "$idt", this)
                    appendln("$idt[${string(name)}]: ${child.tsCtorItfName}")
                }
                is TheMethods -> error("method cannot be child of package")
                is TheField -> error("field cannot be child of package")
            }
        }
        appendln("}")
        appendln()
        children@for ((name, child) in children) {
            when (child) {
                is ThePackage -> {
                    generatePackage(classes, child, this)
                }
                is TheClass -> {
                    if (child.accessExternally.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    generateClass(classes, child, this)
                }
                is TheMethods -> error("method cannot be child of package")
                is TheField -> error("field cannot be child of package")
            }
        }
    }

    private fun elementFilter(element: TheElement): Boolean = when (element) {
        is TheDuplicated -> false
        is ThePackage -> true
        is TheClass -> element.gotClass && element.need && element.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) != 0
        is TheMethods -> true
        is TheField -> true
    }

    private fun generateClass(classes: ClassesManager, theClass: TheClass, builder: StringBuilder, outerTypeParams: List<TypeParam> = listOf(), innerDeep: Int = 0): StringBuilder = builder.apply {
        val ctorItfName = theClass.tsCtorItfName
        val valItfName = theClass.tsValItfName
        val valBodyItfName = theClass.tsValBodyItfName
        val children = theClass.children.entries
                .sortedWith(compareBy<Map.Entry<String, TheElement>> { it.value.type }
                        //.thenComparator { a, b ->  }
                        .thenBy { it.key })
        val typeParams = outerTypeParams + theClass.signature?.params.orEmpty()

        // constructor class
        generateComment(theClass.comments, "", this)
        appendln("interface $ctorItfName {")
        var first = true
        children@for ((name, child) in children) {
            when (child) {
                is ThePackage -> error("package cannot be child of class")
                is TheClass -> {
                    if (child.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    if (child.accessExternallyChecked.and(Opcodes.ACC_STATIC) == 0) continue@children // must static
                    if (!first) appendln()
                    first = false
                    generateComment(child.comments, "$idt", this)
                    appendln("$idt[${string(name)}]: ${child.tsCtorItfName}")
                }
                is TheMethods -> {
                    for ((desc, method) in child.singles) {
                        if (!method.signature.params.all { canPoet(classes, it) }) continue
                        if (method.signature.result != null && !canPoet(classes, method.signature.result!!)) continue
                        if (method.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) continue
                        if (method.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) continue // must not SYNTHETIC

                        if (method.name == "<init>") {
                            if (theClass.accessExternallyChecked.and(Opcodes.ACC_ABSTRACT) != 0) continue
                            if (!first) appendln()
                            first = false
                            generateComment(method.comments, "$idt", this)
                            appendln("${idt}new${ctorDesc(method.signature, theClass, innerDeep)}")
                            continue
                        }
                        if (method.accessChecked.and(Opcodes.ACC_STATIC) == 0) continue // must static
                        if (!first) appendln()
                        first = false
                        generateComment(method.comments, "$idt", this)
                        appendln("$idt[${string(name)}]${methodDesc(method.signature)}")
                    }
                }
                is TheField -> {
                    if (!canPoet(classes, child.signature)) continue@children
                    if (child.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    if (child.accessChecked.and(Opcodes.ACC_STATIC) == 0) continue@children // must static
                    if (child.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) continue@children // must not SYNTHETIC
                    if (!first) appendln()
                    first = false
                    generateComment(child.comments, "$idt", this)
                    appendln("$idt[${string(name)}]: ${tsValItfOrPrimitive(child.signature)}")
                }
            }
        }
        appendln("}")
        appendln()
        // instance body class
        generateComment(theClass.comments, "", this)
        append("type $valBodyItfName")
        typeParams(this, typeParams)
        append(" = ")
        if (theClass.signature != null) {
            val sig = theClass.signature!!
            for (superType in (sig.superInterfaces + sig.superClass)) {
                if (!canPoet(classes, superType)) continue
                append(tsValItfOrPrimitive(superType, true))
                append(" & ")
            }
        } else {
            val superClasses = listOfNotNull(theClass.superClass) + theClass.interfaces
            for (superType in superClasses) {
                val theSuperClass = classes.getClass(superType)
                if (!theSuperClass.gotClass) continue
                if (theSuperClass.accessExternally.and(Opcodes.ACC_PUBLIC) == 0) continue
                append("i_" + superType.replace('/', '_'))
                append(" & ")
            }
        }
        appendln("{")
        first = true
        children@for ((name, child) in children) {
            when (child) {
                is ThePackage -> error("package cannot be child of class")
                is TheClass -> {
                    if (child.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    if (child.accessExternallyChecked.and(Opcodes.ACC_STATIC) != 0) continue@children // must not static
                    if (!first) appendln()
                    first = false
                    generateComment(child.comments, "$idt", this)
                    appendln("$idt[${string(name)}]: ${child.tsCtorItfName}")
                }
                is TheMethods -> {
                    for ((desc, method) in child.singles) {
                        if (!method.signature.params.all { canPoet(classes, it) }) continue
                        if (method.signature.result != null && !canPoet(classes, method.signature.result!!)) continue
                        if (method.accessChecked.and(Opcodes.ACC_STATIC) != 0) continue // must not static
                        if (method.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) continue // must not SYNTHETIC
                        if (method.name == "<init>") continue
                        generateComment(method.comments, "$idt", this)
                        appendln("$idt[${string(name)}]${methodDesc(method.signature)}")
                    }
                }
                is TheField -> {
                    if (!canPoet(classes, child.signature)) continue@children
                    if (child.accessChecked.and(Opcodes.ACC_STATIC) != 0) continue@children // must not static
                    if (child.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) continue@children // must not SYNTHETIC
                    generateComment(child.comments, "$idt", this)
                    appendln("$idt[${string(name)}]: ${tsValItfOrPrimitive(child.signature)}")
                }
            }
        }
        appendln("}")
        appendln()
        // instance class
        generateComment(theClass.comments, "", this)
        append("interface $valItfName")
        typeParams(this, typeParams)
        append(" extends ")
        append(valBodyItfName)
        bodyTypeArgs(this, typeParams)
        appendln(" {}")
        appendln()
        children@for ((name, child) in children) {
            when (child) {
                is ThePackage -> error("package cannot be child of class")
                is TheClass -> {
                    if (child.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    if (child.accessExternallyChecked.and(Opcodes.ACC_STATIC) == 0){
                        // not static
                        generateClass(classes, child, this, typeParams, innerDeep + 1)
                    } else {
                        // static
                        generateClass(classes, child, this)
                    }
                }
                is TheMethods -> {}
                is TheField -> {}
            }
        }
    }

    private fun canPoet(classes: ClassesManager, type: JavaTypeSignature): Boolean = when (type) {
        is BaseType -> true
        is TypeVariable -> true
        is ArrayTypeSignature -> canPoet(classes, type.element)
        is ClassTypeSignature -> {
            val theSuperClass = classes.getClass(type.name)
            theSuperClass.gotClass && theSuperClass.accessExternally.and(Opcodes.ACC_PUBLIC) != 0 && type.args.all { it == null || canPoet(classes, it) }
        }
    }

    private fun tsValItfOrPrimitive(type: JavaTypeSignature, superType: Boolean = false): String = when (type) {
        is BaseType -> when (type.type) {
            BaseType.Kind.Byte -> "t_byte"
            BaseType.Kind.Char -> "t_char"
            BaseType.Kind.Double -> "t_double"
            BaseType.Kind.Float -> "t_float"
            BaseType.Kind.Int -> "t_int"
            BaseType.Kind.Long -> "t_long"
            BaseType.Kind.Short -> "t_short"
            BaseType.Kind.Boolean -> "t_boolean"
        }
        is ClassTypeSignature -> buildString {
            if (!superType && type.name in extraTypeMapping) {
                append(extraTypeMapping[type.name])
            } else {
                append("i_" + type.name.replace('/', '_'))
                if (type.args.isNotEmpty()) {
                    append('<')
                    type.args.joinTo(this) {
                        if (it == null) "any"
                        else tsValItfOrPrimitive(it, true)
                    }
                    append('>')
                }
            }
        }
        is TypeVariable -> type.name
        is ArrayTypeSignature -> "t_array<${tsValItfOrPrimitive(type.element, superType)}>"
    }

    private fun typeParams(builder: StringBuilder, typeParams: List<TypeParam>) = with(builder) {
        if (typeParams.isNotEmpty()) {
            var containList = mutableSetOf<String>()
            var id = 0
            val namePair = typeParams.asReversed()
                    .map {
                        if (it.name in containList) {
                            it to "${it.name}_${id++}"
                        } else {
                            containList.add(it.name)
                            it to it.name
                        }
                    }
                    .asReversed()
            append('<')
            var first = true
            for ((typeParam, name) in namePair) {
                if (!first) append(", ")
                first = false
                append(name)

                if (typeParam.superTypes.isNotEmpty()) {
                    append(" extends ")
                    var first1 = true
                    for (superType in typeParam.superTypes) {
                        if (!first1) append(" & ")
                        first1 = false
                        append(tsValItfOrPrimitive(superType))
                    }
                }
                append(" = any")
            }
            append('>')
        }
    }

    private fun bodyTypeArgs(builder: StringBuilder, typeParams: List<TypeParam>) = with(builder) {
        if (typeParams.isNotEmpty()) {
            val containList = mutableSetOf<String>()
            var id = 0
            val namePair = typeParams.asReversed()
                    .map {
                        if (it.name in containList) {
                            "${it.name}_${id++}"
                        } else {
                            containList.add(it.name)
                            it.name
                        }
                    }
                    .asReversed()
            append('<')
            var first = true
            for (name in namePair) {
                if (!first) append(", ")
                first = false
                append(name)
            }
            append('>')
        }
    }

    private fun methodDesc(signature: MethodSignature) = buildString {
        typeParams(this, signature.typeParams)
        append('(')
        var first = true
        for ((i, typeSignature) in signature.params.withIndex()) {
            if (!first) append(", ")
            first = false
            append("par$i: ${tsValItfOrPrimitive(typeSignature)}")
        }
        append("): ")
        if (signature.result == null)
            append("void")
        else
            append(tsValItfOrPrimitive(signature.result))
    }

    private fun ctorDesc(
            signature: MethodSignature,
            theClass: TheClass,
            skipCountIn: Int
    ) = buildString {
        val typeParams = theClass.signature?.params.orEmpty()
        val result = theClass.typeSignature
        
        var skipCount = 0
        if (true) {
            val outers = mutableListOf<TheClass>()
            var cur = theClass
            while (true) {
                cur = cur.outerClass ?: break
                outers.add(cur)
            }
            if (signature.params.size >= outers.size) { 
                var shouldSkip = true
                for ((i, theClass) in outers.withIndex()) {
                    val param = signature.params[i]
                    if (param !is ClassTypeSignature
                            || param.name != theClass.name) {
                        shouldSkip = false
                        break
                    }
                }
                if (shouldSkip) 
                    skipCount = skipCountIn
            }
        }
        typeParams(this, typeParams)
        append('(')
        var first = true
        for ((i, typeSignature) in signature.params.asSequence().drop(skipCount).withIndex()) {
            if (!first) append(", ")
            first = false
            append("par$i: ${tsValItfOrPrimitive(typeSignature)}")
        }
        append(")")
    }

    private fun generateComment(comments: MutableList<String>, indent: String, builder: StringBuilder) = builder.apply {
        if (comments.isEmpty()) return@apply
        appendln("$indent/**")
        var first = true
        for (comment in comments) {
            if (!first) appendln("$indent * ")
            first = false
            for (line in comment.lineSequence()) {
                appendln("$indent * $line")
            }
        }
        appendln("$indent */")
    }

    private fun string(name: String): String = "'$name'"
}
