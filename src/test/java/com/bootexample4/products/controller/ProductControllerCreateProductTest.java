
package com.bootexample4.products.controller;

import com.bootexample4.products.model.Product;
import com.bootexample4.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerCreateProductTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductController productController;

	private Product validProduct;

	@BeforeEach
	void setUp() {
		validProduct = new Product();
		validProduct.setName("Test Product");
		validProduct.setDescription("Test Description");
		validProduct.setPrice(BigDecimal.valueOf(99.99));
	}

	@Test
    @Tag("valid")
    void createValidProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(validProduct);
        // Act
        Product result = productController.createProduct(validProduct);
        // Assert
        assertEquals(validProduct, result);
        verify(productRepository, times(1)).save(validProduct);
    }

	@Test
	@Tag("invalid")
	void createProductWithNullValues() {
		// Arrange
		Product nullProduct = new Product();
		when(productRepository.save(any(Product.class))).thenReturn(nullProduct);
		// Act
		Product result = productController.createProduct(nullProduct);
		// Assert
		assertEquals(nullProduct, result);
		verify(productRepository, times(1)).save(nullProduct);
	}

	@Test
    @Tag("invalid")
    void createProductWhenRepositoryThrowsException() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));
        // Act & Assert
        assertThrows(RuntimeException.class, () -> productController.createProduct(validProduct));
        verify(productRepository, times(1)).save(validProduct);
    }

	@Test
	@Tag("valid")
	void createProductWithIdAlreadySet() {
		// Arrange
		Product productWithId = new Product();
		productWithId.setId(1L);
		productWithId.setName("Pre-ID Product");
		productWithId.setDescription("Product with ID already set");
		productWithId.setPrice(BigDecimal.valueOf(49.99));
		when(productRepository.save(any(Product.class))).thenReturn(productWithId);
		// Act
		Product result = productController.createProduct(productWithId);
		// Assert
		assertEquals(productWithId, result);
		verify(productRepository, times(1)).save(productWithId);
	}

	@Test
	@Tag("boundary")
	void createProductWithExtremeValues() {
		// Arrange
		Product extremeProduct = new Product();
		extremeProduct.setName("A".repeat(1000)); // Very long name
		extremeProduct.setDescription("B".repeat(5000)); // Very long description
		extremeProduct.setPrice(BigDecimal.valueOf(Double.MAX_VALUE)); // Very high price
		when(productRepository.save(any(Product.class))).thenReturn(extremeProduct);
		// Act
		Product result = productController.createProduct(extremeProduct);
		// Assert
		assertEquals(extremeProduct, result);
		verify(productRepository, times(1)).save(extremeProduct);
	}

	@Test
    @Tag("integration")
    void verifyRepositoryInteraction() {
        // Arrange
        when(productRepository.save(validProduct)).thenReturn(validProduct);
        // Act
        productController.createProduct(validProduct);
        // Assert
        verify(productRepository, times(1)).save(same(validProduct));
        verifyNoMoreInteractions(productRepository);
    }

}