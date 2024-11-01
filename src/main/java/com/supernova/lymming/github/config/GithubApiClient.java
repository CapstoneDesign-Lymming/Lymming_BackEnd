package com.supernova.lymming.github.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component 
public class GithubApiClient {
    // GitHub API 요청을 위한 URL
    private static final String API_URL = "https://api.github.com/user";

    // application.properties 또는 환경변수에서 GITHUB_TOKEN 값을 주입받음
    @Value("${GITHUB_TOKEN}")
    private String token;

    // GitHub API를 호출하는 메서드
    public void callApi() {
        try {
            // API 요청 URL 객체 생성
            URL url = new URL(API_URL);
            // HttpURLConnection 객체 생성하여 요청 방식 설정
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // GET 요청 방식 설정
            connection.setRequestProperty("Authorization", "token " + token); // Authorization 헤더에 토큰 추가

            // API 요청 및 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // 요청 성공
                // 응답 내용을 읽기 위한 BufferedReader 생성
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder(); // 응답 내용을 저장할 StringBuilder

                // 응답 내용을 한 줄씩 읽어 StringBuilder에 추가
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close(); // BufferedReader 닫기

                // 최종 응답 내용을 출력
                System.out.println("Response: " + response.toString());
            } else {
                // 요청 실패 시 응답 코드 출력
                System.out.println("API request failed: " + responseCode);
            }

        } catch (IOException e) {
            // IOException 발생 시 스택 트레이스 출력
            e.printStackTrace();
        }
    }
}
