# 🍀 BIZ SURVEY
<br/>

![Group 369 (1)](https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/37507c2b-69b3-453b-9be3-0b817281f44e)
<br/><br/>
### 팀원
* 박소영, 황원식, 임솔, 박시연
### 개발 기간
* 2023-10 ~ 2023-12 (약 3개월)

<br/><br/>

## 🐬 개요

### 전체 개요
1. 설문지를 생성 및 관리하고 커뮤니티나 외부로 공유할 수 있는 설문 서비스입니다.
<br/><br/>
2. 사용자는 커뮤니티, 개인, 그룹 플랜을 구독할 수 있으며 각 플랜에 맞는 워크스페이스를 사용할 수 있습니다. 
<br/><br/>
3. 사용자가 작성한 설문지는 커뮤니티 또는 외부로 공유할 수 있으며, 각 응답에 따른 통계를 확인할 수 있습니다.
<br/>

### 사용자 - 박소영
* JWT, Oauth2.0을 사용한 로그인
* 최종관리자 페이지 제공
* 마이페이지를 통한 플랜 관리
* 신고 보류 및 처리

### 커뮤니티 - 임솔
* 설문 커뮤니티를 통한 설문 공유
* 일반 커뮤니티를 통한 투표
* 자동완성을 통한 검색
* 신고 접수

### 설문 서비스 - 박시연
* 기본 설문지 관리
* 점수형 설문지 관리
* 전체 통계 시각화 및 파일 추출
* 개별 응답 제공

### 워크스페이스 - 황원식
* 개인 워크스페이스 제공
* 그룹 워크스페이스 관리
* 관리자 초대 및 연락처 관리
* 설문 외부 공유 및 히스토리/통계 제공
  <br/><br/>

## 🐠 기술스택
### 개발 환경
* Window 10
* macOS

### 개발 언어
* Java 11
* JavaScript

### 백엔드
* Spring Boot(2.7.17)
* MySQL
* JPA(Hibernate)
* QueryDSL
* Spring Security
* OAuth2
* Redis
* JJWT

### 프론트
* React
* React-Quill
* React-Beautiful-DND
* React-Router
* MUI
* Emotion
* Echart
* Axios

### 인프라
* AWS EC2
* AWS S3
* AWS Lambda
* AWS RDS
* Jenkins
* Docker
* NginX

### 개발 도구
* GIT
* GitHub
* Visual Studio Code
* InteliJ
* GitHub Desktop
* PostMan

<br/><br/>
## 🪼 ER다이어그램
![ERD_IMG](https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/eab3db88-a1c5-40a4-bb36-387b16e1e1e6)
<br/>

## 🦋 아키텍처
<img width="1140" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/868a50f6-9fa5-43a9-9dd7-599e6ca79cde">
<br/><br/>

## 🌼 화면

<br/>

### 1.커뮤니티
#### 1-1.메인 페이지
<img width="779" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/f42d4e30-2e1f-442b-98b5-e722494fd4f1">

#### 1-2.커뮤니티 페이지
<img width="829" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_BACK/assets/80444077/3e147239-117c-4ee1-bd08-f6a4e4580a9f">


#### 1-3.상세 페이지
<img width="709" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/4d5994d7-2fd7-4cd5-8b7b-42cfdaf0990a">


### 2.워크스페이스
#### 2-1.워크스페이스
<img width="898" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/fde3271b-f7ea-4225-a35f-eca22dc758e7">

#### 2-2.외부 공유
<img width="487" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/428101c4-001d-4668-8a7a-f68216f8405a">

#### 2-3.공유 히스토리
<img width="535" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/41aa3213-35b0-43d7-bd84-b26e0c0c99c2">


### 3.설문 서비스
#### 3-1.설문 생성
<img width="584" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/55eac895-6aba-42e1-be5f-affd960337d9">

#### 3-2.전체 통계
<img width="473" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/9c5f6cda-243e-46b4-814b-fc75d1811917">

#### 3-3.개별 응답
<img width="405" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/aa19da67-a213-45c7-85f6-7ca9d04befd6">


### 4.사용자
#### 4-1.마이페이지
<img width="858" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/57de9fb4-73a0-4317-8a4a-e70880a22063">

#### 4-2.관리자 페이지
<img width="852" alt="image" src="https://github.com/BIPA-8-3/BIZSurvey_FRONT/assets/80444077/9a1f38f0-ce5c-4306-bd25-834529ba1e80">




