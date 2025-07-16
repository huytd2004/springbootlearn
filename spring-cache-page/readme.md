- Cách thức hoạt động của bộ nhớ đệm với Redis trong Spring Boot
+ Cacheable: Khi sử dụng @Cacheable, trước tiên Spring sẽ kiểm tra xem giá trị có trong bộ nhớ đệm Redis hay không. Nếu có, nó sẽ trả về giá trị đã lưu vào bộ nhớ đệm mà không thực thi phương thức. Nếu không, nó sẽ thực thi phương thức, lưu vào bộ nhớ đệm kết quả trong Redis và trả về kết quả.
+ CachePut: Khi sử dụng @CachePut, nó sẽ cập nhật bộ nhớ đệm bằng dữ liệu mới, bất kể phương thức đã được thực thi hay chưa.
+ CacheEvict: Khi sử dụng @CacheEvict, nó sẽ xóa dữ liệu khỏi bộ nhớ đệm, điều này rất hữu ích để duy trì tính nhất quán của bộ nhớ đệm khi dữ liệu cơ sở thay đổi.
- docker start redis-cache, docker stop redis-cache

- post http://localhost:8080/api/products
  {
  "name": "Product 1",
  "description": "Description 1"
  }
- get http://localhost:8080/api/products/1
- get http://localhost:8080/api/products?page=0&size=5



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
