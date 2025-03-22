package app.product_service.service;

import app.product_service.entiry.Product;
import app.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Read
    public List<Product> getAllProducts() {
        return productRepository.findByDeletedFalse();
    }


    // Update
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setStock(updatedProduct.getStock());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // Delete
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .ifPresent(product -> {
                    product.setDeleted(true);
                    productRepository.save(product);
                });
    }
}
