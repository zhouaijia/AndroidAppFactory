package com.bihe0832.android.lib.media;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.bihe0832.android.lib.file.FileUtils;
import com.bihe0832.android.lib.file.action.FileAction;
import com.bihe0832.android.lib.file.mimetype.FileMimeTypes;
import com.bihe0832.android.lib.file.provider.ZixieFileProvider;
import com.bihe0832.android.lib.log.ZLog;
import com.bihe0832.android.lib.utils.os.OSUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import org.jetbrains.annotations.NotNull;

/**
 * @author zixie code@bihe0832.com
 *         Created on 2019-12-17.
 *         Description: Description
 */
public class Media {

    public static Uri getRealPathUri(String mimeType) {
        boolean status = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (FileMimeTypes.INSTANCE.isImageFileByMimeType(mimeType)) {
            if (status) {
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else {
                return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            }
        } else if (FileMimeTypes.INSTANCE.isVideoFileByMimeType(mimeType)) {
            if (status) {
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else {
                return MediaStore.Video.Media.INTERNAL_CONTENT_URI;
            }
        } else if (FileMimeTypes.INSTANCE.isAudioFileByMimeType(mimeType)) {
            if (status) {
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            } else {
                return MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            }
        } else {
            return MediaStore.Files.getContentUri("external");
        }
    }

    public static final String getZixieMediaPath(@NotNull Context context, String folderType) {
        String filePath = Environment.getExternalStoragePublicDirectory(folderType).getPath();
        if (TextUtils.isEmpty(filePath)) {
            //android 11以上，将文件创建在公有目录
            filePath = ZixieFileProvider.getZixieFilePath(context) + folderType;
        }
        return FileUtils.INSTANCE.getFolderPathWithSeparator(filePath);
    }

    private static void updateContentValues(ContentValues contentValues, String mimeType, String subDir, File file) {
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);//文件类型
        if (!TextUtils.isEmpty(subDir)) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + File.separator + subDir);

        }
        if (FileMimeTypes.INSTANCE.isImageFileByMimeType(mimeType) && OSUtils.isAndroidQVersion()) {
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
        }
        String fileName = file.getName();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
        contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        contentValues.put(MediaStore.MediaColumns.TITLE, fileName);
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.SIZE, file.length());
    }

    private static void writeToPhotos(ContentResolver contentResolver, ContentValues contentValues,
            Uri targetUri, String sourceFile) {
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            OutputStream outputStream = contentResolver.openOutputStream(targetUri);
            FileAction.INSTANCE.copyFile(inputStream, outputStream);
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (OSUtils.isAndroidQVersion()) {
            contentValues.clear();
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
            int result = contentResolver.update(targetUri, contentValues, null, null);
            if (result == 0) {
                String selection = MediaStore.Images.Media._ID + " = ?";
                String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(targetUri))};
                contentResolver.update(getRealPathUri(FileMimeTypes.INSTANCE.getMimeType(sourceFile)), contentValues,
                        selection, selectionArgs);

            }
        }
    }

    public static Uri createUriAboveAndroidQ(Context context, String fileMimeType, String subFilePath, String name) {
        ContentValues contentValues = new ContentValues();//内容
        ContentResolver resolver = context.getApplicationContext().getContentResolver();//内容解析器
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);//文件名
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, fileMimeType);//文件类型
        if (OSUtils.isAndroidQVersion()) {
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        if (!TextUtils.isEmpty(subFilePath)) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + File.separator + subFilePath);
        }
        return resolver.insert(getRealPathUri(fileMimeType), contentValues);
    }

    /**
     * AndroidQ以下创建用于保存拍照的照片的uri，(沙盒目录/pictures/subFilePath)
     * 拍照传入的intent中
     * Android7以下：file类型的uri
     * Android7以上：content类型的uri
     *
     * @param context activity
     * @param name 文件名
     * @param subFilePath 子文件夹
     * @return content uri
     */
    public static Uri createUriBelowAndroidQ(Context context, String subFilePath, String name) {
        String picFolder = FileUtils.INSTANCE.getFolderPathWithSeparator(
                ZixieFileProvider.getZixieCacheFolder(context) + subFilePath);
        if (FileUtils.INSTANCE.checkAndCreateFolder(picFolder)) {
            File picture = new File(picFolder, name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //适配Android7以上的path转uri，该方法得到的uri为content类型的
                return ZixieFileProvider.getZixieFileProvider(context, picture);
            } else {
                //Android7以下，该方法得到的uri为file类型的
                return Uri.fromFile(picture);
            }
        } else {
            return null;
        }
    }

    public static void addToPhotos(Context context, String filePath, String subDir, boolean isSameName) {
        if (!FileUtils.INSTANCE.checkFileExist(filePath)) {
            return;
        }
        File image = new File(filePath);
        String mimeType = FileMimeTypes.INSTANCE.getMimeType(filePath);
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        updateContentValues(contentValues, mimeType, subDir, image);
        Uri targetUri = contentResolver.insert(getRealPathUri(mimeType), contentValues);
        if (targetUri != null) {
            writeToPhotos(contentResolver, contentValues, targetUri, filePath);
        } else {
            try {
                String path = getZixieMediaPath(context, subDir);
                if (isSameName) {
                    path = path + FileUtils.INSTANCE.getFileName(filePath);
                } else {
                    path = path + System.currentTimeMillis() + "." + FileUtils.INSTANCE.getExtensionName(filePath);
                }
                File newFile = new File(path);
                FileUtils.INSTANCE.copyFile(image, newFile, false);
                ContentValues newValues = new ContentValues();
                updateContentValues(newValues, mimeType, subDir, image);
                contentResolver.insert(getRealPathUri(mimeType), newValues);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (FileMimeTypes.INSTANCE.isImageFileByMimeType(mimeType)) {
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "", "");
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
        try {
            MediaScannerConnection.scanFile(context, new String[]{getZixieMediaPath(context, subDir)}, null, null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, targetUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addVideoToPhotos(Context context, String imagePath) {
        addToPhotos(context, imagePath, Environment.DIRECTORY_MOVIES, false);
    }

    public static void addPicToPhotos(Context context, String imagePath) {
        addToPhotos(context, imagePath, Environment.DIRECTORY_PICTURES, false);
    }

    public static File uriToFile(Context context, Uri uri) {
        if (uri == null) {
            ZLog.e("uri is null");
            return null;
        }
        File file = null;
        if (uri.getScheme() != null) {
            ZLog.e("uri.getScheme()：" + uri.getScheme());
            if (uri.getScheme().equals(ContentResolver.SCHEME_FILE) && uri.getPath() != null) {
                //此uri为文件，并且path不为空(保存在沙盒内的文件可以随意访问，外部文件path则为空)
                file = new File(uri.getPath());
            } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                //此uri为content类型，将该文件复制到沙盒内
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        if (FileUtils.INSTANCE.checkFileExist(filePath)) {
                            file = new File(filePath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (file == null) {
                            file = ZixieFileProvider.uriToFile(context, uri);
                        }
                    }
                }
            }
        }
        return file;
    }
}
