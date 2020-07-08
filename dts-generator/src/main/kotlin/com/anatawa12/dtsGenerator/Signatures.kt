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


sealed class JavaTypeSignature
class BaseType private constructor(val type: BaseType.Kind) : JavaTypeSignature() {
    enum class Kind {
        Byte, Char, Double, Float, Int, Long, Short, Boolean,
        ;
        val type = BaseType(this)
    }
}

sealed class ReferenceTypeSignature : JavaTypeSignature()
class TypeArgument(val type: ReferenceTypeSignature?, val indicator: Indicator?)
enum class Indicator {
    Plus, 
    Minus,
}
class ClassTypeSignature(val name: String, val args: List<TypeArgument>) : ReferenceTypeSignature()
class TypeVariable(val name: String) : ReferenceTypeSignature()
class ArrayTypeSignature(val element: JavaTypeSignature) : ReferenceTypeSignature()

class ClassSignature(val params: List<TypeParam>, val superClass: ClassTypeSignature, val superInterfaces: List<ClassTypeSignature>)
class MethodSignature(val typeParams: List<TypeParam>, val params: MutableList<JavaTypeSignature>, val result: JavaTypeSignature?)

class TypeParam(val name: String, val superTypes: List<ReferenceTypeSignature>)
