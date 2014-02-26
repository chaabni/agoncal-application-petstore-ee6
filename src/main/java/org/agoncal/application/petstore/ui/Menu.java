/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.UIScoped;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.Reindeer;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.agoncal.application.petstore.domain.Category;
import org.agoncal.application.petstore.service.CatalogService;
import org.vaadin.maddon.label.RichText;
import org.vaadin.maddon.layouts.MVerticalLayout;

@UIScoped
public class Menu extends MVerticalLayout {
    
    public static class CategorySelectedEvent {
        
        private final Category category;

        public CategorySelectedEvent(Category category) {
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }
        
    }
    
    @Inject
    javax.enterprise.event.Event<CategorySelectedEvent> categorySelected;
    
    @Inject
    CatalogService catalogService;

    public Menu() {
        setMargin(false);
        addComponent(new RichText().withMarkDown("## Categories"));
        setWidth(200, Unit.PIXELS);
    }
    
    @PostConstruct
    void setup() {
        List<Category> all = catalogService.findAllCategories();
        for (final Category category : all) {
            final Button button = new Button(category.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    categorySelected.fire(new CategorySelectedEvent(category));
                }
            });
            button.setStyleName(Reindeer.BUTTON_LINK);
            addComponent(button);
        }
    }
        
}
