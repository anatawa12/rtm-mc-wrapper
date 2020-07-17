package com.anatawa12.dtsGenerator

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object JarGen {
    fun generate(args: GenProcessArgs, jarFile: File) {
        jarFile.parentFile.mkdirs()
        ZipOutputStream(jarFile.outputStream()).use { zos ->
            generatePackage(args, args.rootPackage, zos)
        }
    }

    private fun elementFilter(element: TheElement): Boolean = when (element) {
        is TheDuplicated -> false
        is ThePackage -> true
        is TheClass -> element.gotClass && element.need && element.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) != 0
        is TheMethods -> true
        is TheField -> true
    }

    private fun generatePackage(args: GenProcessArgs, thePackage: ThePackage, zos: ZipOutputStream) {
        val children = thePackage.children.entries
                .filter { elementFilter(it.value) }
                .sortedWith(compareBy<Map.Entry<String, TheElement>> { it.value.type }
                        .thenBy { it.key })
        
        children@for ((name, child) in children) {
            if (!args.testElement(child)) continue@children
            when (child) {
                is ThePackage -> {
                    generatePackage(args, child, zos)
                }
                is TheClass -> {
                    if (child.accessExternally.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                    val sourceCode = JavaGen.generateClassJavaFile(args, child)
                    val javaFilePath = child.name.replace('.', '/') + ".java"
                    zos.putNextEntry(ZipEntry(javaFilePath))
                    zos.writer().apply { write(sourceCode) }.flush()
                    ClassFileGen.generateClassFiles(args, child, zos)
                }
                is TheMethods -> error("method cannot be child of package")
                is TheField -> error("field cannot be child of package")
            }
        }
    }

    private fun getPossibleSuperClass(args: GenProcessArgs, superClass: ClassTypeSignature): ClassTypeSignature? {
        var currentClass = superClass
        while (!canPoet(args, currentClass)) {
            val currentTheClass = args.classes.getClass(currentClass.name)
            if (!currentTheClass.gotClass) return null // cannot infer
            currentClass = currentTheClass.signature?.superClass
                    ?: ClassTypeSignature(currentTheClass.superClass!!, emptyList())
        }
        return currentClass
    }

    private fun canVisitMethod(args: GenProcessArgs, theClass: TheClass, method: TheSingleMethod): Boolean {
        val name = method.name
        if (!method.signature.params.all { canPoet(args, it) }) return false
        if (method.signature.result != null && !canPoet(args, method.signature.result!!)) return false
        if (name != "<init>" && method.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) return false
        if (method.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) return false
        return true
    }

    private fun canPoetClass(args: GenProcessArgs, type: String): Boolean {
        val theClass = args.classes.getClass(type)
        if (!args.testElement(theClass)) return false
        if (type.startsWith("java/") || type.startsWith("javax/")) return true
        if (args.alwaysFound.any { type.startsWith(it.replace('.', '/')) }) return true
        return theClass.gotClass && theClass.accessExternally.and(Opcodes.ACC_PUBLIC) != 0
    }

    private fun canPoet(args: GenProcessArgs, type: JavaTypeSignature): Boolean = when (type) {
        is BaseType -> true
        is TypeVariable -> true
        is ArrayTypeSignature -> canPoet(args, type.element)
        is ClassTypeSignature -> {
            canPoetClass(args, type.name) && type.args.all { it.type == null || canPoet(args, it.type) }
        }
    }

    object ClassFileGen {
        fun generateClassFiles(args: GenProcessArgs, child: TheClass, zos: ZipOutputStream, outerTypeParams: List<TypeParam> = emptyList()) {
            val classFilePath = child.name.replace('.', '/') + ".class"
            zos.putNextEntry(ZipEntry(classFilePath))
            zos.write(generateClassFile(args, child, outerTypeParams))
            child.children.asSequence()
                    .filterIsInstance<TheClass>()
                    .filter { child.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) != 0 }
                    .map { generateClassFiles(args, it, zos, outerTypeParams + child.signature?.params.orEmpty()) }
        }

        private fun generateClassFile(args: GenProcessArgs, theClass: TheClass, outerTypeParams: List<TypeParam>): ByteArray {
            val writer = ClassWriter(0)

            val children = theClass.children.entries
                    .sortedWith(compareBy<Map.Entry<String, TheElement>> { it.value.type }
                            //.thenComparator { a, b ->  }
                            .thenBy { it.key })
            val allTypeParams = outerTypeParams + theClass.signature?.params.orEmpty()

            val superClass: ClassTypeSignature?
            val superInterfaces: List<ClassTypeSignature>
            val newClassSignature: ClassSignature?

            if (theClass.signature != null) {
                val sig = theClass.signature!!
                superClass = getPossibleSuperClass(args, sig.superClass)
                superInterfaces = sig.superInterfaces.filter { canPoet(args, it) }
            } else {
                superClass = theClass.superClass
                        ?.let { getPossibleSuperClass(args, ClassTypeSignature(it, emptyList())) }
                superInterfaces = theClass.interfaces
                        .asSequence()
                        .filter { canPoetClass(args, it) }
                        .map { ClassTypeSignature(it, listOf()) }
                        .toList()
            }

            if (superClass != null) {
                newClassSignature = ClassSignature(
                        theClass.signature?.params.orEmpty(),
                        superClass,
                        superInterfaces
                )
            } else {
                newClassSignature = null
            }

            writer.visit(Opcodes.V1_8, classAccess(theClass.accessExternallyChecked), 
                    theClass.name.replace('.', '/'),
                    newClassSignature?.toString(),
                    superClass?.name,
                    superInterfaces.map { it.name }.toTypedArray()
            )

            children@for ((name, child) in children) {
                if (!args.testElement(child)) continue@children
                if (name in args.classes.srgDuplicated) continue@children
                when (child) {
                    is ThePackage -> error("package cannot be child of class")
                    is TheClass -> {
                        if (child.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children

                        // inner class
                        writer.visitInnerClass(
                                child.name,
                                theClass.name,
                                child.name.substringAfter('$'),
                                child.accessExternallyChecked.and(Opcodes.ACC_SUPER.inv())
                        )
                    }
                    is TheMethods -> {
                        for ((_, method) in child.singles) {
                            if (!args.testElement(method)) continue
                            if (!canVisitMethod(args, theClass, method)) continue

                            writer.visitMethod(
                                    method.accessChecked,
                                    method.name, 
                                    method.signature.nonGeneric(allTypeParams).toString(),
                                    method.signature.toString(),
                                    null
                            ).apply {
                                visitCode()
                                visitInsn(Opcodes.RETURN)
                                visitMaxs(0, method.signature.params.size + 3)
                                visitEnd()
                            }
                        }
                    }
                    is TheField -> {
                        if (!canPoet(args, child.signature)) continue@children
                        if (child.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                        if (child.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) continue@children

                        writer.visitField(
                                child.accessChecked,
                                child.name,
                                child.signature.nonGeneric(allTypeParams).toString(),
                                child.signature.toString(),
                                null
                        ).apply {
                            visitEnd()
                        }
                    }
                }
            }

            writer.visitEnd()
            return writer.toByteArray()
        }

        /**
         * removes ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC and add ACC_SUPER if required
         */
        private fun classAccess(access: Int): Int {
            var result = access
            result = result.and((Opcodes.ACC_PRIVATE or Opcodes.ACC_PROTECTED or Opcodes.ACC_STATIC).inv())
            if (result.and(Opcodes.ACC_INTERFACE) == 0)
                result = result.or(Opcodes.ACC_SUPER)
            return result
        }
    }

    object JavaGen {
        fun generateClassJavaFile(classes: GenProcessArgs, theClass: TheClass) = buildSrc {
            val pkg = theClass.name.substringBeforeLast('.', missingDelimiterValue = "")
            if (pkg != "")
                append("package ").append(pkg).appendln(";")
            appendln()
            appendln("@SuppressWarnings(\"unchecked\")")
            generateClassJava(classes, theClass, this)
        }

        private fun canVisitMethodSource(args: GenProcessArgs, theClass: TheClass, method: TheSingleMethod): Boolean {
            if (!canVisitMethod(args, theClass, method)) return false
            val name = method.name
            if (theClass.accessExternallyChecked.and(Opcodes.ACC_ENUM) != 0) {
                if (name == "<init>") return false
                if (name == "values" && method.desc.startsWith("()[")) return false
                if (name == "valueOf" && method.desc.startsWith("(Ljava/lang/String;)L")) return false
            }
            return true
        }

        private fun generateClassJava(args: GenProcessArgs, theClass: TheClass, builder: SrcBuilder, outerClassesIn: List<TheClass> = emptyList()): SrcBuilder = builder.apply {
            var outerClasses = outerClassesIn
            val children = theClass.children.entries
                    .sortedWith(compareBy<Map.Entry<String, TheElement>> { it.value.type }
                            //.thenComparator { a, b ->  }
                            .thenBy { it.key })
            val typeParams = theClass.signature?.params.orEmpty()
            val classSimpleName = theClass.name.substringAfterLast('.').substringAfterLast('$')

            var detectStatic = false
            if (theClass.name.contains('$')) kotlin.run {
                // detect is this class static
                val ctor = (theClass.children["<init>"] ?: return@run)
                        .let { it as TheMethods }
                        .singles
                        .values.first()
                val descSig = if (ctor.isDesc) ctor.signature else SigReader.current.methodDesc(ctor.desc, theClass.name + ":<init>")
                if (descSig.params.size < outerClasses.size) {
                    detectStatic = true
                } else {
                    detectStatic = outerClassesIn.zip(descSig.params).any { (theClass, type) ->
                        if (type !is ClassTypeSignature) return@any true
                        type.name != theClass.name.replace('.', '/')
                    }
                }
            }
            if (detectStatic || theClass.accessExternallyChecked.and(Opcodes.ACC_STATIC) != 0) {
                outerClasses = emptyList()
                theClass.detectStatic = true
            }

            var canExtend = true

            // constructor class
            generateComment(theClass.comments, this)
            append("public ")
            if (theClass.accessExternallyChecked.and(Opcodes.ACC_STATIC) != 0 || detectStatic) append("static ")
            if (theClass.accessExternallyChecked.and(Opcodes.ACC_ENUM) == 0
                    && theClass.accessExternallyChecked.and(Opcodes.ACC_FINAL) != 0) append("final ")
            if (theClass.accessExternallyChecked.and(Opcodes.ACC_ABSTRACT) != 0
                    && theClass.accessExternallyChecked.and(Opcodes.ACC_ENUM) == 0) append("abstract ")
            if (theClass.accessExternallyChecked.and(Opcodes.ACC_INTERFACE) != 0) {
                append("interface ")
                canExtend = false
            } else if (theClass.accessExternallyChecked.and(Opcodes.ACC_ENUM) != 0) {
                append("enum ")
                canExtend = false
            } else {
                append("class ")
            }
            append(classSimpleName)
            typeParams(this, typeParams)

            val implements = if (theClass.accessExternallyChecked.and(Opcodes.ACC_INTERFACE) != 0) " extends "
            else " implements "
            if (theClass.signature != null) {
                val sig = theClass.signature!!
                val superClass = getPossibleSuperClass(args, sig.superClass)
                if (canExtend && superClass != null) {
                    append(" extends ").append(javaType(superClass))
                }
                var first = true
                for (superType in sig.superInterfaces) {
                    if (!canPoet(args, superType)) continue
                    if (first) append(implements)
                    else append(", ")
                    first = false
                    append(javaType(superType))

                }
            } else {
                val superClass = theClass.superClass
                        ?.let { getPossibleSuperClass(args, ClassTypeSignature(it, emptyList())) }
                if (canExtend && superClass != null) {
                    append(" extends ").append(javaType(superClass))
                }
                var first = true
                for (superType in theClass.interfaces) {
                    if (!canPoetClass(args, superType)) continue
                    if (first) append(implements)
                    else append(", ")
                    first = false
                    append(javaClassType(superType))
                }
            }
            appendln("{")
            indent()
            if (theClass.accessExternallyChecked.and(Opcodes.ACC_ENUM) != 0) {
                children@ for ((name, child) in children) {
                    if (name in args.classes.srgDuplicated) continue@children
                    when (child) {
                        is ThePackage -> error("package cannot be child of class")
                        is TheClass -> {
                        }
                        is TheMethods -> {
                        }
                        is TheField -> {
                            if (!canPoet(args, child.signature)) continue@children
                            if (child.accessChecked.and(Opcodes.ACC_ENUM) == 0) continue@children
                            generateComment(child.comments, this)
                            appendln("$name,")
                        }
                    }
                }
                appendln(";")
            }

            var first = true
            children@ for ((name, child) in children) {
                if (!args.testElement(child)) continue@children
                if (name in args.classes.srgDuplicated) continue@children
                when (child) {
                    is ThePackage -> error("package cannot be child of class")
                    is TheClass -> {
                        if (child.accessExternallyChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                        if (!first) appendln()
                        first = false
                        generateClassJava(args, child, builder, outerClasses + theClass)
                    }
                    is TheMethods -> {
                        for ((_, method) in child.singles) {
                            if (!args.testElement(method)) continue
                            if (!canVisitMethodSource(args, theClass, method)) continue

                            if (!first) appendln()
                            first = false

                            generateComment(method.comments, this)
                            if (method.accessChecked.and(Opcodes.ACC_PUBLIC) != 0) append("public ")
                            if (method.accessChecked.and(Opcodes.ACC_STATIC) != 0) append("static ")
                            if (method.accessChecked.and(Opcodes.ACC_ABSTRACT) != 0) append("abstract ")
                            val signature = method.signature
                            typeParams(this, signature.typeParams)
                            if (method.name != "<init>") {
                                if (signature.result == null)
                                    append("void")
                                else
                                    append(javaType(signature.result))
                                append(" ")
                                append(name)
                            } else {
                                append(classSimpleName)
                            }
                            append("(")
                            signature.params.withIndex().joinTo(this) { (i, typeSignature) ->
                                "${javaType(typeSignature)} par$i"
                            }
                            append(")")
                            if (method.accessChecked.and(Opcodes.ACC_ABSTRACT) == 0
                                    && theClass.accessExternallyChecked.and(Opcodes.ACC_INTERFACE) == 0) {
                                appendln("{}")
                            } else {
                                appendln(";")
                            }
                        }
                    }
                    is TheField -> {
                        if (!canPoet(args, child.signature)) continue@children
                        if (child.accessChecked.and(Opcodes.ACC_ENUM) != 0) continue@children
                        if (child.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) continue@children
                        if (child.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) continue@children // must not SYNTHETIC

                        if (!first) appendln()
                        first = false
                        generateComment(child.comments, this)

                        append("public ")
                        if (child.accessChecked.and(Opcodes.ACC_STATIC) != 0) append("static ")
                        if (child.accessChecked.and(Opcodes.ACC_FINAL) != 0) append("final ")
                        appendln("${javaType(child.signature)} ${name};")
                    }
                }
            }

            outdent()
            appendln("}")
            appendln()
        }

        private fun javaType(type: JavaTypeSignature, raw: Boolean = false): String = when (type) {
            is BaseType -> when (type.type) {
                BaseType.Kind.Byte -> "byte"
                BaseType.Kind.Char -> "char"
                BaseType.Kind.Double -> "double"
                BaseType.Kind.Float -> "float"
                BaseType.Kind.Int -> "int"
                BaseType.Kind.Long -> "long"
                BaseType.Kind.Short -> "short"
                BaseType.Kind.Boolean -> "boolean"
            }
            is ClassTypeSignature -> buildSrc {
                append(javaClassType(type.name))
                if (!raw && type.args.isNotEmpty()) {
                    append("<")
                    type.args.joinTo(this) {
                        if (it.type == null) "?"
                        else when (it.indicator) {
                            null -> javaType(it.type)
                            Indicator.Plus -> "? extends ${javaType(it.type)}"
                            Indicator.Minus -> "? extends ${javaType(it.type)}"
                        }
                    }
                    append(">")
                }
            }
            is TypeVariable -> type.name
            is ArrayTypeSignature -> "${javaType(type.element, raw = raw)}[]"
        }

        private fun javaClassType(type: String): String {
            return type.replace('/', '.').replace('$', '.')
        }

        private fun typeParams(builder: SrcBuilder, typeParams: List<TypeParam>) = with(builder) {
            if (typeParams.isNotEmpty()) {
                append("<")
                var first = true
                for (typeParam in typeParams) {
                    if (!first) append(", ")
                    first = false
                    append(typeParam.name)

                    if (typeParam.superTypes.isNotEmpty()) {
                        append(" extends ")
                        var first1 = true
                        for (superType in typeParam.superTypes) {
                            if (!first1) append(" & ")
                            first1 = false
                            append(javaType(superType))
                        }
                    }
                }
                append(">")
            }
        }

        private fun generateComment(comments: MutableList<String>, builder: SrcBuilder) = builder.apply {
            if (comments.isEmpty()) return@apply
            appendln("/**")
            var first = true
            for (comment in comments) {
                if (!first) appendln(" * ")
                first = false
                for (line in comment.lineSequence()) {
                    appendln(" * $line")
                }
            }
            appendln(" */")
        }
    }
}
