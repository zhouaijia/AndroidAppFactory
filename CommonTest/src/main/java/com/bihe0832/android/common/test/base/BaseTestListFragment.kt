package com.bihe0832.android.common.test.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bihe0832.android.common.list.CommonListLiveData
import com.bihe0832.android.common.list.easyrefresh.CommonListFragment
import com.bihe0832.android.common.test.R
import com.bihe0832.android.common.test.item.TestItemData
import com.bihe0832.android.framework.router.openWebPage
import com.bihe0832.android.lib.adapter.CardBaseModule
import com.bihe0832.android.lib.adapter.CardInfoHelper
import com.bihe0832.android.lib.debug.DebugTools
import com.bihe0832.android.lib.debug.InputDialogCompletedCallback
import com.bihe0832.android.lib.http.common.HTTPServer
import com.bihe0832.android.lib.log.ZLog
import com.bihe0832.android.lib.text.TextFactoryUtils
import com.bihe0832.android.lib.thread.ThreadManager

open class BaseTestListFragment : CommonListFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CardInfoHelper.getInstance().setAutoAddItem(true)
    }

    open fun getDataList(): ArrayList<CardBaseModule> {
        return ArrayList<CardBaseModule>().apply {
            add(TestItemData("test"))
        }
    }

    private val mTestDataLiveData by lazy {
        object : TestListLiveData() {
            override fun fetchData() {
                postValue(getDataList())
            }
        }
    }

    override fun getResID(): Int {
        return R.layout.com_bihe0832_fragment_test_tab
    }

    override fun getDataLiveData(): CommonListLiveData {
        return mTestDataLiveData
    }

    protected fun sendInfo(title: String, content: String) {
        DebugTools.sendInfo(context, title, content, false)
    }


    protected fun showInfo(title: String, content: String) {
        DebugTools.showInfo(context, title, content, "发送到第三方应用")
    }

    fun showInputDialog(titleName: String, msg: String, defaultValue: String, listener: InputDialogCompletedCallback) {
        DebugTools.showInputDialog(context, titleName, msg, defaultValue, listener)
    }


    protected open fun startActivity(cls: Class<*>) {
        val intent = Intent(context, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    protected open fun showResult(s: String?) {
        s?.let {
            ThreadManager.getInstance().runOnUIThread {
                ZLog.d(HTTPServer.LOG_TAG, "showResult:$s")
                view?.findViewById<TextView>(R.id.test_tips)?.apply {
                    this.text = TextFactoryUtils.getSpannedTextByHtml("<B>提示信息</b>:<BR> $s")
                    visibility = View.VISIBLE
                }

            }
        }
    }


    open fun openZixieWeb(url: String) {
        openWebPage(url)
    }
}