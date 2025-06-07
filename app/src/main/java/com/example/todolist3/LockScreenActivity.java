package com.example.todolist3;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;

public class LockScreenActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면이 항상 위에 있도록 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        // 아주 간단한 레이아웃
        setContentView(R.layout.lock_screen);
    }
}

