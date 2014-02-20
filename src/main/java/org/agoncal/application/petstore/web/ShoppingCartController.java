package org.agoncal.application.petstore.web;

import org.agoncal.application.petstore.domain.*;
import org.agoncal.application.petstore.service.CatalogService;
import org.agoncal.application.petstore.service.OrderService;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import org.agoncal.application.petstore.tomee.OrderMBean;
import org.agoncal.application.petstore.ui.CartUpdated;

/**
 * @author Antonio Goncalves http://www.antoniogoncalves.org --
 */
@SessionScoped
public class ShoppingCartController extends Controller implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================
    @Inject
    private CatalogService catalogBean;
    @Inject
    private OrderService orderBean;

    private List<CartItem> cartItems = new ArrayList<>();

    private CreditCard creditCard = new CreditCard();

    @Inject
    @LoggedIn
    private Instance<Customer> customerInstances;

    @Inject
    private OrderMBean mbean;

    private Order order;

    @Inject
    javax.enterprise.event.Event<CartUpdated> cartUpdatedEvent;

    // ======================================
    // =              Public Methods        =
    // ======================================
    public void addItem(Item item) {
        for (CartItem cartItem : cartItems) {
            // If item already exists in the shopping cart we just change the quantity
            if (cartItem.getItem().getId().equals(item.getId())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartUpdatedEvent.fire(new CartUpdated());
                return;
            }
        }
        // Otherwise it's added to the shopping cart
        cartItems.add(new CartItem(item, 1));
        cartUpdatedEvent.fire(new CartUpdated());

    }

    public void removeItemFromCart(Item item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().equals(item)) {
                cartItems.remove(cartItem);
                cartUpdatedEvent.fire(new CartUpdated());
                return;
            }
        }
    }

    public String updateQuantity() {
        return null;
    }

    public void confirmOrder() {
        order = orderBean.createOrder(getCustomer(), creditCard, getCartItems());
        cartItems.clear();

        mbean.incr(order.getCustomer().getFullname());

        cartUpdatedEvent.fire(new CartUpdated());
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public boolean shoppingCartIsEmpty() {
        return getCartItems() == null || getCartItems().size() == 0;
    }

    public Float getTotal() {

        if (cartItems == null || cartItems.isEmpty()) {
            return 0f;
        }

        Float total = 0f;

        // Sum up the quantities
        for (CartItem cartItem : cartItems) {
            total += (cartItem.getSubTotal());
        }
        return total;
    }

    // ======================================
    // =         Getters & setters          =
    // ======================================
    public Customer getCustomer() {
        return customerInstances.get();
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CreditCardType[] getCreditCardTypes() {
        return CreditCardType.values();
    }

    public void updateQuantity(Item item, int quantity) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getId().equals(item.getId())) {
                cartItem.setQuantity(quantity);
                return;
            }
        }

    }

}
