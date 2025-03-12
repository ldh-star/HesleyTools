package com.liangguo.lib.common.utils

import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaMethod

/**
 * @author hesleyliang
 * 时间: 2024/9/19 08:44
 * 邮箱: liang.dh@outlook.com
 */
/** 获取一个对象中持有的所有属性，并返回一个属性名字到类型的映射表 */
fun Any.getProperties(): Map<String, KType> {
    // 获取对象的KClass
    val kClass = this::class
    val result = mutableMapOf<String, KType>()
    // 遍历所有属性
    for (property in kClass.memberProperties) {
        // 获取属性名称
        val name = property.name
        // 获取属性类型
        val type = (property as KProperty1<*, *>).returnType
        result[name] = type
    }
    return result
}

/** 把属性对照表转换成可以打印的字符串表 */
fun Map<String, KType>.toPropString(): String {
    val sb = StringBuilder()
    forEach { (key, value) ->
        sb.append('[')
            .append(key)
            .append(']')
            .append(' ')
            .append(value)
            .append('\n')
    }
    return sb.toString()
}

/** 生成类定义字符串 */
fun KClass<out Any>.generateClassDefinition(): String {
    val kClass = this
    val className = kClass.simpleName ?: "Unknown"
    val superClassNames = kClass.superclasses.joinToString(", ") { it.simpleName ?: "Unknown" }
    val constructor = kClass.constructors.firstOrNull()

    val sb = StringBuilder()

    // 生成类名和父类名
    sb.append("class $className")
    if (superClassNames.isNotEmpty()) {
        sb.append(": $superClassNames")
    }
    sb.append(" {\n")

    // 生成构造方法
    constructor?.let {
        val parameters = it.parameters.joinToString(", ") { param ->
            val paramName = param.name ?: "Unknown"
            val paramType = param.type
            "val $paramName: $paramType"
        }
        sb.append("    constructor($parameters)\n")
    }

    // 生成属性
    for (property in kClass.memberProperties) {
        val name = property.name
        val type = property.returnType

        // 获取可见性
        val visibility = property.visibility ?: getJavaFieldVisibility(property.javaField)

        // 检查是否为静态字段
        val isStatic = property.javaField?.modifiers?.let { Modifier.isStatic(it) } == true

        // 生成属性修饰符和类型
        val modifiers = mutableListOf<String>()
        if (isStatic) modifiers.add("static")
        modifiers.add(visibility.name.lowercase())
        sb.append("    ${modifiers.joinToString(" ")} val $name: $type\n")
    }

    // 生成方法
    for (function in kClass.memberFunctions) {
        val name = function.name
        val returnType = function.returnType

        // 获取可见性
        val visibility = function.visibility ?: getJavaMethodVisibility(function.javaMethod)

        // 检查是否为静态方法
        val isStatic = function.javaMethod?.modifiers?.let { Modifier.isStatic(it) } == true

        // 生成方法修饰符和类型
        val modifiers = mutableListOf<String>()
        if (isStatic) modifiers.add("static")
        modifiers.add(visibility.name.lowercase())

        // 生成参数
        val parameters = function.parameters.joinToString(", ") { param ->
            val paramName = param.name ?: "Unknown"
            val paramType = param.type
            "$paramName: $paramType"
        }
        sb.append("    ${modifiers.joinToString(" ")} fun $name($parameters): $returnType\n")
    }

    sb.append("}\n")

    return sb.toString()
}

/** 获取Java字段的可见性并映射到KVisibility */
private fun getJavaFieldVisibility(javaField: java.lang.reflect.Field?): KVisibility {
    return when {
        javaField == null -> KVisibility.PUBLIC
        Modifier.isPrivate(javaField.modifiers) -> KVisibility.PRIVATE
        Modifier.isProtected(javaField.modifiers) -> KVisibility.PROTECTED
        Modifier.isPublic(javaField.modifiers) -> KVisibility.PUBLIC
        else -> KVisibility.PUBLIC
    }
}

/** 获取Java方法的可见性并映射到KVisibility */
private fun getJavaMethodVisibility(javaMethod: java.lang.reflect.Method?): KVisibility {
    return when {
        javaMethod == null -> KVisibility.PUBLIC
        Modifier.isPrivate(javaMethod.modifiers) -> KVisibility.PRIVATE
        Modifier.isProtected(javaMethod.modifiers) -> KVisibility.PROTECTED
        Modifier.isPublic(javaMethod.modifiers) -> KVisibility.PUBLIC
        else -> KVisibility.PUBLIC
    }
}