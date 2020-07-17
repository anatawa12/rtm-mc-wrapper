package com.anatawa12.dtsGenerator

import org.objectweb.asm.Opcodes

object GenUtil {
    fun canPoetClass(args: GenProcessArgs, type: String): Boolean {
        val theClass = args.classes.getClass(type)
        if (!args.testElement(theClass)) return false
        if (type.startsWith("java/") || type.startsWith("javax/")) return true
        if (args.alwaysFound.any { type.startsWith(it.replace('.', '/')) }) return true
        return theClass.gotClass && theClass.accessExternally.and(Opcodes.ACC_PUBLIC) != 0
    }

    fun canPoet(args: GenProcessArgs, type: JavaTypeSignature): Boolean = when (type) {
        is BaseType -> true
        is TypeVariable -> true
        is ArrayTypeSignature -> canPoet(args, type.element)
        is ClassTypeSignature -> {
            canPoetClass(args, type.name) && type.args.all { it.type == null || canPoet(args, it.type) }
        }
    }

    fun canVisitMethod(args: GenProcessArgs, theClass: TheClass, method: TheSingleMethod): Boolean {
        if (!args.testElement(method)) return false
        if (!method.signature.params.all { canPoet(args, it) }) return false
        if (method.signature.result != null && !canPoet(args, method.signature.result!!)) return false
        if (method.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) return false
        if (method.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) return false
        return true
    }

    fun canVisitField(args: GenProcessArgs, theClass: TheClass, field: TheField): Boolean {
        if (!args.testElement(field)) return false
        if (!canPoet(args, field.signature)) return false
        if (field.accessChecked.and(Opcodes.ACC_PUBLIC) == 0) return false
        if (field.accessChecked.and(Opcodes.ACC_SYNTHETIC) != 0) return false
        return true
    }

}
