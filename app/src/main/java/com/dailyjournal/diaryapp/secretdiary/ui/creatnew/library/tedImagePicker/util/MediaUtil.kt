package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.type.CameraMedia
import io.reactivex.Completable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal class MediaUtil {
    companion object {

        internal fun getMediaIntentUri(
            context: Context,
            cameraMedia: CameraMedia,
            savedDirectoryName: String?
        ): Pair<Intent, Uri> {
            val cameraIntent =  Intent(cameraMedia.intentAction)
//            Log.d("getMediaIntentUri", cameraIntent.toString())
//            Log.d("getMediaIntentUri", context.packageManager.toString())
//            Log.d("getMediaIntentUri", cameraIntent.resolveActivity(context.packageManager).toString())
//            if (cameraIntent.resolveActivity(context.packageManager) == null) {
//                throw PackageManager.NameNotFoundException("Can not start Camera")
//            }

            return getMediaUri(context, cameraIntent, cameraMedia, savedDirectoryName)
        }

        private fun getMediaUri(
            context: Context,
            cameraIntent: Intent,
            cameraMedia: CameraMedia,
            savedDirectoryName: String?
        ): Pair<Intent, Uri> {
            val timeStamp =
                SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val fileName = "${cameraMedia}_$timeStamp"

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val directoryName =
                    if (savedDirectoryName != null) {
                        "${cameraMedia.savedDirectoryName}/$savedDirectoryName"
                    } else {
                        cameraMedia.savedDirectoryName
                    }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + cameraMedia.fileSuffix)
                    put(MediaStore.MediaColumns.MIME_TYPE, cameraMedia.mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, directoryName)
                }
                val mediaUri =
                    context.contentResolver.insert(cameraMedia.externalContentUri, contentValues)!!
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri)
                cameraIntent to mediaUri
            } else {

                val directoryName = savedDirectoryName ?: cameraMedia.savedDirectoryName
                val directory = Environment.getExternalStoragePublicDirectory(directoryName)
                if (!directory.exists()) {
                    directory.mkdir()
                }

                val file = File.createTempFile(fileName, cameraMedia.fileSuffix, directory)

                val mediaUri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )

                val resolvedIntentActivities = context.packageManager
                    .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolvedIntentInfo in resolvedIntentActivities) {
                    val packageName = resolvedIntentInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName,
                        mediaUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri)
                cameraIntent to Uri.fromFile(file)
            }
        }

        fun scanMedia(context: Context, uri: Uri): Completable {

            return Completable.create { emitter ->
                MediaScannerConnection.scanFile(context, arrayOf(uri.path), null)
                { _, _ -> emitter.onComplete() }
            }
        }
    }
}
