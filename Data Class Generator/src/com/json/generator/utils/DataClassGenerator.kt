package com.json.generator.utils

import com.google.gson.*
import java.lang.StringBuilder
import javax.swing.JTextArea

class DataClassGenerator {

    private val innerClasses = mutableMapOf<String, String>()
    private lateinit var type: DataClassType

    fun generateOutput(inputText: String, outputField: JTextArea, type: DataClassType) {
        this.type = type
        if (GeneratorUtils.getInstance().isJsonObject(inputText)) {
            outputField.text = generateObject(Gson().fromJson(inputText, JsonObject::class.java), getClassName())
        } else {
            outputField.text =
                "There is no point in creating data class for an array as json root. Make instead a data class for the objects inside the array, and serialize as List<YourObject>/"
        }
    }


    private fun generateObject(jsonObject: JsonObject, className: String): String {
        val classContent: StringBuilder = StringBuilder("$className (\n")
        for (entry in jsonObject.entrySet()) {
            //iterate entries
            when {
                entry.value.isJsonPrimitive -> {
                    //handle primitive
                    val value = entry.value.asJsonPrimitive
                    classContent.append(getPrimitiveVarDeclaration(entry.key, value))
                }
                entry.value.isJsonObject -> {
                    //is object
                    classContent.append(getObjectVarDeclaration(entry.key))
                    if (!innerClasses.containsKey(entry.key)) {
                        //generate inner class for this object recursively
                        innerClasses[entry.key] =
                            generateObject(entry.value.asJsonObject, getClassName(makeCamelCaseName(entry.key)))
                    }
                }
                entry.value.isJsonArray -> {
                    //is array
                    //check if is primitive or object array
                    val jsonArray: JsonArray = entry.value.asJsonArray
                    if (jsonArray.size() > 0) {
                        val first = jsonArray.get(0)
                        when {
                            first.isJsonPrimitive -> {
                                //handle primitive
                                val value = first.asJsonPrimitive
                                classContent.append(getPrimitiveVarDeclaration(entry.key, value, true))
                            }
                            first.isJsonObject -> {
                                //is object
                                classContent.append(getObjectVarDeclaration(entry.key, true))
                                if (!innerClasses.containsKey(entry.key)) {
                                    //generate inner class for this object recursively
                                    innerClasses[entry.key] =
                                        generateObject(first.asJsonObject, getClassName(makeCamelCaseName(entry.key, true)))
                                }
                            }
                        }
                    } else {
                        classContent.append(getObjectVarDeclaration(entry.key, true))
                    }
                }
                entry.value.isJsonNull -> {
                    // is null
                    classContent.append("\t${getAnnotation(entry.key)}(\"${entry.key}\")\n\tvar ${entry.key.toLowerCase()}: Any? = null,\n")
                }
                else -> {
                    //unknown case
                }
            }
        }


        val temp = classContent.removeSuffix(",\n")
        classContent.clear()
        classContent.append(temp)
        classContent.append("\n)\n")


        if (innerClasses.isNotEmpty()) {
            classContent.append("\n\n")

            for (entry in innerClasses.entries) {
                classContent.append(entry.value)
                classContent.append("\n\n")
            }
        }
        innerClasses.clear()
        return String(classContent)
    }

    private fun getPrimitiveVarDeclaration(key: String, jsonElement: JsonPrimitive, isArray: Boolean = false): String {
        return when {
            jsonElement.isBoolean -> {
                "\t${getAnnotation(key)}\n\tvar $key: ${if (isArray) "List<Boolean>?" else "Boolean?"} = null,\n"
            }
            jsonElement.isNumber -> {
                "\t${getAnnotation(key)}\n\tvar $key: ${if (isArray) "List<Long>?" else "Long?"} = null,\n"
            }
            jsonElement.isString -> {
                "\t${getAnnotation(key)}\n\tvar $key: ${if (isArray) "List<String>?" else "String?"} = null,\n"
            }
            else -> "\t${getAnnotation(key)}\n\tvar $key: ${if (isArray) "List<Any>?" else "Any?"} = null,\n"
        }
    }

    private fun getObjectVarDeclaration(key: String, isArray: Boolean = false): String {
        val objName = makeCamelCaseName(key, isArray)

        return "\t${getAnnotation(key)}\n\tvar $key: " +
                "${if (isArray) "List<$objName>" else objName}? = null,\n"
    }

    private fun makeCamelCaseName(key: String, isArray: Boolean = false): String {
        return if (isArray) {
            if (key.endsWith("es")) {
                //plurals ending with -es may have different form at singular (e.g. knife -> knives) -- in this case leave it plural for manual fix after
                key.toLowerCase().replaceFirst(key.first(), key.first().toUpperCase())
            } else {
                key.toLowerCase().replaceFirst(key.first(), key.first().toUpperCase()).removeSuffix("s")
            }
        } else {
            key.toLowerCase().replaceFirst(key.first(), key.first().toUpperCase())
        }
    }

    private fun getClassName(className: String? = null): String {
        return when (type) {
            DataClassType.MOSHI -> {
                "@JsonClass(generateAdapter = true)\ndata class ${className ?: "MoshiDataClass"}"
            }
            DataClassType.GSON -> {
                "data class ${className ?: "GsonDataClass"}"
            }
        }
    }

    private fun getAnnotation(key: String): String {
        return when (type) {
            DataClassType.MOSHI -> {
                "@Json(name=\"$key\")"
            }
            DataClassType.GSON -> {
                "@SerializedName(\"$key\")"
            }
        }
    }
}