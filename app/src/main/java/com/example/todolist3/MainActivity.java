package com.example.todolist3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist3.db.PasswordRepository;

public class MainActivity extends AppCompatActivity {
    private PasswordRepository passwordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Android 13 이상에서 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // SYSTEM_ALERT_WINDOW(다른 앱 위에 표시) 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        }

        // 잠금화면 서비스 시작
        android.content.Intent serviceIntent = new android.content.Intent(this, LockScreenService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // 비밀번호 저장소 초기화
        passwordRepository = new PasswordRepository(this);

        // 버튼 리스너 설정
        Button btnChangePattern = findViewById(R.id.btn_change_pattern);
        // 비밀번호 설정 버튼
        btnChangePattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordRepository.hasPassword()) {
                    // 기존 비밀번호가 있으면 확인 후 변경
                    verifyPasswordThenChange();
                } else {
                    // 비밀번호가 없으면 새로 생성
                    showNewPasswordDialog();
                }
            }
        });
    }

    // 기존 비밀번호 확인 후 변경하는 대화상자
    private void verifyPasswordThenChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);

        final EditText passwordInput = dialogView.findViewById(R.id.password_input);

        builder.setView(dialogView)
                .setTitle("현재 비밀번호 입력")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputPassword = passwordInput.getText().toString();

                        if (passwordRepository.verifyPassword(inputPassword)) {
                            showNewPasswordDialog();
                        } else {
                            Toast.makeText(MainActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 새 비밀번호 입력 대화상자
    private void showNewPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);

        final EditText passwordInput = dialogView.findViewById(R.id.password_input);

        builder.setView(dialogView)
                .setTitle("새 비밀번호 입력 (4자리)")
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = passwordInput.getText().toString();

                        if (newPassword.length() == 4) {
                            long result = passwordRepository.setPassword(newPassword);
                            if (result != -1) {
                                Toast.makeText(MainActivity.this,
                                        "비밀번호가 설정되었습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "비밀번호 설정에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "비밀번호는 4자리여야 합니다", Toast.LENGTH_SHORT).show();
                            showNewPasswordDialog(); // 다시 대화상자 표시
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}