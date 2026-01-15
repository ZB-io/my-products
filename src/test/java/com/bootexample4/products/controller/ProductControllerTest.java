package com.bootexample4.products.controller;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation;
import com.bootexample4.products.model.Product;
import com.bootexample4.products.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.junit.jupiter.api;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.Mockito.when;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.ArgumentMatchers.any; 
 public class ProductControllerTest {
@BeforeEach
public void setUp() {
    
    products = Arrays.asList(new Product(1L, "Product1", "Description1", 10.0), new Product(2L, "Product2", "Description2", 20.0));
}
@Test
@Tag("valid")
public void testRetrieveAllProductsSuccessfully() {
    when(productRepository.findAll()).thenReturn(products);
    List<Product> result = (List<Product>) productController.getAllProducts();
    assertEquals(products.size(), result.size(), "Expected product list size to match");
    assertEquals(products, result, "Expected product list to match");
}
@Test
@Tag("valid")
public void testRetrieveAllProductsWhenNoProductsExist() {
    when(productRepository.findAll()).thenReturn(Collections.emptyList());
    List<Product> result = (List<Product>) productController.getAllProducts();
    assertTrue(result.isEmpty(), "Expected empty product list");
}
@Test
@Tag("invalid")
public void testRetrieveAllProductsWhenRepositoryFails() {
    when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        productController.getAllProducts();
    });
    assertEquals("Database error", exception.getMessage(), "Expected exception message to match");
}
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
@Test
@Tag("valid")
public void createProductSuccess() {
    Product product = new Product();
    product.setId(1L);
    product.setName("Product1");
    product.setDescription("Description1");
    product.setPrice(10.0);
    when(productRepository.save(product)).thenReturn(product);
    Product result = productController.createProduct(product);
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(product.getName(), result.getName());
    assertEquals(product.getDescription(), result.getDescription());
    assertEquals(product.getPrice(), result.getPrice());
}
@Test
@Tag("invalid")
public void createProductNullInput() {
    when(productRepository.save(null)).thenThrow(IllegalArgumentException.class);
    assertThrows(IllegalArgumentException.class, () -> productController.createProduct(null));
}
@Test
@Tag("invalid")
public void createProductInvalidData() {
    Product product = new Product();
    when(productRepository.save(product)).thenThrow(IllegalArgumentException.class);
    assertThrows(IllegalArgumentException.class, () -> productController.createProduct(product));
}
@Test
@Tag("valid")
public void createProductSpecialCharacters() {
    Product product = new Product();
    product.setId(2L);
    product.setName("Product@#");
    product.setDescription("Description!@#");
    product.setPrice(20.0);
    when(productRepository.save(product)).thenReturn(product);
    Product result = productController.createProduct(product);
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(product.getName(), result.getName());
    assertEquals(product.getDescription(), result.getDescription());
    assertEquals(product.getPrice(), result.getPrice());
}
@Test
@Tag("boundary")
public void createProductMaxLength() {
    String maxLengthString = "a".repeat(255);
    Product product = new Product();
    product.setId(3L);
    product.setName(maxLengthString);
    product.setDescription(maxLengthString);
    product.setPrice(30.0);
    when(productRepository.save(product)).thenReturn(product);
    Product result = productController.createProduct(product);
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(product.getName(), result.getName());
    assertEquals(product.getDescription(), result.getDescription());
    assertEquals(product.getPrice(), result.getPrice());
}
@Test
@Tag("invalid")
public void createProductNegativePrice() {
    Product product = new Product();
    product.setId(4L);
    product.setName("Product4");
    product.setDescription("Description4");
    product.setPrice(-10.0);
    when(productRepository.save(product)).thenThrow(IllegalArgumentException.class);
    assertThrows(IllegalArgumentException.class, () -> productController.createProduct(product));
}
@Test
@Tag("valid")
public void createProductZeroPrice() {
    Product product = new Product();
    product.setId(5L);
    product.setName("Product5");
    product.setDescription("Description5");
    product.setPrice(0.0);
    when(productRepository.save(product)).thenReturn(product);
    Product result = productController.createProduct(product);
    assertNotNull(result);
    assertEquals(product.getId(), result.getId());
    assertEquals(product.getName(), result.getName());
    assertEquals(product.getDescription(), result.getDescription());
    assertEquals(product.getPrice(), result.getPrice());
}
@Test
@Tag("invalid")
public void createProductDuplicateName() {
    Product existingProduct = new Product();
    existingProduct.setId(6L);
    existingProduct.setName("Product6");
    existingProduct.setDescription("Description6");
    existingProduct.setPrice(50.0);
    Product newProduct = new Product();
    newProduct.setId(7L);
    newProduct.setName("Product6");
    newProduct.setDescription("Description7");
    newProduct.setPrice(50.0);
    when(productRepository.save(newProduct)).thenThrow(IllegalArgumentException.class);
    assertThrows(IllegalArgumentException.class, () -> productController.createProduct(newProduct));
}
@Test
@Tag("integration")
public void createProductConcurrent() {
    Product product1 = new Product();
    product1.setId(8L);
    product1.setName("Product8");
    product1.setDescription("Description8");
    product1.setPrice(60.0);
    Product product2 = new Product();
    product2.setId(9L);
    product2.setName("Product9");
    product2.setDescription("Description9");
    product2.setPrice(70.0);
    when(productRepository.save(product1)).thenReturn(product1);
    when(productRepository.save(product2)).thenReturn(product2);
    Runnable task1 = () -> productController.createProduct(product1);
    Runnable task2 = () -> productController.createProduct(product2);
    Thread thread1 = new Thread(task1);
    Thread thread2 = new Thread(task2);
    thread1.start();
    thread2.start();
    try {
        thread1.join();
        thread2.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    verify(productRepository, times(1)).save(product1);
    verify(productRepository, times(1)).save(product2);
}
@BeforeEach
public void setUp() {
    MockitoAnnotations.openMocks(this);
}
@Test
@Tag("valid")
public void testGetProductByIdExistingProduct() {
    Long productId = 1L;
    Product product = new Product();
    product.setId(productId);
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setPrice(100.0);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    ResponseEntity<Product> response = productController.getProductById(productId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(product, response.getBody());
}
@Test
@Tag("invalid")
public void testGetProductByIdNonExistingProduct() {
    Long productId = 2L;
    when(productRepository.findById(productId)).thenReturn(Optional.empty());
    ResponseEntity<Product> response = productController.getProductById(productId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
@Test
@Tag("boundary")
public void testGetProductByIdMinValidId() {
    Long productId = 1L;
    Product product = new Product();
    product.setId(productId);
    product.setName("Min Product");
    product.setDescription("Min Description");
    product.setPrice(50.0);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    ResponseEntity<Product> response = productController.getProductById(productId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(product, response.getBody());
}
@Test
@Tag("boundary")
public void testGetProductByIdMaxValidId() {
    Long productId = Long.MAX_VALUE;
    Product product = new Product();
    product.setId(productId);
    product.setName("Max Product");
    product.setDescription("Max Description");
    product.setPrice(200.0);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    ResponseEntity<Product> response = productController.getProductById(productId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(product, response.getBody());
}
@Test
@Tag("invalid")
public void testGetProductByIdNullId() {
    ResponseEntity<Product> response = productController.getProductById(null);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
@Test
@Tag("invalid")
public void testGetProductByIdNegativeId() {
    Long productId = -1L;
    ResponseEntity<Product> response = productController.getProductById(productId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
@Test
@Tag("invalid")
public void testGetProductByIdZeroId() {
    Long productId = 0L;
    ResponseEntity<Product> response = productController.getProductById(productId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
@BeforeEach
public void setup() {
    MockitoAnnotations.openMocks(this);
}
@Test
@Tag("valid")
public void testSuccessfulUpdateOfExistingProduct() {
    Product existingProduct = new Product();
    existingProduct.setId(1L);
    existingProduct.setName("Product 1");
    existingProduct.setDescription("Description 1");
    existingProduct.setPrice(10.0);
    Product updatedProduct = new Product();
    updatedProduct.setId(1L);
    updatedProduct.setName("Updated Product 1");
    updatedProduct.setDescription("Updated Description 1");
    updatedProduct.setPrice(20.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
    ResponseEntity<Product> response = productController.updateProduct(1L, updatedProduct);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedProduct, response.getBody());
}
@Test
@Tag("invalid")
public void testProductNotFoundForUpdate() {
    Product product = new Product();
    product.setId(1L);
    product.setName("Product 1");
    product.setDescription("Description 1");
    product.setPrice(10.0);
    when(productRepository.findById(1L)).thenReturn(Optional.empty());
    ResponseEntity<Product> response = productController.updateProduct(1L, product);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
@Test
@Tag("invalid")
public void testUpdateWithNullProduct() {
    Product existingProduct = new Product();
    existingProduct.setId(1L);
    existingProduct.setName("Product 1");
    existingProduct.setDescription("Description 1");
    existingProduct.setPrice(10.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    ResponseEntity<Product> response = productController.updateProduct(1L, null);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
}
@Test
@Tag("valid")
public void testUpdateWithEmptyFields() {
    Product existingProduct = new Product();
    existingProduct.setId(1L);
    existingProduct.setName("Product 1");
    existingProduct.setDescription("Description 1");
    existingProduct.setPrice(10.0);
    Product updatedProduct = new Product();
    updatedProduct.setId(1L);
    updatedProduct.setName("");
    updatedProduct.setDescription("");
    updatedProduct.setPrice(0.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
    ResponseEntity<Product> response = productController.updateProduct(1L, updatedProduct);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedProduct, response.getBody());
}
@Test
@Tag("integration")
public void testConcurrentUpdate() {
    Product existingProduct = new Product();
    existingProduct.setId(1L);
    existingProduct.setName("Product 1");
    existingProduct.setDescription("Description 1");
    existingProduct.setPrice(10.0);
    Product updatedProduct1 = new Product();
    updatedProduct1.setId(1L);
    updatedProduct1.setName("Updated Product 1");
    updatedProduct1.setDescription("Updated Description 1");
    updatedProduct1.setPrice(20.0);
    Product updatedProduct2 = new Product();
    updatedProduct2.setId(1L);
    updatedProduct2.setName("Updated Product 2");
    updatedProduct2.setDescription("Updated Description 2");
    updatedProduct2.setPrice(30.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(updatedProduct2);
    ResponseEntity<Product> response1 = productController.updateProduct(1L, updatedProduct1);
    ResponseEntity<Product> response2 = productController.updateProduct(1L, updatedProduct2);
    assertEquals(HttpStatus.OK, response1.getStatusCode());
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    assertEquals(updatedProduct2, response2.getBody());
}
@Test
@Tag("invalid")
public void testUpdateWithInvalidProductId() {
    Product product = new Product();
    product.setId(-1L);
    product.setName("Product 1");
    product.setDescription("Description 1");
    product.setPrice(10.0);
    when(productRepository.findById(-1L)).thenReturn(Optional.empty());
    ResponseEntity<Product> response = productController.updateProduct(-1L, product);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
}
@Test
@Tag("valid")
public void testUpdateWithSameDetails() {
    Product existingProduct = new Product();
    existingProduct.setId(1L);
    existingProduct.setName("Product 1");
    existingProduct.setDescription("Description 1");
    existingProduct.setPrice(10.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
    ResponseEntity<Product> response = productController.updateProduct(1L, existingProduct);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(existingProduct, response.getBody());
}
@Test
@Tag("valid")
public void testUpdateWithSpecialCharactersInFields() {
    Product existingProduct = new Product();
    existingProduct.setId(1L);
    existingProduct.setName("Product 1");
    existingProduct.setDescription("Description 1");
    existingProduct.setPrice(10.0);
    Product updatedProduct = new Product();
    updatedProduct.setId(1L);
    updatedProduct.setName("Updated@Product!1");
    updatedProduct.setDescription("Updated#Description$1");
    updatedProduct.setPrice(20.0);
    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
    ResponseEntity<Product> response = productController.updateProduct(1L, updatedProduct);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedProduct, response.getBody());
}
@BeforeEach
public void setUp() {
    product = new Product();
    product.setId(1L);
    product.setName("Test Product");
    product.setDescription("Test Description");
    product.setPrice(100.0);
}
@Test
@Tag("valid")
public void testDeleteExistingProduct() {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    ResponseEntity<Object> response = productController.deleteProduct(1L);
    assertEquals(ResponseEntity.ok().build(), response);
    verify(productRepository, times(1)).delete(product);
}
@Test
@Tag("invalid")
public void testDeleteNonExistingProduct() {
    when(productRepository.findById(2L)).thenReturn(Optional.empty());
    ResponseEntity<Object> response = productController.deleteProduct(2L);
    assertEquals(ResponseEntity.notFound().build(), response);
    verify(productRepository, never()).delete(any(Product.class));
}
@Test
@Tag("boundary")
public void testDeleteProductWithBoundaryId() {
    Long boundaryId = Long.MAX_VALUE;
    Product boundaryProduct = new Product();
    boundaryProduct.setId(boundaryId);
    when(productRepository.findById(boundaryId)).thenReturn(Optional.of(boundaryProduct));
    ResponseEntity<Object> response = productController.deleteProduct(boundaryId);
    assertEquals(ResponseEntity.ok().build(), response);
    verify(productRepository, times(1)).delete(boundaryProduct);
}
@Test
@Tag("invalid")
public void testDeleteProductWithNullId() {
    ResponseEntity<Object> response = productController.deleteProduct(null);
    assertEquals(ResponseEntity.notFound().build(), response);
    verify(productRepository, never()).delete(any(Product.class));
}
@Test
@Tag("integration")
public void testConcurrentDeleteRequests() throws InterruptedException {
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    Runnable deleteTask = () -> productController.deleteProduct(1L);
    Thread thread1 = new Thread(deleteTask);
    Thread thread2 = new Thread(deleteTask);
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    verify(productRepository, times(1)).delete(product);
}
} 