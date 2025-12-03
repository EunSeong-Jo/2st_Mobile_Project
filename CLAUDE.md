# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**알바대 (Alba-Dae)**: 동양미래대학교 학생 특화 아르바이트 매칭 플랫폼

This is an Android mobile application project for a sophomore-level mobile development course. The project uses:
- **Language**: Java
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java Version**: 11

### Project Details
- **Project Name**: 알바대 (Alba-Dae)
- **Target Users**: 재학생 (Students) + 고용주 (Employers) only
- **Development Period**: 2 months (2025.10.05 ~ 2025.12.03)
- **Team Size**: 3 members
- **Target School**: 동양미래대학교 (Dongyang Mirae University)
- **Database**: SQLite (Local)

### Key Features
1. **회원 관리**: 재학생/고용주 구분 인증
2. **채용 공고**: CRUD, 검색, 필터
3. **지원 시스템**: 이력서 작성, 지원, 관리
4. **시간표 (재학생 전용)**: 수업+알바 일정 통합, 겹침 체크
5. **알림**: 지원 결과, 학교 공지 (재학생)
6. **리뷰**: 평점 및 한 줄 리뷰

## Build Commands

### Building the Project
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug APK on connected device/emulator
./gradlew installDebug
```

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run unit tests for debug build
./gradlew testDebugUnitTest

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests com.example.mobile_project.ExampleUnitTest

# Run specific test method
./gradlew test --tests com.example.mobile_project.ExampleUnitTest.addition_isCorrect
```

### Linting and Code Quality
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

### Other Useful Commands
```bash
# List all available tasks
./gradlew tasks

# Check dependencies
./gradlew dependencies

# Clean and rebuild
./gradlew clean build
```

## Project Structure

### Package Organization
- **Main package**: `com.example.mobile_project`
- **Source root**: `app/src/main/java/com/example/mobile_project/`
- **Test root**: `app/src/test/java/com/example/mobile_project/`
- **Instrumented test root**: `app/src/androidTest/java/com/example/mobile_project/`

### Architecture Notes
- Currently a single-activity application with `MainActivity` as the launcher activity
- Uses Edge-to-Edge display with window insets handling for modern Android UI
- Material Design components are available via the Material library dependency
- ConstraintLayout is used for UI layouts

### Dependency Management
Dependencies are managed using Gradle version catalogs (`gradle/libs.versions.toml`):
- AndroidX libraries (AppCompat, Activity, ConstraintLayout)
- Material Design Components
- JUnit for unit testing
- Espresso for UI testing

### Key Configuration Files
- `app/build.gradle.kts`: Module-level build configuration, dependencies, and Android settings
- `build.gradle.kts`: Project-level build configuration
- `gradle/libs.versions.toml`: Centralized dependency version management
- `app/src/main/AndroidManifest.xml`: App manifest with activities and permissions

## UI Design System

### Design Resources
- **Design Guide**: `DESIGN_GUIDE.md` - 전체 UI 디자인 시스템 가이드
- **Colors**: `app/src/main/res/values/colors.xml` - DMU 브랜드 컬러 팔레트
- **Dimensions**: `app/src/main/res/values/dimens.xml` - 간격, 크기, 폰트 사이즈
- **Themes**: `app/src/main/res/values/themes.xml` - 앱 테마 및 컴포넌트 스타일
- **Strings**: `app/src/main/res/values/strings.xml` - 한국어 문자열 리소스

### Brand Colors
```
Primary: DMU Blue (#1E88E5) - 동양미래대 메인 컬러
Secondary: Alba Orange (#FF9800) - 알바 관련 요소
Student: DMU Blue (#1E88E5) - 재학생 전용 UI
Employer: Deep Orange (#FF5722) - 고용주 전용 UI
Schedule Class: DMU Blue (#1E88E5) - 수업 일정
Schedule Work: Alba Orange (#FF9800) - 알바 일정
```

### User Types & Navigation
**재학생 (Student)**:
- Bottom Navigation: 5개 (홈, 채용공고, **시간표**, 알림, 마이페이지)
- 전용 기능: 시간표 관리, 학교 공지 알림, 일정 겹침 체크

**고용주 (Employer)**:
- Bottom Navigation: 4개 (홈, 채용공고, 알림, 마이페이지)
- 전용 기능: 공고 작성/관리, 지원자 관리

### Component Styles
- **Primary Button**: `Widget.AlbaDae.Button.Primary` - DMU Blue background
- **Secondary Button**: `Widget.AlbaDae.Button.Secondary` - Outlined
- **Card**: `Widget.AlbaDae.Card` - 12dp radius, 2dp elevation
- **Text Input**: `Widget.AlbaDae.TextInputLayout` - Outlined with DMU Blue stroke

### Layout Guidelines
- Spacing: 4dp grid system (4dp, 8dp, 12dp, 16dp, 24dp, 32dp, 48dp)
- Corner Radius: Small(4dp), Medium(8dp), Large(12dp), Round(24dp)
- Typography: H1(28sp), H2(24sp), H3(20sp), Body(16sp/14sp), Caption(12sp)
- Icons: Outlined style, 24dp default size

## Database Schema

### Key Tables
```sql
-- 회원 (재학생/고용주만)
CREATE TABLE db_member (
    id TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    email TEXT,
    phone_number TEXT,
    role TEXT CHECK(role IN ('student', 'employer')),
    name TEXT
);

-- 시간표 (재학생 전용)
CREATE TABLE timetable (
    schedule_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT,
    type TEXT CHECK(type IN ('class', 'work')),
    title TEXT,
    day_of_week INTEGER,
    start_time TEXT,
    end_time TEXT,
    FOREIGN KEY (user_id) REFERENCES db_member(id)
);
```

## Development Notes

### Important Patterns
- Material Design 3 components are preferred
- Use ConstraintLayout for complex layouts
- Follow MVVM architecture pattern (if implementing)
- Implement role-based UI (student vs employer)

### Schedule Conflict Check
When a student applies for a job, automatically check:
1. Load student's timetable (class + work schedules)
2. Compare with new work schedule time slots
3. Show red border + dialog if conflict detected
4. Optionally block application if conflict exists

### Color Usage by Role
```java
if (userType == UserType.STUDENT) {
    setThemeColor(R.color.student_primary);
    showScheduleTab(true);
} else if (userType == UserType.EMPLOYER) {
    setThemeColor(R.color.employer_primary);
    showScheduleTab(false);
}
```