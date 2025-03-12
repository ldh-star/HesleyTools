import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

private val COMPILE_SDK_VERSION: String by project

android {
    namespace = "com.liangguo.lib.oem.sdk"
    compileSdk = COMPILE_SDK_VERSION.toInt()
}

/** 保留的类路径列表 */
val PREFIXES_CLASS_LIST = listOf(
    "com.android.systemui",
//    "androidx.appcompat.R",
    "com.google.android.material",
)

/** 保留的类路径列表转换成 com/android/xxx 的形式 */
val PREFIXES_CLASS_LIST_MAPPED = PREFIXES_CLASS_LIST.map { it.replace('.', '/') }

/** 递归找到所有的子文件 */
fun findFiles(dir: File, extensions: List<String>): List<File> {
    val result = mutableListOf<File>()
    dir.walkTopDown().forEach { file ->
        if (extensions.any { file.extension == it }) {
            result.add(file)
        }
    }
    return result
}

// 过滤jar或aar文件中的内容
fun filterJarOrAar(inputFile: File, outputFile: File, prefixes: List<String>) {
    val inputStream = ZipInputStream(FileInputStream(inputFile))
    val outputStream = ZipOutputStream(FileOutputStream(outputFile))
    var isFileEmpty = true
    var entry: ZipEntry? = inputStream.nextEntry
    while (entry != null) {
        val entryName = entry.name
        if (prefixes.any { entryName.startsWith(it) }) {
            isFileEmpty = false
            outputStream.putNextEntry(ZipEntry(entryName))
            inputStream.copyTo(outputStream)
            outputStream.closeEntry()
        }
        entry = inputStream.nextEntry
    }

    inputStream.close()
    outputStream.close()

    // 如果输出文件是空的，则删除它
    if (isFileEmpty) {
        outputFile.delete()
        outputLog("删除空文件：$outputFile")
    }
}

// 递归找到所有的jar和aar文件，并记录相对路径
fun findFilesWithRelativePaths(
    dir: File,
    extensions: List<String>,
    baseDir: File
): List<Pair<File, String>> {
    val result = mutableListOf<Pair<File, String>>()
    dir.walkTopDown().forEach { file ->
        if (extensions.any { file.extension == it }) {
            val relativePath = file.relativeTo(baseDir).path.replace(File.separator, "_")
            result.add(file to relativePath)
        }
    }
    return result
}

// 自定义任务来过滤jar和aar文件
tasks.register("filterJarAndAarFiles") {
    doLast {
        val libsDir = file("libs")
        outputLog("libs文件夹：$libsDir")
        val filteredDir = File(libsDir.parentFile, "filteredLibs")
        if (!filteredDir.exists()) {
            filteredDir.mkdirs()
        }
        val filesToFilter = findFilesWithRelativePaths(libsDir, listOf("jar", "aar"), libsDir)
        filesToFilter.forEach { (file, relativePath) ->
            val outputFile = File(filteredDir, relativePath)
            outputLog("处理文件：${file.path}  ->  $outputFile")
            if (outputFile.exists()) {
                outputFile.delete()
            }
            filterJarOrAar(file, outputFile, PREFIXES_CLASS_LIST_MAPPED)
        }
    }
}

// 确保在构建应用前，先执行自定义的过滤任务
// 手动构建 ./gradlew filterJarAndAarFiles
tasks.named("preBuild") {
    dependsOn(tasks.named("filterJarAndAarFiles"))
}

dependencies {
    // lib_oem_sdk模块的依赖，外部模块依赖该模块请使用 compileOnly
    api(fileTree("filteredLibs"))
//    compileOnly(fileTree("filteredLibs"))
}

// 本gradle脚本日志输出
fun outputLog(str: String) {
    println("【lib_oem_sdk】$str")
}