package com.example.todolist3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDBHelper extends SQLiteOpenHelper {
    // 데이터베이스 정보
    private static final String DATABASE_NAME = "todo_database";
    private static final int DATABASE_VERSION = 1;

    // 테이블 이름
    public static final String TABLE_TASKS = "tasks";

    // 컬럼 이름
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_IS_COMPLETED = "is_completed";

    // 테이블 생성 쿼리
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COLUMN_CONTENT + " TEXT NOT NULL, " +
            COLUMN_CREATED_AT + " TEXT NOT NULL, " +
            COLUMN_DUE_DATE + " TEXT, " +
            COLUMN_IS_COMPLETED + " INTEGER NOT NULL DEFAULT 0);";

    // 테이블 삭제 쿼리
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_TASKS;

    public TodoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 업그레이드 시 테이블을 삭제하고 다시 생성
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}