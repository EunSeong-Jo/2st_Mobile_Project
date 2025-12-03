package com.example.mobile_project.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite Database Helper
 * 데이터베이스 생성 및 버전 관리
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "albadae.db";
    private static final int DATABASE_VERSION = 1;

    // Singleton instance
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. users 테이블 생성
        db.execSQL(CREATE_TABLE_USERS);

        // 2. job_postings 테이블 생성
        db.execSQL(CREATE_TABLE_JOB_POSTINGS);

        // 3. applications 테이블 생성
        db.execSQL(CREATE_TABLE_APPLICATIONS);

        // 4. schedules 테이블 생성
        db.execSQL(CREATE_TABLE_SCHEDULES);

        // 5. reviews 테이블 생성
        db.execSQL(CREATE_TABLE_REVIEWS);

        // 6. notifications 테이블 생성
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);

        // 7. nearby_places 테이블 생성
        db.execSQL(CREATE_TABLE_NEARBY_PLACES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전 업그레이드 시 테이블 재생성
        db.execSQL("DROP TABLE IF EXISTS notifications");
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS schedules");
        db.execSQL("DROP TABLE IF EXISTS applications");
        db.execSQL("DROP TABLE IF EXISTS job_postings");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ==================== 테이블 생성 쿼리 ====================

    /**
     * 1. users 테이블
     * 재학생과 고용주 정보 저장
     */
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "user_type TEXT NOT NULL, " + // 'student' or 'employer'
                    "name TEXT NOT NULL, " +
                    "phone TEXT, " +
                    // 재학생 전용 필드
                    "student_id TEXT, " +
                    "department TEXT, " +
                    "grade INTEGER, " +
                    // 고용주 전용 필드
                    "business_name TEXT, " +
                    "business_number TEXT, " +
                    // 공통 필드
                    "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    /**
     * 2. job_postings 테이블
     * 채용공고 정보
     */
    private static final String CREATE_TABLE_JOB_POSTINGS =
            "CREATE TABLE job_postings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "employer_id INTEGER NOT NULL, " +
                    "company_name TEXT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "description TEXT, " +
                    "salary INTEGER NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "work_time TEXT NOT NULL, " +
                    "work_days TEXT, " + // 근무 요일
                    "requirements TEXT, " + // 우대사항
                    "status TEXT NOT NULL DEFAULT 'active', " + // 'active', 'closed'
                    "view_count INTEGER DEFAULT 0, " +
                    "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (employer_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

    /**
     * 3. applications 테이블
     * 지원 내역
     */
    private static final String CREATE_TABLE_APPLICATIONS =
            "CREATE TABLE applications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "job_posting_id INTEGER NOT NULL, " +
                    "student_id INTEGER NOT NULL, " +
                    "resume TEXT, " + // 간단한 이력서 내용
                    "cover_letter TEXT, " + // 지원 동기
                    "status TEXT NOT NULL DEFAULT 'pending', " + // 'pending', 'accepted', 'rejected'
                    "applied_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

    /**
     * 4. schedules 테이블
     * 시간표 (재학생 전용: 수업 + 알바 일정)
     */
    private static final String CREATE_TABLE_SCHEDULES =
            "CREATE TABLE schedules (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " + // 'class' or 'work'
                    "day_of_week INTEGER NOT NULL, " + // 0=일, 1=월, 2=화, ..., 6=토
                    "start_time TEXT NOT NULL, " + // HH:mm 형식
                    "end_time TEXT NOT NULL, " + // HH:mm 형식
                    "location TEXT, " +
                    "memo TEXT, " +
                    "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

    /**
     * 5. reviews 테이블
     * 리뷰 및 평점
     */
    private static final String CREATE_TABLE_REVIEWS =
            "CREATE TABLE reviews (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "job_posting_id INTEGER NOT NULL, " +
                    "student_id INTEGER NOT NULL, " +
                    "rating REAL NOT NULL, " + // 1.0 ~ 5.0
                    "comment TEXT, " +
                    "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

    /**
     * 6. notifications 테이블
     * 알림 (지원 결과, 학교 공지 등)
     */
    private static final String CREATE_TABLE_NOTIFICATIONS =
            "CREATE TABLE notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " + // 'application', 'school', 'general'
                    "is_read INTEGER NOT NULL DEFAULT 0, " + // 0=unread, 1=read
                    "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";

    /**
     * 7. nearby_places 테이블
     * 주변 음식점/카페 추천
     */
    private static final String CREATE_TABLE_NEARBY_PLACES =
            "CREATE TABLE nearby_places (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "category TEXT NOT NULL, " + // 'restaurant', 'cafe'
                    "address TEXT NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL, " +
                    "rating REAL DEFAULT 0.0, " + // 0.0 ~ 5.0
                    "distance INTEGER DEFAULT 0" + // 미터 단위
                    ")";

    // ==================== 테이블 이름 상수 ====================

    public static final String TABLE_USERS = "users";
    public static final String TABLE_JOB_POSTINGS = "job_postings";
    public static final String TABLE_APPLICATIONS = "applications";
    public static final String TABLE_SCHEDULES = "schedules";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_NEARBY_PLACES = "nearby_places";

    // ==================== 컬럼 이름 상수 ====================

    // users 테이블
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_TYPE = "user_type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_DEPARTMENT = "department";
    public static final String COLUMN_GRADE = "grade";
    public static final String COLUMN_BUSINESS_NAME = "business_name";
    public static final String COLUMN_BUSINESS_NUMBER = "business_number";

    // job_postings 테이블
    public static final String COLUMN_EMPLOYER_ID = "employer_id";
    public static final String COLUMN_COMPANY_NAME = "company_name";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SALARY = "salary";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_WORK_TIME = "work_time";
    public static final String COLUMN_WORK_DAYS = "work_days";
    public static final String COLUMN_REQUIREMENTS = "requirements";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_VIEW_COUNT = "view_count";

    // applications 테이블
    public static final String COLUMN_JOB_POSTING_ID = "job_posting_id";
    public static final String COLUMN_RESUME = "resume";
    public static final String COLUMN_COVER_LETTER = "cover_letter";
    public static final String COLUMN_APPLIED_AT = "applied_at";

    // schedules 테이블
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_MEMO = "memo";

    // reviews 테이블
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_COMMENT = "comment";

    // 공통 컬럼
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // ==================== 더미 데이터 추가 ====================

    /**
     * 더미 데이터 추가 (앱 첫 실행 시 한 번만 호출)
     */
    public void insertDummyData() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // 1. 회원 더미 데이터
            insertDummyUsers(db);

            // 2. 채용 공고 더미 데이터
            insertDummyJobPostings(db);

            // 3. 시간표 더미 데이터
            insertDummySchedules(db);

            // 4. 지원 내역 더미 데이터
            insertDummyApplications(db);

            // 5. 리뷰 더미 데이터
            insertDummyReviews(db);

            // 6. 주변 장소 더미 데이터
            insertDummyNearbyPlaces(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertDummyUsers(SQLiteDatabase db) {
        // 학생 계정 1개 (stu@dongyang.ac.kr)
        db.execSQL("INSERT OR IGNORE INTO users (email, password, user_type, name, phone, student_id, department, grade) " +
                "VALUES ('stu@dongyang.ac.kr', '1234', 'student', '김학생', '010-1111-2222', '20211234', '컴퓨터정보공학과', 2)");

        // 고용주 계정 1개 (emp@gmail.com)
        db.execSQL("INSERT OR IGNORE INTO users (email, password, user_type, name, phone, business_name, business_number) " +
                "VALUES ('emp@gmail.com', '1234', 'employer', '이사장', '010-9999-8888', '알바대 카페', '123-45-67890')");
    }

    private void insertDummyJobPostings(SQLiteDatabase db) {
        // 고용주 ID 동적으로 가져오기 (emp@gmail.com)

        // 공고 1 - 주말 카페 알바
        db.execSQL("INSERT INTO job_postings (employer_id, company_name, title, description, salary, location, work_time, work_days, requirements, status, view_count) " +
                "SELECT id, '알바대 카페', '주말 카페 알바 모집', " +
                "'밝고 친절한 학생 구합니다. 커피 제조 교육 가능하며, 친절한 분위기에서 근무 가능합니다.', " +
                "10000, '구로역 2번 출구', '09:00-18:00', '토,일', '카페 경험자 우대', 'active', 45 " +
                "FROM users WHERE email = 'emp@gmail.com'");

        // 공고 2 - 평일 야간 카페 알바
        db.execSQL("INSERT INTO job_postings (employer_id, company_name, title, description, salary, location, work_time, work_days, requirements, status, view_count) " +
                "SELECT id, '알바대 카페', '평일 야간 카페 아르바이트', " +
                "'야간 근무 가능한 성실한 학생 모집합니다. 카페 업무 전반을 담당하게 됩니다.', " +
                "11000, '동양미래대 정문 앞', '22:00-06:00', '월,화,수,목', '성실하고 책임감 있는 분', 'active', 67 " +
                "FROM users WHERE email = 'emp@gmail.com'");

        // 공고 3 - 점심시간 홀서빙
        db.execSQL("INSERT INTO job_postings (employer_id, company_name, title, description, salary, location, work_time, work_days, requirements, status, view_count) " +
                "SELECT id, '알바대 카페', '주중 점심시간 홀서빙', " +
                "'점심시간 홀서빙 및 주방 보조 업무입니다. 식사 제공됩니다.', " +
                "9860, '동양미래대 후문', '11:00-14:00', '월,화,수,목,금', '학교 근처 거주자 우대', 'active', 32 " +
                "FROM users WHERE email = 'emp@gmail.com'");

        // 공고 4 - 마감된 공고
        db.execSQL("INSERT INTO job_postings (employer_id, company_name, title, description, salary, location, work_time, work_days, requirements, status, view_count) " +
                "SELECT id, '알바대 카페', '평일 오전 카페 알바 (마감)', " +
                "'평일 오전 시간대 근무 가능한 분 모집했습니다. 현재 마감되었습니다.', " +
                "10000, '구로역 2번 출구', '06:00-12:00', '월,화,수,목,금', '없음', 'closed', 89 " +
                "FROM users WHERE email = 'emp@gmail.com'");

        // 공고 5 - 주말 주간 알바
        db.execSQL("INSERT INTO job_postings (employer_id, company_name, title, description, salary, location, work_time, work_days, requirements, status, view_count) " +
                "SELECT id, '알바대 카페', '주말 주간 카페 알바', " +
                "'주말 주간 시간대 근무 가능한 학생을 찾습니다.', " +
                "10500, '동양미래대 정문 앞', '10:00-18:00', '토,일', '책임감 있는 분', 'active', 23 " +
                "FROM users WHERE email = 'emp@gmail.com'");
    }

    private void insertDummySchedules(SQLiteDatabase db) {
        // 학생 1의 시간표 (ID: 1)

        // 월요일 수업
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '자료구조', 'class', 2, '09:00', '11:00', 'A동 301호', '중간고사 3월 15일')");

        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '데이터베이스', 'class', 2, '13:00', '15:00', 'B동 201호', '팀프로젝트 있음')");

        // 화요일 수업
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '모바일프로그래밍', 'class', 3, '10:00', '12:00', 'C동 401호', '안드로이드 스튜디오 필수')");

        // 수요일 수업
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '자료구조', 'class', 4, '09:00', '11:00', 'A동 301호', '')");

        // 목요일 수업
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '모바일프로그래밍', 'class', 5, '10:00', '12:00', 'C동 401호', '')");

        // 금요일 수업
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '데이터베이스', 'class', 6, '13:00', '15:00', 'B동 201호', '')");

        // 토요일 알바 (카페)
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '구로역 카페', 'work', 7, '09:00', '18:00', '구로역 2번 출구', '주휴수당 포함')");

        // 일요일 알바 (카페)
        db.execSQL("INSERT INTO schedules (student_id, title, type, day_of_week, start_time, end_time, location, memo) " +
                "VALUES (1, '구로역 카페', 'work', 1, '09:00', '18:00', '구로역 2번 출구', '')");
    }

    private void insertDummyApplications(SQLiteDatabase db) {
        // 학생 1 → 공고 1 (수락됨)
        db.execSQL("INSERT INTO applications (job_posting_id, student_id, resume, cover_letter, status) " +
                "VALUES (1, 1, '동양미래대학교 컴퓨터정보공학과 2학년 김학생입니다.', " +
                "'주말 근무 가능하며 카페 알바 경험이 있습니다. 성실하게 근무하겠습니다.', 'accepted')");

        // 학생 1 → 공고 2 (대기 중)
        db.execSQL("INSERT INTO applications (job_posting_id, student_id, resume, cover_letter, status) " +
                "VALUES (2, 1, '동양미래대학교 컴퓨터정보공학과 2학년 김학생입니다.', " +
                "'야간 근무 가능하며 책임감 있게 일하겠습니다.', 'pending')");

        // 학생 1 → 공고 3 (거절됨)
        db.execSQL("INSERT INTO applications (job_posting_id, student_id, resume, cover_letter, status) " +
                "VALUES (3, 1, '동양미래대학교 컴퓨터정보공학과 2학년 김학생입니다.', " +
                "'점심시간 근무 가능합니다. 학교에서 가까워 지각 없이 출근하겠습니다.', 'rejected')");

        // 학생 1 → 공고 5 (대기 중)
        db.execSQL("INSERT INTO applications (job_posting_id, student_id, resume, cover_letter, status) " +
                "VALUES (5, 1, '동양미래대학교 컴퓨터정보공학과 2학년 김학생입니다.', " +
                "'주말 주간 근무 가능합니다. 카페 알바 경험이 많지 않지만 빠르게 배우겠습니다.', 'pending')");
    }

    private void insertDummyReviews(SQLiteDatabase db) {
        // 공고 1에 대한 리뷰 (학생 1)
        db.execSQL("INSERT INTO reviews (job_posting_id, student_id, rating, comment) " +
                "VALUES (1, 1, 4.5, '사장님이 친절하시고 근무 환경이 좋습니다. 커피 만드는 법도 배울 수 있어요!')");

        // 공고 2에 대한 리뷰 (학생 1)
        db.execSQL("INSERT INTO reviews (job_posting_id, student_id, rating, comment) " +
                "VALUES (2, 1, 3.5, '야간이라 조금 힘들지만 시급은 괜찮습니다.')");

        // 공고 3에 대한 리뷰 (학생 1)
        db.execSQL("INSERT INTO reviews (job_posting_id, student_id, rating, comment) " +
                "VALUES (3, 1, 5.0, '식사 제공되고 점심시간만 일해서 좋아요! 추천합니다.')");

        // 공고 1에 대한 추가 리뷰 (학생 1)
        db.execSQL("INSERT INTO reviews (job_posting_id, student_id, rating, comment) " +
                "VALUES (1, 1, 4.0, '분위기 좋고 손님들도 친절하셔서 일하기 편합니다.')");
    }

    private void insertDummyNearbyPlaces(SQLiteDatabase db) {
        // 기준 좌표: 37.500508, 126.867771 (동양미래대학교)

        // 카페 (7개)
        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('스타벅스 구로디지털역점', 'cafe', '서울 구로구 디지털로 273', 37.485276, 126.901565, 4.5, 320)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('투썸플레이스 구로점', 'cafe', '서울 구로구 디지털로26길 123', 37.484115, 126.900997, 4.2, 450)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('이디야커피 동양미래대점', 'cafe', '서울 구로구 경인로47길 36', 37.500821, 126.869234, 4.0, 180)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('카페베네 구로역점', 'cafe', '서울 구로구 구로중앙로 152', 37.503412, 126.881920, 3.8, 550)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('커피빈 구로점', 'cafe', '서울 구로구 디지털로32길 30', 37.486123, 126.895234, 4.3, 680)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('메가커피 동양미래대점', 'cafe', '서울 구로구 경인로53길 15', 37.501234, 126.871123, 4.1, 290)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('빽다방 구로역점', 'cafe', '서울 구로구 새말로 97', 37.499876, 126.882345, 4.4, 420)");

        // 음식점 (8개)
        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('맘스터치 구로디지털역점', 'restaurant', '서울 구로구 디지털로 300', 37.485123, 126.902456, 4.3, 380)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('김밥천국 동양미래대점', 'restaurant', '서울 구로구 경인로47길 32', 37.500456, 126.868901, 3.9, 150)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('교촌치킨 구로점', 'restaurant', '서울 구로구 새말로 102', 37.502123, 126.880234, 4.6, 490)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('본죽 구로역점', 'restaurant', '서울 구로구 구로중앙로 165', 37.503789, 126.883567, 4.1, 610)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('롯데리아 구로디지털점', 'restaurant', '서울 구로구 디지털로26길 111', 37.484567, 126.899876, 4.0, 520)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('한솥도시락 동양미래대점', 'restaurant', '서울 구로구 경인로53길 20', 37.501012, 126.870567, 4.2, 240)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('신전떡볶이 구로점', 'restaurant', '서울 구로구 구로중앙로 148', 37.502890, 126.881456, 4.5, 580)");

        db.execSQL("INSERT OR IGNORE INTO nearby_places (name, category, address, latitude, longitude, rating, distance) " +
                "VALUES ('서브웨이 구로디지털역점', 'restaurant', '서울 구로구 디지털로 285', 37.485678, 126.901234, 4.1, 410)");
    }
}
