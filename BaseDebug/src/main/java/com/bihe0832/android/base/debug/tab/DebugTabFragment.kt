package com.bihe0832.android.base.debug.tab

import android.view.View
import com.bihe0832.android.common.debug.base.BaseDebugListFragment
import com.bihe0832.android.common.debug.item.DebugItemData
import com.bihe0832.android.lib.adapter.CardBaseModule

class DebugTabFragment : BaseDebugListFragment() {

    override fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(DebugItemData("CommonTabActivity", View.OnClickListener { startActivityWithException(CommonTabActivity::class.java) }))
            add(DebugItemData("SegmentTabActivity", View.OnClickListener { startActivityWithException(SegmentTabActivity::class.java) }))
            add(DebugItemData("SlidingTabActivity", View.OnClickListener { startActivityWithException(SlidingTabActivity::class.java) }))

        }
    }
}