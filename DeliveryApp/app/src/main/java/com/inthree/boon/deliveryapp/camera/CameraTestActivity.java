package com.inthree.boon.deliveryapp.camera;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.activity.ProgressIndicatorActivity;

public class CameraTestActivity extends AppCompatActivity {
	public static final String EXTRA_CAMERA_DATA = "camera_data";
	private static String LOGTAG	= "CameraActivity";
	String img_location;
	RelativeLayout.LayoutParams fullScreenParams ;
	int fullWidth = 0;
	int fullHeight = 0;
    String cam_title;
    String getImage;
    String shipment_num;
    String file_path;
	Button button_done;
	private CameraUtils  	mCamUtils				= null;        
    private Button			capturePic			= null;
    private ImageView			flipCamera			= null;
    private Button			cameraFlash			= null;
    private RelativeLayout  cameraLayout  		= null;
    Button retake_image_button;
    ImageView iv_cam_flash;
	LinearLayout fl_imagepreview;
    private LinearLayout	parentView;
    byte[] mCameraData;
	ProgressIndicatorActivity dialogLoading;
	ProgressIndicatorActivity dialogLoading1;
	public static int mOrientation;
	CameraAsyncTask mcameraAsync;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		if(null != actionBar)
			actionBar.hide();

		setContentView(R.layout.activity_frame_compositor_test);
		file_path = String.valueOf(this.getFilesDir()) + "/DeliveryApp/";
		Intent dash = getIntent();
		if (null != dash) {
			cam_title = dash.getStringExtra("act_title");
			getImage =  dash.getStringExtra("image_path");
			shipment_num =  dash.getStringExtra("shipment_num");
//			Log.v("file_path_getImage","--"+getImage);

		} else {
			cam_title = "";
			getImage = "";
//			Log.v("file_path_getImage_null","--"+getImage);
		}

		capturePic	= (Button)findViewById(R.id.button_capture);
		button_done = (Button)findViewById(R.id.button_done);
		fl_imagepreview = (LinearLayout) findViewById(R.id.fl_imagepreview);
		retake_image_button = (Button) findViewById(R.id.retake_image_button);
		iv_cam_flash = (ImageView) findViewById(R.id.iv_cam_flash);
		capturePic.setVisibility(View.VISIBLE);
		capturePic.setOnClickListener(OnCapture);

		button_done.setOnClickListener(OnDone);
		button_done.setEnabled(false);
		flipCamera	= (ImageView)findViewById(R.id.button_flip);
		flipCamera.setVisibility(View.VISIBLE);
		flipCamera.setOnClickListener(OnFlip);

//		cameraFlash = (Button)findViewById(R.id.button_flash);
//		cameraFlash.setVisibility(View.VISIBLE);
//		cameraFlash.setVisibility(View.GONE);
//		cameraFlash.setOnClickListener(OnFlashClick);
		iv_cam_flash.setOnClickListener(OnFlashClick);
		retake_image_button.setOnClickListener(OnSetupCameraAgain);

		mOrientation = this.getResources().getConfiguration().orientation;

		ScreenDimensions fullscreen = getScreenDimensions(mOrientation,(double)16/(double)9);
		calculateLayoutParams(fullscreen);

		cameraLayout = (RelativeLayout)findViewById(R.id.full_camera_content);
		cameraLayout.setVisibility(View.VISIBLE);
//		mcameraAsync = new CameraAsyncTask();
//		mcameraAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		/*((CameraTestActivity)this).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout, onImageSetup,shipment_num);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/

        try{
	    mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout, onImageSetup,shipment_num);
        }catch(Exception ex){
        }


//		mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout, onImageSetup,shipment_num);

		parentView = (LinearLayout) findViewById(R.id.picture_content_parent_view_host);

		LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
		parentLayout.gravity = Gravity.CENTER;
		parentView.setLayoutParams(parentLayout);
		parentView.setGravity(Gravity.CENTER);

		// Hide flip camera button if only one camera a available
		mCamUtils.handleFlipVisibility();

		//Set Title
		CameraTestActivity.this.setTitle(cam_title);

		if(!getImage.equals("")){
			cameraLayout.setVisibility(View.GONE);
			fl_imagepreview.setVisibility(View.VISIBLE);
			capturePic.setVisibility(View.GONE);
			button_done.setEnabled(true);
			setupImageFoundDisplay(getImage);
		}

	}
/*oncreate ends*/
	/**
	 * Action to be performed when image is capture is clicked
	 */
	private OnClickListener OnCapture =  new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialogLoading = new ProgressIndicatorActivity(CameraTestActivity.this);
			dialogLoading.showProgress();
			dialogLoading.setCancelable(true);
			button_done.setEnabled(true);
			retake_image_button.setVisibility(View.VISIBLE);
			capturePic.setVisibility(View.GONE);
			mCamUtils.clickPicture();
		}
	};


	private OnClickListener OnDone =  new OnClickListener() {
		@Override
		public void onClick(View v) {
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


	/**
	 * Action to be performed when image is capture is clicked
	 */
	private OnClickListener OnFlashClick =  new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamUtils.toggleFlashSettings();

		}
	};

	/**
	 * Action to be performed when flip camera is clicked
	 */
	private OnClickListener OnFlip =  new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCamUtils.flipCamera();
			
	    	double ar = mCamUtils.getHighestAspectRatio();
	    	ScreenDimensions screen = getScreenDimensions(mOrientation, ar);	    	
			calculateLayoutParams(screen);
	    	mCamUtils.setPreviewLayoutParams(fullScreenParams);
		}
	};
	
	/**
	 * Callback called by the camera Utils when image file has been created
	 */
	private CameraUtils.ImageClicked onImageClick = new CameraUtils.ImageClicked() {
		@Override
		public void imageClicked(File pictureFile) {
			
     	    MediaScannerConnection.scanFile(getApplicationContext(), new String[] {pictureFile.getPath()},
						null, null);
			img_location = String.valueOf(pictureFile) ;
//			Log.v("img_location",img_location);
		}

		@Override
		public void flashSet(String flashMode) {
//			cameraFlash.setText(flashMode);
//			Log.v("Flash_mode",flashMode);
			if(flashMode.equals("off")){
            iv_cam_flash.setImageResource(R.drawable.flash_off);
			}else if(flashMode.equals("auto")){
				iv_cam_flash.setImageResource(R.drawable.flash_auto);
			}else if(flashMode.equals("on")){
				iv_cam_flash.setImageResource(R.drawable.flash);
			}
		}

		@Override
		public void hideFlipButton() {			
			flipCamera.setVisibility(View.INVISIBLE);
		}

		@Override
		public void enableFlashButton(boolean flag) {
			if(flag){
//				cameraFlash.setVisibility(View.VISIBLE);
				iv_cam_flash.setVisibility(View.VISIBLE);
			}else{
//				cameraFlash.setVisibility(View.GONE);
			}
			
		}

		@Override
		public void CameraUnAvailable() {
			// TODO Auto-generated method stub
			
		}
	};

	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		//       mtx.postRotate(degree);
		mtx.setRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	public Bitmap rotateBitmap(Bitmap original, float degrees) {
		int width = original.getWidth();
		int height = original.getHeight();

		Matrix matrix = new Matrix();
		matrix.preRotate(degrees);

		Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
		Canvas canvas = new Canvas(rotatedBitmap);
		canvas.drawBitmap(original, 5.0f, 0.0f, null);

		return rotatedBitmap;
	}

	private CameraUtils.cameraSetupImage onImageSetup = new CameraUtils.cameraSetupImage(){
		@Override
		public void cameraData(byte[] data) {
			mCameraData = data;
			Bitmap rotatedImage = null;
			BitmapDrawable background = null;
//			setupImageDisplay1();
//			cameraLayout.setVisibility(View.GONE);
			fl_imagepreview.setVisibility(View.VISIBLE);
			Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);

			if(bitmap.getWidth() > bitmap.getHeight()){
			 rotatedImage = 	rotate(bitmap,90);
				background = new BitmapDrawable(rotatedImage);
			}else if(bitmap.getWidth() < bitmap.getHeight()){
				background = new BitmapDrawable(bitmap);
			}
//			background = new BitmapDrawable(bitmap);
			fl_imagepreview.setBackgroundDrawable(background);
			retake_image_button.setEnabled(true);
			dialogLoading.dismiss();
		}
	};

	private void setupImageDisplay1() {
		Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
//		mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout, onImageSetup);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        mCameraImage.setImageBitmap(bitmap);
		BitmapDrawable background = new BitmapDrawable(bitmap);
		fl_imagepreview.setBackgroundDrawable(background);
//		mCamera.stopPreview();

	}




	private OnClickListener OnSetupCameraAgain =  new OnClickListener() {
		@Override
		public void onClick(View v) {
			dialogLoading1 = new ProgressIndicatorActivity(CameraTestActivity.this);
			dialogLoading1.showProgress();
			dialogLoading1.setCancelable(true);
//			Log.v("second_img","OnSetupCameraAgain");
			fl_imagepreview.setVisibility(View.GONE);

			cameraLayout.setVisibility(View.VISIBLE);
			capturePic.setVisibility(View.VISIBLE);
			button_done.setEnabled(false);
			mCamUtils.restartPreviewAfterPictureClick();
			dialogLoading1.dismiss();
		}
	};

	private void setupImageFoundDisplay(String img) {
		String full_path =  file_path + img;
//		Log.v("second_img",full_path);
		Bitmap rotatedImage = null;
		BitmapDrawable background = null;
		Bitmap bitmap = BitmapFactory.decodeFile(full_path);
		retake_image_button.setEnabled(true);
//		cameraLayout.setVisibility(View.GONE);
//		fl_imagepreview.setVisibility(View.VISIBLE);
		if(bitmap.getWidth() > bitmap.getHeight()){
			rotatedImage = 	rotate(bitmap,90);
			background = new BitmapDrawable(rotatedImage);
		}else if(bitmap.getWidth() < bitmap.getHeight()){
			background = new BitmapDrawable(bitmap);
		}
//		BitmapDrawable background = new BitmapDrawable(bitmap);
		fl_imagepreview.setBackgroundDrawable(background);

	}

	private void setupImageDisplay() {
		Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
//		mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout,onImageSetup);
//		mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        mCameraImage.setImageBitmap(bitmap);


		BitmapDrawable background = new BitmapDrawable(bitmap);
		cameraLayout.setBackgroundDrawable(background);
//		mCamera.stopPreview();

	}


    @Override
    protected void onResume() {
    	super.onResume();       	
    	
    	mOrientation = this.getResources().getConfiguration().orientation;
    	
    	mCamUtils.resetCamera();    	
    
    	double ar = mCamUtils.getHighestAspectRatio();
    	ScreenDimensions screen = getScreenDimensions(mOrientation, ar);
        
		calculateLayoutParams(screen);	
    	mCamUtils.setPreviewLayoutParams(fullScreenParams);
    	
    	mCamUtils.setFlashParams(mCamUtils.getFlashMode()); 		    	
    	
    	mCamUtils.setCameraDisplayOrientation(this);
    	
    	ScreenDimensions fullscreen = getScreenDimensions(mOrientation,(double)16/(double)9);
        LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
        parentLayout.gravity = Gravity.CENTER;
        parentView.setLayoutParams(parentLayout);
        
    }
   
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

		mOrientation = newConfig.orientation;
		
    	double ar = mCamUtils.getHighestAspectRatio();
    	ScreenDimensions screen = getScreenDimensions(mOrientation, ar);			
        calculateLayoutParams(screen);
    	mCamUtils.setPreviewLayoutParams(fullScreenParams);
		
		mCamUtils.setCameraDisplayOrientation(this);		
		
    	ScreenDimensions fullscreen = getScreenDimensions(mOrientation,(double)16/(double)9);
        LinearLayout.LayoutParams parentLayout = new LinearLayout.LayoutParams(fullscreen.getDisplayWidth(), fullscreen.getDisplayHeight());
        parentLayout.gravity = Gravity.CENTER;
        parentView.setLayoutParams(parentLayout);

	}

	
	private void calculateLayoutParams(ScreenDimensions screen){

		fullHeight = screen.getDisplayHeight();
		fullWidth = screen.getDisplayWidth();

		int left_margins = 0;
		int top_margins = 0;
		
		ScreenDimensions max = getScreenDimensions(screen.orientation, (double)16/(double)9);		
		
		if(screen.aspectratio < max.aspectratio){
			if(Configuration.ORIENTATION_LANDSCAPE == screen.orientation){
				fullHeight = max.getDisplayHeight();			
				fullWidth =  (int)(screen.aspectratio * (double)fullHeight); 
				left_margins = max.getDisplayWidth() - fullWidth;
				
			}else if(Configuration.ORIENTATION_PORTRAIT == screen.orientation){
				fullWidth = max.getDisplayWidth();
				fullHeight = (int)(screen.aspectratio * (double)fullWidth); 
				top_margins = max.getDisplayHeight() - fullHeight; 
			}
		}

        fullScreenParams = new RelativeLayout.LayoutParams(fullWidth ,fullHeight);        
        fullScreenParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        fullScreenParams.setMargins(left_margins, top_margins, 0, 0);

	}
	   
	
	@Override
	public void onPause() {
		super.onPause();
		mCamUtils.releaseCamera();  		
	}  
	
	
	protected ScreenDimensions getScreenDimensions(int orientation, double ar){
		
		ScreenDimensions screen = null;
		
		 Display display = getWindowManager().getDefaultDisplay();
		 if(null != display)
		 {
			  screen = new ScreenDimensions();
			  
			  int mSurfaceHeight = 0;
			  int mSurfaceWidth = 0;
			  int mdisplayWidth = 0;
			  int mdisplayHeight = 0;
				
			  Point size = new Point();
			  display.getSize(size);

	
			   mdisplayWidth = size.x;
			   mdisplayHeight = size.y;
				
			   if(Configuration.ORIENTATION_LANDSCAPE == orientation){
	
				   if(mdisplayWidth < mdisplayHeight){
					   int temp = mdisplayHeight;
					   mdisplayHeight = mdisplayWidth;
					   mdisplayWidth = temp;					   
				   }
				   /*
				    * Surface view should be like this
				    * 		___________________________
				    * 	   |		    16			   |
				    * 	   |						   |
				    *      |						   | 9
				    *      |						   |
				    *      |						   |
				    *      |___________________________|
				    */
				   
				   if( ar <= ((double)mdisplayWidth)/((double)mdisplayHeight)) {
					   mSurfaceWidth = mdisplayWidth;
					   mSurfaceHeight = (int)((double)mSurfaceWidth / ar );
				   }else{
					   mSurfaceHeight = mdisplayHeight;
					   mSurfaceWidth = (int)((double)mSurfaceHeight * ar );
				   }
				   
				   screen.setDisplayHeight(mSurfaceHeight);
				   screen.setDisplayWidth(mSurfaceWidth);
				   screen.setOrientation(Configuration.ORIENTATION_LANDSCAPE);				   
			   }
			   else{
				   
				   if(mdisplayWidth > mdisplayHeight){
					   int temp = mdisplayHeight;
					   mdisplayHeight = mdisplayWidth;
					   mdisplayWidth = temp;					   
				   }
				   
				   /*
				    * Surface view should be like this
				    * 		_______________
				    * 	   |		9  	   |
				    * 	   |			   |
				    *      |			   | 
				    *      |			   |
				    *      |			   |
				    *      |			   | 16
				    *      |			   |
				    *      |			   |
				    *      |			   |
				    *      |			   |
				    *      |_______________|
				    */
				   
				   if(ar >= ((double)mdisplayHeight/(double)mdisplayWidth)) {
					   mSurfaceWidth = mdisplayWidth;
					   mSurfaceHeight = (int)((double)mSurfaceWidth * ar);
				   }else{
					   mSurfaceHeight = mdisplayHeight;
					   mSurfaceWidth = (int)((double)mSurfaceHeight / ar);
				   }	
				   
				   screen.setDisplayHeight(mSurfaceHeight);
				   screen.setDisplayWidth(mSurfaceWidth);
				   screen.setOrientation(Configuration.ORIENTATION_PORTRAIT);
				}			   			
		}
		
		 screen.aspectratio = ar;
		 
//		 Log.i(LOGTAG, "screen aspect ratio h = "+screen.getDisplayHeight());
//		 Log.i(LOGTAG, "screen aspect ratio w = "+screen.getDisplayWidth());
		 
		 return screen;
	}

	private class CameraAsyncTask extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected void onPreExecute() {

			try {
//                mProgressView.setText("0% Completed");

			} catch (Exception ignored) {
			}
		}

		protected Boolean doInBackground(Void... urls) {

			try{
				mCamUtils = new CameraUtils(getApplicationContext(), onImageClick, cameraLayout, onImageSetup,shipment_num);
			}catch(Exception ex){
			}

			return true;
		}



		protected void onProgressUpdate(Integer... progress) {


			try {
//                mProgressView.setText(Integer.toString(progress[0]) + "% Completed");
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}

		protected void onPostExecute(Boolean flag) {
			// Hide flip camera button if only one camera a available
			mCamUtils.handleFlipVisibility();

			//Set Title
			CameraTestActivity.this.setTitle(cam_title);

			if(!getImage.equals("")){
				cameraLayout.setVisibility(View.GONE);
				fl_imagepreview.setVisibility(View.VISIBLE);
				capturePic.setVisibility(View.GONE);
				button_done.setEnabled(true);
				setupImageFoundDisplay(getImage);
			}

		}
	}

}


