package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private Repository<Integer, Order> orderRepo;

    @Mock
    private Repository<Integer, Product> productRepo;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Use an AutoCloseable to ensure mocks are handled correctly in Java 11
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepo, productRepo);
    }

    @Test
    public void testAddOrder_ShouldCallSave() {
        // Safety check: if this fails, the issue is Module/Mockito setup
        assertNotNull(orderService, "Service was not initialized!");

        Order mockOrder = new Order(1);
        orderService.addOrder(mockOrder);

        verify(orderRepo, times(1)).save(mockOrder);
    }

    @Test
    public void testComputeTotal_ShouldReturnCorrectValue() {
        assertNotNull(orderService, "Service was not initialized!");

        int productId = 10;
        double price = 50.0;
        int quantity = 2;

        Product mockProduct = new Product(productId, "Bere", price, null, null);
        Order order = new Order(1, new ArrayList<>(), 0.0);
        order.addItem(new OrderItem(mockProduct, quantity));

        // Stubbing the behavior
        when(productRepo.findOne(productId)).thenReturn(mockProduct);

        double actualTotal = orderService.computeTotal(order);

        assertEquals(100.0, actualTotal, 0.001, "Calculul totalului este incorect!");
        verify(productRepo, atLeastOnce()).findOne(productId);
    }
}