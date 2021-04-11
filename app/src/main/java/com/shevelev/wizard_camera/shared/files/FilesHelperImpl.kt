package com.shevelev.wizard_camera.shared.files

import android.content.Context
import com.shevelev.wizard_camera.R
import com.shevelev.wizard_camera.utils.id.IdUtil
import java.io.File
import javax.inject.Inject
import kotlin.math.absoluteValue

class FilesHelperImpl
@Inject
constructor(
    private val appContext: Context
) : FilesHelper {
    override fun createFileForShot(): File {
        val name = IdUtil.generateLongId().absoluteValue
        val dir = getShotsDirectory()

        return File(dir, "$name.jpg")
    }

    override fun getShotFileByName(fileName: String): File = File(getShotsDirectory(), fileName)

    override fun removeShotFileByName(fileName: String) = getShotFileByName(fileName).apply { delete() }

    private fun getShotsDirectory(): File {
        val dir = File(appContext.externalMediaDirs[0], appContext.getString(R.string.appName))
        if(!dir.exists()) {
            dir.mkdir()
        }

        return dir
    }
}