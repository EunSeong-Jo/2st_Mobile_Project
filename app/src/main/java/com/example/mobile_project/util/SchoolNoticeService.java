package com.example.mobile_project.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.mobile_project.model.SchoolNotice;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 동양미래대학교 공지사항 크롤링 서비스
 */
public class SchoolNoticeService {

    private static final String TAG = "SchoolNoticeService";
    private static final String NOTICE_URL = "https://www.dongyang.ac.kr/dmu/4904/subview.do";
    private static final String BASE_URL = "https://www.dongyang.ac.kr";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface NoticeCallback {
        void onSuccess(List<SchoolNotice> notices);
        void onError(String errorMessage);
    }

    /**
     * 공지사항 가져오기
     */
    public void fetchNotices(NoticeCallback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Fetching notices from: " + NOTICE_URL);

                // Jsoup으로 HTML 파싱
                Document doc = Jsoup.connect(NOTICE_URL)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .timeout(30000) // 30초로 증가
                        .followRedirects(true)
                        .ignoreHttpErrors(false)
                        .maxBodySize(0) // 무제한
                        .get();

                List<SchoolNotice> notices = new ArrayList<>();
                Set<String> addedUrls = new HashSet<>(); // URL 중복 체크용

                // 공지사항 테이블에서 데이터 추출
                // 동양미래대 공지사항 페이지는 table 구조 사용
                Elements rows = doc.select("tbody tr");

                Log.d(TAG, "Found " + rows.size() + " notice rows");

                for (Element row : rows) {
                    try {
                        // 제목과 링크 추출 (td 내부의 a 태그)
                        Elements tds = row.select("td");
                        if (tds.size() < 4) continue; // 최소한 번호, 제목, 작성자, 날짜 필요

                        // 첫 번째 td (번호 컬럼) 로그 출력으로 확인
                        String numberColumn = tds.first().text().trim();
                        Log.d(TAG, "Number column: [" + numberColumn + "]");

                        Element titleElement = row.select("td a").first();
                        if (titleElement == null) continue;

                        String title = titleElement.text().trim();

                        // 제목 정리
                        // 1. "새글" 텍스트 제거
                        title = title.replace("새글", "").trim();

                        // 2. 대괄호 앞의 텍스트 제거 (예: "기타 [제목]" -> "[제목]")
                        int bracketIndex = title.indexOf('[');
                        if (bracketIndex > 0) {
                            title = title.substring(bracketIndex).trim();
                        }

                        // 3. 제목 끝의 숫자 제거 (예: "제목 1" -> "제목")
                        title = title.replaceAll("\\s+\\d+$", "").trim();

                        String href = titleElement.attr("href");

                        // 상대 경로를 절대 경로로 변환
                        String fullUrl = href;
                        if (href.startsWith("/")) {
                            fullUrl = BASE_URL + href;
                        } else if (!href.startsWith("http")) {
                            fullUrl = BASE_URL + "/" + href;
                        }

                        // URL 중복 체크 (같은 공지가 여러 번 나오는 경우 방지)
                        if (addedUrls.contains(fullUrl)) {
                            Log.d(TAG, "Skipping duplicate notice: " + title);
                            continue;
                        }

                        // 날짜 추출 (일반적으로 마지막에서 2번째 td)
                        String date = "";
                        if (tds.size() >= 5) {
                            // 작성일 컬럼 (YYYY.MM.DD 형식)
                            date = tds.get(tds.size() - 2).text().trim();
                        }

                        // 작성자 추출 (날짜 앞 td)
                        String author = "";
                        if (tds.size() >= 4) {
                            // 작성자 컬럼
                            author = tds.get(tds.size() - 3).text().trim();
                        }

                        // NEW 표시 확인 ("새글" 텍스트 또는 이미지)
                        boolean isNew = titleElement.text().contains("새글") ||
                                       row.select("img[alt*=새글], img[alt*=new], img[alt*=NEW]").size() > 0;

                        // 공지사항이나 빈 제목 필터링
                        if (!title.isEmpty() && !title.equals("제목")) {
                            SchoolNotice notice = new SchoolNotice(title, fullUrl, date, author, isNew);
                            notices.add(notice);
                            addedUrls.add(fullUrl); // URL 추가

                            Log.d(TAG, "Notice: " + title + " | " + date + " | " + author);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing row", e);
                    }
                }

                // 메인 스레드에서 콜백 호출
                List<SchoolNotice> finalNotices = notices;
                mainHandler.post(() -> {
                    if (finalNotices.isEmpty()) {
                        callback.onError("공지사항을 찾을 수 없습니다.");
                    } else {
                        callback.onSuccess(finalNotices);
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "Network error", e);
                mainHandler.post(() -> callback.onError("네트워크 오류: " + e.getMessage()));
            } catch (Exception e) {
                Log.e(TAG, "Parsing error", e);
                mainHandler.post(() -> callback.onError("공지사항 불러오기 실패: " + e.getMessage()));
            }
        });
    }

    /**
     * ExecutorService 종료
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}