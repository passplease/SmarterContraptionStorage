FROM gradle:8.14.3-jdk21

WORKDIR /home/gradle

COPY ./.gradle ./.gradle
COPY ./run/ ./run/