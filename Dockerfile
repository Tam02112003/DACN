FROM amazoncorretto:22-jdk AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép file JAR vào image
COPY target/DACN-0.0.1-SNAPSHOT.jar app.jar

# Sao chép thư mục static nếu cần
COPY src/main/resources/static /app/static

# Tạo volume cho thư mục img
VOLUME /app/static/img

# Phơi ra cổng ứng dụng
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "app.jar"]