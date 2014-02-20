/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.agoncal.application.petstore.service.CustomerService;
import org.agoncal.application.petstore.web.AccountController;
import org.agoncal.application.petstore.web.ShoppingCartController;
import org.vaadin.maddon.fields.MTextField;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 *
 */
@UIScoped
public class ShopHeader extends MHorizontalLayout {

    @Inject
    CustomerService customerService;

    @Inject
    AccountController accountController;

    @Inject
    ShoppingCartController cartController;

    @Inject
    CartView cartView;

    @Inject
    CustomerDetails customerDetails;

    public Label items = new Label("0");
    public Button cart = new Button("Cart empty", new Button.ClickListener() {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            getUI().getNavigator().navigateTo(CartView.ID);
        }
    });

    private TextField user = new MTextField().withInputPrompt("username");
    private PasswordField pw = new PasswordField();
    private Button accountDetails = new Button("<<username>>",
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getUI().addWindow(customerDetails);
                }
            });

    private MVerticalLayout toolBox = new MVerticalLayout().withMargin(false).
            withWidth(null);

    public ShopHeader() {
        final Header header = new Header("Vaadinized Petshop");
        addComponents(header, toolBox);
        withFullWidth().withMargin(false).expand(header);

        cart.setStyleName(Reindeer.BUTTON_LINK);
        accountDetails.setStyleName(Reindeer.BUTTON_LINK);

        pw.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                accountController.doLogin(user.getValue(), pw.getValue());
                setup();
            }
        });

    }

    @PostConstruct
    void setup() {
        updateToolBox();
        updateCart(null);
    }

    private void updateToolBox() {
        toolBox.removeAllComponents();
        toolBox.addComponent(cart);
        if (accountController.isLoggedIn()) {
            accountDetails.setCaption(accountController.
                    getLoggedinCustomer().getLogin());
            toolBox.addComponent(accountDetails);
        } else {
            toolBox.addComponent(new MHorizontalLayout(user, pw).withMargin(
                    false));
        }
        toolBox.alignAll(Alignment.TOP_RIGHT);
    }

    private void updateCart(@Observes CartUpdated e) {
        if (cartController.shoppingCartIsEmpty()) {
            cart.setCaption("Cart empty");
        } else {
            int size = cartController.getCartItems().size();
            cart.setCaption(String.
                    format("%s item(s) in Cart, total %s $", size,
                            cartController.getTotal()));
        }
    }

    void authenticated(@Observes CustomerDetailsChanged e) {
        setup();
    }

}
