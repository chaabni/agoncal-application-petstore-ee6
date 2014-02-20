/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jsoup.safety.Whitelist;
import org.vaadin.maddon.label.RichText;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 *
 */
@CDIUI
public class MainUI extends UI {
    
    @Inject
    private CDIViewProvider viewProvider;
    
    @Inject
    private Menu menu;
    @Inject
    ShopHeader header;
    
    private Panel mainPanel = new Panel();
    
    @Override
    public void init(VaadinRequest request) {
        setSizeFull();
        mainPanel.setSizeFull();
        MHorizontalLayout midleRow = new MHorizontalLayout(menu,
                mainPanel).withFullWidth().withFullHeight().withMargin(false).
                expand(mainPanel);
        
        MVerticalLayout main = new MVerticalLayout(
                header, midleRow,
                new RichText().withSafeHtmlResource("/footer.html").
                setWhitelist(Whitelist.relaxed().addAttributes(":all", "style"))).
                withFullHeight();
        main.setExpandRatio(midleRow, 1);
        setContent(main);
        
        Navigator navigator = new Navigator(this, mainPanel);
        navigator.addProvider(viewProvider);
        navigator.addViewChangeListener(new ViewChangeListener() {
            
            @Override
            public boolean beforeViewChange(
                    ViewChangeListener.ViewChangeEvent event) {
                return true;
            }
            
            @Override
            public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
                mainPanel.setScrollTop(0);
            }
        });
        
    }
    
    public void categorySelected(@Observes Menu.CategorySelectedEvent e) {
        getNavigator().navigateTo("category/" + e.getCategory().getId());
    }
    
}
