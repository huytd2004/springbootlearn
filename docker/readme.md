- Mở markdown : Ctrl + K V (preview song song) or Ctrl + Shift + V
- Tài liệu :
  https://dev.to/dev_tips/the-only-docker-guide-youll-ever-need-beginner-to-expert-1bca

- Quy trình :

  - Viết Dockerfile để đóng gói ứng dụng.
  - Build image từ Dockerfile.
  - Chạy container từ image vừa build.
  - Quản lý container (start, stop, xóa...).

- Chạy frontend trên docker :
  - Dockerfile :
  ```dockerfile
  # Sử dụng Node.js version 18 làm base image
  FROM node:18
  # Đặt thư mục làm việc trong container là /app
  WORKDIR /app
  # Copy file package.json vào thư mục /app (chỉ copy package.json để tận dụng cache của Docker)
  COPY package.json ./
  # Cài đặt các dependencies được khai báo trong package.json
  RUN npm install
  # Copy toàn bộ source code hiện tại vào thư mục /app trong container
  COPY . .
  # Mở port 3000 để container có thể lắng nghe (React dev server chạy trên cổng này)
  EXPOSE 3000
  # Khi container chạy, thực hiện lệnh "npm start" để khởi động React development server
  CMD ["npm", "start"]
  ```
  - Lệnh run image (không run trực tiếp trong docker desktop) :
  ```cmd
  docker run -p 3000:3000 client
  ```
- Run backend in docker :

  - Dockerfile :

  ```dockerfile
  # Stage 1: build
  # Start with a Maven image that includes JDK 21
  FROM maven:3.9.8-amazoncorretto-21 AS build

  # Copy source code and pom.xml file to /app folder
  WORKDIR /app
  COPY pom.xml .
  COPY src ./src

  # Build source code with maven
  RUN mvn package -DskipTests

  #Stage 2: create image
  # Start with Amazon Correto JDK 21
  FROM amazoncorretto:21.0.4

  # Set working folder to App and copy complied file from above step
  WORKDIR /app
  COPY --from=build /app/target/*.jar app.jar

  # Command to run the application
  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```

  - Viết docker-compose.yml :

  ```dockerfile
  version: "3.8"
  services:
  backend:
    build: ./server #build từ folder có chứa dockerfile
    container_name: server
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://host.docker.internal:5432/department_manager
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: 123456
    extra_hosts:
      - "host.docker.internal:host-gateway"
  ```

  Nếu muốn sử dụng image có sẵn

  ```dockerfile
  services:
  backend:
    image: spring-backend:latest   # dùng image đã có
    ports:
      - "8080:8080"
  ```

  Sau đó build với lệnh : docker-compose up --build và run với lệnh docker-compose up
  Lý do phải dùng docker-compose : do localhost ở trong container backend được hiểu là localhost ở trong container nên localhost ở máy sẽ không chung network => không kết nối được.

- Lý do dùng volume : nếu dùng image database thì sau mỗi lần chạy thì sẽ mất dữ liệu => dữ liệu cần lưu ở ngoài => sử dụng volume
