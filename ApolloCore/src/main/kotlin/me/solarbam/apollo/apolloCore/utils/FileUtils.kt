package me.solarbam.apollo.apolloCore.utils
import java.io.File


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
            FileUtils.deleteFile(file)
        }
    }
    directory.delete()
}

/****************************************************
 * File extension functions
 ****************************************************/
fun File.copyTo(target: File) {
    FileUtils.copyFile(this, target)
}

fun File.deleteRecursively() {
    deleteDirectory(this)
}

fun File.copyRecursively(target: File) {
    FileUtils.copyDirectory(this, target)
}