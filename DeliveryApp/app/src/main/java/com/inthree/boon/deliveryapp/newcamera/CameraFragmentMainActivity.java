package com.inthree.boon.deliveryapp.newcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.internal.controller.view.CameraView;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultListener;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentVideoRecordTextAdapter;
import com.github.florent37.camerafragment.widgets.CameraSettingsView;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.MediaActionSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.ProgressIndicatorActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("MissingPermission")
public class CameraFragmentMainActivity extends AppCompatActivity {

    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;
    private final static String FILE_PATH_ARG = "file_path_arg";
    @BindView(R.id.settings_view)
    CameraSettingsView settingsView;
    @BindView(R.id.flash_switch_view)
    FlashSwitchView flashSwitchView;
    @BindView(R.id.front_back_camera_switcher)
    CameraSwitchView cameraSwitchView;
    @BindView(R.id.record_button)
    RecordButton recordButton;
    @BindView(R.id.photo_video_camera_switcher)
    MediaActionSwitchView mediaActionSwitchView;
    @BindView(R.id.proof_id)
    AppCompatTextView proof;
    @BindView(R.id.record_duration_text)
    TextView recordDurationText;
    @BindView(R.id.record_size_mb_text)
    TextView recordSizeText;
    @BindView(R.id.cameraLayout)
    RelativeLayout cameraLayout;
    private final static String RESPONSE_CODE_ARG = "response_code_arg";
    /* @BindView(R.id.addCameraButton)
     View addCameraButton;*/
    Context mContext;

    private static String responseCode = "";

    /**
     * Get the values
     */
    String Values = "";


    String file_path = "/data/data/com.github.florent37.camerafragment/files/Fragment/";

    /**
     * Heading for the textview
     */
    private String heading;

    /**
     * Get the shipment id
     */
    private String shipmentNumber;

    /**
     * progress bar
     */
    ProgressIndicatorActivity dialogLoading;

    private CameraFragmentResultListener cameraFragmentResultListener;

    /**
     * Check wether customer retake the camera empty or pic retake
     */
    private String retake;

    String currentDateTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerafragment_activity_main);
        ButterKnife.bind(this);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mContext = this;
        Intent intent = getIntent();

        CameraView cameraView;

        responseCode = intent.getStringExtra(RESPONSE_CODE_ARG);
        Values = intent.getStringExtra("fileName");
        heading = intent.getStringExtra("heading");
        shipmentNumber = intent.getStringExtra("shipmentId");


        retake = intent.getStringExtra("retake");

        String picName = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en","US")).format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        currentDateTimeString = format.format(new Date());
        currentDateTimeString = picName;





        if (heading != null)
            proof.setText(heading);
        else
            proof.setText("Proof");

        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
                //addCamera();
            } else {
                addCamera();

            }
        } else {
            addCamera();

        }
        settingsView.setVisibility(View.INVISIBLE);
        cameraSwitchView.setVisibility(View.GONE);
    }

    @OnClick(R.id.flash_switch_view)
    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    @OnClick(R.id.front_back_camera_switcher)
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        if(retake!=null){
            if(retake.equalsIgnoreCase("emptyRetake")){
                PreviewActivity.deleteMediaFile();
            }else if(retake.equalsIgnoreCase("picRetake")){
                PreviewActivity.deleteMediaFile();
            }
        }

        dialogLoading = new ProgressIndicatorActivity(CameraFragmentMainActivity.this);
        dialogLoading.showProgress();
        dialogLoading.setCancelable(false);

       /* SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        currentDateTimeString = format.format(new Date());*/

        final CameraFragmentApi cameraFragment = getCameraFragment();

        if (cameraFragment != null) {
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                                                       @Override
                                                       public void onVideoRecorded(String filePath) {
                                                           Toast.makeText(getBaseContext(), "onVideoRecorded " + filePath, Toast.LENGTH_SHORT).show();
                                                       }

                                                       @Override
                                                       public void onPhotoTaken(byte[] bytes, String filePath) {
                                                          compressImage(filePath);
                                                    /*       File pictureFile = getOutputMediaFile1(mContext);
                                                           Bitmap realImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                           try {
                                                               FileOutputStream fos = new FileOutputStream(pictureFile);


                                                               realImage = mark(realImage, "123456");
                                                               realImage.compress(Bitmap.CompressFormat.PNG, 80, fos);
                                                               fos.write(bytes);
                                                               fos.close();



                                                           } catch (FileNotFoundException e) {
                                                               e.printStackTrace();
                                                           } catch (IOException e) {
                                                               e.printStackTrace();
                                                           }*/
//                                                           Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                                                       }
                                                   },
                    String.valueOf(this.getFilesDir()) + "/DeliveryApp/",
                    Values + currentDateTimeString+"_"+shipmentNumber);
        }
    }




    @OnClick(R.id.settings_view)
    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }

    @OnClick(R.id.photo_video_camera_switcher)
    public void onMediaActionSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchActionPhotoVideo();
        }
    }

    /*@OnClick(R.id.addCameraButton)
    public void onAddCameraClicked() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            addCamera();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {
        //addCameraButton.setVisibility(View.GONE);
        cameraLayout.setVisibility(View.VISIBLE);


        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build(),shipmentNumber);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            cameraFragment.setResultListener(new CameraFragmentResultListener() {
                @Override
                public void onVideoRecorded(String filePath) {
                    Intent intent = PreviewActivity.newIntentVideo(CameraFragmentMainActivity.this, filePath);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }


                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {

//                    String compressedFilePath = compressImage(filePath);

//                    Log.v("compress_path",compressedFilePath);

                    Intent intent = new Intent(CameraFragmentMainActivity.this, PreviewActivity.class);
                    intent.putExtra(FILE_PATH_ARG, filePath);
                    intent.putExtra("heading", heading);
                    intent.putExtra("retake", retake);
                    intent.putExtra("code", String.valueOf(REQUEST_PREVIEW_CODE));
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                    dialogLoading.dismiss();


                    //finish();
                    /* Intent intent = PreviewActivity.newIntentPhoto(CameraFragmentMainActivity.this, filePath,
                            NewCameraActivi);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);*/
                }


            });

            cameraFragment.setStateListener(new

                                                    CameraFragmentStateAdapter() {

                                                        @Override
                                                        public void onCurrentCameraBack() {
                                                            cameraSwitchView.displayBackCamera();
                                                        }

                                                        @Override
                                                        public void onCurrentCameraFront() {
                                                            cameraSwitchView.displayFrontCamera();
                                                        }

                                                        @Override
                                                        public void onFlashAuto() {
                                                            flashSwitchView.displayFlashAuto();
                                                        }

                                                        @Override
                                                        public void onFlashOn() {
                                                            flashSwitchView.displayFlashOn();
                                                        }

                                                        @Override
                                                        public void onFlashOff() {
                                                            flashSwitchView.displayFlashOff();
                                                        }

                                                        @Override
                                                        public void onCameraSetupForPhoto() {
                                                            mediaActionSwitchView.displayActionWillSwitchVideo();
                                                            recordButton.displayPhotoState();
                                                            flashSwitchView.setVisibility(View.VISIBLE);
                                                        }

                                                        @Override
                                                        public void onCameraSetupForVideo() {
                                                            mediaActionSwitchView.displayActionWillSwitchPhoto();
                                                            recordButton.displayVideoRecordStateReady();
                                                            flashSwitchView.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void shouldRotateControls(int degrees) {
                                                            ViewCompat.setRotation(cameraSwitchView, degrees);
                                                            ViewCompat.setRotation(mediaActionSwitchView, degrees);
                                                            ViewCompat.setRotation(flashSwitchView, degrees);
                                                            ViewCompat.setRotation(recordDurationText, degrees);
                                                            ViewCompat.setRotation(recordSizeText, degrees);
                                                        }

                                                        @Override
                                                        public void onRecordStateVideoReadyForRecord() {
                                                            recordButton.displayVideoRecordStateReady();
                                                        }

                                                        @Override
                                                        public void onRecordStateVideoInProgress() {
                                                            recordButton.displayVideoRecordStateInProgress();
                                                        }

                                                        @Override
                                                        public void onRecordStatePhoto() {
                                                            recordButton.displayPhotoState();
                                                        }

                                                        @Override
                                                        public void onStopVideoRecord() {
                                                            recordSizeText.setVisibility(View.GONE);
                                                            //cameraSwitchView.setVisibility(View.VISIBLE);
                                                            settingsView.setVisibility(View.VISIBLE);
                                                        }

                                                        @Override
                                                        public void onStartVideoRecord(File outputFile) {
                                                        }
                                                    });

            cameraFragment.setControlsListener(new

                                                       CameraFragmentControlsAdapter() {
                                                           @Override
                                                           public void lockControls() {
                                                               cameraSwitchView.setEnabled(false);
                                                               recordButton.setEnabled(false);
                                                               settingsView.setEnabled(false);
                                                               flashSwitchView.setEnabled(false);
                                                               recordButton.setVisibility(View.INVISIBLE);
                                                           }

                                                           @Override
                                                           public void unLockControls() {
                                                               cameraSwitchView.setEnabled(true);
                                                               recordButton.setEnabled(true);
                                                               settingsView.setEnabled(true);
                                                               flashSwitchView.setEnabled(true);
                                                               recordButton.setVisibility(View.VISIBLE);
                                                           }

                                                           @Override
                                                           public void allowCameraSwitching(boolean allow) {
                                                               cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                                                               cameraSwitchView.setVisibility(View.GONE);
                                                           }

                                                           @Override
                                                           public void allowRecord(boolean allow) {
                                                               recordButton.setEnabled(allow);
                                                           }

                                                           @Override
                                                           public void setMediaActionSwitchVisible(boolean visible) {
                                                               mediaActionSwitchView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                                                           }
                                                       });

            cameraFragment.setTextListener(new

                                                   CameraFragmentVideoRecordTextAdapter() {
                                                       @Override
                                                       public void setRecordSizeText(long size, String text) {
                                                           recordSizeText.setText(text);
                                                       }

                                                       @Override
                                                       public void setRecordSizeTextVisible(boolean visible) {
                                                           recordSizeText.setVisibility(visible ? View.VISIBLE : View.GONE);
                                                       }

                                                       @Override
                                                       public void setRecordDurationText(String text) {
                                                           recordDurationText.setText(text);
                                                       }

                                                       @Override
                                                       public void setRecordDurationTextVisible(boolean visible) {
                                                           recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                                                       }
                                                   });

        }
    }



    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(this.getFilesDir(), "/DeliveryApp");
        if (!file.exists()) {
            file.mkdirs();
        }


        String uriSting = (file.getAbsolutePath() + "/" + Values + currentDateTimeString+"_"+shipmentNumber + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_PREVIEW_CODE && resultCode == RESULT_OK) {
                setResult(resultCode, data);
            }
            finish();
        } catch (Exception ex) {
            Toast.makeText(CameraFragmentMainActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
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

    private File getOutputMediaFile1(Context context) {

        File mediaStorageDir = new File(context.getFilesDir(), "DeliveryApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("DeliveryApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
//		mediaFile = new File(mediaStorageDir.getPath() + File.separator
//				+ "IMG_" + timeStamp + ".jpg");
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "1234" + "_IMG_" + timeStamp + ".png");

        return mediaFile;
    }

}
