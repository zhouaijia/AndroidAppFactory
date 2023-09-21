package com.bihe0832.android.common.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import com.bihe0832.android.framework.ZixieContext;
import com.bihe0832.android.framework.file.AAFFileWrapper;
import com.bihe0832.android.lib.aaf.tools.AAFDataCallback;
import com.bihe0832.android.lib.media.image.BitmapUtil;
import com.bihe0832.android.lib.media.image.TextToImageUtils;

/**
 * Summary
 *
 * @author code@bihe0832.com
 *         Created on 2023/9/19.
 *         Description:
 */
public class MediaTools {

    public static final String TAG = "MediaUtils";

    public static Bitmap createImageFromText(Context context, String content, int width, int height) {
        Bitmap result = TextToImageUtils.createImageFromText(
                context, width, height, "#000000", 16,
                null, 0,
                "", "#FFFFFF", 10, 1f, Layout.Alignment.ALIGN_NORMAL, 0,
                "“" + content, "#FFFFFF", 12, 1.25f, 4, Layout.Alignment.ALIGN_NORMAL, 12);
        return result;
    }

    public static void convertAudioWithTextToVideo(Context context, String audioPath, String text,
            AAFDataCallback<String> callback) {
        try {
            int width = 720;
            int height = 1280;
            Bitmap imageData = MediaTools.createImageFromText(context, text, width, height);
            String imagePath = BitmapUtil.saveBitmapWithPath(ZixieContext.INSTANCE.getApplicationContext(),
                    imageData,
                    AAFFileWrapper.INSTANCE.getTempImagePath(".jpg"));

            FFmpegTools.convertAudioWithImageToVideo(width, height, audioPath, imagePath, callback);
        } catch (Exception e) {
            callback.onError(-1, "convertAudioWithTextToVideo exception:" + e);
        }
    }
}