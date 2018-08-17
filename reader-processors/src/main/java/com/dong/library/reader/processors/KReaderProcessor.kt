@file:Suppress("ProtectedInFinal", "unused")

package com.dong.library.reader.processors

import com.dong.library.reader.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.dong.library.reader.annotations.*
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.dong.library.reader.annotations.model.KReaderMetadata
import com.dong.library.reader.annotations.model.KReaderType
import java.io.File
import javax.annotation.processing.*

import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import java.util.*
import javax.lang.model.element.Element

abstract class KBaseProcessor : AbstractProcessor() {

    protected lateinit var mElements: Elements
    protected lateinit var mTypes: Types
    protected lateinit var mLogger: Logger
    protected lateinit var mFormatModuleName: String
    protected lateinit var mOriginalModuleName: String

    override fun init(environment: ProcessingEnvironment) {
        super.init(environment)
        mElements = environment.elementUtils
        mTypes = environment.typeUtils
        mLogger = Logger(environment.messager)

        val options = environment.options
        if (options.isNotEmpty()) {
            mOriginalModuleName = options[MODULE_NAME] ?: ""
            mFormatModuleName = mOriginalModuleName.replace("[^0-9a-zA-Z_]+".toRegex(), "")
        }
        mLogger.info("[$mOriginalModuleName] ${this::class.java.simpleName} init")
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (set.isEmpty()) {
            return false
        }

        if (mOriginalModuleName.isBlank()) {
            mLogger.warning("this module name is null!!! skip this module!!")
            return false
        }

        try {
            mLogger.info("[$mOriginalModuleName] ${this::class.java.simpleName} process!!!")
            collectInfo(roundEnv)
        } catch (e: Exception) {
            mLogger.error(e)
        }

        return true
    }

    fun FileSpec.writeFile() {

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val outputFile = File(kaptKotlinGeneratedDir).apply {
            mkdirs()
        }
        //mLogger.info("[$mOriginalModuleName] writeFile ${outputFile.toPath()}")
        writeTo(outputFile.toPath())
    }

    abstract fun collectInfo(roundEnv: RoundEnvironment)
}

@AutoService(Processor::class)
@SupportedOptions(MODULE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.dong.library.reader.annotations.Reader", "com.dong.library.reader.annotations.ReaderApi")
class KReaderProcessor : KBaseProcessor() {

    private val readerMap = HashMap<String, KReaderMetadata>()

    override fun collectInfo(roundEnv: RoundEnvironment) {

        readerMap.clear()

        val readers = roundEnv.getElementsAnnotatedWith(Reader::class.java)

        mLogger.info("processor readers is empty?${readers.isEmpty()}")

        if (readers.isEmpty()) {
            return
        }

        mLogger.info("Found ${readers.size} readers in [$mOriginalModuleName]")

        val loader = ParameterizedTypeName.get(ClassName("kotlin.collections", "MutableMap"), String::class.asClassName(), KReaderMetadata::class.asClassName())
        //Generate implement IRouteLoader interface class
        val methodBuilder = FunSpec.builder(com.dong.library.reader.METHOD_LOAD)
                .addParameter("map", loader)
                .addModifiers(KModifier.OVERRIDE)

        collectMap(roundEnv, Reader::class.java, KReaderType.READER.className) { element, annotation ->

            annotation.keys.forEach { key: String ->
                if (!readerMap.containsKey(key)) {
                    readerMap[key] = KReaderMetadata(name = element.asType().toString())

                    mLogger.info("Found KReader --- ${element.asType()}")

                    /**
                     * val readerType
                     * val key: String = "",
                     * val name: String = "undefine",
                     * val apiCls: Class<*> = Any::class.java,
                     * val clazz: Class<*> = Any::class.java
                     */
                    methodBuilder.addStatement(
                            "map[%S] = %T(%T.%L, %S, %S, %T::class.java)",
                            key,
                            KReaderMetadata::class,
                            KReaderType::class,
                            KReaderType.READER,
                            key,
                            element.asType().toString(),
                            element.asType())
                }
            }
        }

        mLogger.info("$ROUTE_LOADER_NAME$SEPARATOR$mFormatModuleName")

        val classLoader = TypeSpec.classBuilder("$ROUTE_LOADER_NAME$SEPARATOR$mFormatModuleName")
                .addSuperinterface(ClassName.bestGuess(com.dong.library.reader.READER_LOADER))
                .addKdoc(com.dong.library.reader.WARNINGS)
                .addFunction(methodBuilder.build())
                .build()

        val kotlinFile = FileSpec.builder(PACKAGE, "$ROUTE_LOADER_NAME$SEPARATOR$mFormatModuleName")
                .addType(classLoader)
                .build()

        kotlinFile.writeFile()
    }

    private fun <T : Annotation> collectMap(roundEnv: RoundEnvironment, clazz: Class<T>, type: String?, each: (element: Element, annotation: T) -> Unit) {
        val elements = roundEnv.getElementsAnnotatedWith(clazz)

        val mirrors = if (type != null) {
            mElements.getTypeElement(type).asType()
        } else {
            null
        }

        elements.filter {
            if (mirrors != null) {
                mTypes.isSubtype(it.asType(), mirrors)
            } else {
                true
            }
        }.forEach {
            each.invoke(it, it.getAnnotation(clazz))
        }
    }
}

//
//@AutoService(Processor::class)
//@SupportedOptions(MODULE_NAME)
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedAnnotationTypes("com.dong.library.reader.annotations.ReaderApi")
//class KApiProcessor : KBaseProcessor() {
//
//    override fun collectInfo(roundEnv: RoundEnvironment) {
//        val elements = roundEnv.getElementsAnnotatedWith(ReaderApi::class.java)
//        if (elements.isEmpty()) return
//
//        val tmApi = mElements.getTypeElement(KReaderType.READER_SERVICE.className).asType()
//    }
//}


class Logger(private val mMsg: Messager) {

    private fun print(kind: Diagnostic.Kind, info: CharSequence?) {
        if (!info.isNullOrBlank()) {
            mMsg.printMessage(kind, "$PREFIX_OF_LOGGER $info")
        }
    }

    fun info(info: CharSequence?) {
        print(Diagnostic.Kind.NOTE, ">>> $info <<<")
    }

    fun warning(waring: CharSequence?) {
        print(Diagnostic.Kind.WARNING, "### $waring ###")
    }

    fun error(error: CharSequence?) {
        print(Diagnostic.Kind.ERROR, "There is an error [$error]")
    }

    fun error(error: Throwable) {
        print(Diagnostic.Kind.ERROR, "There is an error [${error.message}]\n ${formatStackTrace(error.stackTrace)}")
    }

    private fun formatStackTrace(stackTrace: Array<StackTraceElement>): String {
        val sb = StringBuilder()
        for (element in stackTrace) {
            sb.append("    at ").append(element.toString()).append("\n")
        }
        return sb.toString()
    }

    companion object {
        private const val PREFIX_OF_LOGGER = "KReader::Compiler::"
    }
}
