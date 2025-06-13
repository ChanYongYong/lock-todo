package com.example.todolist3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoRepository {
    private TodoDBHelper dbHelper;
    private SQLiteDatabase database;

    public TodoRepository(Context context) {
        dbHelper = new TodoDBHelper(context);
    }
    public int completeTaskByContent(String content) {
        open();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_IS_COMPLETED, 1);

        int count = database.update(TodoDBHelper.TABLE_TASKS, values,
                TodoDBHelper.COLUMN_CONTENT + " = ? AND " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 0",
                new String[]{content});
        close();
        return count;
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

    // 할 일 추가
    public long addTask(String content, String dueDate) {
        open();

        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_CONTENT, content);

        // 현재 시간을 생성 시간으로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(new Date());
        values.put(TodoDBHelper.COLUMN_CREATED_AT, currentTime);

        // 기한 날짜 설정 (null 체크)
        if (dueDate != null && !dueDate.isEmpty() && !dueDate.equals("날짜")) {
            values.put(TodoDBHelper.COLUMN_DUE_DATE, dueDate);
        }

        // 기본적으로 미완료 상태(0)로 저장
        values.put(TodoDBHelper.COLUMN_IS_COMPLETED, 0);

        long id = database.insert(TodoDBHelper.TABLE_TASKS, null, values);
        close();
        return id;
    }

    // 할 일 완료 처리
    public int completeTask(long taskId) {
        open();
        ContentValues values = new ContentValues();
        values.put(TodoDBHelper.COLUMN_IS_COMPLETED, 1);

        int count = database.update(TodoDBHelper.TABLE_TASKS, values,
                TodoDBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)});
        close();
        return count;
    }

    // 미완료된 할 일 개수 조회
    public int getIncompleteTaskCount() {
        open();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM " + TodoDBHelper.TABLE_TASKS +
                        " WHERE " + TodoDBHelper.COLUMN_IS_COMPLETED + " = 0",
                null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        close();
        return count;
    }

    // 미완료된 할 일 목록 조회 (최대 5*페이지수 만큼)
    public List<Task> getIncompleteTasks(int limit) {
        open();
        List<Task> taskList = new ArrayList<>();

        Cursor cursor = database.query(
                TodoDBHelper.TABLE_TASKS,
                null,
                TodoDBHelper.COLUMN_IS_COMPLETED + " = 0",
                null,
                null,
                null,
                TodoDBHelper.COLUMN_DUE_DATE + " ASC, " + TodoDBHelper.COLUMN_CREATED_AT + " ASC",
                String.valueOf(limit)
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_ID));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_CONTENT));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_CREATED_AT));

            // due_date는 NULL일 수 있으므로 체크
            String dueDate = null;
            int dueDateColumnIndex = cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_DUE_DATE);
            if (!cursor.isNull(dueDateColumnIndex)) {
                dueDate = cursor.getString(dueDateColumnIndex);
            }

            int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(TodoDBHelper.COLUMN_IS_COMPLETED));

            Task task = new Task(id, content, createdAt, dueDate, isCompleted == 1);
            taskList.add(task);
        }

        cursor.close();
        close();
        return taskList;
    }

    // 할 일 데이터 클래스
    public static class Task {
        private long id;
        private String content;
        private String createdAt;
        private String dueDate;
        private boolean isCompleted;

        public Task(long id, String content, String createdAt, String dueDate, boolean isCompleted) {
            this.id = id;
            this.content = content;
            this.createdAt = createdAt;
            this.dueDate = dueDate;
            this.isCompleted = isCompleted;
        }

        public long getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getDueDate() {
            return dueDate;
        }

        public boolean isCompleted() {
            return isCompleted;
        }
    }
}