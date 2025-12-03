# 알바대(Alba-Dae) UI 디자인 가이드

> 동양미래대학교의 브랜드 아이덴티티를 반영한 대학생 특화 알바 매칭 플랫폼

## 📋 목차
1. [브랜드 아이덴티티](#1-브랜드-아이덴티티)
2. [색상 시스템](#2-색상-시스템)
3. [타이포그래피](#3-타이포그래피)
4. [컴포넌트 디자인](#4-컴포넌트-디자인)
5. [화면 레이아웃](#5-화면-레이아웃)
6. [사용자 유형 및 플로우](#6-사용자-유형-및-플로우)

---

## 1. 브랜드 아이덴티티

### 동양미래대학교 (DMU) 특징
- **심벌**: 펼쳐진 책 + 날개 + 뻗어나가는 빛
- **교육 이념**: 창의적인 전문인, 바른 인성의 교양인, 봉사하는 사회인
- **브랜드 키워드**: 미래지향적, 실용적, 창의적, 열린, 혁신적

### 알바대 디자인 컨셉
```
🎯 타겟: 동양미래대학교 재학생 + 고용주
💡 핵심 가치: 편리함, 신뢰성, 맞춤형
🎨 디자인 방향: 모던하고 친근한, 정보 전달이 명확한
```

---

## 2. 색상 시스템

### Primary Colors
```xml
<!-- DMU Blue (동양미래대 블루) -->
<color name="dmu_blue">#1E88E5</color>        <!-- 메인 컬러 -->
<color name="sky_blue">#42A5F5</color>         <!-- 보조 컬러 -->
<color name="light_blue">#90CAF9</color>       <!-- 배경/비활성 -->
<color name="blue_50">#E3F2FD</color>          <!-- 아주 연한 배경 -->
```

**사용 예시**:
- `dmu_blue`: 메인 버튼, 헤더, 재학생 전용 요소
- `sky_blue`: 학교 일정(시간표), 보조 액션
- `light_blue`: 카드 배경, 비활성 상태

### Secondary Colors
```xml
<!-- Alba Orange (알바 오렌지) -->
<color name="alba_orange">#FF9800</color>      <!-- 알바 관련 요소 -->
<color name="highlight_yellow">#FFC107</color> <!-- 강조, 알림 배지 -->
<color name="success_green">#4CAF50</color>    <!-- 성공 메시지 -->
```

**사용 예시**:
- `alba_orange`: 알바 일정(시간표), CTA 버튼
- `highlight_yellow`: 중요 알림, 경고
- `success_green`: 지원 승인, 완료 상태

### Semantic Colors
```xml
<color name="error_red">#F44336</color>        <!-- 에러, 일정 겹침 -->
<color name="text_primary">#212121</color>     <!-- 본문 텍스트 -->
<color name="text_secondary">#757575</color>   <!-- 보조 텍스트 -->
<color name="background">#FAFAFA</color>       <!-- 앱 배경 -->
<color name="divider">#E0E0E0</color>          <!-- 구분선 -->
```

### 사용자 유형별 색상
```xml
<!-- 재학생: 파란색 계열 -->
<color name="student_primary">#1E88E5</color>

<!-- 고용주: 오렌지 계열 -->
<color name="employer_primary">#FF5722</color>
```

### 시간표 색상
```xml
<color name="schedule_class">#1E88E5</color>    <!-- 수업: 파란색 -->
<color name="schedule_work">#FF9800</color>     <!-- 알바: 오렌지색 -->
<color name="schedule_conflict">#F44336</color> <!-- 겹침: 빨간색 테두리 -->
```

---

## 3. 타이포그래피

### Font Family
- **Primary**: Noto Sans KR (한글)
- **Secondary**: Roboto (영문, 숫자)

### Font Scale
```xml
<dimen name="text_h1">28sp</dimen>      <!-- 화면 타이틀 -->
<dimen name="text_h2">24sp</dimen>      <!-- 섹션 헤더 -->
<dimen name="text_h3">20sp</dimen>      <!-- 카드 제목 -->
<dimen name="text_body1">16sp</dimen>   <!-- 본문 -->
<dimen name="text_body2">14sp</dimen>   <!-- 부가 정보 -->
<dimen name="text_caption">12sp</dimen> <!-- 힌트, 레이블 -->
<dimen name="text_button">16sp</dimen>  <!-- 버튼 -->
```

### Font Weight
- **H1, H2, H3**: Bold (700)
- **Button**: Medium (500)
- **Body, Caption**: Regular (400)

---

## 4. 컴포넌트 디자인

### 4.1 Buttons

#### Primary Button
```xml
<style name="Widget.AlbaDae.Button.Primary" parent="Widget.Material3.Button">
    <item name="backgroundTint">@color/dmu_blue</item>
    <item name="android:textColor">@color/white</item>
    <item name="cornerRadius">24dp</item>
    <item name="android:minHeight">48dp</item>
</style>
```
**용도**: 주요 액션 (로그인, 지원하기, 저장 등)

#### Secondary Button
```xml
<style name="Widget.AlbaDae.Button.Secondary" parent="Widget.Material3.Button.OutlinedButton">
    <item name="strokeColor">@color/dmu_blue</item>
    <item name="android:textColor">@color/dmu_blue</item>
    <item name="cornerRadius">24dp</item>
    <item name="android:minHeight">48dp</item>
</style>
```
**용도**: 보조 액션 (취소, 뒤로가기 등)

### 4.2 Cards

#### 공고 카드
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="120dp"
    app:cardCornerRadius="12dp"
    app:cardElevation="2dp"
    android:layout_margin="8dp">

    <!-- 회사명, 공고 제목, 급여, 위치, 시간, 평점, 북마크 -->

</com.google.android.material.card.MaterialCardView>
```

**레이아웃 구조**:
```
┌────────────────────────────────┐
│ [회사명]              ⭐4.5 💖│
│ [공고 제목]                     │
│ 💰 시급 12,000원                │
│ 📍 서울 구로구  ⏰ 주 3일, 4시간 │
└────────────────────────────────┘
```

#### 시간표 카드
- **학교 일정**: `schedule_class` 배경, White 텍스트
- **알바 일정**: `schedule_work` 배경, White 텍스트
- **겹침 경고**: `error_red` 2dp 테두리

### 4.3 Bottom Navigation

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:layout_width="match_parent"
    android:layout_height="56dp"
    app:itemIconTint="@color/bottom_nav_selector"
    app:itemTextColor="@color/bottom_nav_selector">

    <menu>
        <item android:id="@+id/nav_home" android:icon="@drawable/ic_home" android:title="@string/nav_home"/>
        <item android:id="@+id/nav_jobs" android:icon="@drawable/ic_jobs" android:title="@string/nav_jobs"/>
        <item android:id="@+id/nav_schedule" android:icon="@drawable/ic_schedule" android:title="@string/nav_schedule"/>
        <item android:id="@+id/nav_notifications" android:icon="@drawable/ic_notifications" android:title="@string/nav_notifications"/>
        <item android:id="@+id/nav_my_page" android:icon="@drawable/ic_person" android:title="@string/nav_my_page"/>
    </menu>

</com.google.android.material.bottomnavigation.BottomNavigationView>
```

**재학생 버전**: 5개 메뉴 (홈, 채용공고, 시간표, 알림, 마이페이지)
**고용주 버전**: 4개 메뉴 (홈, 채용공고, 알림, 마이페이지) - 시간표 제외

### 4.4 Input Fields

```xml
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.AlbaDae.TextInputLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:hint="@string/email"/>

</com.google.android.material.textfield.TextInputLayout>
```

---

## 5. 화면 레이아웃

### 5.1 스플래시 화면
- **배경**: Primary Gradient (`dmu_blue` → `sky_blue`)
- **로고**: DMU 심벌 변형 (책 + 날개 + 빛)
- **앱 이름**: 알바대 (Alba-Dae)
- **태그라인**: 대학생의 알바 매칭

### 5.2 회원 유형 선택 (수정됨)

**2가지 유형만 제공**:

```
┌─────────────────────────┐
│   회원 유형 선택          │
│  어떻게 이용하시나요?     │
│                         │
│  ┌─────────────────┐   │
│  │   🎓           │   │
│  │ 재학생 인증      │   │
│  │ 학교 이메일 인증  │   │
│  │ ✓ 시간표 관리    │   │
│  │ ✓ 학교 공지 알림  │   │
│  └─────────────────┘   │
│                         │
│  ┌─────────────────┐   │
│  │   💼           │   │
│  │ 고용주 인증      │   │
│  │ 사업자등록번호    │   │
│  └─────────────────┘   │
│                         │
│  [다음 단계]             │
└─────────────────────────┘
```

### 5.3 홈 화면

#### 재학생용 홈
```
┌─────────────────────────┐
│ ☰  알바대        🔔 ❤️  │  ← 헤더 (Gradient)
├─────────────────────────┤
│ 안녕하세요, OOO님! 👋    │
│ [검색창]  🔍             │
│                         │
│ 📍 내 주변 알바          │
│ [공고 카드 1]            │
│ [공고 카드 2]            │
│                         │
│ ⭐ 추천 알바             │
│ [카드] [카드] [카드]     │
│                         │
│ 📅 다가오는 일정 (특화)   │
│ 오늘 14:00 데이터베이스  │
│ 내일 18:00 편의점 알바   │
└─────────────────────────┘
│ 🏠 채용 📅 🔔 👤        │  ← Bottom Nav (5개)
└─────────────────────────┘
```

#### 고용주용 홈
```
┌─────────────────────────┐
│ ☰  알바대        🔔      │
├─────────────────────────┤
│ 안녕하세요, [업체명]님!   │
│                         │
│ 📊 나의 통계             │
│ 활성 공고: 3개           │
│ 받은 지원: 12건          │
│                         │
│ 📋 내 공고 관리          │
│ [공고 카드 1]  [수정]    │
│ [공고 카드 2]  [수정]    │
│                         │
│ 💬 최근 지원자           │
│ [지원자 카드 1]          │
│ [지원자 카드 2]          │
│                         │
│ [+ 새 공고 작성]         │
└─────────────────────────┘
│ 🏠 채용 🔔 👤           │  ← Bottom Nav (4개)
└─────────────────────────┘
```

### 5.4 시간표 (재학생 전용)
```
┌─────────────────────────┐
│ 시간표        [+] [월간] │
├─────────────────────────┤
│ 2025년 10월              │
│ < 1주차 (10.6~10.12) >  │
├─────────────────────────┤
│   월  화  수  목  금     │
│ 9 ──────────────────── │
│10 [수업][수업]    [수업] │  ← 파란색
│11 [수업][수업]    [수업] │
│12 ──────────────────── │
│17         [알바]        │  ← 오렌지색
│18 [알바]  [알바] [알바]  │
│19 [알바]  [알바] [알바]  │
└─────────────────────────┘
```

**일정 겹침 체크**:
- 알바 지원 시 자동으로 시간표 확인
- 겹치면 빨간색 테두리 + 경고 다이얼로그
- "이미 등록된 일정과 겹칩니다." 메시지

---

## 6. 사용자 유형 및 플로우

### 6.1 재학생 (Student)

#### 특징
- **인증 방식**: 학교 이메일 (@dongyang.ac.kr)
- **전용 기능**: 시간표 관리, 학교 공지 알림
- **메인 컬러**: DMU Blue (#1E88E5)

#### 주요 기능 플로우
```
1. 회원가입 → 재학생 선택 → 학교 이메일 입력
2. 공고 검색 → 상세보기 → 지원하기
3. 지원 시 시간표 자동 체크 → 겹침 경고
4. 시간표에서 수업+알바 일정 통합 관리
5. 학교 공지사항 실시간 알림 수신
```

#### Bottom Navigation (5개)
- 🏠 홈
- 📋 채용공고
- 📅 **시간표** (재학생 전용)
- 🔔 알림
- 👤 마이페이지

### 6.2 고용주 (Employer)

#### 특징
- **인증 방식**: 사업자등록번호
- **전용 기능**: 공고 작성/관리, 지원자 관리
- **메인 컬러**: Alba Orange (#FF9800)

#### 주요 기능 플로우
```
1. 회원가입 → 고용주 선택 → 사업자등록번호 입력
2. 공고 작성 → 단계별 입력
3. 지원자 목록 확인 → 이력서 조회
4. 채팅으로 면접 일정 조율
5. 지원 승인/거절 처리
```

#### Bottom Navigation (4개)
- 🏠 홈
- 📋 채용공고 (관리 모드)
- 🔔 알림
- 👤 마이페이지

---

## 7. 아이콘 가이드

### 스타일
- **타입**: Outlined (아웃라인)
- **두께**: 2dp
- **크기**: Small(20dp), Medium(24dp), Large(32dp)

### 주요 아이콘
```
홈: ic_home
채용공고: ic_work
시간표: ic_calendar_today
알림: ic_notifications
마이페이지: ic_person
검색: ic_search
필터: ic_filter_list
즐겨찾기: ic_favorite
위치: ic_place
시간: ic_schedule
평점: ic_star
채팅: ic_chat
추가: ic_add
수정: ic_edit
삭제: ic_delete
```

---

## 8. Spacing & Layout

### Spacing System (4dp Grid)
```xml
<dimen name="spacing_xxs">4dp</dimen>   <!-- 아이콘 간격 -->
<dimen name="spacing_xs">8dp</dimen>    <!-- 밀집된 요소 -->
<dimen name="spacing_s">12dp</dimen>    <!-- 관련 요소 -->
<dimen name="spacing_m">16dp</dimen>    <!-- 기본 패딩 -->
<dimen name="spacing_l">24dp</dimen>    <!-- 섹션 간격 -->
<dimen name="spacing_xl">32dp</dimen>   <!-- 큰 섹션 -->
<dimen name="spacing_xxl">48dp</dimen>  <!-- 화면 상하단 -->
```

### Elevation (그림자)
```xml
<dimen name="elevation_0">0dp</dimen>   <!-- 배경 -->
<dimen name="elevation_1">2dp</dimen>   <!-- 카드 -->
<dimen name="elevation_2">4dp</dimen>   <!-- 버튼 -->
<dimen name="elevation_3">8dp</dimen>   <!-- FAB -->
<dimen name="elevation_4">16dp</dimen>  <!-- Dialog -->
<dimen name="elevation_5">24dp</dimen>  <!-- Modal -->
```

---

## 9. 접근성 (Accessibility)

### 색상 대비
- 텍스트와 배경 간 WCAG AA 기준 준수 (4.5:1 이상)
- `text_primary` (#212121) on `surface` (#FFFFFF): 16.1:1 ✅

### 터치 영역
- 최소 터치 영역: 48dp × 48dp
- 아이콘 버튼: 40dp × 40dp (패딩 포함 시 48dp)

### Content Description
- 모든 아이콘 버튼에 `android:contentDescription` 필수
- 예: `android:contentDescription="@string/bookmark"`

---

## 10. 애니메이션

### 화면 전환
- **진입**: Fade In + Slide Up (300ms)
- **퇴장**: Fade Out + Slide Down (200ms)

### 버튼 상태
- **Normal → Pressed**: Scale(0.95) + Alpha(0.8)
- **Duration**: 100ms

### 리스트 아이템
- **Stagger Animation**: 각 카드 50ms 지연

---

## 11. 구현 체크리스트

### ✅ 필수 리소스 파일
- [x] `colors.xml` - 색상 팔레트
- [x] `dimens.xml` - 간격 및 크기
- [x] `themes.xml` - 앱 테마 및 스타일
- [x] `strings.xml` - 문자열 리소스
- [ ] `drawable/` - 아이콘 및 이미지
- [ ] `layout/` - 화면 레이아웃

### 📱 핵심 화면 레이아웃
- [ ] `activity_splash.xml` - 스플래시
- [ ] `activity_login.xml` - 로그인
- [ ] `activity_signup.xml` - 회원가입
- [ ] `activity_user_type_selection.xml` - 유형 선택
- [ ] `activity_main.xml` - 메인 (Bottom Nav)
- [ ] `fragment_home.xml` - 홈
- [ ] `fragment_jobs.xml` - 채용공고
- [ ] `fragment_schedule.xml` - 시간표 (재학생)
- [ ] `fragment_notifications.xml` - 알림
- [ ] `fragment_my_page.xml` - 마이페이지

### 🎨 재사용 가능 컴포넌트
- [ ] `item_job_card.xml` - 공고 카드
- [ ] `item_schedule_card.xml` - 시간표 카드
- [ ] `item_notification.xml` - 알림 아이템
- [ ] `dialog_schedule_conflict.xml` - 일정 겹침 다이얼로그

---

## 💡 개발 팁

### 색상 사용 예시
```java
// 재학생 전용 UI
if (userType == UserType.STUDENT) {
    binding.toolbar.setBackgroundColor(getColor(R.color.student_primary));
    binding.scheduleCard.setCardBackgroundColor(getColor(R.color.schedule_class));
}

// 고용주 전용 UI
if (userType == UserType.EMPLOYER) {
    binding.toolbar.setBackgroundColor(getColor(R.color.employer_primary));
}
```

### 시간표 일정 겹침 체크
```java
// 알바 지원 시
if (scheduleConflicts(newWorkSchedule, existingSchedules)) {
    showConflictDialog();
    binding.scheduleCard.setStrokeColor(getColor(R.color.schedule_conflict));
    binding.scheduleCard.setStrokeWidth(4); // 2dp
}
```

---

## 📞 문의

디자인 시스템 관련 문의: Design Guide 참조
동양미래대학교 브랜드 가이드: [DMU 공식 웹사이트](https://www.dongyang.ac.kr)

---

**Last Updated**: 2025.11.06
**Version**: 1.0
**Designer**: Claude Code SuperClaude Framework
