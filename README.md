# 구현 목표
###  본 프로젝트는 게임 'Reigns'를 자바로 구현하는 것이 목적이며, 이 게임은 주어진 이벤트에 대해 양자택일을 통해 주어진 4가지의 수치를 0, 100이 되지 않도록 적절하게 조절하여 최대한 오래 살아남는 것을 목표로 하는 게임입니다.
### 게임 종료 조건은 4가지의 수치 중 어느 하나라도 0이나 100에 도달하면 종료되며, 최대한 오래 살아남아 점수를 올리는 것이 목적입니다

# 구현 기능

* Java GUI, AWT API를 이용해 GUI 구현
* Reigns 게임의 이벤트 및 Y/N 선택 기능 구현
* 추가적으로 Scoreboard 시스템 구현

# Reference

[1] https://github.com/psw9808/Java-Term-Project 로부터 기본 아이디어 가져옴

[2] https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html "Swing GUI"

[3] https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F/java/awt/package-summary.html "Java AWT"

# 지원 Operating System 및 실행 방법

|OS| 지원 여부 |
|-----|--------|
|windows | :o:  |
| Linux  | :o: |
|MacOS  | :x:  |

## Windows
```
1. https://www.oracle.com/java/technologies/downloads/#java17 에서 java 설치

2. 환경 변수 설정
   
   win+R 키를 눌러 나오는 실행 창에서 sysdm.cpl 입력.

   고급 -> 환경 변수 -> 시스템 변수 -> 새로 만들기 -> 자바 설치 경로에 JAVA_HOME (이름 무관) 환경 변수 만들기

   %JAVA_HOME%\lib;.; 경로에 CLASSPATH 환경 변수 만들기

   시스템 변수 -> 편집 -> 새로 만들기 -> %JAVA_HOME%\bin 지정

3. 이후 자바 IDE 설치 후 IDE를 통해 자바 파일 실행 
```
## Linux

### 1. docker 설치

```
1. sudo apt-get update # 우분투 패키지 업데이트

2. sudo apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common # 필요한 패키지 설치

3. curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - # GPG 키 추가

4. sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" # apt 저장소 추가

5. sudo apt-get update # 패키지 업데이트

6. sudo apt-get install docker-ce docker-ce-cli containerd.io # 도커 설치

7. sudo systemctl status docker / sudo docker run hello-world # 도커 상태 확인 / 도커 실행 

```

### 2. 호스트 상태 지정

```

1. 터미널에서 xhost +local:docker로 x11 포워딩 지정

2. x11이 설치되지 않았다면 sudo apt-get install x11-apps 를 통해 다운로드

3. 위 방법으로도 실행되지 않는다면 sudo apt-get install xvfb \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxext6
    실행하기

```

### 3. Dockerfile 빌드 및 실행

```
1. docker build -t kingdom:version

2. docker run --name kingdom -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix kingdom:version # X11 포워딩으로 GUI 출력 (검증)

3. docker run --rm -it -v /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY --runtime nvidia -e NVIDIA_DRIVER_CAPABILITIES=all -e NVIDIA_VISIBLE_DEVICES=all kingdom:version (미검증)

```

## MacOS

#### 지원하지 않음. (MacOS에서의 Docker 작동 검증 X)

# 코드 설명

## Kingdom.java

### class Kingdom
- Description : 시스템을 실행하는 메인 클래스
  1. StoryScreen Story - 프로그램 실행 시 최초 1회에 한해 Story.txt 파일의 내용 GUI로 출력.
  2. System.start() 함수와 System.end() 함수를 이용해 사용자가 게임 종료를 선택하기 전까지 게임 반복 실행.

### class LoginScreen
- Description : 새로운 게임 시작 시 사용자명을 입력하는 작은 창을 띄우는 클래스
   TextField에 사용자명을 입력하고 Enter 버튼을 누를 시 사용자 명 전달함

### class StoryScreen
- Description : 최초 1회 실행 시 Story 파일을 창에 출력하는 클래스
     동일 경로 내 story.txt 파일을 파일 입출력 기능을 이용해 읽어옴.
 
### class GameScreen
- Description : 게임이 실행되는 클래스
  1. PollArea : 현재 점수와 4가지 수치가 그래프 형태로 표현되는 TextArea.
  2. EventArea : 이벤트 내용을 출력하는 TextArea.
  3. ybutton/nbutton : 이벤트에 대해서 예/아니오 선택지를 전달하는 JButton.
  반복문에 위치한 states.showstate(), states.printstate(), event.occurevent() 함수를 통하여 내부 값을 조정함.

### class EndScreen
- Description : 게임 종료 시 결과창과 스코어보드를 출력하고 게임 재시작 여부를 묻는 창을 출력하는 클래스
  1. printscore() 함수 : 동일 경로 내의 scoreboard.txt 파일에 기록된 사용자명:점수 형태의 값들을 읽어와 현재 사용자의 점수를 추가한 뒤 내림차순으로 정렬하고 상위 10명의 점수만 남김.
  2. Class Pair<T1,T2> : 위 printscore() 함수의 정렬 기능을 위해 선언한 유저 자료형.

### class System
- Description : 어떤 창이 띄워지고 실행되는지 전체적 흐름을 담당하는 클래스
  1. start() 함수 : LoginScreen -> GameScreen 순으로 실행.
  2. end() 함수 : EndScreen 실행 후 EndScreen의 사용자 선택에 따라 게임 종료 / 반복 선택.

### clas Statesbase
- Description : 상속을 위한 superclass (별 기능과 의미 없음).

### class States
- Description : 각 Screen에서 사용할 함수들을 모아 놓은 클래스
  1. setStates() 함수 : 게임 시작 시 1회에 한하여 States 초기화하는 함수.
  2. showStates() 함수 : GameScreen의 PollArea에 그래프를 전달하는 함수.
  3. printStates() 함수 : 게임 종료 조건 만족 여부를 판단하고 만약 만족한다면 게임 종료 로그를 전달하는 함수.
  4. changeStates() 함수 : 입력받은 이벤트에 따라 현재 4가지 수치를 변동시키는 함수.

### class Events
- Description : 각 Screen에서 사용할 함수들을 모아 놓은 클래스
  occurevent() 함수 : event.txt 파일로부터 이벤트를 무작위로 하나 읽어와 전달하는 함수.

# TODO List
* 원본 Reigns 게임에는 사망하는 것 이외에 조건을 만족할 경우 (특정 sequential 이벤트 모두 성공) 볼 수 있는 특수 엔딩들이 있음. 이를 구현하기.
* 방향키로 예/아니오 선택하는 기능 추가하기
* 원본 게임과 같이 마우스로 가운데 카드를 왼쪽/오른쪽으로 당겨 예/아니오를 선택하는 기능 추가하기(Java Swing으로 구현하기 어려워서 버튼으로 대체함).
* 현재 java GUI가 도커에서 x11 포워딩으로 실행할 경우 창 크기가 의도한 크기대로 나오지 않고 작게 나오는 문제 있음. 이를 수정하기.
  - GameScreen의 크기를 1980 X 1200 해상도로 지정하는 것 보다 1000 X 400 해상도로 지정하는 것이 크기가 더 큼
















