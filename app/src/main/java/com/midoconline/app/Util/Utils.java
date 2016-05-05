package com.midoconline.app.Util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.midoconline.app.R;

import org.w3c.dom.Text;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prashant on 6/10/15.
 */
public class Utils {

    static Dialog progressDialog;
    static String DOCUMENT_APP_PATH = "com.android.documentsui.DocumentsActivity";
    static String TEMP_IMAGE_FILE = "temp_image.jpeg";

    /**
     * call method to show progress dialog
     */
    public static void showProgress(Context context) {
        if (context != null) {
            progressDialog = new Dialog(context);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.layout_progress);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.findViewById(R.id.emptyTextView).setVisibility(View.GONE);
            progressDialog.show();
        }
    }
    /**
     * call method to show progress dialog
     */
    public static void showProgress(Context context,String msg) {
        if (context != null) {
            progressDialog = new Dialog(context);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.layout_progress);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            TextView msgTextView = (TextView)progressDialog.findViewById(R.id.emptyTextView);
            msgTextView.setText(msg);
            progressDialog.show();
        }
    }

    /**
     * call method to close progress dialog
     */
    public static void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * show snackbar
     * @param view
     * @param msg
     */
    public static void ShowSnackBar(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }


    public  static void ShowDialog(String msg,Context context){
        final Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.show_dialog_layout);
        mDialog.setCancelable(true);
        final TextView title = (TextView) mDialog.findViewById(R.id.text_maintitle);
        final TextView Subtitle = (TextView) mDialog.findViewById(R.id.text_subtitle);
        Subtitle.setText(msg);

        mDialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    public static String GetHashKey(Context context){
        // Add code to print out the key hash
        String hashKey = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.facebook.samples.hellofacebook",
                    PackageManager.GET_SIGNATURES);
            MessageDigest md;
            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return hashKey;
    }

    /**
     * call method to hide keyboard programmatically
     **/
    public static void hideKeyboard(View view, Context context) {
        if (view != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static Map<String, String> setHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.KEY_QBHEADER, token);

        return headers;
    }

    /**
     * call method to get device id
     */
    public static String getDeviceId(Context context) {
        // TODO: 6/1/16 check permission READ_PHONE_STATE
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getDeviceId() != null) {
                return tm.getDeviceId();
            } else {
                return Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    /**
     * Image picker
     *
     * @param context Context
     * @return intent
     */
    public static Intent getPickImageChooserIntent(Context context) {

        // Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

//        // collect all camera intents
//        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for (ResolveInfo res : listCam) {
//            Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            if (outputFileUri != null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            }
//            allIntents.add(intent);
//        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals(DOCUMENT_APP_PATH)) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(mainIntent, context.getString(R.string.choose_image_text));
        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * get Capture Image Output Uri
     *
     * @param context Context
     * @return uri
     */
    private static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), TEMP_IMAGE_FILE));
        }
        return outputFileUri;
    }

    /**
     * Pick Image Result Uri
     *
     * @param data    intent
     * @param context Context
     * @return uri
     */
    public static Uri getPickImageResultUri(Intent data, Context context) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri(context) : data.getData();
    }

    /***
     * show SnackBar with message
     *
     * @param message
     */
    public static void showSnackBar(View view, String message) {
        if (StringUtils.isNotEmpty(message))
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * call method to get path from URI
     */
    public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        if (contentUri != null) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = activity.getContentResolver().query(contentUri,
                    filePathColumn, null, null, null);
            // Move to first row
            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                return cursor.getString(columnIndex);
            }

        }
        return null;
    }
}
