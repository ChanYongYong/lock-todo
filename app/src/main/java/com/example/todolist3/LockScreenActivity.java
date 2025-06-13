package com.example.todolist3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.todolist3.adapter.EditTextPagerAdapter;
import com.example.todolist3.db.TodoRepository;

import java.util.List;

public class LockScreenActivity extends Activity implements EditTextPagerAdapter.OnPageContentChangeListener {
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

        // 네비게이션 바 숨기기
        hideSystemUI();
        // ViewPager2, repository 초기화
        viewPager = findViewById(R.id.lock_screen_viewpager);

        // 어댑터 설정 (페이지 수는 원하는대로 조정)
        refreshTaskList();

        findViewById(R.id.lock_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);

                // 액티비티 종료
                finish();
            }
        });
    }

    // 3. 시스템 UI(상태바, 네비게이션 바) 숨기기
    private void hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    // 앱 전환 방지 및 시스템 UI 계속 숨김 유지
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public void onPageContentChanged(int position, boolean isEmpty) {
        // 페이지 내용 변경 시 필요한 작업
    }

    @Override
    public void onPageFull(int position) {
        // 이 메서드는 더 이상 사용하지 않음 (페이지가 가득 찼을 때 자동으로 추가하지 않음)
    }

    @Override
    public void onTaskSaved(int position) {
        // 할 일 저장 시 호출됨
        // 해당 페이지가 마지막 페이지이고 모든 입력란이 채워졌는지 확인
        if (position == adapter.getItemCount() - 1 && adapter.isPageFull(position)) {
            // 새 페이지를 추가
            int currentCount = adapter.getItemCount();

            // 기존 할 일 목록 가져오기
            TodoRepository todoRepository = new TodoRepository(this);
            List<TodoRepository.Task> incompleteTasks = todoRepository.getIncompleteTasks(100);

            // 새 어댑터 생성 (페이지 1개 추가)
            EditTextPagerAdapter newAdapter = new EditTextPagerAdapter(currentCount + 1);
            newAdapter.setExistingTasks(incompleteTasks);
            newAdapter.setOnPageContentChangeListener(this);

            // 새 어댑터로 교체
            viewPager.setAdapter(newAdapter);
            adapter = newAdapter;

            // 현재 페이지 유지
            viewPager.setCurrentItem(position, false);
        }
    }

    public void refreshTaskList() {
        // 현재 페이지 위치 저장
        int currentPage = 0;
        if (viewPager != null) {
            currentPage = viewPager.getCurrentItem();
        }

        // TodoRepository를 사용하여 미완료된 할 일 항목 가져오기
        TodoRepository todoRepository = new TodoRepository(this);
        List<TodoRepository.Task> incompleteTasks = todoRepository.getIncompleteTasks(100);

        // 미완료된 할 일 항목 수에 따라 페이지 수 계산
        int taskCount = incompleteTasks.size();

        // 페이지당 5개씩 표시, 마지막 페이지가 꽉 차있으면 빈 페이지 추가
        int fullPagesNeeded = taskCount / 5;
        int remainingItems = taskCount % 5;

        // 꽉 찬 페이지가 있고 남은 항목이 없으면 새 페이지 추가
        int newPageCount = (remainingItems == 0 && taskCount > 0) ?
                fullPagesNeeded + 1 :
                Math.max(1, (int) Math.ceil(taskCount / 5.0));

        // 어댑터가 이미 존재하는지 확인
        boolean isFirstLoad = (adapter == null);

        // 새로운 어댑터 생성 및 설정
        adapter = new EditTextPagerAdapter(newPageCount);
        adapter.setExistingTasks(incompleteTasks);

        // 리스너로 현재 액티비티 설정
        adapter.setOnPageContentChangeListener(this);

        // 어댑터 설정 및 적용
        viewPager.setAdapter(adapter);

        // 페이지 위치 복원 (첫 로드가 아닌 경우만)
        if (!isFirstLoad) {
            // 유효한 페이지 범위 내에서만 이동
            if (currentPage < newPageCount) {
                viewPager.setCurrentItem(currentPage, false);
            } else if (newPageCount > 0) {
                // 현재 페이지가 범위를 벗어났을 경우 마지막 페이지로
                viewPager.setCurrentItem(newPageCount - 1, false);
            }
        }
    }
}