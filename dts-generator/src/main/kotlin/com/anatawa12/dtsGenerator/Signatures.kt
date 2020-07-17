package com.anatawa12.dtsGenerator

import java.lang.IllegalArgumentException

class SigReader private constructor(){
    var i = 0

    private fun javaTypeSignature(signature: String) = when (signature[i++]) {
        'B' -> BaseType.Kind.Byte.type
        'C' -> BaseType.Kind.Char.type
        'D' -> BaseType.Kind.Double.type
        'F' -> BaseType.Kind.Float.type
        'I' -> BaseType.Kind.Int.type
        'J' -> BaseType.Kind.Long.type
        'S' -> BaseType.Kind.Short.type
        'Z' -> BaseType.Kind.Boolean.type
        else -> { 
            i--
            referenceTypeSignature(signature)
        }
    }

    private fun referenceTypeSignature(signature: String): ReferenceTypeSignature = when (signature[i]) {
        'L' -> classTypeSignature(signature)
        'T' -> {
            i++ // skip 'T'
            val name = identifier(signature)
            check(signature[i++] == ';') { fail(signature, "expected ';'", -1) }
            TypeVariable(name)
        }
        '[' -> {
            i++ // skip '['
            ArrayTypeSignature(javaTypeSignature(signature))
        }
        else -> error(fail(signature, "expected reference type signature"))
    }

    private fun classTypeSignature(signature: String): ClassTypeSignature {
        check(signature[i++] == 'L') { fail(signature, "expected class type signature", -1) }
        val typeArgs = mutableListOf<TypeArgument>()
        val typeName = buildString {
            while (true) {
                append(identifier(signature))
                if (signature[i] != '/') break
                i++ // skip '/'
                append('/')
            }
            // SimpleClassTypeSignatures
            while (true) {
                if (signature[i] == '<') {
                    // arguments
                    i++ // skip '<'
                    while (signature[i] != '>') {
                        typeArgs += when (signature[i++]) {
                            '*' -> TypeArgument(null, null)
                            '+' -> TypeArgument(referenceTypeSignature(signature), Indicator.Plus)
                            '-' -> TypeArgument(referenceTypeSignature(signature), Indicator.Minus)
                            else -> {
                                i--
                                TypeArgument(referenceTypeSignature(signature), null)
                            }
                        }
                    }
                    i++ // skip '>'
                }
                if (signature[i] != '.') break
                i++ // skip '.'
                append('$')
                append(identifier(signature))
            }
        }
        check(signature[i++] == ';') { fail(signature, "expected ';'", -1) }
        return ClassTypeSignature(typeName, typeArgs)
    }

    private fun typeParameters(signature: String): List<TypeParam> {
        check(signature[i++] == '<') { fail(signature, "expected type parameters", -1) }
        val parameters = mutableListOf<TypeParam>()
        while (signature[i] != '>') {
            val name = identifier(signature, ":")
            val superTypes = mutableListOf<ReferenceTypeSignature>()
            check(signature[i++] == ':') { fail(signature, "expected class bound", -1) }
            if (signature[i] != ':') {
                superTypes += referenceTypeSignature(signature)
            }
            while (signature[i] == ':') {
                i++ // skip ':'
                superTypes += referenceTypeSignature(signature)
            }
            parameters += TypeParam(name, superTypes)
        }
        check(signature[i++] == '>') { fail(signature, "expected '>'", -1) }
        return parameters
    }

    fun classSignature(signature: String, classInfo: String): ClassSignature = try {
        i = 0
        val params = if (signature[i] == '<') typeParameters(signature) else listOf()
        val superClass = classTypeSignature(signature)
        val superInterfaces = mutableListOf<ClassTypeSignature>()
        while (i in signature.indices) {
            superInterfaces += classTypeSignature(signature)
        }

        ClassSignature(params, superClass, superInterfaces)
    } catch (e: Exception) {
        throw IllegalArgumentException("reading signature of $classInfo: '$signature'", e)
    }

    fun methodDesc(signature: String, method: String): MethodSignature = try {
        i = 0
        val typeParams = if (signature[i] == '<') typeParameters(signature) else emptyList()
        check(signature[i++] == '(') { fail(signature, "expected '('", -1) }
        val params = mutableListOf<JavaTypeSignature>()
        while (signature[i] != ')') {
            params += javaTypeSignature(signature)
        }
        i++ // skip ')'
        
        val result = if (signature[i] == 'V') {
            i++
            null
        } else {
            javaTypeSignature(signature)
        }
        // ignore throws
        MethodSignature(typeParams, params, result)
    } catch (e: Exception) {
        throw IllegalArgumentException("reading signature of $method: '$signature'", e)
    }

    fun fieldDesc(signature: String, field: String) = try {
        i = 0
        javaTypeSignature(signature)
    } catch (e: Exception) {
        throw IllegalArgumentException("reading signature of $field: '$signature'", e)
    }

    private fun identifier(signature: String, notA: String = "") = buildString {
        val excludes = ".;[/<>$notA"
        check (signature[i] !in excludes) { fail(signature, "expected identifier") }
        while (signature[i] !in excludes)
            append(signature[i++])
    }

    private fun fail(signature: String, msg: String, offset: Int = 0): String = "read fail at $i: '$signature': $msg, got char: ${signature[i + offset]}"

    companion object {
        private val local = ThreadLocal.withInitial { SigReader() }
        val current get() = local.get()!!
    }
}


sealed class JavaTypeSignature {
    open fun nonGeneric(params: List<TypeParam>) = this
}
class BaseType private constructor(val type: BaseType.Kind) : JavaTypeSignature() {
    override fun toString() = when (type) {
        Kind.Byte -> "B"
        Kind.Char -> "C"
        Kind.Double -> "D"
        Kind.Float -> "F"
        Kind.Int -> "I"
        Kind.Long -> "J"
        Kind.Short -> "S"
        Kind.Boolean -> "Z"
    }

    enum class Kind {
        Byte, Char, Double, Float, Int, Long, Short, Boolean,
        ;
        val type = BaseType(this)
    }
}

sealed class ReferenceTypeSignature : JavaTypeSignature()
class TypeArgument(val type: ReferenceTypeSignature?, val indicator: Indicator?) {
    override fun toString() = when (indicator) {
        Indicator.Plus -> "+$type"
        Indicator.Minus -> "-$type"
        null ->
            if (type == null) "*"
            else "$type"
    }
}

enum class Indicator {
    Plus, 
    Minus,
}
class ClassTypeSignature(val name: String, val args: List<TypeArgument>) : ReferenceTypeSignature() {
    override fun nonGeneric(params: List<TypeParam>): JavaTypeSignature = ClassTypeSignature(name, emptyList())

    override fun toString(): String = buildString {
        append('L').append(name)
        if (args.isNotEmpty())
            args.joinTo(this, "", "<", ">")
        append(';')
    }
}
class TypeVariable(val name: String) : ReferenceTypeSignature() {
    override fun nonGeneric(params: List<TypeParam>): JavaTypeSignature
            = params.last { it.name == name }.superTypes.first().nonGeneric(params)

    override fun toString(): String = "T$name;"
}
class ArrayTypeSignature(val element: JavaTypeSignature) : ReferenceTypeSignature() {
    override fun nonGeneric(params: List<TypeParam>): JavaTypeSignature = ArrayTypeSignature(element.nonGeneric(params))
    override fun toString(): String = "[$element"
}

class ClassSignature(val params: List<TypeParam>, val superClass: ClassTypeSignature, val superInterfaces: List<ClassTypeSignature>) {
    override fun toString(): String = buildString {
        if (params.isNotEmpty()) 
            params.joinTo(this, "", "<", ">")
        append(superClass)
        superInterfaces.joinTo(this, "")
    }
}

data class MethodSignature(val typeParams: List<TypeParam>, val params: List<JavaTypeSignature>, val result: JavaTypeSignature?) {
    override fun toString(): String = buildString {
        if (typeParams.isNotEmpty())
            typeParams.joinTo(this, "", "<", ">")
        params.joinTo(this, "", "(", ")")
        if (result == null) append('V')
        else append(result)
    }

    fun nonGeneric(typeParams: List<TypeParam>): MethodSignature {
        val allTypeParams = when {
            this.typeParams.isEmpty() -> typeParams
            typeParams.isEmpty() -> this.typeParams
            else -> this.typeParams + typeParams
        }
        return MethodSignature(emptyList(), 
                params.map { it.nonGeneric(allTypeParams) }, 
                result?.nonGeneric(allTypeParams))
    }
}

class TypeParam(val name: String, val superTypes: List<ReferenceTypeSignature>) {
    override fun toString(): String {
        return name + superTypes.joinToString("") { ":$it" }
    }
}
