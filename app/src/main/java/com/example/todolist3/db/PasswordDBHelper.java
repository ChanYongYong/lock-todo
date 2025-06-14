package com.example.todolist3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PasswordDBHelper extends SQLiteOpenHelper {
    // 데이터베이스 정보
    private static final String DATABASE_NAME = "password_database";
    private static final int DATABASE_VERSION = 1;

    // 테이블 이름
    public static final String TABLE_PASSWORDS = "passwords";

    // 컬럼 이름
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_CREATED_AT = "created_at";

    // 테이블 생성 쿼리
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_PASSWORDS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_CREATED_AT + " TEXT NOT NULL);";

    // 테이블 삭제 쿼리
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_PASSWORDS;

    public PasswordDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}