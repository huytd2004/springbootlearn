- Cách thức hoạt động của bộ nhớ đệm với Redis trong Spring Boot
+ Cacheable: Khi sử dụng @Cacheable, trước tiên Spring sẽ kiểm tra xem giá trị có trong bộ nhớ đệm Redis hay không. Nếu có, nó sẽ trả về giá trị đã lưu vào bộ nhớ đệm mà không thực thi phương thức. Nếu không, nó sẽ thực thi phương thức, lưu vào bộ nhớ đệm kết quả trong Redis và trả về kết quả.
+ CachePut: Khi sử dụng @CachePut, nó sẽ cập nhật bộ nhớ đệm bằng dữ liệu mới, bất kể phương thức đã được thực thi hay chưa.
+ CacheEvict: Khi sử dụng @CacheEvict, nó sẽ xóa dữ liệu khỏi bộ nhớ đệm, điều này rất hữu ích để duy trì tính nhất quán của bộ nhớ đệm khi dữ liệu cơ sở thay đổi.

- post http://localhost:8080/api/products
  {
  "name": "Product 1",
  "description": "Description 1"
  }
- get http://localhost:8080/api/products/1


Trong Spring Data JPA, `Pagination` (phân trang) và `Filtering` (lọc dữ liệu) là hai tính năng phổ biến để xử lý dữ liệu lớn. Spring Data JPA cung cấp `Pageable` để hỗ trợ phân trang và sắp xếp dữ liệu một cách dễ dàng. Dưới đây là hướng dẫn chi tiết cách sử dụng `Pagination` và `Filtering` trong Spring Data JPA.

---

### **1. Cấu hình cơ bản**
Trước khi bắt đầu, hãy đảm bảo rằng bạn đã cài đặt các dependency cần thiết trong file `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

### **2. Tạo Entity**
Giả sử bạn có một Entity `Product`:

```java
package com.example.paginationfiltering.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private Double price;
}
```

---

### **3. Tạo Repository**
Spring Data JPA cung cấp interface `PagingAndSortingRepository` hoặc `JpaRepository` để hỗ trợ phân trang và sắp xếp. Chỉ cần khai báo interface như sau:

```java
package com.example.paginationfiltering.repository;

import com.example.paginationfiltering.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods (cho filtering) sẽ được thêm sau
}
```

---

### **4. Tạo Service**
Trong Service, bạn sẽ sử dụng `Pageable` để xử lý phân trang.

#### **ProductService.java**
```java
package com.example.paginationfiltering.service;

import com.example.paginationfiltering.model.Product;
import com.example.paginationfiltering.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory(category, pageable);
    }
}
```

---

### **5. Tạo Controller**
Controller sẽ nhận các tham số phân trang từ request (ví dụ: `page` và `size`) và chuyển chúng đến Service.

#### **ProductController.java**
```java
package com.example.paginationfiltering.controller;

import com.example.paginationfiltering.model.Product;
import com.example.paginationfiltering.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/category")
    public Page<Product> getProductsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsByCategory(category, page, size);
    }
}
```

---

### **6. Thêm Filtering**
Spring Data JPA cho phép bạn tạo các phương thức truy vấn tùy chỉnh trong Repository. Ví dụ, để lọc sản phẩm theo `category`, bạn có thể thêm phương thức sau vào `ProductRepository`:

#### **ProductRepository.java**
```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(String category, Pageable pageable);
}
```

---

### **7. Testing API**
Giả sử bạn đã chạy ứng dụng Spring Boot trên cổng `8080`. Dưới đây là các API mẫu:

#### **Lấy tất cả sản phẩm (phân trang)**
- **URL**: `http://localhost:8080/api/products?page=0&size=5`
- **Mô tả**: Lấy danh sách sản phẩm, mỗi trang có 5 sản phẩm.

#### **Lọc sản phẩm theo category (phân trang)**
- **URL**: `http://localhost:8080/api/products/category?category=Electronics&page=0&size=5`
- **Mô tả**: Lấy danh sách sản phẩm thuộc danh mục `Electronics`, mỗi trang có 5 sản phẩm.

---

### **8. Sắp xếp (Sorting)**
Spring Data JPA cũng hỗ trợ sắp xếp dữ liệu bằng cách sử dụng `Sort`.

#### **Thêm sắp xếp vào Service**
```java
import org.springframework.data.domain.Sort;

public Page<Product> getAllProducts(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return productRepository.findAll(pageable);
}
```

#### **Thêm sắp xếp vào Controller**
```java
@GetMapping("/sorted")
public Page<Product> getAllProductsSorted(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy) {
    return productService.getAllProducts(page, size, sortBy);
}
```

#### **API Sắp xếp**
- **URL**: `http://localhost:8080/api/products/sorted?page=0&size=5&sortBy=price`
- **Mô tả**: Lấy danh sách sản phẩm, mỗi trang có 5 sản phẩm, sắp xếp theo giá (`price`).

---

### **9. Kết hợp Filtering, Pagination và Sorting**
Bạn có thể kết hợp cả 3 tính năng trong cùng một API. Ví dụ:

#### **Service**
```java
public Page<Product> getProductsByCategorySorted(
        String category, int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return productRepository.findByCategory(category, pageable);
}
```

#### **Controller**
```java
@GetMapping("/category/sorted")
public Page<Product> getProductsByCategorySorted(
        @RequestParam String category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "name") String sortBy) {
    return productService.getProductsByCategorySorted(category, page, size, sortBy);
}
```

---

### **10. Tổng kết**
- **Pagination**: Sử dụng `Pageable` để phân trang dữ liệu.
- **Filtering**: Tạo các phương thức truy vấn tùy chỉnh trong Repository.
- **Sorting**: Sử dụng `Sort` để sắp xếp dữ liệu.

Với cách tiếp cận này, bạn có thể dễ dàng xây dựng các API RESTful hỗ trợ phân trang, lọc và sắp xếp dữ liệu trong Spring Boot.