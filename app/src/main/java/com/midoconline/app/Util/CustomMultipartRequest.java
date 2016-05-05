package com.midoconline.app.Util;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;


//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/*
Ver : 1.0
Last Updated : 27-Nov-2015
This class handles the multipart api calls
 */
public class CustomMultipartRequest extends StringRequest {

//    private static final String TAG = CustomMultipartRequest.class.getSimpleName();
//    private final Response.Listener<String> mListener;
//    private final Map<String, String> mStringPart;
//    private final Map<String, String> mHeaderPart;
//    private final Map<String, File> mFilePart;
//    private MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
//    private List<String> mFilePath;



    /**
     * constructor for update profile
     */
    public CustomMultipartRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> mStringPart, Map<String, String> mHeaderPart, Map<String, File> mFilePart) {
        super(method, url, listener, errorListener);
//        mListener = listener;
//        this.mStringPart = mStringPart;
//        this.mHeaderPart = mHeaderPart;
//        this.mFilePart = mFilePart;
//        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        mEntityBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
//        buildMultipartProfileEntity();
//        buildMultipartTextEntity();
    }
//
//    /**
//     * Return the mime-type of a file. If not mime-type can be found, it returns null.
//     * http://stackoverflow.com/a/8591230
//     *
//     * @param file The file to check for it's mime-type.
//     * @return The mime-type as a string.
//     */
//    private static String getMimeType(File file) {
//        String type = null;
//        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
//        if (extension != null) {
//            MimeTypeMap mime = MimeTypeMap.getSingleton();
//            type = mime.getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }
//
//    /**
//     * build Multipart File Entity List for profile
//     */
//    private void buildMultipartProfileEntity() {
//        Log.d(TAG, "Size: " + mFilePart.size());
//        for (Map.Entry<String, File> entry : mFilePart.entrySet()) {
//            String key = entry.getKey();
//            Log.d(TAG, "key: " + key);
//            File file = entry.getValue();
//            Log.d(TAG, "value: " + file.getName());
//            String mimeType = getMimeType(file);
//            mEntityBuilder.addBinaryBody(key, file, ContentType.create(mimeType), file.getName());
//        }
//    }
//
//    /**
//     * build Multipart text Entity List for all
//     */
//    private void buildMultipartTextEntity() {
//        for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
//            String key = entry.getKey();
//            Log.d(TAG, "key: " + key);
//            String value = entry.getValue();
//            Log.d(TAG, "value: " + value);
//            if (key != null && value != null)
//                mEntityBuilder.addTextBody(key, value);
//        }
//    }
//
//
//
//    @Override
//    public String getBodyContentType() {
//        return mEntityBuilder.build().getContentType().getValue();
//    }
//
//    @Override
//    public byte[] getBody() throws AuthFailureError {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            mEntityBuilder.build().writeTo(bos);
//        } catch (IOException e) {
//            Log.e(TAG, "IOException writing to ByteArrayOutputStream");
//        }
//        return bos.toByteArray();
//    }
//
//
//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        return mHeaderPart;
//    }
//
//    @Override
//    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//        try {
//            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        }
//    }
//
//    @Override
//    protected void deliverResponse(String response) {
//        mListener.onResponse(response);
//    }

}