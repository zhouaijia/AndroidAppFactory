package com.bihe0832.android.lib.download.wrapper;

import android.content.Context;
import com.bihe0832.android.lib.download.DownloadItem;
import com.bihe0832.android.lib.download.core.DownloadManager;
import com.bihe0832.android.lib.download.core.list.DownloadTaskList;
import java.util.List;
import org.jetbrains.annotations.NotNull;


/**
 * ZTSDKApiForDownload
 *
 * @author zixie code@bihe0832.com Created on 2020/9/23.
 */

public class DownloadUtils {


    /**
     * 初始化
     *
     * @param context        Application Context
     * @param maxDownloadNum 同时容许下载的最大数量，如果主要用于大文件下载：建议3个，最大不建议超过5个
     * @param isDebug        是否开启调试模式
     */
    public static final void init(Context context, int maxDownloadNum, Boolean isDebug) {
        DownloadManager.INSTANCE.init(context, maxDownloadNum, isDebug);
    }

    public static final void init(Context context, boolean isDebug) {
        DownloadManager.INSTANCE.init(context, isDebug);
    }

    public static void onDestroy() {
        DownloadManager.INSTANCE.onDestroy();
    }

    /**
     * 添加一个下载任务
     *
     * @param info 添加任务的信息，除 downloadURL ，其余都非必填，下载本地仅支持传入文件夹，不支持传入下载文件路径，如果是要下载到指定文件，请参考 DownloadTools 二次分封装
     */
    public static final void startDownload(Context context, @NotNull DownloadItem info, boolean forceDownload) {
        DownloadManager.INSTANCE.init(context);
        if (forceDownload && info.getDownloadPriority() < DownloadItem.FORCE_DOWNLOAD_PRIORITY) {
            info.setDownloadPriority(DownloadItem.FORCE_DOWNLOAD_PRIORITY);
        }
        DownloadManager.INSTANCE.addTask(info);
    }

    public static final void startDownload(Context context, @NotNull DownloadItem info) {
        info.setForceDownloadNew(true);
        startDownload(context, info, info.isForceDownloadNew());
    }

    /**
     * 通过下载地址获取任务信息
     *
     * @param downloadURL 下载地址
     */
    public static final DownloadItem getTaskByDownloadURL(@NotNull final String downloadURL) {
        return DownloadTaskList.INSTANCE.getTaskByDownloadURL(downloadURL);
    }

    public static long getDownloadIDByURL(String url) {
        return DownloadItem.getDownloadIDByURL(url);
    }

    /**
     * 暂停一个下载任务
     *
     * @param downloadID 添加任务的信息
     */
    public static final void pauseDownload(@NotNull final long downloadID) {
        DownloadManager.INSTANCE.pauseTask(downloadID, true, false);
    }

    /**
     * 恢复一个下载任务
     *
     * @param downloadID    恢复任务的信息
     * @param pauseOnMobile 4G是否暂停下载
     */
    public static final void resumeDownload(long downloadID, boolean pauseOnMobile) {
        DownloadManager.INSTANCE.resumeTask(downloadID, null, true, pauseOnMobile);
    }

    /**
     * 删除一个下载任务
     *
     * @param downloadID 删除任务的信息
     * @param deleteFile 删除任务时是否删除相关文件
     */
    public static final void deleteTask(long downloadID, boolean deleteFile) {
        DownloadManager.INSTANCE.deleteTask(downloadID, true, deleteFile);
    }

    /**
     * 暂停所有下载任务
     */
    public static final void pauseAll(boolean pauseMaxPriorityDownload) {
        DownloadManager.INSTANCE.pauseAllTask(true, pauseMaxPriorityDownload);
    }

    /**
     * 暂停所有下载中的任务
     */
    public static final void pauseDownloading(boolean pauseMaxPriorityDownload) {
        DownloadManager.INSTANCE.pauseDownloadingTask(true, pauseMaxPriorityDownload);
    }

    /**
     * 暂停所有在等待的下载任务
     */
    public static final void pauseWaiting(boolean pauseMaxPriorityDownload) {
        DownloadManager.INSTANCE.pauseWaitingTask(true, pauseMaxPriorityDownload);
    }

    /**
     * 恢复所有下载任务
     *
     * @param pauseOnMobile 4G是否暂停下载
     */

    public static final void resumeAll(boolean pauseOnMobile) {
        DownloadManager.INSTANCE.resumeAllTask(pauseOnMobile);
    }

    /**
     * 恢复所有下载失败的任务
     *
     * @param pauseOnMobile 4G是否暂停下载
     */
    public static final void resumeFailed(boolean pauseOnMobile) {
        DownloadManager.INSTANCE.resumeFailedTask(pauseOnMobile);
    }

    /**
     * 恢复所有被暂停的下载任务
     *
     * @param pauseOnMobile 4G是否暂停下载
     */
    public static final void resumePause(boolean pauseOnMobile) {
        DownloadManager.INSTANCE.resumePauseTask(pauseOnMobile);
    }

    /**
     * 获取任务列表
     *
     * @return 所有下载任务列表
     */
    @NotNull
    public static final List<DownloadItem> getAll() {
        return DownloadManager.INSTANCE.getAllTask();
    }

    /**
     * 获取已经下载完成的任务
     *
     * @return
     */
    @NotNull
    public static final List<DownloadItem> getFinished() {
        return DownloadManager.INSTANCE.getFinishedTask();
    }

    /**
     * 获取正在下载的任务
     *
     * @return
     */
    @NotNull
    public static final List<DownloadItem> getDownloading() {
        return DownloadManager.INSTANCE.getDownloadingTask();
    }

    /**
     * 获取正在等待的下载任务
     *
     * @return
     */
    @NotNull
    public static final List<DownloadItem> getWaiting() {
        return DownloadManager.INSTANCE.getWaitingTask();
    }
}