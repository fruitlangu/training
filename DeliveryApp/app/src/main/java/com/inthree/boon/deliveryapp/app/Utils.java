/*
 * @category Delivery app
 * @copyright Copyright (C) 2017 boonbox. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inthree.boon.deliveryapp.BuildConfig;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.server.ServerUrls;
import com.inthree.boon.deliveryapp.views.ReadmoreTextView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Helper class methods shared by various class.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public final class Utils {

    /**
     * The constant TAG.
     */
    private static final String TAG = Utils.makeLogTag(Utils.class);

    /**
     * Default Constructor
     */
    public Utils() {

    }

    /**
     * To check the internet connectivity and show error message if network not available.
     *
     * @param context Contains the context
     * @return boolean True if internet is available false
     */
    public static boolean checkNetworkAndShowDialog(Context context) {
        if (!checkNetConnection(context)) {
            Logger.showShortMessage(context, context.getString(R.string.check_internet));
            return false;
        }
        return true;
    }

    /**
     * Checking internet state.
     *
     * @param context Activity context
     * @return boolean True if internet is enabled else false
     */
    public static boolean checkNetConnection(Context context) {
        ConnectivityManager miManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo miInfo = miManager.getActiveNetworkInfo();
        boolean networkStatus = false;

        //Checking the network connection is in wifi or mobile data
        if (miInfo != null && miInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            networkStatus = true;
        } else if (miInfo != null && miInfo.getType() == ConnectivityManager.TYPE_MOBILE &&
                miInfo.isConnectedOrConnecting())
            networkStatus = true;
        return networkStatus;
    }

    /**
     * ReThrowable Method
     *
     * @param tag   Tag
     * @param cause Cause
     */
    public static void e(final String tag, Throwable cause) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, tag, cause);
        }
    }

    /**
     * Used to make Log for the class
     *
     * @param cls Class
     * @return String log
     */
    public static String makeLogTag(Class<?> cls) {
        try {
            return makeLogTag(Class.forName(cls.getSimpleName()));
        } catch (ClassNotFoundException e) {
            Utils.e(TAG, e);
        }
        return null;
    }

    /**
     * This is used to determine if the given motion event is inside the given view.
     *
     * @param ev           View
     * @param currentFocus Motion event.
     * @return boolean True, if the given motion event is inside the given view
     */
    public static boolean isTouchInsideView(final MotionEvent ev, final View currentFocus) {
        final int[] loc = new int[2];
        currentFocus.getLocationOnScreen(loc);
        return ev.getRawX() <= loc[0] || ev.getRawY() <= loc[1] || ev.getRawX() >= (loc[0] +
                currentFocus.getWidth()) || ev.getRawY() >= (loc[1] + currentFocus.getHeight());
    }

    /**
     * Method used to changed the time format as ago.
     *
     * @param time    Server time.
     * @param context Activity Context
     * @return String Converted format
     */
    public static String changeTimeFormat(String time, Context context) {
        String timeDetails = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.simple_date_format));
            Date past = format.parse(time);
            Date now = new Date();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if (seconds < 60) {
                timeDetails = seconds + context.getResources().getString(R.string.seconds_ago);
            } else if (minutes < 60) {
                timeDetails = minutes + context.getString(R.string.minutes_ago);
            } else if (hours < 24) {
                timeDetails = hours + context.getString(R.string.hours_ago);
            } else {
                timeDetails = days + context.getString(R.string.days_ago);
            }
        } catch (Exception j) {
            Logger.logError(j);
        }
        return timeDetails;
    }

    /**
     * Method used to changed the date format.
     *
     * @param date Server date.
     * @return String Converted format
     */
    public static String changeDateFormat(String date) {
        String dateFormat = "dd MMM";
        return dateFormat(date, dateFormat);
    }

    /**
     * Method used to changed the date format.
     *
     * @param date Server date.
     * @return Converted format
     */
    public static String liveTimer(String date) {
        String dateFormat = "dd MMM, yyyy";
        return dateFormat(date, dateFormat);
    }

    /**
     * Method used to changed the date format.
     *
     * @param date Server date.
     * @return String Converted format
     */
    public static String notificationDateFormat(String date) {
        String dateFormat = "dd MMM, yyyy HH:mm a";
        return dateFormat(date, dateFormat);
    }

    /**
     * Method used to changed the date format.
     *
     * @param date Server date.
     * @return Converted format
     */
    public static String liveDateFormat(String date) {
        String dateFormat = "MMM dd yyyy, h:mm a";
        return dateFormat(date, dateFormat);
    }

    /**
     * Used to load the images using glide.
     *
     * @param context  Instance of the activity.
     * @param imageUrl Url path from the response.
     * @param view     Image view to load the url.
     */
    public static void glideLoadImageWithRoundedCorners(Context context, String imageUrl, ImageView view) {
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 2))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Used to load the images using glide.
     *
     * @param context  Instance of the activity.
     * @param imageUrl Url path from the response.
     * @param view     Image view to load the url.
     */
    public static void glideLoadImageWithLeftandTopCorners(Context context, String imageUrl, ImageView view) {
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 8, 2, RoundedCornersTransformation
                        .CornerType.TOP_LEFT), new RoundedCornersTransformation(context, 8, 2,
                        RoundedCornersTransformation.CornerType.BOTTOM_LEFT))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Used to load the images using glide.
     *
     * @param context  Instance of the activity.
     * @param imageUrl Url path from the response.
     * @param view     Image view to load the url.
     */
    public static void glideLoadImageTopCorners(Context context, String imageUrl, ImageView view) {
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 8, 2, RoundedCornersTransformation
                        .CornerType.TOP_LEFT), new RoundedCornersTransformation(context, 8, 2,
                        RoundedCornersTransformation.CornerType.TOP_RIGHT))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Used to load the images using glide.
     *
     * @param context  Instance of the activity.
     * @param imageUrl Url path from the response.
     * @param view     Image view to load the url.
     */
    public static void glideLoadImage(Context context, String imageUrl, ImageView view) {
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Used to load the images using glide.
     *
     * @param context  Instance of the activity.
     * @param imageUrl Url path from the response.
     * @param view     Image view to load the url.
     */
    public static void profileUpdate(Context context, String imageUrl, ImageView view) {
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Used to load the images using glide.
     *
     * @param context  Instance of the activity.
     * @param imageUrl Url path from the response.
     * @param view     Image view to load the url.
     */
    public static void commentPicture(Context context, String imageUrl, ImageView view) {
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Measure the content width of over flow menu for alignment.
     *
     * @param listAdapter Adapter details.
     * @param context     Instance of the activity.
     * @return int Size of the list.
     */
    public static int measureContentWidth(ListAdapter listAdapter, Context context) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = listAdapter.getCount();

        for (int i = 0; i < count; i++) {
            final int positionType = listAdapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(context);
            }

            itemView = listAdapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }
        return maxWidth;
    }

    /**
     * Used to share the content.
     *
     * @param activity  Instance of the activity.
     * @param videoSlug Video url slug details
     */
    public static void intentShare(Activity activity, String videoSlug) {
        String shareUrl = ServerUrls.BASE_URL.replace("api/v1/", "") + "#/video-detail/" + videoSlug;
        ShareCompat.IntentBuilder
                .from(activity)
                .setText(shareUrl)
                .setType("text/plain")
                .setChooserTitle(R.string.share_via)
                .startChooser();
    }

    /**
     * Method used to convert the date format.
     *
     * @param date       Actual date.
     * @param dateFormat Format to convert.
     * @return String Converted date.
     */
    private static String dateFormat(String date, String dateFormat) {
        String timeDetails;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertDate = null;
        try {
            convertDate = sdf.parse(date);
        } catch (Exception ex) {
            Logger.logError(ex);
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        timeDetails = formatter.format(convertDate);
        return timeDetails;
    }

   /* *//**
     * Method used to get the error response from the api response.
     *
     * @param context  Instance from the class.
     * @param response Response from the api.
     *//*
    public static void onSuccessError(Context context, Response<UserResponse> response) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = RestClient.getRetrofit(httpClient);
        Converter<ResponseBody, UserResponse> errorConverter = retrofit.responseBodyConverter
                (UserResponse.class, new Annotation[0]);

        //Checking the success response for un_authorised user or already logged in another device.
        if (response.raw().code() == 500 || response.raw().code() == 404) {
            Toast.makeText(context, response.raw().code() + " " + response.raw().message(), Toast.LENGTH_SHORT).show();
        } else {
            try {
                UserResponse error = errorConverter.convert(response.errorBody());

                if (error.getStatusCode() != null && error.getStatusCode() == 403) {
                    logOut(context);
                    AppController.storeIntegerPreference(context.getString(R.string.un_authorised), error
                            .getStatusCode());
                }
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Logger.logError(e);
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

    *//**
     * Method used to logout the application
     *
     * @param context Instance of the class
     *//*
    private static void logOut(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        AppController.clearPreferences();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }*/

    /**
     * Method used to show the toast message
     *
     * @param context Instance of the class
     * @param message Message to show in toast
     */
    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

   /* *//**
     * Method used to cancel the notification from notificaiton bar
     *
     * @param activity         Instance of the class
     * @param notifyId         Notification id
     * @param notificationSize notification size
     *//*
    public static void cancelNotification(Activity activity, int notifyId, int notificationSize) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) activity.getSystemService(ns);
        nMgr.cancel(notifyId);
        int notificationCount = AppController.getIntegerPreferences(activity.getString(R.string
                .notification_count), 0);
        notificationCount = notificationCount - notificationSize;
        AppController.storeIntegerPreference(activity.getString(R.string
                .notification_count), notificationCount);
    }*/

    /**
     * Method used to set the view more option in textView
     *
     * @param activity   Activity Context
     * @param tv         TextView
     * @param maxLine    Maximum TextView line
     * @param expandText Expanded text
     * @param viewMore   View more Status
     * @param scrollView Scroll view
     */
    public static void makeTextViewResizable(final Activity activity, final TextView tv, final int maxLine, final
    String expandText, final boolean viewMore, final NestedScrollView scrollView) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                if (Build.VERSION.SDK_INT > 16) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
                maxLineBinding(activity, tv, expandText, maxLine, scrollView, viewMore);
            }
        });
    }

    private static void maxLineBinding(Activity activity, TextView tv, String expandText, int maxLine,
                                       NestedScrollView scrollView, boolean viewMore) {
        if (maxLine == 0) {
            int lineEndIndex = tv.getLayout().getLineEnd(0);
            String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
            tv.setText(text);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setText(addClickablePartTextViewResizable(activity, Html.fromHtml(tv.getText().toString()), tv,
                    expandText, viewMore, scrollView), TextView.BufferType.SPANNABLE);
        } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
            int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
            String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
            tv.setText(text);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setText(addClickablePartTextViewResizable(activity, Html.fromHtml(tv.getText().toString()), tv,
                    expandText, viewMore, scrollView), TextView.BufferType.SPANNABLE);
        } else {
            int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
            String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
            tv.setText(Html.fromHtml(text));
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setText(addClickablePartTextViewResizable(activity, Html.fromHtml(tv.getText().toString()), tv,
                    expandText, viewMore, scrollView), TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Method used to set the view more option in textView
     *
     * @param activity     Activity Context
     * @param strSpanned   String spanned
     * @param tv           Textview
     * @param spanableText Spanable text
     * @param viewMore     View more Status
     * @param scrollView   Scroll view
     * @return SpannableText
     */
    private static SpannableStringBuilder
    addClickablePartTextViewResizable(final Activity activity, final Spanned strSpanned, final TextView tv,
                                      final String spanableText, final boolean viewMore, final NestedScrollView
                                              scrollView) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ReadmoreTextView(activity) {

                            @Override
                            public void onClick(View widget) {
                                if (viewMore) {
                                    tv.setLayoutParams(tv.getLayoutParams());
                                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                                    tv.invalidate();
                                    makeTextViewResizable(activity, tv, -1, activity.getString(R.string.read_less),
                                            false, scrollView);
                                } else {
                                    tv.setLayoutParams(tv.getLayoutParams());
                                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                                    tv.invalidate();
                                    makeTextViewResizable(activity, tv, 4, activity.getString(R.string.read_more),
                                            true, scrollView);
                                    scrollView.scrollTo(0, 0);
                                }
                            }
                        }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }


    /**
     * This is for customize aliert box
     */




    /**
     * Alert dialog show to people
     */
    public static void AlertDialog(Context context, String message,String ok) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .setConfirmText(ok)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    /**
     * Alert dialog for get the cancel
     * @param context Get the cont+
     *                ext of an activity
     * @param content Get the content
     * @param okmsg Get the  ok message of text
     * @param canmessage Get the cancel message
     */
    public static void AlertDialogCancel(Context context,String title, String content, String okmsg, String
            canmessage) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }


    /**
     * Alert dialog for get the cancel
     * @param context Get the cont+
     *                ext of an activity
     * @param content Get the content
     * @param okmsg Get the  ok message of text
     * @param canmessage Get the cancel message
     */
    public static void AlertDialogMsg(Context context, String title, String content, String okmsg, String
            canmessage, Activity activity, final Activity senderactivity) {
        new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setCancelText(canmessage).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        })
                .setConfirmText(okmsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();



                    }
                })
                .show();
    }



}

