package com.inthree.boon.deliveryapp.camera;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraUtils {
	private boolean safeToTakePicture = false;
	String shipmentNo;
	Context mContext;
	private byte[] mCameraData;
	private static String LOGTAG = "CameraUtils";
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private int 		cameraId			= CameraInfo.CAMERA_FACING_BACK;
	 
	private Context  				mApplication;
	private Camera 		 				mCamera;
	cameraSetupImage mCameraImg;
	/**
     * This flag tells weather the camera preview is running or stopped
     * true - camera preview is running
     * false - camera preview is not running
     */
	private boolean 					mPreviewRunning			  = true;
	private OrientationEventListener 	mOrientationEventListener = null;
	private CameraPreview 				mPreview				  = null;
	private int 						orientation				  = -1;
	private String						mFlashSetting			  = null;

	private ImageClicked				mCallback 				  = null;
	private RelativeLayout  			preview					  = null;
	private LayoutParams previewLayout			  = null;
	private PreviewCallbacks			mPreviewCallbacks;
	private int							mCurrentWidth;
	private int 						mCurrentHeight;
	/**
     * Interface to handle action when image is clicked.
     * Implement this method to handle any processing on image clicked action.
     * An file info object will be given, containing all the image path 
     *  
     * @author SHRISH
     *
     */
    public interface ImageClicked {
    	
    	/**
    	 * Called when camera has finished clicking image
    	 * @param pictureFile
    	 */    	
    	public void imageClicked(File pictureFile);
    	

    	public void flashSet(String mode);    	
    	
    	/**
    	 * Called when camera has decided if to show flip button or not
    	 */
    	public void hideFlipButton();
    	
    	/**
    	 * Called when camera detects there is no flash
    	 */
    	public void enableFlashButton(boolean flag);
    	
    	/**
    	 * Called when camera is unavailable
    	 */
    	public void CameraUnAvailable();


    }	

    public interface cameraSetupImage{
		public void cameraData(byte[] data);
	}

    
    /**
     * Interface to handle surface related callbacks
     * 
     * @author SHRISH
     *
     */
    public interface PreviewCallbacks{
    	
    	public void surfaceCreated();
    	
    	public void surfaceChanged(int width, int height);
    	
    	public void surfaceDestroyed();
    	
    	public void stopCameraPreview();
    	
    	public void startCameraPreview();
    	
    	public boolean isPreviewRunning();
    	    	
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


	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		//       mtx.postRotate(degree);
		mtx.setRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	/**
	 * Picture callback to the camera
	 */
    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFile1(mApplication);
//			mCamera.startPreview();
			Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
//            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);



            if (pictureFile == null){
				safeToTakePicture = true;
                return;
            }            
            
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);

				/*ExifInterface exif=new ExifInterface(pictureFile.toString());

                Log.d("EXIF_value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
				if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
					realImage= rotate(realImage, 90);
				} else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
					realImage= rotate(realImage, 270);
				} else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
					realImage= rotate(realImage, 180);
				} else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
					realImage= rotate(realImage, 90);
				}*/

				realImage = mark(realImage, shipmentNo);
//				realImage = Bitmap.createScaledBitmap(realImage, 1000, 1000, true);
				realImage = scaleBitmap(realImage);
				realImage.compress(Bitmap.CompressFormat.PNG, 80, fos);
                fos.write(data);
                fos.close();                                 	   
         	   
//                restartPreviewAfterPictureClick();
                
                /*
                 * Make the callback to the calling activity to handle picture clicked
                 */
         	    mCallback.imageClicked(pictureFile);
         	    
            } catch (FileNotFoundException e) {
            	e.printStackTrace();            	
            } catch (IOException e) {
            	e.printStackTrace();
            }
			safeToTakePicture = true;
//			mCamera.startPreview();
			mCameraImg.cameraData(data) ;

        }
    };


	private Bitmap scaleBitmap(Bitmap bm) {
		int width = bm.getWidth();
		int height = bm.getHeight();
        int maxWidth = 1000;
		int maxHeight = 1000;

		Log.v("Pictures", "Width and height are " + width + "--" + height);

		if (width > height) {
			// landscape
			float ratio = (float) width / maxWidth;
			width = maxWidth;
			height = (int)(height / ratio);
		} else if (height > width) {
			// portrait
			float ratio = (float) height / maxHeight;
			height = maxHeight;
			width = (int)(width / ratio);
		} else {
			// square
			height = maxHeight;
			width = maxWidth;
		}

		Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

		bm = Bitmap.createScaledBitmap(bm, width, height, true);
		return bm;
	}


    public void setCameraDisplayOrientation(Activity activity) {

    	if(null == mCamera){
    		return;
    	 }
    	
    	   CameraInfo info =
    	       new CameraInfo();

    	   Camera.getCameraInfo(cameraId, info);

    	   int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    	   int degrees = 0;

    	   switch (rotation) {
    	       case Surface.ROTATION_0: degrees = 0; break;
    	       case Surface.ROTATION_90: degrees = 90; break;
    	       case Surface.ROTATION_180: degrees = 180; break;
    	       case Surface.ROTATION_270: degrees = 270; break;
    	   }

    	   
    	   if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
    		   orientation = (info.orientation + degrees) % 360;
    		   orientation = (360 - orientation) % 360;  // compensate the mirror
    	   } else {  // back-facing
    		   orientation = (info.orientation - degrees + 360) % 360;
    	   }
    	   
    	   if(null != mCamera){
    		   mCamera.setDisplayOrientation(orientation);
    	   }
    	   Log.v("orientation_display", String.valueOf(orientation));
    	}
    
	/**
	 * Constructor
	 * @param context
	 */
	public CameraUtils(Context context, ImageClicked callback, RelativeLayout previewLayout,cameraSetupImage cameraImg,String shipNo){
//	public CameraUtils(Context context, ImageClicked callback, RelativeLayout previewLayout){
		
		mApplication = context;
		mCallback = callback;
		preview = previewLayout;
		mCameraImg = cameraImg;
		shipmentNo = shipNo;
        mOrientationEventListener = new OrientationEventListener(mApplication, 
				SensorManager.SENSOR_DELAY_NORMAL) {

			@Override
			public void onOrientationChanged(int orientation) {
				
				if ((orientation == ORIENTATION_UNKNOWN) || (mCamera == null)) {
					return;
				}
				
				Parameters params 				= mCamera.getParameters();
				CameraInfo info = new CameraInfo();
				
				Camera.getCameraInfo(cameraId, info);
				
				orientation = (orientation + 45) / 90 * 90;
					     			     
				int rotation = 0;
				
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				}
				else {  
					/*
					 * back-facing camera
					 */
					rotation = (info.orientation + orientation) % 360;
				}
					     
				params.setRotation(rotation);
				
				if(null == mCamera) {
		        	return;
		        }
				
				mCamera.setParameters(params);
			}

        };
        
        mPreviewCallbacks = new PreviewCallbacks(){

			@Override
			public void surfaceCreated() {
				
			}

			@Override
			public void surfaceChanged(int width, int height) {
				mCurrentWidth = width;
				mCurrentHeight = height;
				safeToTakePicture = true;
			}

			@Override
			public void surfaceDestroyed() {
				
			}
        	
			@Override
			public void stopCameraPreview() {
				stopPreview();
				
			}

			@Override
			public void startCameraPreview() {
				startPreview();
				safeToTakePicture = true;
				
			}

			@Override
			public boolean isPreviewRunning() { 
				return mPreviewRunning;
			}
        	
        };
        
	}
    
	/**
	/**
	 * Creates the file/director for storing image in the appropriate folder
	 * 
	 * @param type - image type
	 * @return File of the picture
	 */
    @SuppressLint("SimpleDateFormat")
	private File getOutputMediaFile(int type){
    	
        File mediaStorageDir = getFileStorageDir(mApplication, "Layout_test");

        /*
         *  Create the storage directory if it does not exist
         */
        if (! mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        /*
         * Create a media file name
         */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp +".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }    	

	/*
	 * getFileStorageDir
	 * Required for initializing the ServerManager
	 */
    public static File getFileStorageDir(Context context, String name) {

		String state = Environment.getExternalStorageState();
		File file = null;

		if (Environment.MEDIA_MOUNTED.equals(state)){

			file = new File(Environment.getExternalStorageDirectory()+"/layout", name);
			if ((!file.mkdirs()) && (!file.isDirectory())){
					Log.v(LOGTAG, "Directory Creation Failed");
				return null;
			}

			Log.v(LOGTAG, "Directory Created = " + file.getAbsolutePath());
		}else{
			Log.v(LOGTAG, "External Storage Not Mounted! Problem!!!");
		}
		return file;
	}

	private File getOutputMediaFile1(Context context) {

		File mediaStorageDir = new File(mApplication.getFilesDir(), "DeliveryApp");

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
				+shipmentNo +"_IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

    /**
     * Releases the camera so that other applications can open
     */
    public void releaseCamera(){
    	
    	if(null == mCamera){
    		return;
    	}
    	
    	if(mOrientationEventListener.canDetectOrientation()){
    		mOrientationEventListener.disable();
    	}
    	
    	mPreview.resetCamera();
    	stopPreview();
    	mCamera.release();
		mCamera = null;
    	preview.removeView(mPreview);
    	mPreview = null;
    }
    
    public void stopPreview(){
    	if((null != mCamera)&&(mPreviewRunning)){
		 try {
    		mCamera.stopPreview();
		 } catch (Exception e) {
 			Log.d(LOGTAG, "unable to stop preview");
 			e.printStackTrace();
	 			return;
 		}	
     		
			 mPreviewRunning = false;
    	}
    }
    
    public void startPreview(){
    	if((null != mCamera)&&(!mPreviewRunning)){
    	    try {
    	    	mCamera.startPreview();	
    		} catch (Exception e) {
    			Log.d(LOGTAG, "unable to start preview");
    			e.printStackTrace();
    			return;
    		}	
    		
    	    mPreviewRunning = true;
    		
    	}
    }
    
    /**
     * Sets the preview layout params for the preview surface
     * @param lParams
     */
    public void setPreviewLayoutParams(LayoutParams lParams){
    	previewLayout = lParams;
    	if(null != mPreview){
    		mPreview.setLayoutParams(lParams);
    	}
    }
    
    /**
     * Resets the camera and adjusts the preview margins
     */
    public void resetCamera(){
    	
        /*
         * Create an instance of Camera	
         */
        mCamera = getCameraInstance();
        if(null == mCamera) {
        	mCallback.CameraUnAvailable();
        	return;
        }
        	    
	    /*
	     * start orientation listener 
	     */		
		if(mOrientationEventListener.canDetectOrientation()){			
			mOrientationEventListener.enable();
		}
	    
		/**
		 * Get the highest aspect ratio of the picture supported
		 */
		double ar = getHighestAspectRatio();
        
        /*
         * Create our Preview view and set it as the content of our activity.
         */
        mPreview = new CameraPreview(mApplication, mCamera, mPreviewCallbacks, ar); 
        preview.addView(mPreview);
        mPreview.setZOrderOnTop(true);
        mPreview.setZOrderMediaOverlay(true);
        initCameraParams();
                    
    }
    
    private void initCameraParams(){
        if(null != previewLayout){
        	mPreview.setLayoutParams(previewLayout);
        }
        
        if(null != mFlashSetting){
        	setFlashParams(mFlashSetting);
        }
        
        if(null == mCamera) {
        	return;
         }
        
        if(-1 != orientation){
        	mCamera.setDisplayOrientation(orientation);
        }
        
        setCamFocusMode();

    }
    
    private void setCamFocusMode(){
    	
    	if(null == mCamera) {
        	return;
         }
    	
		/* Set Auto focus */ 
        Parameters parameters = mCamera.getParameters();
		List<String>	focusModes = parameters.getSupportedFocusModes();
		if(focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		} else 
		if(focusModes.contains(Parameters.FOCUS_MODE_AUTO)){
			parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
		}	
		
		mCamera.setParameters(parameters);
    }
    
    /**
	 * A safe way to get an instance of the Camera object
	 * @return
	 */
	private Camera getCameraInstance(){
	        
	    Camera c = null;
	    try {
	        c = Camera.open(cameraId);
	    }
	    catch (Exception e){
	        e.printStackTrace();
	    }
	    
	    return c;
	}
	
	/**
	 * Restarts the camera after the picture has been clicked
	 */
	public void restartPreviewAfterPictureClick() {
		
		if(null == mCamera) {
        	return;
         }
		
	    try {
	    	mCamera.startPreview();	
	    	mPreviewRunning = true;
		} catch (Exception e) {
			Log.d(LOGTAG, "unable to stop preview");
			e.printStackTrace();
		}		
	    
	    setCamFocusMode();
	    
	}  
	
	/**
	 * Method to flip camera, based on the current camera orientation
	 */
	public void flipCamera(){
		
		if(null == mCamera) {
        	return;
         }
		
		switch (cameraId) {
		case CameraInfo.CAMERA_FACING_BACK:
			cameraId	= CameraInfo.CAMERA_FACING_FRONT;
			break;

		default:
			cameraId	= CameraInfo.CAMERA_FACING_BACK;
			break;
		}
		
		stopPreview();		
		mCamera.release();	
		

		mPreview.resetCamera();
		mCamera = null;
		if(mOrientationEventListener.canDetectOrientation()){
			mOrientationEventListener.disable();
		}

		mCamera = Camera.open(cameraId);
		mPreview.setCamera(mCamera);
		
		if(mOrientationEventListener.canDetectOrientation()){			
			mOrientationEventListener.enable();
		}
		
		try {
			mCamera.setPreviewDisplay(mPreview.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}

		double ar = getHighestAspectRatio();
		mPreview.setAspectRatio(ar);
		
		initCameraParams();

		mPreview.calculateImageSize(mCurrentWidth, mCurrentHeight);
		
		startPreview();
		
	}
	
	/**
	 * Set the flash mode for camera
	 */
	public void toggleFlashSettings(){
	
		if(null == mCamera){
			return;
		}
		
		Parameters	params		= mCamera.getParameters();
		List<String>		flashModes 	= params.getSupportedFlashModes();
		
		if(flashModes == null){	
			mCallback.enableFlashButton(false);
			return;
		}else{
			mCallback.enableFlashButton(true);
		}
		
		Log.d(LOGTAG, " mFlashSetting mode = "+mFlashSetting);
		
		if((Parameters.FLASH_MODE_AUTO.equals(mFlashSetting)) &&
				(flashModes.contains(Parameters.FLASH_MODE_ON))) {
			setFlashParams(Parameters.FLASH_MODE_ON);
												
		} else if ((Parameters.FLASH_MODE_ON.equals(mFlashSetting)) &&
				(flashModes.contains(Parameters.FLASH_MODE_OFF))) {
			setFlashParams(Parameters.FLASH_MODE_OFF);
		
		} else if (flashModes.contains(Parameters.FLASH_MODE_AUTO)){
			setFlashParams(Parameters.FLASH_MODE_AUTO);
		}			
				
	}
	
	/**
	 * Returns the current flash setting of the device
	 * @return
	 */
	public String getFlashMode(){
		if(null == mCamera){
			return null;
		}
		
		mFlashSetting = mCamera.getParameters().getFlashMode();
		
		return mFlashSetting;
	}
	
	/**
	 * Sets the flash mode of the camera. this is a internal method only
	 * @return
	 */
	public void setFlashParams(String flash){
		
		if(null == mCamera){
			return;
		}
		
		Parameters	params		= mCamera.getParameters();
		List<String>		flashModes 	= params.getSupportedFlashModes();
		
		if(flashModes == null){	
			mCallback.enableFlashButton(false);
			return;
		}else{
			mCallback.enableFlashButton(true);
		}
		
		if(flashModes.contains(flash)){		
			mFlashSetting = flash;
			params.setFlashMode(flash);
			mCallback.flashSet(flash);
			mCamera.setParameters(params);
			
			Log.d(LOGTAG, " new flash mode = "+flash);
			
		}else{
			Log.e(LOGTAG, " INVALID FLASH MODE");
		}
	}
	
	/**
	 * Checks the number of cameras available and handles the flip visibility
	 */
	public void handleFlipVisibility() {
        if(Camera.getNumberOfCameras() == 1){
        	mCallback.hideFlipButton();
        }

	}
	
	/**
	 * Method to take a picture. The imageClicked callback will be called 
	 */
	public void clickPicture(){
//		mCamera.startPreview();
			if((null == mCamera)||(!mPreviewRunning)) {
        	return;
         }else{
				if (safeToTakePicture) {
					mCamera.takePicture(null, null, mPicture);
					safeToTakePicture = false;
				}
			}


//			mCamera.takePicture(null, null, mPicture);


		                
	}	


	public boolean isPreviewValid(){
		if(null == mPreview){
			return false;
		}		
		return true;
	}

	public void setPreviewVisible(){
		if(isPreviewValid()){
			mPreview.setVisibility(View.VISIBLE);
		}
	}
	
	public void setPreviewInvisible(){
		if(isPreviewValid()){
			mPreview.setVisibility(View.GONE);
		}
	}

	public CameraAspectRatio getHighestPreviewSize(double ar){
		CameraAspectRatio 		ratio = new CameraAspectRatio();
		
		if(null == mCamera) {
        	return ratio;
        }
		Parameters 				parameters 		= mCamera.getParameters();
		List<Camera.Size> 		listPreviewSize = parameters.getSupportedPreviewSizes();
		ArrayList<Camera.Size>	temp			= new ArrayList<Camera.Size>();
		
		for(Camera.Size size : listPreviewSize){
			Double 	mPrevRatio 		= (double) size.width / (double)size.height;  
			
			if(mPrevRatio.equals(ar)){
				if(size.width > ratio.width){
					ratio.width = size.width;
					ratio.height = size.height;
					ratio.ratio = ar;
				}
			}
		}
		
		return ratio;
		
	}	
	
	/**
	 * Gets the highest aspect ratio for the image supported by the device
	 * This will determine the preview aspect ratio
	 * 
	 * @return
	 */
	public double getHighestAspectRatio(){
		
		//CameraAspectRatio 		ratio = new CameraAspectRatio();
		double picratio = 0;
		double prevratio = 0;
		double finalratio = ((double)16/(double)9);
		
		if(null == mCamera) {
        	return finalratio;
        }
				
		Parameters 				parameters 		= mCamera.getParameters();
	    List<Camera.Size> 		listPreviewSize = parameters.getSupportedPreviewSizes();
	    List<Camera.Size> 		listPicureSize = parameters.getSupportedPictureSizes();
    	Double					highestPicRatio    = 0.0;
    	Double					highestPrevRatio    = 0.0;
	    
    	
        for (Camera.Size size : listPicureSize) {	                	
            
        	double 	mPicRatio 		= (double) size.width / (double)size.height;
        	
        	if(mPicRatio > highestPicRatio){
            	highestPicRatio = mPicRatio;
            	picratio = highestPicRatio;
            }
        }
        
        if(picratio > ((double)16/(double)9)){
        	picratio = ((double)16/(double)9);
        }
        
        for(Camera.Size size : listPreviewSize){
        
        	double 	mPrevRatio 		= (double) size.width / (double)size.height;        	
        	
        	if(mPrevRatio > highestPrevRatio){
        		highestPrevRatio = mPrevRatio;
        		prevratio = highestPrevRatio;
        	}
        }
        
        if(prevratio > ((double)16/(double)9)){
        	prevratio = ((double)16/(double)9);
        }
        
        if(prevratio > picratio){
        	finalratio = picratio;
        }else{
        	finalratio = prevratio;
        }
        
        return finalratio;
	}
    
	/**
	 * Class to hold aspect ratio, width and height
	 * 
	 * @author SHRISH
	 *
	 */
	public class CameraAspectRatio{
		int width;
		int height;
		double ratio;
		
		public CameraAspectRatio(){
			width = 16;
			height = 9;
			ratio = ((double)16/(double)9);
		}
	}
	
}
