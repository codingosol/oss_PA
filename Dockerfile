# 베이스 이미지 설정
FROM openjdk:11

# 필요한 패키지 설치 (Xvfb 및 X11 라이브러리 포함)
RUN apt-get update && apt-get install -y \
    xvfb \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxext6 \
    x11-apps \
    fonts-nanum \
    locales

# 로케일 설정
RUN locale-gen ko_KR.UTF-8
ENV LANG ko_KR.UTF-8
ENV LANGUAGE ko_KR:ko
ENV LC_ALL ko_KR.UTF-8
# 애플리케이션 파일을 컨테이너에 복사
COPY src/Kingdom.java /app/src/Kingdom.java
COPY event.txt /app/event.txt
COPY scoreboard.txt /app/scoreboard.txt
COPY story.txt /app/story.txt

# 작업 디렉토리 설정
WORKDIR /app

# 자바 파일 컴파일 
RUN javac -encoding UTF-8 src/Kingdom.java

# 애플리케이션 실행 
CMD ["java", "-Dfile.encoding=UTF-8", "-cp", "src", "Kingdom"]

# xhost +local:docker로 x11 포워딩 지정 후
# 1. docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix kingdom:version 로 실행 (검증)
# 하지만 GUI 창의 크기가 제대로 조절되지 않는 문제 있음 (1000*400)은 제대로 표기 되는 크기이나 (1980*1020) 해상도로 실행할 경우 창의 크기가 오히려 작아짐
# 2. docker run --rm -it -v /tmp/.X11-unix:/tmp/.X11-unix -e DISPLAY --runtime nvidia -e NVIDIA_DRIVER_CAPABILITIES=all -e NVIDIA_VISIBLE_DEVICES=all kingdom:version (미검증)