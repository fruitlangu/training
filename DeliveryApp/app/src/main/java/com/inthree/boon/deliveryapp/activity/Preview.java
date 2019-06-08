package com.inthree.boon.deliveryapp.activity;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    private boolean previewIsRunning;
    private Camera camera;
    private SurfaceHolder mSurfaceHolder;

    public Preview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        // ...
        // but do not start the preview here!
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // set preview size etc here ... then
        myStartPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        myStopPreview();
        camera.release();
        camera = null;
    }

    // safe call to start the preview
    // if this is called in onResume, the surface might not have been created yet
    // so check that the camera has been set up too.
    public void myStartPreview() {
        if (!previewIsRunning && (camera != null)) {
            camera.startPreview();
            previewIsRunning = true;
        }
    }

    // same for stopping the preview
    public void myStopPreview() {
        if (previewIsRunning && (camera != null)) {
            camera.stopPreview();
            previewIsRunning = false;
        }
    }
}
