package com.bihe0832.android.base.debug.share

import android.view.View
import com.bihe0832.android.base.debug.empty.DebugBottomActivity
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.common.debug.module.DebugEnvFragment
import com.bihe0832.android.common.share.ShareAPPActivity
import com.bihe0832.android.lib.adapter.CardBaseModule

class DebugShareFragment : DebugEnvFragment() {


    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugItemData("分享APK", View.OnClickListener { startActivityWithException(ShareAPPActivity::class.java) }))
            add(DebugItemData("底部分享Activity", View.OnClickListener { startActivityWithException(DebugBottomActivity::class.java) }))
        }
    }
}