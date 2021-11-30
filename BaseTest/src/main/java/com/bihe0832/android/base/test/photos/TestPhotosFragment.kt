package com.bihe0832.android.base.test.photos


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import com.bihe0832.android.common.photos.*
import com.bihe0832.android.common.test.base.BaseTestListFragment
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.common.test.item.TestTipsData
import com.bihe0832.android.framework.constant.ZixieActivityRequestCode
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.file.ZixieFileProvider
import com.bihe0832.android.lib.log.ZLog
import java.io.File


class TestPhotosFragment : BaseTestListFragment() {
    val LOG_TAG = "Test"

    var needCrop = false

    var takePhosUri: Uri? = null
    var cropUri: Uri? = null

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(
                TestTipsData("当前图片地址： ")
            )
            add(TestItemData("拍照", View.OnClickListener {
                takePhosUri = activity!!.getAutoChangedPhotoUri()
                activity?.takePhoto(takePhosUri)
            }))
            add(TestItemData("选择图片", View.OnClickListener {
                needCrop = false
                activity?.choosePhoto()
            }))
            add(TestItemData("选择并裁剪", View.OnClickListener {
                needCrop = true
                activity?.choosePhoto()
            }))
        }
    }

    private fun cropPhotos(sourceUri: Uri?) {
        cropUri = activity!!.getAutoChangedPhotoUri()
        activity!!.cropPhoto(
            sourceUri,
            cropUri
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK == resultCode) {
            ZLog.d("PhotoChooser in PhotoChooser onResult requestCode：$requestCode；resultCode：$resultCode")
            when (requestCode) {
                ZixieActivityRequestCode.TAKE_PHOTO ->
                    cropPhotos(takePhosUri)
                ZixieActivityRequestCode.CHOOSE_PHOTO -> if (data != null && data.data != null) {
                    ZLog.d("PhotoChooser in PhotoChooser onResult requestCode：$requestCode；resultCode：$resultCode $data")
                    if (needCrop) {
                        cropPhotos(data.getData())
                    } else {
                        showResult(
                            "图片地址:" +
                                    ZixieFileProvider.uriToFile(
                                        activity!!,
                                        data.getData()
                                    ).absolutePath
                        )
                    }
                } else {
                    ZLog.d("PhotoChooser in PhotoChooser onResult requestCode：$requestCode；resultCode：$resultCode")
                }
                ZixieActivityRequestCode.CROP_PHOTO -> {
                    ZLog.d("PhotoChooser in PhotoChooser onResult requestCode：" + requestCode + "；resultCode：" + data.toString())
                    ZixieFileProvider.uriToFile(
                        activity!!,
                        cropUri
                    ).absolutePath.let {
                        showResult(
                            "图片地址:$it"
                        )
                        ZLog.d("PhotoChooser in cropUri：$it")
                    }

                }
                else -> {
                    ZLog.d("PhotoChooser in PhotoChooser onResult requestCode：" + requestCode + "；resultCode：" + data.toString())
                }
            }
        } else {
            ZLog.d("PhotoChooser in PhotoChooser onResult requestCode：$requestCode；resultCode：$resultCode")
        }
    }


}