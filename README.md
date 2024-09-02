# Casper 신차출시 이벤트를 고도화 프로젝트

원본 리포지토리 : https://github.com/softeerbootcamp4th/Team8-PineApple-BE
> 현대 소프티어 부트캠프 Team 밟으실 수수숲...
> 개발기간: 2024.07.22 ~ 2024.08.25

- 다소 짧은 프로젝트 기간 탓에 개발하지 못했던 부분들을 추가합니다.

## 목차
1. [프로젝트 소개](#프로젝트-소개)
2. [기술 스택](#기술-스택)
3. [프로젝트 구조](#프로젝트-구조)
4. [컨벤션](#컨벤션)

## 프로젝트 소개
### 신차 소개 이벤트 페이지 (https://casper-event.store)
#### 주요 서비스
1. 메인 스토리 이벤트
> - 운전 중 발생할 수 있는 다양한 상황이 제시되고, 아이템 결합(경품 응모) 시 해당 상황을 해결할 수 있는 캐스퍼 EV의 기능 정보를 제공함
> - 월드컵 이벤트에서 얻은 '자동차' 아이템과 일일 퀴즈 이벤트에서 얻은 '툴박스' 아이템을 결합하여 경품에 응모함

2. 운전 중 피하고 싶은 상황 월드컵 이벤트
> - 캐스퍼 EV와 관련된 다양한 상황을 두 개씩 제시하고, 하나씩 선택하며 토너먼트 방식으로 진행함
> - 최종으로 선택한 결과를 확인하고, 해당 상황을 해결할 수 있는 캐스퍼 EV의 기능 정보를 제공함
> - 참여 시 메인 스토리 이벤트에 참여할 수 있는 '자동차' 아이템을 제공함

3. 일일 퀴즈 이벤트
> - 캐스퍼 EV의 기능과 관련된 일일 퀴즈 이벤트를 진행함
> - 참여 시 메인 스토리 이벤트에 참여할 수 있는 '툴박스' 아이템을 하루에 한 번 제공함

4. 기대평 이벤트
> - 운전 중 피하고 싶은 상황 월드컵 이벤트 참여 후 해당 상황과 관련된 캐스퍼 EV 기대평 작성
> - 참여 시 메인 스토리 이벤트에 참여할 수 있는 '툴박스' 아이템을 하루에 한 번 제공함

### 신차 소개 이벤트 어드민 페이지 (https://hyundai-admin.store)
#### 주요 서비스
1. 이벤트 페이지 정보 수정
> - 미니 퀴즈 질문 수정
> - 미니 퀴즈 답변 수정
> - 응모 결과 수정
> - 상품 목록 수정
> - 응모 당첨 확률 수정

2. 경품 코드 업로드
> - 선착순 경품 Qr 코드 업로드
> - 응모 당첨 경품 Qr 코드 업로드

3. 이벤트 현황
> - 잔여 상품 수 확인
> - 응모 내역 확인

4. 지표 분석
> - 일자별 가입자 수 변화 추이 확인(Day n Retention)
> - 일자별 방문자 수 확인

5. 1등 추첨

### 시연 영상
https://github.com/user-attachments/assets/94467c6a-4e96-4502-9d78-7df3b3a5f2a1

## 기술 스택
#### Environment
![js](https://img.shields.io/badge/IntelliJ_IDEA-000000=?style=for-the-badge&logo=intellijidea&logoColor=white)
![js](https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white)
![js](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)

#### Config
![gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

#### Development
![java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)
![springboot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![mysql](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![redis](https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)

#### Test
![junit5](https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![apachejmeter](https://img.shields.io/badge/apachejmeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white)

#### release
![amazonec2](https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![amazons3](https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)

#### Communication
![slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
![notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)


## ERD
![소프티어_ERD](https://github.com/user-attachments/assets/2f5b3f9d-250d-4338-bf32-779c119c6547)
## 프로젝트 구조
### 아키텍처
![소프티어 백엔드 아키텍처(v3) drawio](https://github.com/user-attachments/assets/31ae3ffc-404e-4606-80c6-a3718146bd14)

### 디렉토리 구조
```plaintext
├── java
│   └── softeer
│       └── team_pineapple_be
│           ├── TeamPineappleBeApplication.java
│           ├── domain
│           │   ├── admin
│           │   ├── comment
│           │   ├── draw
│           │   ├── fcfs
│           │   ├── member
│           │   ├── quiz
│           │   └── worldcup
│           └── global
│               ├── auth
│               ├── cloud
│               ├── common
│               ├── config
│               ├── exception
│               ├── lock
│               ├── message
│               └── shortenurl
└── resources
    ├── application.yml
    ├── messages.properties
    └── redis-script
```

## 컨벤션

### 브랜치명
```
<!-- 컨벤션 -->
타입/#이슈번호/세부내용

<!-- 예시 -->
feat/#27/Button

```

### 이슈 제목
```
<!-- 컨벤션 -->
[타입] 내용

<!-- 예시 -->
[feat] Component Base - Button
```

### PR 제목
```
<!-- 컨벤션 -->
타입 : #이슈번호/내용

<!-- 예시 -->
feat : #27/Component Base - Button 기능 개발
```
