package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileOrderRepository;
import drinkshop.service.validator.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ServiceValidatorRepoIntegrationTest {

    private OrderService service;
    private FileOrderRepository repo;
    private OrderValidator validator;

    @Mock
    private Repository<Integer, Product> productRepo;

    // Use a path within a data folder for consistency
    private final String TEST_FILE = "data/orders_test.txt";

    @BeforeEach
    void setUp() throws IOException {
        // 1. Initialize Mocks first
        MockitoAnnotations.openMocks(this);

        // 2. Ensure 'data' directory exists
        Path dataPath = Paths.get("data");
        if (Files.notExists(dataPath)) {
            Files.createDirectories(dataPath);
        }

        // 3. Reset the test file
        Path filePath = Paths.get(TEST_FILE);
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);

        // 4. Initialize real components and service
        repo = new FileOrderRepository(TEST_FILE, productRepo);
        validator = new OrderValidator();
        service = new OrderService(repo, productRepo);
    }

    @Test
    void testAddAndFindOrder_Integration() {
        assertNotNull(service, "Service should be initialized");

        Order order = new Order(10);
        Product p = new Product(1, "Coffee", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.WATER_BASED);
        order.addItem(new OrderItem(p, 1));
        order.setTotalPrice(10.0);

        validator.validate(order);
        service.addOrder(order);

        Order found = service.findById(10);
        assertNotNull(found);
        assertEquals(10, found.getId());
    }

    @Test
    void testDeleteOrder_Integration() {
        assertNotNull(service, "Service should be initialized");

        Product water = new Product(2, "Water", 2.0, CategorieBautura.TEA, TipBautura.WATER_BASED);
        OrderItem item = new OrderItem(water, 5);

        Order order = new Order(20);
        order.addItem(item);

        service.addOrder(order);
        service.deleteOrder(20);

        assertNull(service.findById(20));
    }
}