package app.product_service.repository;

import app.product_service.entiry.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedFalse();
}
