package com.bihe0832.android.lib.file.content

import android.text.TextUtils
import com.bihe0832.android.lib.file.FileUtils
import com.bihe0832.android.lib.request.URLUtils
import java.io.File
import java.util.*


/**
 *
 * @author zixie code@bihe0832.com
 * Created on 2020-01-10.
 */
object FileName {

    fun getFileName(filePath: String?): String {
        var noQueryURL = URLUtils.getNoQueryUrl(filePath)
        if (TextUtils.isEmpty(noQueryURL)) {
            noQueryURL = filePath
        }
        noQueryURL?.let {
            val split = noQueryURL.lastIndexOf(File.separator)
            return if (split > -1) {
                noQueryURL.substring(split + 1)
            } else {
                noQueryURL
            }
        }
        return ""
    }

    fun getExtensionName(filename: String?): String {
        getFileName(filename).let { noQueryURL ->
            val dot = noQueryURL.lastIndexOf('.')
            if (dot > -1 && dot < noQueryURL.length - 1) {
                return noQueryURL.substring(dot + 1)
            }
        }
        return ""
    }

    fun getFileNameWithoutEx(source: String?): String {
        getFileName(source)?.let { filename ->
            val dot = filename.lastIndexOf('.')
            val split = filename.lastIndexOf(File.separator)
            if (split < dot) {
                if (dot > -1 && dot < filename.length) {
                    return if (split > -1) {
                        filename.substring(split + 1, dot)
                    } else {
                        filename.substring(0, dot)
                    }
                }
            }
        }
        return ""
    }

    fun isInvalidFilename(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
            return false
        }
        val size = fileName.length
        var c: Char
        for (i in 0 until size) {
            c = fileName[i]
            if (Arrays.binarySearch(FileUtils.ILLEGAL_FILENAME_CHARS, c) >= 0) {
                return true
            }
        }
        return false
    }

}