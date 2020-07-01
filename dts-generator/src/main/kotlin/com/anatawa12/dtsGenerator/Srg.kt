package com.anatawa12.dtsGenerator

class SrgManager {
    val classes = mutableMapOf<String, String>()
    val fields = mutableMapOf<String, Field>()
    val methods = mutableMapOf<String, Method>()

    fun addClass(obfClass: String, srgClass: String) {
        this.classes[obfClass] = srgClass
    }

    fun addField(obfClassField: String, srgClass: String, srgName: String) {
        this.fields[obfClassField] = Field(srgClass, srgName)
    }

    fun addMethod(obfClassMethod: String, srgClass: String, srgName: String, srgDesc: String) {
        this.methods[obfClassMethod] = Method(srgClass, srgName, srgDesc)
    }

    data class Field(val className: String, val name: String)
    data class Method(val className: String, val name: String, val desc: String)
}
