package com.anatawa12.dtsGenerator


class ClassesManager {
    val rootPackage = ThePackage(".", this)
    var srgDuplicated = mutableSetOf<String>()

    fun getPackage(packageElement: List<String>): ThePackage {
        var pkg = rootPackage
        var name = ""
        for (pkgName in packageElement) {
            name += "." + pkgName
            val thisPkg = pkg.children.computeIfAbsent(pkgName) { ThePackage(name, this) }
            if (thisPkg !is ThePackage)
                throw IllegalArgumentException("name duplicated with package and class: $name")
            pkg = thisPkg
        }
        return pkg
    }

    fun getClass(className: String): TheClass {
        val elements = className.split('.', '/')
        val className = elements.last()
        val pkg = this.getPackage(elements.dropLast(1))
        val classNameElements = className.split('$')

        var printName = elements.dropLast(1).joinToString(".")

        val classNameElement1 = classNameElements.first()
        printName += ".${classNameElement1}"
        var cls = pkg.children[classNameElement1]
                .let { it ?: TheClass(printName, this).apply { pkg.children[classNameElement1] = this } }
                .let { it as? TheClass }
                ?: throw IllegalArgumentException("name duplicated with package and class: $printName")

        for (classNameElement in classNameElements.asSequence().drop(1)) {
            printName += "$${classNameElement}"
            cls = cls.children[classNameElement]
                    .let { it ?: TheClass(printName, this).apply { outerClass = cls }.apply { cls.children[classNameElement] = this } }
                    .let { it as? TheClass }
                    ?: throw IllegalArgumentException("name duplicated with package and class: $printName")
        }
        return cls
    }
}

enum class TheElementType {
    Field,
    Method,
    Class,
    Package,
    Duplicated,
}

sealed class TheElement(val type: TheElementType)

object TheDuplicated : TheElement(TheElementType.Duplicated)

data class ThePackage(val name: String, val manager: ClassesManager) : TheElement(TheElementType.Package) {
    val children = mutableMapOf<String, TheElement>()
    val tsItfName by lazy { "p${name.replace('.', '_')}" }
}
data class TheClass(val name: String, val manager: ClassesManager) : TheElement(TheElementType.Class) {
    var need = false
    var gotClass = false
    var detectStatic = false
    var outerClass: TheClass? = null
    /** internal name */
    var superClass: String? = null
    /** internal name */
    lateinit var interfaces: Array<out String>
    val comments = mutableListOf<String>()
    var signature: ClassSignature? = null
    var accessExternally: Int = -1
    val accessExternallyChecked: Int get() = accessExternally.takeUnless { it == -1 } ?: error("access for $name not set")

    val typeSignature = SigReader.current.fieldDesc("L${name.replace('.', '/')};", "the class name: '${name}'")

    val tsCtorItfName by lazy { "c_${name.replace('.', '_')}" }
    val tsValItfName by lazy { "i_${name.replace('.', '_')}" }
    val tsValBodyItfName by lazy { "b_${name.replace('.', '_')}" }

    val children = mutableMapOf<String, TheElement>()

    fun getMethod(name: String, desc: String): TheSingleMethod? {
        val methods = children.computeIfAbsent(name, ::TheMethods)
        if (methods == TheDuplicated) return null // return dummy
        if (methods !is TheMethods) {
            println("duplicate elements in class ${this.name}: $name: $methods")
            children[name] = TheDuplicated
            return null // return dummy
        }
        val method = methods.singles.computeIfAbsent(desc.substringBefore(')')) { TheSingleMethod(name, desc) }
        if (method.desc != desc) {
            if (name.startsWith("func_")) manager.srgDuplicated.add(name)
            println("duplicate method in class ${this.name} with same name: $name: $desc: $method")
            children[name] = TheDuplicated
            return null // return dummy
        }
        return method
    }

    fun getField(name: String, desc: String): TheField? {
        val field = children.computeIfAbsent(name) { TheField(name, desc) }
        if (field == TheDuplicated) return null // return dummy
        if (field !is TheField) {
            println("duplicate elements in class ${this.name}: $name: $field")
            children[name] = TheDuplicated
            return null // return dummy
        }
        if (field.desc != desc) {
            if (name.startsWith("field_")) manager.srgDuplicated.add(name)
            println("duplicate field in class ${this.name} with same nameing: $name: $desc: $field")
            children[name] = TheDuplicated
            return null // return dummy
        }
        return field
    }
}

data class TheMethods(val name: String) : TheElement(TheElementType.Method) {
    /** argument desc -> single */
    val singles = mutableMapOf<String, TheSingleMethod>()
}
data class TheSingleMethod(val name: String, val desc: String) {
    val comments = mutableListOf<String>()
    var isDesc = true
    var signature = SigReader.current.methodDesc(desc, "$name$desc")
        set(value) {
            field = value
            isDesc = false
        }
    var access: Int = -1
    val accessChecked: Int get() = access.takeUnless { it == -1 } ?: error("access for $name not set")
}
data class TheField (val name: String, val desc: String) : TheElement(TheElementType.Field) {
    val comments = mutableListOf<String>()
    var signature = SigReader.current.fieldDesc(desc, "$name$desc")
    var access: Int = -1
    val accessChecked: Int get() = access.takeUnless { it == -1 } ?: error("access for $name not set")
}
