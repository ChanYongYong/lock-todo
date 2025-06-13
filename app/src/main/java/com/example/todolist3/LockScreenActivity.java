package com.example.todolist3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.todolist3.adapter.EditTextPagerAdapter;

public class LockScreenActivity extends Activity {
    private ViewPager2 viewPager;
    private EditTextPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면이 항상 위에 있도록 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.lock_screen);

        // ViewPager2 초기화
        viewPager = findViewById(R.id.lock_screen_viewpager);

        // 어댑터 설정 (페이지 수는 원하는대로 조정)
        adapter = new EditTextPagerAdapter(1); // 3페이지로 설정
        viewPager.setAdapter(adapter);

        // 페이지 내용 변경 감지
        adapter.setOnPageContentChangeListener(new EditTextPagerAdapter.OnPageContentChangeListener() {
            @Override
            public void onPageContentChanged(int position, boolean isEmpty) {
                // 필요한 경우 페이지 내용 변경 시 로직 추가
            }
        });

        // 잠금화면을 터치하면 액티비티만 종료하고 홈 화면으로 이동
        findViewById(R.id.lock_screen_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 화면으로 이동하는 인텐트
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);

                // 액티비티 종료
                finish();
            }
        });
    }


}