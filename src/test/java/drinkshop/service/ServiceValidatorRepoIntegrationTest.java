package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileOrderRepository;
import drinkshop.service.validator.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceValidatorRepoIntegrationTest {

    private OrderService service;
    private FileOrderRepository repo; // Integration for R class
    private OrderValidator validator;
    
    @Mock
    private Repository<Integer, Product> productRepo; // Service dependency

    private final String TEST_FILE = "orders_test.txt";

    @BeforeEach
    void setUp() throws IOException {
        File f = new File(TEST_FILE);
        if (f.exists()) {
            f.delete();
        }

        repo = new FileOrderRepository(TEST_FILE, productRepo);
        validator = new OrderValidator();
        service = new OrderService(repo, productRepo);
    }

    @Test
    void testAddAndFindOrder_Integration() {
        Order order = new Order(10);
        order.addItem(new OrderItem(new Product(1, "Coffee", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.WATER_BASED), 1));
        order.setTotalPrice(10.0);

        validator.validate(order);
        service.addOrder(order);

        Order found = service.findById(10);
        assertNotNull(found);
        assertEquals(10, found.getId());
    }

    @Test
    void testDeleteOrder_Integration() {
        // 1. Use a real Product with a real ID
        Product water = new Product(2, "Water", 2.0, CategorieBautura.TEA, TipBautura.WATER_BASED);

        // 2. Wrap it in an OrderItem
        OrderItem item = new OrderItem(water, 5);

        Order order = new Order(20);
        order.addItem(item);

        // 3. This will now work because item.getProduct().getId() returns 2
        service.addOrder(order);

        service.deleteOrder(20);

        assertNull(service.findById(20));
    }
}