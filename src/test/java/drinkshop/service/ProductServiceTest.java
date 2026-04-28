package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductServiceTest {
    private ProductService productService;
    private final String FILE_PATH = "data/products.txt";

    @BeforeEach
    public void setUp() throws IOException {
        // 1. Use Path for better cross-platform compatibility (Windows vs Linux)
        Path dataDir = Paths.get("data");
        if (Files.notExists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        // 2. Clear the file for every test to ensure isolation
        Path file = dataDir.resolve("products.txt");
        Files.write(file, new byte[0]);

        // 3. Initialize
        Repository<Integer, Product> productRepo = new FileProductRepository(FILE_PATH);
        productService = new ProductService(productRepo);
    }

    @Test
    @Tag("ECP")
    @DisplayName("TC1 ECP: Add Valid Product")
    public void testAddProduct_ValidData_ECP() {
        assertNotNull(productService, "ProductService must be initialized");
        Product produsValid = new Product(102, "Ananas", 10.0, CategorieBautura.JUICE, TipBautura.DAIRY);
        int dimensiuneInitiala = productService.getAllProducts().size();

        productService.addProduct(produsValid);

        assertEquals(dimensiuneInitiala + 1, productService.getAllProducts().size(), "Valid Product should be added with success!");
    }

    @Test
    @Tag("ECP")
    @DisplayName("TC2 ECP: Add Product with Invalid Name")
    public void testAddProduct_InvalidName_ECP() {
        Product produsNumeInvalid = new Product(2, "Mere", 10.0, CategorieBautura.JUICE, TipBautura.DAIRY);

        assertThrows(ValidationException.class, () -> {
            productService.addProduct(produsNumeInvalid);
        }, "Name is too short, at least 6 characters!");
    }

    @Test
    @Tag("ECP")
    @DisplayName("TC3 ECP: Add Product with Invalid Price")
    public void testAddProduct_InvalidPrice_ECP() {
        Product produsPretInvalid = new Product(3, "Ananas", -20.0, CategorieBautura.JUICE, TipBautura.DAIRY);

        assertThrows(ValidationException.class, () -> {
            productService.addProduct(produsPretInvalid);
        }, "Price with negative value or 0, not ok!");
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC1 BVA: Name length 6 (Valid)")
    public void testAddProduct_NameLen6_BVA() {
        Product p = new Product(101, "Ananas", 10.0, CategorieBautura.JUICE, TipBautura.DAIRY);
        int sizeBefore = productService.getAllProducts().size();

        productService.addProduct(p);

        assertEquals(sizeBefore + 1, productService.getAllProducts().size());
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC2 BVA: Name length 5 (Invalid)")
    public void testAddProduct_NameLen5_BVA() {
        Product p = new Product(102, "Anana", 10.0, CategorieBautura.JUICE, TipBautura.DAIRY);

        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC3 BVA: Name length 4 (Invalid)")
    public void testAddProduct_NameLen4_BVA() {
        Product p = new Product(103, "Anan", 10.0, CategorieBautura.JUICE, TipBautura.DAIRY);

        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC4 BVA: Price 0.01 (Valid)")
    public void testAddProduct_Price001_BVA() {
        Product p = new Product(104, "Ananas", 0.01, CategorieBautura.JUICE, TipBautura.DAIRY);
        int sizeBefore = productService.getAllProducts().size();

        productService.addProduct(p);

        assertEquals(sizeBefore + 1, productService.getAllProducts().size());
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC5 BVA: Price 0.00 (Invalid)")
    public void testAddProduct_Price000_BVA() {
        Product p = new Product(105, "Ananas", 0.00, CategorieBautura.JUICE, TipBautura.DAIRY);

        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC6 BVA: Price -0.01 (Invalid)")
    public void testAddProduct_PriceMinus001_BVA() {
        Product p = new Product(106, "Ananas", -0.01, CategorieBautura.JUICE, TipBautura.DAIRY);

        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("WBT")
    @DisplayName("WBT: Categorie is null")
    public void testExpandedFilter_NullCategorie() {
        List<Product> result = productService.expandedFilterByCategorie(null);
        assertEquals(0, result.size());
    }

    @Test
    @Tag("WBT")
    @DisplayName("WBT: Categorie is ALL")
    public void testExpandedFilter_AllCategorie() {
        productService.addProduct(new Product(201, "Produs1", 10.0, CategorieBautura.JUICE, TipBautura.PLANT_BASED));
        productService.addProduct(new Product(202, "Produs2", 15.0, CategorieBautura.ICED_COFFEE, TipBautura.BASIC));

        List<Product> result = productService.expandedFilterByCategorie(CategorieBautura.ALL);
        assertEquals(2, result.size());
    }

    @Test
    @Tag("WBT")
    @DisplayName("WBT: Specific Categorie")
    public void testExpandedFilter_SpecificCategorie() {
        productService.addProduct(new Product(301, "SucMere", 10.0, CategorieBautura.JUICE, TipBautura.PLANT_BASED));
        productService.addProduct(new Product(302, "Cafa buna", 12.0, CategorieBautura.ICED_COFFEE, TipBautura.PLANT_BASED));
        productService.addProduct(new Product(303, "SucPortocale", 11.0, CategorieBautura.JUICE, TipBautura.PLANT_BASED));

        List<Product> result = productService.expandedFilterByCategorie(CategorieBautura.JUICE);

        assertEquals(2, result.size());
    }
}