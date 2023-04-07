package com.bihe0832.android.base.debug.immersion

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bihe0832.android.base.debug.R
import com.bihe0832.android.framework.ui.main.CommonActivity
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.ui.common.ColorTools


class DebugImmersionActivity : CommonActivity() {

    override fun getStatusBarColor(): Int {
        return Color.parseColor("#00ffffff")
//        return Color.TRANSPARENT
    }

    override fun getNavigationBarColor(): Int {
        return Color.RED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar("TestMain" + TextFactoryUtils.getSpecialText("Activity", Color.GREEN), true)
//        updateIcon(false, "https://cdn.bihe0832.com/images/cv_512.jpg", -1)

        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color: ${Color.WHITE}")
        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color 2: ${ContextCompat.getColor(this, R.color.colorPrimary)}")
        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color: ${ContextCompat.getColor(this, R.color.white)}")
        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color: ${Color.parseColor("#FFFFFF")}")
        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color: ${Color.parseColor("#00FFFFFF")}")
        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color: ${ColorTools.getColorWithAlpha(0f, Color.WHITE)}")
        ZLog.d("DebugImmersionActivity", "DebugImmersionActivity color: ${ColorTools.getColorWithAlpha(1f, Color.WHITE)}")
    }
}
