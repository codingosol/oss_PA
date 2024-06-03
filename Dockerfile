# 베이스 이미지로 Maven 사용
FROM maven:3.8.4-openjdk-11 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Maven의 종속성 캐시를 활용하기 위해 먼저 pom.xml을 복사하고 종속성을 다운로드
COPY pom.xml .
RUN mvn dependency:go-offline

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN mvn package

# 런타임 이미지로 OpenJDK 사용
FROM openjdk:11-jre-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]