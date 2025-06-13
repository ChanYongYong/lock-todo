package com.example.todolist3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDBHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름과 버전 정의
    private static final String DATABASE_NAME = "todolistDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_IS_COMPLETED = "is_completed";

    // 테이블 생성 SQL 문
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_CONTENT + " TEXT NOT NULL, " +
                    COLUMN_CREATED_AT + " TEXT NOT NULL, " +
                    COLUMN_DUE_DATE + " TEXT, " +
                    COLUMN_IS_COMPLETED + " INTEGER NOT NULL DEFAULT 0);";

    // 생성자 추가
    public TodoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 데이터베이스가 처음 생성될 때 호출
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // 데이터베이스 업그레이드 시 호출(필수 메서드)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 간단한 업그레이드 로직: 테이블 삭제 후 재생성
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
    // 데이터 쓰기 작업을 수행하는 메서드
    public void executeSQL(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(sql);
        } finally {
            db.close();
        }
    }

    // 데이터 쓰기 작업을 수행하는 메서드 (매개변수 포함)
    public void executeSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(sql, bindArgs);
        } finally {
            db.close();
        }
    }

    // 데이터 조회 작업을 수행하는 메서드
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, selectionArgs);
        // 주의: 이 메서드는 db를 닫지 않으므로, Cursor 사용이 끝난 후 반드시 close() 해야 함
    }

}