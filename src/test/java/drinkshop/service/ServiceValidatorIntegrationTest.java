package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceValidatorIntegrationTest {

    @Mock
    private Repository<Integer, Order> orderRepo;
    @Mock
    private Repository<Integer, Product> productRepo;

    private OrderValidator validator;
    private OrderService service;

    @BeforeEach
    void setUp() {
        // Explicitly open mocks to avoid NPEs in strict environments
        MockitoAnnotations.openMocks(this);
        validator = new OrderValidator();
        service = new OrderService(orderRepo, productRepo);
    }

    @Test
    void testAddOrder_ValidOrder_CallsRepoSave() {
        assertNotNull(service, "Service should be initialized");

        Order validOrder = new Order(1);
        Product p = new Product(1, "Tea", 5.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        validOrder.addItem(new OrderItem(p, 2));

        // Ensure the validator passes
        validator.validate(validOrder);

        // Test the service call
        service.addOrder(validOrder);

        verify(orderRepo, times(1)).save(validOrder);
    }

    @Test
    void testAddOrder_InvalidId_ThrowsValidationException() {
        assertNotNull(service, "Service should be initialized");
        Order invalidOrder = new Order(-1);

        // Assert that the validator catches the bad ID
        assertThrows(ValidationException.class, () -> {
            validator.validate(invalidOrder);
        });

        // Verify the repo is never touched when validation fails
        verify(orderRepo, never()).save(any());
    }
}