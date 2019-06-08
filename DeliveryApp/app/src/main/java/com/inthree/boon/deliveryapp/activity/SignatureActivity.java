package com.inthree.boon.deliveryapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.app.Utils;
import com.inthree.boon.deliveryapp.utils.ExternalDbOpenHelper;
import com.inthree.boon.deliveryapp.utils.NavigationTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SignatureActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    String shipment_num;
    /**
     * Clear the signature pad
     */
    Button mClear;

    /**
     * sign the signature pad
     */
    Button mGetSign;

    /**
     * Cancel the signature pad
     */
    Button mCancel;

    /**
     * Storage the file format png
     */
    File file;

    /**
     * Get the content of linearlayout
     */
    LinearLayout mContent;

    /**
     * Get the view
     */
    View view;

    /**
     * sign the signature by using canvas
     */
    signature mSignature;

    /**
     * Get the bitmap of sign image
     */
    Bitmap bitmap;

    /**
     * Get the activity name
     */
    String activityName;


    /**
     * Get the db name of helper
     */
    ExternalDbOpenHelper dbOpenHelper;

    /**
     * Initialize the database
     */
    SQLiteDatabase database;

    /**
     * Check whether boolean is false or true
     */
    boolean saveError=false;


    /**
     * Get the string qid
     */
    String qid;

    /**
     * Get the question type
     */
    String questionType;

    /**
     * Get the question
     */
    String question;

    /**
     * Get the answer
     */
    String answer;

    /**
     * Get the status
     */
    String status;


    /**
     * Navigation tracker to be initiate
     */
    NavigationTracker navigationTracker;

    String file_path = "/data/data/com.inthree.boon.deliveryapp/files";
    String directory = file_path + "/UserSignature/";
    //    String directory = Environment.getExternalStorageDirectory().getPath() + "/UserSignature/";
    String picName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, new Locale("en","US"));

    String storedPath = directory + picName + ".png";

    /**
     * This is for display the feed question in dynamic way
     */
    private LinearLayout linearFeedback;
    private TextView textQuestion;
    private StringBuffer buffer;
    private String barRate;
    private String hashMapString;
    private HashMap<String, String> data;
    private TextView txtFeedbackHead;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_login)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.delivery_truck);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        dbOpenHelper = new ExternalDbOpenHelper(this, Constants.DB_NAME);
        database = dbOpenHelper.openDataBase();
        database.enableWriteAheadLogging();

        mContent = (LinearLayout) findViewById(R.id.canvasLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        Intent dash = getIntent();

        if (null != dash) {

            shipment_num = dash.getStringExtra("shipment_num");

            if(shipment_num.contains("/")) {
                shipment_num = shipment_num.replaceAll("/", "");
            }
        }
        data = new HashMap<String, String>();

        linearFeedback = (LinearLayout) findViewById(R.id.feedback_root);
        txtFeedbackHead = (TextView) findViewById(R.id.feedback_ques);
        scrollView = (ScrollView) findViewById(R.id.sv1);


        file_path = String.valueOf(this.getFilesDir());
//         directory = file_path + "/UserSignature/";
        directory = file_path + "/DeliveryApp/";

//        picName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        picName = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en","US")).format(new Date());
//        picName = currentDateTimeString;
        storedPath = directory + picName + ".png";

        storedPath = directory + "sign" + picName + "_" + shipment_num + ".png";
//        storedPath = directory + "sign"+"_"+ shipment_num + ".png";

        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) findViewById(R.id.clear);
        mGetSign = (Button) findViewById(R.id.getsign);
        mGetSign.setEnabled(true);
        mCancel = (Button) findViewById(R.id.cancel);
        view = mContent;
        mGetSign.setOnClickListener(onButtonClick);
        mClear.setOnClickListener(onButtonClick);
        mCancel.setOnClickListener(onButtonClick);
//        file_path = String.valueOf(this.getFilesDir()) + "/UserSignature/"+storedPath;
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/" + storedPath;
        // Method to create Directory, if the Directory doesn't exists
        file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }
        feedback();

    }


    /**
     * Dynamic layout need to show for feedback  of customers
     */
    private void feedback() {
        data.clear();
        Cursor feedback = database.rawQuery("select IFNULL(qid,'') as qid,IFNULL(que_type,'') as que_type,IFNULL(question,'') as " +
                " question, IFNULL(status,'') as status from FeedBackMaster where que_type='rating' ", null);

        if (feedback.getCount() > 0) {
            feedback.moveToFirst();
            while (!feedback.isAfterLast()) {
                txtFeedbackHead.setVisibility(View.VISIBLE);
                qid = feedback.getString(feedback.getColumnIndex("qid"));
                questionType = feedback.getString(feedback.getColumnIndex("que_type"));
                question = feedback.getString(feedback.getColumnIndex("question"));
                status = feedback.getString(feedback.getColumnIndex("status"));


                TextView textView = new TextView(this);
                int valueInheight = (int) getResources().getDimension(R.dimen.feed_height);//dynamically create textview
              //  LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, valueInheight);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int valueInleft = (int) getResources().getDimension(R.dimen.feed_mar_left);
                int valueInright = (int) getResources().getDimension(R.dimen.feed_mar_right);
                int valueIntop = (int) getResources().getDimension(R.dimen.feed_mar_top);
                int valueInbot = (int) getResources().getDimension(R.dimen.feed_mar_bot);
                textParams.setMargins(valueInleft, valueIntop, valueInright, valueInbot);
                textView.setPadding(15, 0, 0, 0);
                textView.setTextSize(22);
                textView.setBackgroundResource(R.color.dark_white);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setGravity(Gravity.CENTER_VERTICAL);                       //set the gravity too
                textView.setText(question);
                textView.setLayoutParams(textParams);
                RatingBar ratingBar = new RatingBar(this);
                ratingBar.setRating(0);
                ratingBar.setNumStars(5);
               //
                // ratingBar.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 10, 40, 10);
                ratingBar.setLayoutParams(layoutParams);
                ratingBar.setStepSize((float) 0.5);

                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

                if (linearFeedback != null) {
                    linearFeedback.addView(textView);
                    linearFeedback.addView(ratingBar);
                }

                barRate = String.valueOf(ratingBar.getRating());
                data.put(qid, barRate);
                final Gson gson = new Gson();
                hashMapString = gson.toJson(data);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        data.clear();
                        barRate = String.valueOf(ratingBar.getRating());
                        data.put(qid, barRate);
                        hashMapString = gson.toJson(data);
                    }
                });

                feedback.moveToNext();
            }
            feedback.close();
        }


    }


    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == mClear) {
//                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(true);
                saveError=false;
            } else if (v == mGetSign) {
//                Log.v("log_tag", "Panel Saved");
                if (Build.VERSION.SDK_INT >= 23) {
//                    isStoragePermissionGranted();
                    if (checkPermission()) {
                        view.setDrawingCacheEnabled(true);
                        if(saveError) {
                            mSignature.save(view, storedPath, hashMapString);
                        }else{
                            Utils.AlertDialogCancel(SignatureActivity.this,getResources().getString(R.string.sign_error),getResources().getString(R.string.sign_erst),getResources().getString(R.string.ok),getResources().getString(R.string.cancel_label));

                        }
                        // Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        requestPermission(); // Code for permission
                    }
                } else {
                    view.setDrawingCacheEnabled(true);
                    if(saveError) {
                        mSignature.save(view, storedPath, hashMapString);
                        //  Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                        // Calling the same class
                        recreate();
                    }else{
                        Utils.AlertDialogCancel(SignatureActivity.this,getResources().getString(R.string.sign_error),getResources().getString(R.string.sign_erst),getResources().getString(R.string.ok),getResources().getString(R.string.cancel_label));

                    }

                }
            } else if (v == mCancel) {
//                Log.v("log_tag", "Panel Canceled");
                // Calling the BillDetailsActivity
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        }
    };

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SignatureActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(SignatureActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SignatureActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SignatureActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    view.setDrawingCacheEnabled(true);
                    mSignature.save(view, storedPath, hashMapString);
                    // Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            view.setDrawingCacheEnabled(true);
            mSignature.save(view, storedPath);
            Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
            // Calling the same class
            recreate();
        }
        else
        {
            Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
        }
    }*/

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            //getParent().requestDisallowInterceptTouchEvent(true);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String storedPath, String feedBack) {
//            Log.v("log_tag", "Width: " + v.getWidth());
//            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(storedPath);
                v.draw(canvas);

//                String title = "Received goods in good condition \n";
                String title = getResources().getString(R.string.sign_received_condition);
                // Add watermark
                bitmap = mark(bitmap, title, shipment_num);

                // Convert the output file to Image such as .png


                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("imagePath", storedPath);
                    returnIntent.putExtra("Feedback", feedBack);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
//                Log.v("log_tag", e.toString());
            }

        }

        public Bitmap mark(Bitmap src, String watermark, String shipno) {
            int w = src.getWidth();
            int h = src.getHeight();
            Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(src, 0, 0, null);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(35);
            paint.setAntiAlias(true);
            paint.setUnderlineText(false);
            canvas.drawText(watermark, 20, 25, paint);
            canvas.drawText(shipno, 20, 55, paint);
            return result;
        }

        public void clear() {
            path.reset();
            invalidate();
            mGetSign.setEnabled(true);
            saveError=false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);
            saveError=true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

//            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.e("Onstart", "MainonStart");

        /**
         * This is for tracking the classes when user working onit an app
         */

        /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "1", "0");
    }


    @Override
    protected void onStop() {
        super.onStop();
//        Log.e("Onstop", "MainonStop");
        /*Get the current activity name*/
        activityName = this.getClass().getSimpleName();
        navigationTracker = new NavigationTracker(this);
        navigationTracker.trackingClasses(activityName, "0", "0");
    }
}
