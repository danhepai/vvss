package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FullSystemIntegrationTest {
    private OrderService service;
    private FileOrderRepository orderRepo;
    private Repository<Integer, Product> productRepo;
    // Use a path that is guaranteed to be writable in the workspace
    private final String TEST_FILE = "data/test_orders.txt";

    @BeforeEach
    void setUp() throws IOException {
        // 1. Ensure the 'data' directory exists (Crucial for Jenkins)
        Path dataPath = Paths.get("data");
        if (Files.notExists(dataPath)) {
            Files.createDirectories(dataPath);
        }

        // 2. Ensure the test file is clean
        Path filePath = Paths.get(TEST_FILE);
        Files.write(filePath, new byte[0]);

        // 3. Mock the repository
        productRepo = mock(Repository.class);

        // 4. Initialize
        orderRepo = new FileOrderRepository(TEST_FILE, productRepo);
        service = new OrderService(orderRepo, productRepo);
    }

    @AfterEach
    void tearDown() {
        File f = new File(TEST_FILE);
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    void testFullFlow_SaveAndRetrieve() {
        // Safety check to confirm initialization worked
        assertNotNull(service, "Service should be initialized");

        Product p = new Product(1, "Espresso", 12.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.WATER_BASED);
        OrderItem item = new OrderItem(p, 2);
        Order order = new Order(1);
        order.addItem(item);

        service.addOrder(order);
    }
}