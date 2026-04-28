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
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private Repository<Integer, Order> orderRepo;

    @Mock
    private Repository<Integer, Product> productRepo;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepo, productRepo);
    }

    /**
     * Test 1: Verifică adăugarea unei comenzi.
     * Evidențiază utilizarea VERIFY conform Lab04.pdf.
     */
    @Test
    public void testAddOrder_ShouldCallSave() {
        Order mockOrder = new Order(1);

        orderService.addOrder(mockOrder);

        verify(orderRepo, times(1)).save(mockOrder);
    }

    /**
     * Test 2: Verifică calculul totalului comenzii.
     * Evidențiază utilizarea ASSERT și configurarea comportamentului mock-urilor (Stubbing).
     */
    @Test
    public void testComputeTotal_ShouldReturnCorrectValue() {
        int productId = 10;
        double price = 50.0;
        int quantity = 2;

        Product mockProduct = new Product(productId, "Bere", price, null, null);
        Order order = new Order(1, new ArrayList<>(), 0.0);
        order.addItem(new OrderItem(mockProduct, quantity));

        when(productRepo.findOne(productId)).thenReturn(mockProduct);

        double actualTotal = orderService.computeTotal(order);

        assertEquals(100.0, actualTotal, 0.001, "Calculul totalului este incorect!");

        verify(productRepo, atLeastOnce()).findOne(productId);
    }
}