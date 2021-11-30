package com.bihe0832.android.lib.http.common;

import static com.bihe0832.android.lib.http.common.core.BaseConnection.HTTP_REQ_VALUE_CONTENT_TYPE_URL_ENCODD;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.bihe0832.android.lib.http.common.core.BaseConnection;
import com.bihe0832.android.lib.http.common.core.FileInfo;
import com.bihe0832.android.lib.http.common.core.HTTPConnection;
import com.bihe0832.android.lib.http.common.core.HTTPSConnection;
import com.bihe0832.android.lib.http.common.core.HttpBasicRequest;
import com.bihe0832.android.lib.http.common.core.HttpFileUpload;
import com.bihe0832.android.lib.log.ZLog;
import com.bihe0832.android.lib.thread.ThreadManager;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 网络请求分发、执行类
 */
public class HTTPServer {

    public static final String LOG_TAG = "bihe0832 REQUEST";
    public static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成

    //是否为测试版本
    private static final boolean DEBUG = true;
    private Handler mCallHandler;
    private static final int MSG_REQUEST = 0;
    private static volatile HTTPServer instance;

    public static HTTPServer getInstance() {
        if (instance == null) {
            synchronized (HTTPServer.class) {
                if (instance == null) {
                    instance = new HTTPServer();
                }
            }
        }
        return instance;
    }

    private HTTPServer() {
        mCallHandler = new Handler(ThreadManager.getInstance().getLooper(ThreadManager.LOOPER_TYPE_HIGHER)) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_REQUEST:
                        if (msg.obj != null && msg.obj instanceof HttpBasicRequest) {
                            executeRequestInExecutor((HttpBasicRequest) msg.obj);
                        } else {
                            ZLog.d(LOG_TAG, msg.toString());
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public String doFileUpload(Context context, final String requestUrl, final Map<String, String> strParams,
            final List<FileInfo> fileParams) {
        return new HttpFileUpload()
                .postRequest(context, HTTPServer.getInstance().getConnection(requestUrl), strParams, fileParams);
    }

    public void doRequestAsync(HttpBasicRequest request) {
        Message msg = mCallHandler.obtainMessage();
        msg.what = MSG_REQUEST;
        msg.obj = request;
        mCallHandler.sendMessage(msg);
    }

    public String doRequestSync(final String url) {
        return doRequestSync(url, (byte[]) null, HTTP_REQ_VALUE_CONTENT_TYPE_URL_ENCODD);
    }

    public String doRequestSync(final String url, final String params) {
        byte[] bytes = null;

        try {
            bytes = params.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doRequestSync(url, bytes, HTTP_REQ_VALUE_CONTENT_TYPE_URL_ENCODD);
    }

    public String doRequestSync(final String url, byte[] bytes, final String contentType) {
        final String finalContentType;
        if (TextUtils.isEmpty(contentType)) {
            finalContentType = HTTP_REQ_VALUE_CONTENT_TYPE_URL_ENCODD;
        } else {
            finalContentType = contentType;
        }

        BaseConnection connection = getConnection(url);
        HttpBasicRequest basicRequest = new HttpBasicRequest() {
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public HttpResponseHandler getResponseHandler() {
                return null;
            }

            @Override
            public String getContentType() {
                return finalContentType;
            }
        };
        if (bytes != null) {
            basicRequest.data = bytes;
        }
        String result = executeRequest(basicRequest, connection);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return result;
        } else {
            ZLog.e(LOG_TAG, "Code:" + connection.getResponseCode() + ";Messag:" + connection.getResponseMessage());
            ZLog.e(LOG_TAG, "responseBody is null");
            return "";
        }
    }

    private void executeRequestInExecutor(final HttpBasicRequest request) {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                executeRequest(request);
            }
        });
    }

    private void executeRequest(HttpBasicRequest request) {

        String url = request.getUrl();
        BaseConnection connection = getConnection(url);
        String result = executeRequest(request, connection);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            request.getResponseHandler().onResponse(connection.getResponseCode(), result);
        } else {
            if (TextUtils.isEmpty(result)) {
                if (DEBUG) {
                    ZLog.e(LOG_TAG, request.getClass().getName());
                }
                ZLog.e(LOG_TAG, "responseBody is null");
                if (TextUtils.isEmpty(connection.getResponseMessage())) {
                    request.getResponseHandler().onResponse(connection.getResponseCode(), "");
                } else {
                    request.getResponseHandler()
                            .onResponse(connection.getResponseCode(), connection.getResponseMessage());
                }
            } else {
                request.getResponseHandler().onResponse(connection.getResponseCode(), result);
            }
        }
    }

    public BaseConnection getConnection(String url) {
        BaseConnection connection = null;
        if (url.startsWith("https:")) {
            connection = new HTTPSConnection(url);
        } else {
            connection = new HTTPConnection(url);
        }
        return connection;
    }

    private String executeRequest(HttpBasicRequest request, BaseConnection connection) {
        String url = request.getUrl();
        if (DEBUG) {
            ZLog.w(LOG_TAG, "=======================================");
            ZLog.w(LOG_TAG, request.getClass().toString());
            ZLog.w(LOG_TAG, url);
            if (request.data != null) {
                ZLog.w(LOG_TAG, new String(request.data));
            }
            ZLog.w(LOG_TAG, "=======================================");
        }
        request.setRequestTime(System.currentTimeMillis() / 1000);
        String result = connection.doRequest(request);
        if (DEBUG) {
            ZLog.w(LOG_TAG, "=======================================");
            ZLog.w(LOG_TAG, request.getClass().toString());
            ZLog.w(LOG_TAG, result);
            ZLog.w(LOG_TAG, String.valueOf(connection.getResponseCode()));
            ZLog.w(LOG_TAG, connection.getResponseMessage());
            ZLog.w(LOG_TAG, "=======================================");
        }
        return result;
    }


}