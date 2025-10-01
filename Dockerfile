FROM gradle:8.11.1-jdk21-focal

WORKDIR /temp

COPY . .

RUN gradle runGameTestServer && rm -rm -f ./temp