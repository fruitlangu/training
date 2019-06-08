package com.inthree.boon.deliveryapp.newcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.camerafragment.internal.enums.MediaAction;
import com.github.florent37.camerafragment.internal.ui.view.AspectFrameLayout;
import com.github.florent37.camerafragment.internal.utils.Utils;
import com.inthree.boon.deliveryapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Created by memfis on 7/6/16.
 */
public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PreviewActivity";

    public static final int ACTION_CONFIRM = 900;
    public static final int ACTION_RETAKE = 901;
    public static final int ACTION_CANCEL = 902;

    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    private final static String FILE_PATH_ARG = "file_path_arg";
    private final static String RESPONSE_CODE_ARG = "response_code_arg";
    private final static String VIDEO_POSITION_ARG = "current_video_position";
    private final static String VIDEO_IS_PLAYED_ARG = "is_played";
    private final static String MIME_TYPE_VIDEO = "video";
    private final static String MIME_TYPE_IMAGE = "image";

    /**
     * Delete the media file
     */
   static File mediaFile;

    private int mediaAction;
    private  static  String previewFilePath;
    private int requestCode;

    private SurfaceView surfaceView;
    private FrameLayout photoPreviewContainer;
    private ImageView imagePreview;
    private ViewGroup buttonPanel;
    private AspectFrameLayout videoPreviewContainer;
    private View cropMediaAction;
    private TextView ratioChanger;

    private MediaController mediaController;
    private MediaPlayer mediaPlayer;

    private int currentPlaybackPosition = 0;
    private boolean isVideoPlaying = true;

    private int currentRatioIndex = 0;
    private float[] ratios;
    private String[] ratioLabels;
    private Activity activity;
    String activityIntent;
    private static final int REQUEST_PREVIEW_CODE = 1001;


    private AppCompatTextView heading;

    /**
     * Header for the action bar
     */
    private String header;
    private String previewStatus;
    public static String retake;

    public static Intent newIntentPhoto(Context context, String filePath, String newCameraActivity) {
        return new Intent(context, PreviewActivity.class)
                .putExtra(MEDIA_ACTION_ARG, MediaAction.ACTION_PHOTO)
                .putExtra("Intent", newCameraActivity)
                .putExtra(FILE_PATH_ARG, filePath);
    }

    public static Intent newIntentVideo(Context context, String filePath) {
        return new Intent(context, PreviewActivity.class)
                .putExtra(MEDIA_ACTION_ARG, MediaAction.ACTION_VIDEO)
                .putExtra(FILE_PATH_ARG, filePath);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        String originalRatioLabel = getString(R.string.preview_controls_original_ratio_label);
        ratioLabels = new String[]{originalRatioLabel, "1:1", "4:3", "16:9"};
        ratios = new float[]{0f, 1f, 4f / 3f, 16f / 9f};

        surfaceView = (SurfaceView) findViewById(R.id.video_preview);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mediaController == null) return false;
                if (mediaController.isShowing()) {
                    mediaController.hide();
                    showButtonPanel(true);
                } else {
                    showButtonPanel(false);
                    mediaController.show();
                }
                return false;
            }
        });
        heading = (AppCompatTextView) findViewById(R.id.Actionbar);
        videoPreviewContainer = (AspectFrameLayout) findViewById(R.id.previewAspectFrameLayout);
        photoPreviewContainer = (FrameLayout) findViewById(R.id.photo_preview_container);
        buttonPanel = (ViewGroup) findViewById(R.id.preview_control_panel);
        View confirmMediaResult = findViewById(R.id.confirm_media_result);
        View reTakeMedia = findViewById(R.id.re_take_media);
        View cancelMediaAction = findViewById(R.id.cancel_media_action);
        cropMediaAction = findViewById(R.id.crop_image);
        ratioChanger = (TextView) findViewById(R.id.ratio_image);
        ratioChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRatioIndex = (currentRatioIndex + 1) % ratios.length;
                ratioChanger.setText(ratioLabels[currentRatioIndex]);
            }
        });

        cropMediaAction.setVisibility(View.GONE);
        ratioChanger.setVisibility(View.GONE);

        if (cropMediaAction != null)
            cropMediaAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

        if (confirmMediaResult != null)
            confirmMediaResult.setOnClickListener(this);

        if (reTakeMedia != null)
            reTakeMedia.setOnClickListener(this);

        if (cancelMediaAction != null)
            cancelMediaAction.setOnClickListener(this);

        Bundle args = getIntent().getExtras();

        mediaAction = MediaAction.ACTION_PHOTO;



         previewFilePath = args.getString(FILE_PATH_ARG);


        requestCode = Integer.parseInt(args.getString("code"));
        header = args.getString("heading");
        previewStatus = args.getString("PreviewActivity");
        retake = args.getString("retake");

        if (header != null)
            heading.setText(header);
        else
            heading.setText("Proof");
        //activityIntent = args.getString("Intent");
        Log.v("previewFilePath", "--" + previewFilePath);

        if (mediaAction == MediaAction.ACTION_VIDEO) {
            displayVideo(savedInstanceState);
        } else if (mediaAction == MediaAction.ACTION_PHOTO) {
            displayImage();
        } else {
            String mimeType = Utils.getMimeType(previewFilePath);
            if (mimeType.contains(MIME_TYPE_VIDEO)) {
                displayVideo(savedInstanceState);
            } else if (mimeType.contains(MIME_TYPE_IMAGE)) {
                displayImage();
            } else finish();
        }
    }





    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveVideoParams(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaController != null) {
            mediaController.hide();
            mediaController = null;
        }
    }

    private void displayImage() {
        videoPreviewContainer.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
        showImagePreview();
        ratioChanger.setText(ratioLabels[currentRatioIndex]);
    }

    private void showImagePreview() {
        imagePreview = new ImageView(this);
        File file = new File(previewFilePath);
        Uri imageUri = Uri.fromFile(file);
       /* Glide.with(this)
                .load(previewFilePath)
                .into(imagePreview);*/

        Glide.with(this) //passing context
                .load(previewFilePath) //passing your url to load image.
                .error(R.mipmap.ic_launcher)
                .dontAnimate()//in case of any glide exception or not able to download then this image will be appear . if you won't mention this error() then nothing to worry placeHolder image would be remain as it is.
                .diskCacheStrategy(DiskCacheStrategy.ALL) //using to load into cache then second time it will load fast.// when image (url) will be loaded by glide then this face in animation help to replace url image in the place of placeHolder (default) image.
                .fitCenter()//this method help to fit image into center of your ImageView
                .into(imagePreview);

       /* ImageLoader.Builder builder = new ImageLoader.Builder(this);
        builder.load(previewFilePath).build().into(imagePreview);*/
        photoPreviewContainer.removeAllViews();
        photoPreviewContainer.addView(imagePreview);
    }


    private void displayVideo(Bundle savedInstanceState) {
        cropMediaAction.setVisibility(View.GONE);
        ratioChanger.setVisibility(View.GONE);
        if (savedInstanceState != null) {
            loadVideoParams(savedInstanceState);
        }
        photoPreviewContainer.setVisibility(View.GONE);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                showVideoPreview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void showVideoPreview(SurfaceHolder holder) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(previewFilePath);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaController = new MediaController(PreviewActivity.this);
                    mediaController.setAnchorView(surfaceView);
                    mediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
                        @Override
                        public void start() {
                            mediaPlayer.start();
                        }

                        @Override
                        public void pause() {
                            mediaPlayer.pause();
                        }

                        @Override
                        public int getDuration() {
                            return mediaPlayer.getDuration();
                        }

                        @Override
                        public int getCurrentPosition() {
                            return mediaPlayer.getCurrentPosition();
                        }

                        @Override
                        public void seekTo(int pos) {
                            mediaPlayer.seekTo(pos);
                        }

                        @Override
                        public boolean isPlaying() {
                            return mediaPlayer.isPlaying();
                        }

                        @Override
                        public int getBufferPercentage() {
                            return 0;
                        }

                        @Override
                        public boolean canPause() {
                            return true;
                        }

                        @Override
                        public boolean canSeekBackward() {
                            return true;
                        }

                        @Override
                        public boolean canSeekForward() {
                            return true;
                        }

                        @Override
                        public int getAudioSessionId() {
                            return mediaPlayer.getAudioSessionId();
                        }
                    });

                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();

                    videoPreviewContainer.setAspectRatio((double) videoWidth / videoHeight);

                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentPlaybackPosition);

                    if (!isVideoPlaying)
                        mediaPlayer.pause();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    finish();
                    return true;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error media player playing video.");
            finish();
        }
    }

    private void saveVideoParams(Bundle outState) {
        if (mediaPlayer != null) {
            outState.putInt(VIDEO_POSITION_ARG, mediaPlayer.getCurrentPosition());
            outState.putBoolean(VIDEO_IS_PLAYED_ARG, mediaPlayer.isPlaying());
        }
    }

    private void loadVideoParams(Bundle savedInstanceState) {
        currentPlaybackPosition = savedInstanceState.getInt(VIDEO_POSITION_ARG, 0);
        isVideoPlaying = savedInstanceState.getBoolean(VIDEO_IS_PLAYED_ARG, true);
    }

    private void showButtonPanel(boolean show) {
        if (show) {
            buttonPanel.setVisibility(View.VISIBLE);
        } else {
            buttonPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent resultIntent = new Intent();
        if (view.getId() == R.id.confirm_media_result) {

            /*try {*/
            if (previewStatus != null) {
                if (previewStatus.equalsIgnoreCase("PreviewStatus")) {
                    setResult(RESULT_CANCELED);
                    PreviewActivity.this. finish();
                }
            } else {
                if (retake != null) {
                    Log.v("resultIntent"," u "+ "qwertyu");
                    if (retake.equalsIgnoreCase("emptyRetake")) {
                        Log.v("resultIntent"," u "+ "qwerty1");
                        Intent intent = getIntent();
                        intent.putExtra(FILE_PATH_ARG, previewFilePath);
                        intent.putExtra("retake", retake);
                        intent.putExtra(RESPONSE_CODE_ARG, String.valueOf(ACTION_CONFIRM));
                        setResult(RESULT_OK, intent);
                        PreviewActivity.this.finish();
                    } else if (retake.equalsIgnoreCase("picRetake")) {
                        Log.v("resultIntent"," u "+ "qwerty2");
                        Intent intent = getIntent();
                        intent.putExtra(FILE_PATH_ARG, previewFilePath);
                        intent.putExtra("retake", retake);
                        intent.putExtra(RESPONSE_CODE_ARG, String.valueOf(ACTION_CONFIRM));
                        setResult(RESULT_OK, intent);
                        PreviewActivity.this.finish();
                    }

                } else {
                    Log.v("resultIntent"," u "+ "qwerty3");
                    Intent intent = getIntent();
                    intent.putExtra(FILE_PATH_ARG, previewFilePath);
                    intent.putExtra(RESPONSE_CODE_ARG, String.valueOf(ACTION_CONFIRM));
                    setResult(RESULT_OK, intent);
                    PreviewActivity.this.finish();
                }
            }




              /*  Class newClass = Class.forName(activityIntent);
                Intent resume = new Intent(this, newClass);
                resume.putExtra("idProofFilePath", previewFilePath);
                startActivity(resume);
                finish();*/
          /*  } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }*/
           /* resultIntent.putExtra(RESPONSE_CODE_ARG, ACTION_CONFIRM).putExtra(FILE_PATH_ARG, previewFilePath);
            setResult(RESULT_OK, resultIntent);
            finish();*/
        } else if (view.getId() == R.id.re_take_media) {
           // deleteMediaFile();
            //resultIntent.putExtra(RESPONSE_CODE_ARG, ACTION_RETAKE);
            Intent intent = getIntent();
            intent.putExtra(RESPONSE_CODE_ARG, String.valueOf(ACTION_RETAKE));
            setResult(RESULT_OK, intent);
            finish();


        } else if (view.getId() == R.id.cancel_media_action) {
            //deleteMediaFile();
           /* Intent intent = getIntent();
            intent.putExtra(RESPONSE_CODE_ARG, String.valueOf(ACTION_CANCEL));
            setResult(RESULT_OK, intent);
            finish();*/
             finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // deleteMediaFile();
    }

    public static boolean deleteMediaFile() {
        if(previewFilePath!=null) {
            mediaFile = new File(previewFilePath);
            return mediaFile.delete();
        }
        return false;
    }

    public static String getMediaFilePatch(@NonNull Intent resultIntent) {
        return resultIntent.getStringExtra(FILE_PATH_ARG);
    }

    public static boolean isResultConfirm(@NonNull Intent resultIntent) {
        return ACTION_CONFIRM == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    public static boolean isResultRetake(@NonNull Intent resultIntent) {
        return ACTION_RETAKE == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

    public static boolean isResultCancel(@NonNull Intent resultIntent) {
        return ACTION_CANCEL == resultIntent.getIntExtra(RESPONSE_CODE_ARG, -1);
    }

}
