package com.example.mobile_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobile_project.model.User;

/**
 * User 테이블 Data Access Object
 * 회원가입, 로그인, 사용자 정보 관리
 */
public class UserDAO {

    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * 회원가입 - 재학생
     */
    public long registerStudent(String email, String password, String name, String phone,
                                 String studentId, String department, int grade) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);
        values.put(DatabaseHelper.COLUMN_USER_TYPE, "student");
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_STUDENT_ID, studentId);
        values.put(DatabaseHelper.COLUMN_DEPARTMENT, department);
        values.put(DatabaseHelper.COLUMN_GRADE, grade);

        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        return result;
    }

    /**
     * 회원가입 - 고용주
     */
    public long registerEmployer(String email, String password, String name, String phone,
                                  String businessName, String businessNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);
        values.put(DatabaseHelper.COLUMN_USER_TYPE, "employer");
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_BUSINESS_NAME, businessName);
        values.put(DatabaseHelper.COLUMN_BUSINESS_NUMBER, businessNumber);

        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        return result;
    }

    /**
     * 로그인
     */
    public User login(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
            cursor.close();
        }

        return user;
    }

    /**
     * 이메일로 사용자 조회
     */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
            cursor.close();
        }

        return user;
    }

    /**
     * ID로 사용자 조회
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
            cursor.close();
        }

        return user;
    }

    /**
     * 이메일 중복 체크
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_ID},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    /**
     * 사용자 정보 수정
     */
    public int updateUser(int userId, String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        return db.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
    }

    /**
     * 비밀번호 변경
     */
    public int updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PASSWORD, newPassword);

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        return db.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
    }

    /**
     * 사용자 삭제
     */
    public int deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};

        return db.delete(DatabaseHelper.TABLE_USERS, whereClause, whereArgs);
    }

    /**
     * Cursor에서 User 객체 생성
     */
    private User getUserFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD));
        String userType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_TYPE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));

        String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DEPARTMENT));
        int grade = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GRADE));

        String businessName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUSINESS_NAME));
        String businessNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUSINESS_NUMBER));

        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT));
        String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UPDATED_AT));

        return new User(id, email, password, userType, name, phone,
                studentId, department, grade,
                businessName, businessNumber,
                createdAt, updatedAt);
    }
}