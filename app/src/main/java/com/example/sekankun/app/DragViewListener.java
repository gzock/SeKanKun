package com.example.sekankun.app;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Gzock on 2014/04/10.
 */
public class DragViewListener implements View.OnTouchListener {
    // ドラッグ対象のView
    private LinearLayout dragView;
    // ドラッグ中に移動量を取得するための変数
    private int oldx;
    private int oldy;

    public DragViewListener(LinearLayout dragView) {
        this.dragView = dragView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // タッチしている位置取得
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 今回イベントでのView移動先の位置
                int left = dragView.getLeft() + (x - oldx);
                int top = dragView.getTop() + (y - oldy);
                // Viewを移動する
                dragView.layout(left, top, left + dragView.getWidth(), top
                        + dragView.getHeight());
                break;
        }

        // 今回のタッチ位置を保持
        oldx = x;
        oldy = y;
        // イベント処理完了
        return true;
    }
}
