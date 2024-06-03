# Reference

[1] https://github.com/psw9808/Java-Term-Project 로부터 기본 아이디어 가져옴

[2] https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html "Swing GUI"

[3] https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F/java/awt/package-summary.html "Java AWT"

# 지원 Operating System 및 실행 방법

## Windows
```
1. https://www.oracle.com/java/technologies/downloads/#java17 에서 java 설치

2. 환경 변수 설정
   
   win+R 키를 눌러 나오는 실행 창에서 sysdm.cpl 입력.

   고급 -> 환경 변수 -> 시스템 변수 -> 새로 만들기 -> 자바 설치 경로에 JAVA_HOME (이름 무관) 환경 변수 만들기

   %JAVA_HOME%\lib;.; 경로에 CLASSPATH 환경 변수 만들기

   시스템 변수 -> 편집 -> 새로 만들기 -> %JAVA_HOME%\bin 지정

3. 이후 자바 IDE 설치 후 실행
```
## Linux

### 1. docker 설치

```
sudo apt-get update # 우분투 패키지 업데이트

sudo apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common # 필요한 패키지 설치

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - # GPG 키 추가

sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" # apt 저장소 추가

sudo apt-get update # 패키지 업데이트

sudo apt-get install docker-ce docker-ce-cli containerd.io # 도커 설치

sudo systemctl status docker / sudo docker run hello-world # 도커 상태 확인 / 도커 실행 

```

### 2. 호스트 상태 지정

```

터미널에서 xhost +local:docker로 x11 포워딩 지정

x11이 설치되지 않았다면 sudo apt-get install x11-apps 를 통해 다운로드

```

### 3. Dockerfile 빌드 및 실행

```
docker build -t kingdom:0.1

docker run --name kingdom -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix kingdom:0.1 # X11 포워딩으로 GUI 출력

```















