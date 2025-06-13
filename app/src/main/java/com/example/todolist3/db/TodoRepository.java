package com.example.todolist3.db;

import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoRepository {
    private TodoDBHelper dbHelper;

    public TodoRepository(Context context) {
        dbHelper = new TodoDBHelper(context);
    }

    // 할 일 추가
    public long addTask(String content, String dueDate) {
        String currentDateTime = getCurrentDateTime();
        String sql = "INSERT INTO " + TodoDBHelper.TABLE_TASKS +
                " (" + TodoDBHelper.COLUMN_CONTENT + ", " +
                TodoDBHelper.COLUMN_CREATED_AT + ", " +
                TodoDBHelper.COLUMN_DUE_DATE + ", " +
                TodoDBHelper.COLUMN_IS_COMPLETED + ") VALUES (?, ?, ?, 0)";

        dbHelper.executeSQL(sql, new Object[]{content, currentDateTime, dueDate});

        // 방금 추가된 항목의 ID 조회
        Cursor cursor = dbHelper.rawQuery("SELECT last_insert_rowid() as id", null);
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();

        return id;
    }

    // 현재 시간 가져오기
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // 할 일 목록 조회
    public List<Task> getAllTasksByCreatedAt() {
        List<Task> taskList = new ArrayList<>();
        String sql = "SELECT * FROM " + TodoDBHelper.TABLE_TASKS +
                " ORDER BY " + TodoDBHelper.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = dbHelper.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_ID));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_CONTENT));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_CREATED_AT));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_DUE_DATE));
            boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_IS_COMPLETED)) == 1;

            Task task = new Task(id, content, createdAt, dueDate, isCompleted);
            taskList.add(task);
        }

        cursor.close();
        return taskList;
    }

    // 할 일 데이터 객체
    public static class Task {
        private int id;
        private String content;
        private String createdAt;
        private String dueDate;
        private boolean isCompleted;

        public Task(int id, String content, String createdAt, String dueDate, boolean isCompleted) {
            this.id = id;
            this.content = content;
            this.createdAt = createdAt;
            this.dueDate = dueDate;
            this.isCompleted = isCompleted;
        }

        // getter 메소드들
        public int getId() { return id; }
        public String getContent() { return content; }
        public String getCreatedAt() { return createdAt; }
        public String getDueDate() { return dueDate; }
        public boolean isCompleted() { return isCompleted; }
    }
}