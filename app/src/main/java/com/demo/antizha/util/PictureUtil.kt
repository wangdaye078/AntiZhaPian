package com.demo.antizha.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.demo.antizha.R
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.style.PictureParameterStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle


object PictureUtil {
    private var picParamStyle: PictureParameterStyle? = null

    fun checkPermission(activity: Activity, ischeckOpen: Boolean): Boolean {
        val permission = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val opened = (permission == PackageManager.PERMISSION_GRANTED)
        if (ischeckOpen == opened)
            return true
        if (opened) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", activity.packageName, null)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        } else
            activity.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        return false
    }

    private fun initPictureParameterStyle(activity: Activity) {
        if (picParamStyle == null) {
            picParamStyle = PictureParameterStyle()
        }
        picParamStyle!!.isChangeStatusBarFontColor = false
        picParamStyle!!.isOpenCompletedNumStyle = false
        picParamStyle!!.isOpenCheckNumStyle = false
        picParamStyle!!.pictureStatusBarColor = Color.parseColor("#393a3e")
        picParamStyle!!.pictureTitleBarBackgroundColor = Color.parseColor("#393a3e")
        picParamStyle!!.pictureTitleUpResId = R.drawable.picture_icon_arrow_up
        picParamStyle!!.pictureTitleDownResId = R.drawable.picture_icon_arrow_down
        picParamStyle!!.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval
        picParamStyle!!.pictureLeftBackIcon = R.drawable.picture_icon_back
        picParamStyle!!.pictureTitleTextColor =
            ContextCompat.getColor(activity, R.color.picture_color_white)
        picParamStyle!!.pictureRightDefaultTextColor =
            ContextCompat.getColor(activity, R.color.picture_color_white)
        picParamStyle!!.pictureCheckedStyle = R.drawable.picture_checkbox_selector
        picParamStyle!!.pictureBottomBgColor =
            ContextCompat.getColor(activity, R.color.picture_color_grey)
        picParamStyle!!.pictureCheckNumBgStyle = R.drawable.picture_num_oval
        picParamStyle!!.picturePreviewTextColor =
            ContextCompat.getColor(activity, R.color.picture_color_fa632d)
        picParamStyle!!.pictureUnPreviewTextColor =
            ContextCompat.getColor(activity, R.color.picture_color_white)
        picParamStyle!!.pictureCompleteTextColor =
            ContextCompat.getColor(activity, R.color.picture_color_fa632d)
        picParamStyle!!.pictureUnCompleteTextColor =
            ContextCompat.getColor(activity, R.color.picture_color_white)
        picParamStyle!!.picturePreviewBottomBgColor =
            ContextCompat.getColor(activity, R.color.picture_color_grey)
        picParamStyle!!.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete
        picParamStyle!!.pictureOriginalControlStyle =
            R.drawable.picture_original_wechat_checkbox
        picParamStyle!!.pictureOriginalFontColor =
            ContextCompat.getColor(activity, R.color.white)
        picParamStyle!!.pictureExternalPreviewGonePreviewDelete = true
        picParamStyle!!.pictureNavBarColor = Color.parseColor("#393a3e")
    }

    fun getImageSelectModel(activity: Activity,
                            isWeChatStyle: Boolean,
                            queryCount: Float,
                            maxCount: Int): PictureSelectionModel {
        val model = PictureSelector.create(activity).openGallery(PictureMimeType.ofImage())
        model.imageEngine(GlideEngine.createGlideEngine())
        model.theme(R.style.picture_default_style)
        model.isWeChatStyle(isWeChatStyle)
        model.setLanguage(-1)
        model.setPictureWindowAnimationStyle(PictureWindowAnimationStyle())
        model.isWithVideoImage(true)
        model.maxSelectNum(maxCount)
        model.minSelectNum(1)
        model.maxVideoSelectNum(maxCount)
        model.imageSpanCount(4)
        model.isReturnEmpty(false)
        model.setRequestedOrientation(1)
        model.isCamera(true)
        model.isZoomAnim(true)
        model.isGif(true)
        model.cutOutQuality(90)
        model.freeStyleCropEnabled(true)
        model.showCropFrame(true)
        model.showCropGrid(true)
        model.queryMaxFileSize(queryCount)
        model.minimumCompressSize(100)
        return model
    }
}