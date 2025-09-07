- Tài liệu :

* https://www.javaguides.net/2023/04/microservices-using-spring-boot.html
* https://github.com/ali-bouali/springboot-3-micro-service-demo/tree/main
* https://www.youtube.com/watch?v=KJ0cSvYj41c

- Lỗi : ERROR: null value in column "id" of relation "school" violates not-null constraint
  Mặc dù khi dùng api tạo thì ok nhưng khi dùng query trong sql thì lỗi
  Lý do là @GenerateValue không tự tạo bảng chứa PK serial mà chỉ thêm id trong mỗi request api

- get http://localhost:8090/api/v1/schools/with-students/1
