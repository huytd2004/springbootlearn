package com.example.springcachepage.service;



import com.example.springcachepage.entity.Product;
import com.example.springcachepage.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

//    @Cacheable(value = "products", key = "#id") //Chỉ ra rằng giá trị trả về của phương thức cần được lưu vào bộ nhớ đệm.
//    public Optional<Product> getProductById(Long id) {
//        System.out.println("Fetching product from database...");
//        return productRepository.findById(id);
//    }
public Page<Product> getAllProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return productRepository.findAll(pageable);
}

    @CachePut(value = "products", key = "#product.id")//Cập nhật bộ nhớ đệm mà không ảnh hưởng đến quá trình thực thi phương thức.
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")//Xóa dữ liệu khỏi bộ nhớ đệm.
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
}
