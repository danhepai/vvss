package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceValidatorIntegrationTest {

    @Mock
    private Repository<Integer, Order> orderRepo;
    @Mock
    private Repository<Integer, Product> productRepo;
    
    private OrderValidator validator; // Integration test for V class
    private OrderService service;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator();
        service = new OrderService(orderRepo, productRepo);
    }

    @Test
    void testAddOrder_ValidOrder_CallsRepoSave() {
        Order validOrder = new Order(1);
        validOrder.addItem(new OrderItem(new Product(1, "Tea", 5.0, CategorieBautura.TEA, TipBautura.WATER_BASED), 2));
        
        // Manual call to validator within service logic or test
        validator.validate(validOrder); 
        service.addOrder(validOrder);

        verify(orderRepo, times(1)).save(validOrder);
    }

    @Test
    void testAddOrder_InvalidId_ThrowsValidationException() {
        Order invalidOrder = new Order(-1); // Invalid ID
        
        assertThrows(ValidationException.class, () -> {
            validator.validate(invalidOrder);
        });
        verify(orderRepo, never()).save(any());
    }
}