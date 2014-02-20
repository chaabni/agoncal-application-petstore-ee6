package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;
import java.util.List;
import javax.inject.Inject;
import org.agoncal.application.petstore.domain.Category;
import org.agoncal.application.petstore.domain.Item;
import org.agoncal.application.petstore.domain.Product;
import org.agoncal.application.petstore.service.CatalogService;
import org.agoncal.application.petstore.web.ShoppingCartController;
import org.vaadin.maddon.label.RichText;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 * A view to display categories, products and items. 
 * 
 * TODO should be split into smaller pieces
 */
@CDIView(value = "category", supportsParameters = true)
public class CategoryView extends MVerticalLayout implements View {

    @Inject
    CatalogService catalogService;

    @Inject
    ShoppingCartController cartController;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        String viewId = event.getParameters();
        Category category = catalogService.findCategory(Long.parseLong(viewId));
        addComponent(new RichText().withMarkDown("#" + category.getName() + "\n" + category.getDescription()));
        
        int columns = (Page.getCurrent().getBrowserWindowWidth() - 300) / 300;
        List<Product> products = catalogService.findProducts(category.getName());
        if (products != null && !products.isEmpty()) {
            GridLayout productLayout = new GridLayout(columns, 1);
            productLayout.setWidth("100%");
            for (Product product : products) {
                productLayout.addComponent(new ProductView(product));
            }
            addComponent(productLayout);
        }
    }

    class ProductView extends MVerticalLayout {

        private final Product product;

        public ProductView(Product p) {
            this.product = p;

            addComponent(new RichText().withMarkDown(
                    String.format("## %s \n %s", p.getName(), p.getDescription())));
            final Button showItems = new Button("Show items", new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    List<Item> findItems = catalogService.findItems(product.getId());
                    for (Item item : findItems) {
                        addComponent(new ItemView(item));
                    }
                    removeComponent(event.getButton());
                }
            });
            showItems.setStyleName(Reindeer.BUTTON_LINK);
            addComponent(showItems);

        }

        class ItemView extends MHorizontalLayout {

            private final Item item;

            private Button addToCart = new Button("Add to cart", new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    cartController.addItem(item);
                }
            });

            public ItemView(Item i) {
                setMargin(false);
                this.item = i;
                withFullWidth();

                addToCart.setStyleName(Reindeer.BUTTON_LINK);
                final Label header = new Label(item.getName());
                header.setDescription(item.getDescription());
                Label cost = new Label(item.getUnitCost() + " $");
                addComponents(header, cost, addToCart);

            }

        }

    }

}
