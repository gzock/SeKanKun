package com.example.sekankun.app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import java.io.IOException;

/**
 * Created by Gzock on 14/02/19.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera mCam;

    /**
     * コンストラクタ
     */
    public CameraPreview(Context context, Camera cam) {
        super(context);

        mCam = cam;

        // サーフェスホルダーの取得とコールバック通知先の設定
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * SurfaceView 生成
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // カメラインスタンスに、画像表示先を設定
            mCam.setPreviewDisplay(holder);
            // プレビュー開始
            mCam.startPreview();
        } catch (IOException e) {
            //
        }
    }

    /**
     * SurfaceView 破棄
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * SurfaceHolder が変化したときのイベント
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 画面回転に対応する場合は、ここでプレビューを停止し、
        // 回転による処理を実施、再度プレビューを開始する。
    }
}
