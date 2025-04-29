
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerGetAllProductsTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductController productController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
	}

	@Test
	@Tag("valid")
	void getAllProductsWhenProductsExist() {
		// Arrange
		List<Product> expectedProducts = Arrays.asList(new Product(1L, "Product 1", "Description 1", 10.99),
				new Product(2L, "Product 2", "Description 2", 20.99));
		when(productRepository.findAll()).thenReturn(expectedProducts);
		// Act
		List<Product> actualProducts = productController.getAllProducts();
		// Assert
		assertEquals(expectedProducts, actualProducts);
		assertEquals(2, actualProducts.size());
		verify(productRepository, times(1)).findAll();
	}

	@Test
    @Tag("boundary")
    void getAllProductsWhenNoProductsExist() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        // Act
        List<Product> actualProducts = productController.getAllProducts();
        // Assert
        assertTrue(actualProducts.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

	@Test
    @Tag("invalid")
    void getAllProductsWhenRepositoryThrowsException() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            productController.getAllProducts();
        });
        assertEquals("Database error", exception.getMessage());
        verify(productRepository, times(1)).findAll();
    }

	@Test
	@Tag("valid")
	void getAllProductsVerifyResponseStructure() {
		// Arrange
		Product product1 = new Product(1L, "Test Product 1", "Test Description 1", 9.99);
		Product product2 = new Product(2L, "Test Product 2", "Test Description 2", 19.99);
		Product product3 = new Product(3L, "Test Product 3", "Test Description 3", 29.99);

		List<Product> expectedProducts = Arrays.asList(product1, product2, product3);
		when(productRepository.findAll()).thenReturn(expectedProducts);
		// Act
		List<Product> actualProducts = productController.getAllProducts();
		// Assert
		assertEquals(expectedProducts.size(), actualProducts.size());
		for (int i = 0; i < expectedProducts.size(); i++) {
			assertEquals(expectedProducts.get(i).getId(), actualProducts.get(i).getId());
			assertEquals(expectedProducts.get(i).getName(), actualProducts.get(i).getName());
			assertEquals(expectedProducts.get(i).getDescription(), actualProducts.get(i).getDescription());
			assertEquals(expectedProducts.get(i).getPrice(), actualProducts.get(i).getPrice());
		}
		verify(productRepository, times(1)).findAll();
	}

	@Test
	@Tag("integration")
	void getAllProductsWithLargeDataset() {
		// Arrange
		List<Product> largeProductList = new ArrayList<>();
		for (int i = 1; i <= 1000; i++) {
			largeProductList.add(new Product((long) i, "Product " + i, "Description " + i, i * 10.0));
		}
		when(productRepository.findAll()).thenReturn(largeProductList);
		// Act
		long startTime = System.currentTimeMillis();
		List<Product> actualProducts = productController.getAllProducts();
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		// Assert
		assertEquals(1000, actualProducts.size());
		assertTrue(executionTime < 1000, "Method execution took too long: " + executionTime + "ms");
		verify(productRepository, times(1)).findAll();
	}

}