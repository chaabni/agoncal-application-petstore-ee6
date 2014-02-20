package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.agoncal.application.petstore.domain.Address;
import org.agoncal.application.petstore.domain.CartItem;
import org.agoncal.application.petstore.domain.Customer;
import org.agoncal.application.petstore.domain.Item;
import org.agoncal.application.petstore.web.AccountController;
import org.agoncal.application.petstore.web.ShoppingCartController;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.maddon.fields.MTextField;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.label.RichText;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 * A view to check current cart, fill in credit card details and to place an
 * order.
 */
@CDIView(value = CartView.ID)
public class CartView extends MVerticalLayout implements View {

    @Inject
    ShoppingCartController cartController;

    @Inject
    AccountController accountController;

    @Inject
    CustomerDetails customerDetails;

    public static final String ID = "cart";

    private MVerticalLayout cartItems = new MVerticalLayout().withMargin(false);

    private Header total = new Header("").setHeaderLevel(3);

    private Button loginOrEditDetails = new Button("Login or create an accout",
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getUI().addWindow(customerDetails);
                }
            });

    private RichText shippingAddress = new RichText();

    private Button proceedWithCheckout = new Button("Checkout",
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (!accountController.isLoggedIn()) {
                        getUI().addWindow(customerDetails);
                    } else if (!cardForm.isValid()) {
                        Notification.show("Fill in proper credit card details");
                    } else {
                        confirmOrder();
                    }
                }

            });

    private CreditCardForm cardForm = new CreditCardForm();

    public CartView() {
        addComponents(new Header("Your shopping cart items:"), cartItems, total,
                new Header("Shipping address"), shippingAddress,
                loginOrEditDetails, cardForm, proceedWithCheckout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        update(null);
        cardForm.setCreditCard(cartController.getCreditCard());
    }

    static final String ITEM_LABEL = "x %s: %s, (%s $ each)";

    void update(@Observes CartUpdated e) {
        cartItems.removeAllComponents();

        for (final CartItem cartItem : cartController.getCartItems()) {
            final Item item = cartItem.
                    getItem();
            Label itemLabel = new Label(String.format(ITEM_LABEL, item.
                    getProduct().getName(), item.
                    getName(), item.getUnitCost()));

            TextField quantity = new MTextField(new MethodProperty(cartItem,
                    "quantity")).withWidth(3, Unit.EM);
            quantity.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    updateTotal();
                }
            });
            Button remove = new Button("X", new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    cartController.removeItemFromCart(item);
                }
            });
            cartItems.addComponent(
                    new MHorizontalLayout(quantity, itemLabel, remove).
                    withMargin(false).alignAll(Alignment.MIDDLE_LEFT));
        }
        updateTotal();
    }

    private void updateTotal() {
        total.setValue(String.format("Total: %s $", cartController.getTotal()));
    }

    void customerDetailsChanged(@Observes CustomerDetailsChanged e) {
        if (accountController.isLoggedIn()) {
            final Customer c = accountController.getLoggedinCustomer();
            final Address a = c.getHomeAddress();
            shippingAddress.withMarkDown(String.format("%s \n%s %s", a.
                    getStreet1(),
                    a.getZipcode(), a.getCity()));
            shippingAddress.setVisible(true);
            loginOrEditDetails.setCaption("Edit your details");
        } else {
            shippingAddress.setVisible(false);
            loginOrEditDetails.setCaption("Login or create an account");
        }
    }

    private void confirmOrder() {
        String confirmMessage = String.format(
                "%s $ will be drawn from your Credit card. Are you sure to proceed?",
                cartController.getTotal());
        ConfirmDialog.show(getUI(), confirmMessage,
                new ConfirmDialog.Listener() {

                    @Override
                    public void onClose(ConfirmDialog cd) {
                        if (cd.isConfirmed()) {
                            cartController.confirmOrder();
                            Notification.show(
                                    "Your order has been placed, follow emails to track your order.");
                            // Navigate back to front "page"
                            getUI().getNavigator().navigateTo("");
                        }
                    }
                });
    }

}
