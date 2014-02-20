package org.agoncal.application.petstore.service;

import org.agoncal.application.petstore.domain.Category;
import org.agoncal.application.petstore.domain.Item;
import org.agoncal.application.petstore.domain.Product;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Ignore;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 */
//@Ignore
@RunWith(Arquillian.class)
@Ignore
public class CatalogServiceTest extends AbstractServiceTest {

    // ======================================
    // =             Attributes             =
    // ======================================

    @Inject
    private CatalogService catalogService;

    // ======================================
    // =          Lifecycle Methods         =
    // ======================================

    // ======================================
    // =              Unit tests            =
    // ======================================

    @Test
    public void shouldCRUDaCategory() {

        // Finds all the objects
        int initialNumber = catalogService.findAllCategories().size();

        // Creates an object
        Category category = new Category("Fish", "Any of numerous cold-blooded aquatic vertebrates characteristically having fins, gills, and a streamlined body");

        // Persists the object
        category = catalogService.createCategory(category);
        Long id = category.getId();

        // Finds all the objects and checks there's an extra one
        assertEquals("Should have an extra object", initialNumber + 1, catalogService.findAllCategories().size());

        // Finds the object by primary key
        category = catalogService.findCategory(id);
        assertEquals("Fish", category.getName());

        // Updates the object
        category.setName("Big Fish");
        catalogService.updateCategory(category);

        // Finds the object by primary key
        category = catalogService.findCategory(id);
        assertEquals("Big Fish", category.getName());

        // Deletes the object
        catalogService.removeCategory(category);

        // Checks the object has been deleted
        assertNull("Should has been deleted", catalogService.findCategory(id));

        // Finds all the objects and checks there's one less
        assertEquals("Should have an extra object", initialNumber, catalogService.findAllCategories().size());
    }

    @Test
    public void shouldCRUDaProduct() {

        // Finds all the objects
        int initialNumber = catalogService.findAllProducts().size();

        // Creates an object
        Category category = new Category("Fish", "Any of numerous cold-blooded aquatic vertebrates characteristically having fins, gills, and a streamlined body");
        Product product = new Product("Angelfish", "Saltwater fish from Australia", category);

        // Persists the object
        product = catalogService.createProduct(product);
        Long id = product.getId();

        // Finds all the objects and checks there's an extra one
        assertEquals("Should have an extra object", initialNumber + 1, catalogService.findAllProducts().size());

        // Finds the object by primary key
        product = catalogService.findProduct(id);
        assertEquals("Angelfish", product.getName());

        // Updates the object
        product.setName("Big Angelfish");
        catalogService.updateProduct(product);

        // Finds the object by primary key
        product = catalogService.findProduct(id);
        assertEquals("Big Angelfish", product.getName());

        // Deletes the object
        catalogService.removeProduct(product);

        // Checks the object has been deleted
        assertNull("Should has been deleted", catalogService.findProduct(id));

        // Finds all the objects and checks there's one less
        assertEquals("Should have an extra object", initialNumber, catalogService.findAllProducts().size());
    }

    @Test
    public void shouldCRUDanItem() {

        // Finds all the objects
        int initialNumber = catalogService.findAllItems().size();

        // Creates an object
        Category category = new Category("Fish", "Any of numerous cold-blooded aquatic vertebrates characteristically having fins, gills, and a streamlined body");
        Product product = new Product("Angelfish", "Saltwater fish from Australia", category);
        Item item = new Item("Large", 10.00f, "fish1.jpg", product, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum velit ante, malesuada porta condimentum eget, tristique id magna. Donec ac justo velit. Suspendisse potenti. Donec vulputate vulputate molestie. Quisque vitae arcu massa, dictum sodales leo. Sed feugiat elit vitae ante auctor ultrices. Duis auctor consectetur arcu id faucibus. Curabitur gravida.");

        // Persists the object
        item = catalogService.createItem(item);
        Long id = item.getId();

        // Finds all the objects and checks there's an extra one
        assertEquals("Should have an extra object", initialNumber + 1, catalogService.findAllItems().size());

        // Finds the object by primary key
        item = catalogService.findItem(id);
        assertEquals("Large", item.getName());

        // Updates the object
        item.setName("Large fish");
        catalogService.updateItem(item);

        // Finds the object by primary key
        item = catalogService.findItem(id);
        assertEquals("Large fish", item.getName());

        // Deletes the object
        catalogService.removeItem(item);

        // Checks the object has been deleted
        assertNull("Should has been deleted", catalogService.findItem(id));

        // Finds all the objects and checks there's one less
        assertEquals("Should have an extra object", initialNumber, catalogService.findAllItems().size());
    }

    @Test
    public void itemPriceValidation() {

        // Creates an object
        Category category = new Category("Fish", "Here fishy fishy fishy!");
        Product product = new Product("Angelfish", "Saltwater fish from Australia", category);
        Item item = new Item("Sickly Fish", 5.00f, "fish1.jpg", product, "This one doesn't look so healthy, sell it cheap!");

        try {
            // Persists the object
            item = catalogService.createItem(item);

            fail("Price should violate @Price constraint");
        } catch (EJBException e) {
            if (!(e.getCause() instanceof ConstraintViolationException)) {
                fail("Wrong exception type received");
            }

            // PASS -- ConstraintViolationException
        }
    }
}
