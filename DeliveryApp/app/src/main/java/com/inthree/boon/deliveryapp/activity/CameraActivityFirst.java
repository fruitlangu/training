package com.inthree.boon.deliveryapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.inthree.boon.deliveryapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//public class CameraActivityFirst extends AppCompatActivity implements PictureCallback, SurfaceHolder.Callback {
public class CameraActivityFirst extends AppCompatActivity implements  SurfaceHolder.Callback {
    private double mAspectRatio;
    public static final String EXTRA_CAMERA_DATA = "camera_data";
FrameLayout camera_frame;
    private static final String KEY_IS_CAPTURING = "is_capturing";
    private Camera.Size size;
    String directory = Environment.getExternalStorageDirectory().getPath() + "/Deliver/";
    String picName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String storedPath = directory + picName + ".png";
    String currentDateandTime;
    private Camera mCamera;
    private ImageView mCameraImage;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private byte[] mCameraData;
    String img_location;
    private boolean mIsCapturing;
    Bitmap bitmap;
    View view;
    String cam_title;
    String getImage;
    String shipment_num;
    Button doneButton;
    String file_path_old="/storage/emulated/0/Pictures/DeliveryApp/";
//    String file_path = "/data/data/com.inthree.boon.deliveryapp/files/DeliveryApp/";
    String file_path = "";
    Button retake_image_button;
    private boolean safeToTakePicture = false;
    float mDist;
    private Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;
    ProgressIndicatorActivity dialogLoading;

    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogLoading = new ProgressIndicatorActivity(CameraActivityFirst.this);
            dialogLoading.showProgress();
            captureImage();
        }
    };

    private OnClickListener mRecaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
//            Log.v("here_huh","no");
        }
    };

    private OnClickListener recapture = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCaptureImageButton.setVisibility(View.VISIBLE);
            retake_image_button.setVisibility(View.GONE);
            doneButton.setEnabled(false);
            setupImageCapture();
//            Log.v("here_huh","yeah");
//            mCameraImage.setVisibility(View.INVISIBLE);
//            mCamera.startPreview();
        }
    };


    private OnClickListener mDoneButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            Log.v("mCameraData", String.valueOf(mCameraData));
//            save(view, storedPath);

     /*      Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);

            Uri tempUri = getImageUri(getApplicationContext(), bitmap);
            File finalFile = new File(getRealPathFromURI(tempUri));
            Log.v("finalFile", String.valueOf(finalFile));*/
//            if (mCameraData != null) {
            if (img_location != null) {
                Intent intent = new Intent();
//                intent.putExtra(EXTRA_CAMERA_DATA, finalFile.toString());
                intent.putExtra(EXTRA_CAMERA_DATA, img_location);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    };

/*    private OnClickListener mDoneButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("mCameraData", String.valueOf(mCameraData));
//            save(view, storedPath);

//            Intent intent = new Intent();
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] bytes = stream.toByteArray();
//            intent.putExtra(EXTRA_CAMERA_DATA,bytes);
//            setResult(RESULT_OK, intent);
            Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
            Uri tempUri = getImageUri(getApplicationContext(), bitmap);
            File finalFile = new File(getRealPathFromURI(tempUri));
            Log.v("finalFile", String.valueOf(finalFile));
            if (mCameraData != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CAMERA_DATA, finalFile);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    };*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_frame = (FrameLayout) findViewById(R.id.camera_frame);
        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        mCaptureImageButton = (Button) findViewById(R.id.capture_image_button);
        doneButton = (Button) findViewById(R.id.done_button);
        retake_image_button = (Button) findViewById(R.id.retake_image_button);
        file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
//        Log.v("file_path_cam",file_path);
        Intent dash = getIntent();
        if (null != dash) {
            cam_title = dash.getStringExtra("act_title");
            getImage =  dash.getStringExtra("image_path");
//            Log.v("file_path_getImage","--"+getImage);
            shipment_num =  dash.getStringExtra("shipment_num");
        } else {
            cam_title = "";
            getImage = "";
//            Log.v("file_path_getImage_null","--"+getImage);
        }
        CameraActivityFirst.this.setTitle(cam_title);
        if(!getImage.equals("")){
//            mCameraPreview.setVisibility(View.INVISIBLE);
//            Log.v("getImage","--"+getImage);
            setupImageFoundDisplay(getImage);
        }else{
            mCameraImage.setVisibility(View.INVISIBLE);
        }

        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);


        doneButton.setOnClickListener(mDoneButtonClickListener);
        retake_image_button.setOnClickListener(recapture);

        mIsCapturing = true;
        mCameraPreview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mCamera.autoFocus(new Camera.AutoFocusCallback(){
                    @Override
                    public void onAutoFocus(boolean arg0, Camera arg1) {
//                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);

                    }
                });

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivityFirst.this, "Unable to open camera.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /*@Override
    public void onPictureTaken(byte[] data, Camera camera) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());
        File pictureFile = getOutputMediaFile1();

        Log.e("pictureFile", String.valueOf(pictureFile));
        if (pictureFile == null) {
            safeToTakePicture = true;
            return;
        }
//        safeToTakePicture = true;
//        mCameraData = data;
//        img_location = String.valueOf(pictureFile) ;
//        setupImageDisplay();
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

            ExifInterface exif=new ExifInterface(pictureFile.toString());

            Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                realImage= rotate(realImage, 90);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                realImage= rotate(realImage, 270);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                realImage= rotate(realImage, 180);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                realImage= rotate(realImage, 90);
            }
//            fos.write(data);

//            realImage = mark(realImage, shipment_num);
            realImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            fos.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        }
        safeToTakePicture = true;
        mCameraData = data;
        img_location = String.valueOf(pictureFile) ;
        setupImageDisplay();
    }*/

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Bitmap rotateImages(Bitmap bmp, String imageUrl) {
        if (bmp != null) {
            ExifInterface ei;
            int orientation = 0;
            try {
                ei = new ExifInterface(imageUrl);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

            } catch (IOException e) {
                e.printStackTrace();
            }
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_UNDEFINED:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }
            Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
                    bmpHeight, matrix, true);
            return resizedBitmap;
        } else {
            return bmp;
        }
    }



  /*  @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
        File finalFile = new File(getRealPathFromURI(tempUri));
        File pictureFileDir = finalFile;

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Toast.makeText(getApplicationContext(), "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (Exception error) {
            Toast.makeText(getApplicationContext(), "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
    }*/



    /*@Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
       Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
        File finalFile = new File(getRealPathFromURI(tempUri));

        Log.e("CamERROR",String.valueOf(finalFile));
        if(bitmap==null){
            Toast.makeText(getApplicationContext(), "not taken", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "taken", Toast.LENGTH_SHORT).show();
            setupImageDisplay();
        }



    }*/

/*    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {

                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                    safeToTakePicture = true;
                }
            } catch (IOException e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {
//                    calculateImageSize(width, height);
                    Camera.Parameters parameters = mCamera.getParameters();
                    List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//                    Camera.Size previewSize = previewSizes.get(4); //480h x 720w
                    Camera.Size previewSize = previewSizes.get(2); //480h x 720w
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                    mCamera.setParameters(parameters);

                    Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    if(display.getRotation() == Surface.ROTATION_0) {
                        mCamera.setDisplayOrientation(90);
                    } else if(display.getRotation() == Surface.ROTATION_270) {
                        mCamera.setDisplayOrientation(180);
                    }
                    mCamera.startPreview();
                    safeToTakePicture = true;
                }
            } catch (IOException e) {
                Toast.makeText(CameraActivityFirst.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void captureImage() {
        if (safeToTakePicture) {
            mCamera.takePicture(null, null, jpegCallback);
            safeToTakePicture = false;
        }
        doneButton.setEnabled(true);
    }

    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mCamera.startPreview();
        mCaptureImageButton.setText("Capture");
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
    }

    private void setupImageDisplay() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        mCameraImage.setImageBitmap(bitmap);
        BitmapDrawable background = new BitmapDrawable(bitmap);
        camera_frame.setBackgroundDrawable(background);
        mCamera.stopPreview();
        mCameraPreview.setVisibility(View.INVISIBLE);
        mCameraImage.setVisibility(View.VISIBLE);
        mCaptureImageButton.setText("Retake");
        doneButton.setEnabled(true);
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
        dialogLoading.dismiss();
    }

    private void setupImageFoundDisplay(String img) {
        String full_path =  file_path + img;
//        Log.v("second_img",full_path);
        Bitmap bitmap = BitmapFactory.decodeFile(full_path);
//        mCameraImage.setImageBitmap(bitmap);
        BitmapDrawable background = new BitmapDrawable(bitmap);
        camera_frame.setBackgroundDrawable(background);
        mCameraPreview.setVisibility(View.INVISIBLE);
        mCameraImage.setVisibility(View.VISIBLE);
//        mCaptureImageButton.setText("Retake");
        mCaptureImageButton.setVisibility(View.GONE);
        doneButton.setEnabled(true);
        retake_image_button.setOnClickListener(recapture);
    }


    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "DeliveryApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.e("DeliveryApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private File getOutputMediaFile1() {
       /* File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "DeliveryApp");*/
        File mediaStorageDir = new File(getFilesDir(), "DeliveryApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                Log.e("DeliveryApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(34);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        canvas.drawText(watermark, 20, 35, paint);
        return result;
    }



    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            currentDateandTime = sdf.format(new Date());
            File pictureFile = getOutputMediaFile1();
            if (pictureFile == null) {
                safeToTakePicture = true;
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
//                realImage= rotateImages(realImage,pictureFile.toString());
                ExifInterface exif=new ExifInterface(pictureFile.toString());

//                Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                    realImage= rotate(realImage, 90);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                    realImage= rotate(realImage, 270);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                    realImage= rotate(realImage, 180);
                } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                    realImage= rotate(realImage, 90);
                }
//            fos.write(data);

                realImage = mark(realImage, shipment_num);
                realImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);

                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
            safeToTakePicture = true;
            mCameraData = data;
            img_location = String.valueOf(pictureFile) ;
            setupImageDisplay();

        }

    };

    public void calculateImageSize(int width, int height){
        if(null == mCamera){
            return;
        }

        Camera.Parameters parameters 	= mCamera.getParameters();
        Camera.Size previewSize = getOptimalPreviewSize(width, height);
        Camera.Size	pictureSize	= getOptimalPictureSize(previewSize.width, previewSize.height);

//        Log.i("122", " cam preview picture w , h -"+pictureSize.width+" "+pictureSize.height);
//        Log.i("122", " cam preview preview w , h -"+previewSize.width+" "+previewSize.height);

        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
    }

    private Camera.Size getOptimalPreviewSize(int width, int height) {

        Camera.Parameters parameters 		= mCamera.getParameters();
        List<Camera.Size> 		listPreviewSize = parameters.getSupportedPreviewSizes();
        ArrayList<Camera.Size> temp			= new ArrayList<Camera.Size>();

        for (Camera.Size size : listPreviewSize) {

            Double 	ratio 		= (double) size.width / size.height;


            /**
             * Since the camera and display will be 16:9, other resolutions
             * will cause grey band to appear
             */
            if(ratio.equals(mAspectRatio)){
                temp.add(size);
                break;
            }
        }

        if(1 == temp.size()) {
            return temp.get(0);
        }else if(1 > temp.size()) {
            return selectOptimalPreviewSize((ArrayList<Camera.Size>)listPreviewSize, width,
                    height);
        }else{
            return selectOptimalPreviewSize(temp, width, height);
        }
    }

    private Camera.Size getOptimalPictureSize(int width, int height) {

        Camera.Size				optimalSize			= null;
        double 		 			targetRatio		= (double)width / height;
        int 					maxResolution	= 0;
        Camera.Parameters parameters 		= mCamera.getParameters();
        List<Camera.Size> 		listPictureSize = parameters.getSupportedPictureSizes();
        ArrayList<Camera.Size>	temp			= new ArrayList<Camera.Size>();

        for (Camera.Size size : listPictureSize) {
            Double 	ratio 		= (double) size.width / size.height;


            /**
             * Since the camera and display will be 16:9, other resolutions
             * will cause grey band to appear
             */
            if (ratio.equals(mAspectRatio)) {
                temp.add(size);

                break;
            }
        }

        if(0 == temp.size()){

            optimalSize = listPictureSize.get(0);
            return optimalSize;
        }else if(1 == temp.size()) {
            return temp.get(0);
        }else{
            for(int i = 0 ; i < temp.size() ; i++) {
                Camera.Size	size		= temp.get(i);
                int currentResolution	= size.width * size.height;

                if(currentResolution > maxResolution){
                    maxResolution	= currentResolution;
                    optimalSize		= size;
                }
            }

            if(null == optimalSize){

//                Log.e("fdfdf", " NULL PICTURE SIZE");
            }
            return optimalSize;
        }
    }

    private Camera.Size selectOptimalPreviewSize(ArrayList<Camera.Size> previewSizeList,
                                                 int targetWidth, int targetHeight) {
        Camera.Size				optimalSize			= null;
        int 					minDiffHeight		= Integer.MAX_VALUE;
        int 					minDiffWidth		= Integer.MAX_VALUE;
        ArrayList<Camera.Size>	temp				= new ArrayList<Camera.Size>();

        for(int i = 0 ; i < previewSizeList.size() ; i++) {
            Camera.Size	size		= previewSizeList.get(i);
            int currentHeight		= size.height;
            int currentDiffHeight	= Math.abs(currentHeight - targetHeight);
            if(currentDiffHeight < minDiffHeight){
                minDiffHeight	= currentDiffHeight;
            }
        }

        for(int i = 0 ; i < previewSizeList.size() ; i++) {
            Camera.Size	size		= previewSizeList.get(i);

            if(Math.abs(size.height - targetHeight) == minDiffHeight){
                temp.add(size);
            }
        }

        if(1 == temp.size()) {
            return temp.get(0);
        }
        else {
            for(int i = 0 ; i < temp.size() ; i++) {
                Camera.Size	size		= temp.get(i);
                int currentWidth		= size.width;
                int currentDiffWidth	= Math.abs(currentWidth - targetWidth);
                if(currentDiffWidth < minDiffWidth){
                    minDiffWidth	= currentDiffWidth;
                    optimalSize		= size;
                }
            }
        }

        return optimalSize;

    }

}
