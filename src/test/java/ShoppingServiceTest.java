import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ShoppingServiceTest {
    private final ProductDao productDao = Mockito.mock(ProductDao.class);

    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDao);

    /**
     * Тестирование метода получения всех товаров
     */
    @Test
    void getAllProductsTest() {
        List<Product> products = List.of(new Product(), new Product());
        when(productDao.getAll()).thenReturn(products);

        List<Product> actual = shoppingService.getAllProducts();

        assertEquals(products, actual);
    }

    /**
     * Тестирования метода получения товара по названию
     */
    @Test
    public void testGetProductByName() {
        Product product = new Product();
        product.setName("Pencil");

        when(productDao.getByName(any(String.class))).thenReturn(product);

        Product actual = shoppingService.getProductByName("Pencil");

        assertEquals(product.getName(), actual.getName());
        verify(productDao, times(1)).getByName("Pencil");
    }

    /**
     * Тестирования пустого списка товаров в корзине
     */
    @Test
    public void testBuyWhenCartGetProductsIsEmpty() {
        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts()).thenReturn(Collections.emptyMap());

        try {
            assertFalse(shoppingService.buy(cart));
        } catch (BuyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Тестирование покупки товаров большего количества, чем есть на самом деле
     */
    @Test
    public void testBuyThrowBuyException() {
        Map<Product, Integer> products = new HashMap<>();
        Product product = new Product();
        product.addCount(1);
        products.put(product, 2);

        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts()).thenReturn(products);

        Assertions.assertThrows(BuyException.class, () -> {
            shoppingService.buy(cart);
        });
    }

    /**
     * Тестирование покупки товаров при количестве меньшем, либо равным, чем есть на самом деле
     */
    @Test
    public void testBuyReturnTrue() {
        Map<Product, Integer> products = new HashMap<>();
        Product product = new Product();
        product.addCount(2);
        products.put(product, 1);

        Cart cart = Mockito.mock(Cart.class);
        when(cart.getProducts()).thenReturn(products);

        try {
            assertTrue(shoppingService.buy(cart));
        } catch (BuyException e) {
            throw new RuntimeException(e);
        }
    }
}
