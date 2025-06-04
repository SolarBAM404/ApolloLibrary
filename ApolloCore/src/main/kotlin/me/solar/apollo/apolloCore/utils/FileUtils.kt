@file:JvmName("FileUtils")

package me.solar.apollo.apolloCore.utils

import java.io.File

/**
 * Copy file from source to target.
 *
 * @param source source file
 * @param target target file
 */
fun copyFile(source: File, target: File) {
    source.inputStream().use { input ->
        target.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

/**
 * Copy directory from source to target.
 *
 * @param source source directory
 * @param target target directory
 */
fun copyDirectory(source: File, target: File) {
    source.listFiles()?.forEach { file ->
        val targetFile = File(target, file.name)
        if (file.isDirectory) {
            targetFile.mkdirs()
            copyDirectory(file, targetFile)
        } else {
            copyFile(file, targetFile)
        }
    }
}

/**
 * Delete file.
 *
 * @param file file to delete
 */
fun deleteFile(file: File) {
    file.delete()
}

/**
 * Delete directory.
 *
 * @param directory directory to delete
 */
fun deleteDirectory(directory: File) {
    directory.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            deleteDirectory(file)
        } else {
            deleteFile(file)
        }
    }
    directory.delete()
}

/****************************************************
 * File extension functions
 ****************************************************/
fun File.copyTo(target: File) {
    copyFile(this, target)
}

fun File.deleteRecursively() {
    deleteDirectory(this)
}

fun File.copyRecursively(target: File) {
    copyDirectory(this, target)
}