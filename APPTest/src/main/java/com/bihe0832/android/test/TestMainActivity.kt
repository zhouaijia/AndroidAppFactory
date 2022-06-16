package com.bihe0832.android.test

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.view.Gravity
import com.bihe0832.android.app.leakcanary.LeakCanaryManager.addWatch
import com.bihe0832.android.base.debug.permission.DebugPermissionsActivity
import com.bihe0832.android.common.debug.DebugMainFragment
import com.bihe0832.android.framework.router.RouterConstants
import com.bihe0832.android.framework.ui.PermissionResultOfAAF
import com.bihe0832.android.framework.ui.main.CommonActivity
import com.bihe0832.android.lib.adapter.CardInfoHelper
import com.bihe0832.android.lib.debug.icon.DebugLogTips
import com.bihe0832.android.lib.immersion.hideBottomUIMenu
import com.bihe0832.android.lib.lifecycle.ApplicationObserver
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.permission.PermissionManager
import com.bihe0832.android.lib.permission.ui.PermissionsActivity
import com.bihe0832.android.lib.router.annotation.APPMain
import com.bihe0832.android.lib.router.annotation.Module
import com.bihe0832.android.lib.sqlite.impl.CommonDBManager
import com.bihe0832.android.lib.utils.os.BuildUtils

@APPMain
@Module(RouterConstants.MODULE_NAME_DEBUG)
class TestMainActivity : CommonActivity() {
    val LOG_TAG = "DebugHttpActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar("TestMainActivity", false)
        if (BuildUtils.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        }


        CardInfoHelper.getInstance().setAutoAddItem(true)
        PermissionManager.addPermissionDesc(HashMap<String, String>().apply {
            put(Manifest.permission.CAMERA, "相机")
            put(Manifest.permission.RECORD_AUDIO, "录音")
            put(Manifest.permission.SYSTEM_ALERT_WINDOW, "悬浮窗")
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "访问存储卡")
        })

        PermissionManager.addPermissionScene(HashMap<String, String>().apply {
            put(Manifest.permission.CAMERA, "扫描二维码")
            put(Manifest.permission.RECORD_AUDIO, "语音录制")
            put(Manifest.permission.SYSTEM_ALERT_WINDOW, "悬浮窗")
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储权限测试")
        })

//        PermissionManager.checkPermission(this, true, getPermissionResult(), Manifest.permission.CAMERA)
//        UpdateManager.checkUpdateAndShowDialog(this, false)
        DebugLogTips.initModule(this, true, Gravity.RIGHT or Gravity.BOTTOM)
        hideBottomUIMenu()
        CommonDBManager.init(this)

        ApplicationObserver.addDestoryListener(object : ApplicationObserver.APPDestroyListener {

            override fun onAllActivityDestroyed() {
                ZLog.d("onAllActivityDestroyed")
            }
        })
//        ThemeManager.getThemeInfo()?.let {
//            setTheme(if (it.isDark) R.style.DarkTheme else R.style.DefaultTheme)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addWatch(this)
    }

    override fun getStatusBarColor(): Int {
        return ContextCompat.getColor(this, R.color.white)
    }

    override fun getNavigationBarColor(): Int {
        return ContextCompat.getColor(this, R.color.result_point_color)
    }

    override fun getPermissionList(): List<String> {
        return ArrayList<String>().apply {
            if (System.currentTimeMillis() - PermissionManager.getPermissionDenyTime(Manifest.permission.CAMERA) > 1000L * 10) {
                add(Manifest.permission.CAMERA)
            }

            if (System.currentTimeMillis() - PermissionManager.getPermissionDenyTime(Manifest.permission.WRITE_EXTERNAL_STORAGE) > 1000L * 10) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

//            add(Manifest.permission.RECORD_AUDIO)
//            add(Manifest.permission.SYSTEM_ALERT_WINDOW)
        }
    }

    override fun getPermissionResult(): PermissionManager.OnPermissionResult {
        return PermissionResultOfAAF(false)
    }


    override fun getPermissionActivityClass(): Class<out PermissionsActivity> {
        return DebugPermissionsActivity::class.java
    }

    override fun onResume() {
        super.onResume()
        if (findFragment(TestMainFragment::class.java) == null) {
            loadRootFragment(R.id.common_fragment_content, TestMainFragment())
        }
//        mIconManager.showIcon()
//        hideBottomUIMenu()
    }


    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
