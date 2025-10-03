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
