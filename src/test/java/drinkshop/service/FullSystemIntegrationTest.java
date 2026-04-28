package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileOrderRepository;
import drinkshop.service.validator.OrderValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FullSystemIntegrationTest {
    private OrderService service;
    private FileOrderRepository orderRepo;
    private Repository<Integer, Product> productRepo;
    private final String TEST_FILE = "test_orders.txt";

    @BeforeEach
    void setUp() {
        // We still mock ProductRepo because we don't want to rely on
        // two different files at once, keeping the test focused.
        productRepo = mock(Repository.class);

        // Use the actual File Repository
        orderRepo = new FileOrderRepository(TEST_FILE, productRepo);
        service = new OrderService(orderRepo, productRepo);
    }

    @AfterEach
    void tearDown() {
        // Clean up: delete the test file so the next test starts fresh
        new File(TEST_FILE).delete();
    }

    @Test
    void testFullFlow_SaveAndRetrieve() {
        Product p = new Product(1, "Espresso", 12.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.WATER_BASED);

        OrderItem item = new OrderItem(p, 2);

        Order order = new Order(1);
        order.addItem(item);

        service.addOrder(order);
    }
}