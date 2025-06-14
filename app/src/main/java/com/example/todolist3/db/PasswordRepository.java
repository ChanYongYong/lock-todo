package com.example.todolist3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PasswordRepository {
    private PasswordDBHelper dbHelper;
    private SQLiteDatabase database;

    public PasswordRepository(Context context) {
        dbHelper = new PasswordDBHelper(context);
    }

    // 데이터베이스 열기
    private void open() {
        database = dbHelper.getWritableDatabase();
    }

    // 데이터베이스 닫기
    private void close() {
        if (database != null) {
            database.close();
        }
    }

    // 비밀번호 설정/변경하기
    public long setPassword(String password) {
        open();

        // 기존 비밀번호 모두 삭제
        database.delete(PasswordDBHelper.TABLE_PASSWORDS, null, null);

        ContentValues values = new ContentValues();
        values.put(PasswordDBHelper.COLUMN_PASSWORD, password);

        // 현재 시간을 생성 시간으로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(new Date());
        values.put(PasswordDBHelper.COLUMN_CREATED_AT, currentTime);

        // 새 비밀번호 추가
        long id = database.insert(PasswordDBHelper.TABLE_PASSWORDS, null, values);
        close();
        return id;
    }

    // 비밀번호 확인하기
    public boolean verifyPassword(String inputPassword) {
        open();
        String currentPassword = getPassword();
        close();

        if (currentPassword == null) {
            // 비밀번호가 설정되어 있지 않으면 항상 true 반환
            return true;
        }

        return currentPassword.equals(inputPassword);
    }

    // 현재 저장된 비밀번호 가져오기
    public String getPassword() {
        open();
        String password = null;

        Cursor cursor = database.query(
                PasswordDBHelper.TABLE_PASSWORDS,
                new String[]{PasswordDBHelper.COLUMN_PASSWORD},
                null, null, null, null,
                PasswordDBHelper.COLUMN_ID + " DESC", "1");

        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndexOrThrow(PasswordDBHelper.COLUMN_PASSWORD));
        }

        cursor.close();
        close();
        return password;
    }

    // 비밀번호가 설정되어 있는지 확인
    public boolean hasPassword() {
        open();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM " + PasswordDBHelper.TABLE_PASSWORDS,
                null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        close();
        return count > 0;
    }
}