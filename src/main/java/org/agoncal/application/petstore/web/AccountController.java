package org.agoncal.application.petstore.web;

import java.io.Serializable;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.login.LoginContext;
import org.agoncal.application.petstore.domain.Customer;
import org.agoncal.application.petstore.service.CustomerService;
import org.agoncal.application.petstore.ui.CustomerDetailsChanged;

/**
 * @author Antonio Goncalves http://www.antoniogoncalves.org --
 */
@Named
@SessionScoped
public class AccountController extends Controller implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================
    @Inject
    private CustomerService customerService;

    @Inject
    private Credentials credentials;

    @Inject
    private Conversation conversation;

    @Produces
    @LoggedIn
    private Customer loggedinCustomer;

    @Inject
    @SessionScoped
    private transient LoginContext loginContext;

    @Inject
    private Event<CustomerDetailsChanged> customerDetailsChangedEvent;

    // ======================================
    // =              Public Methods        =
    // ======================================
    public void doLogin(String user, String pw) {
        credentials.setLogin(user);
        credentials.setPassword(pw);
        doLogin();
    }

    public String doLogin() {

        String navigateTo = null;
        try {
            loginContext.login();
            loggedinCustomer = customerService.findCustomer(credentials.
                    getLogin());

            navigateTo = "main.faces";
        } catch (Exception e) {
            addMessage(this.getClass().getName(), "doLogin", e);
        }
        customerDetailsChangedEvent.fire(new CustomerDetailsChanged());
        return navigateTo;
    }

    public String doCreateNewAccount() {

        // Login has to be unique
        if (customerService.doesLoginAlreadyExist(credentials.getLogin())) {
            addWarningMessage("Login already exists");
            return null;
        }

        // Id and password must be filled
        if ("".equals(credentials.getLogin()) || "".equals(credentials.
                getPassword()) || "".equals(credentials.getPassword2())) {
            addWarningMessage("Id and passwords have to be filled");
            return null;
        } else if (!credentials.getPassword().equals(credentials.getPassword2())) {
            addWarningMessage("Both entered passwords have to be the same");
            return null;
        }

        // Login and password are ok
        loggedinCustomer = new Customer();
        loggedinCustomer.setLogin(credentials.getLogin());
        loggedinCustomer.setPassword(credentials.getPassword());

        return "createaccount.faces";
    }

    public String doCreateCustomer() {
        String navigateTo = null;

        try {
            // Creates the customer
            loggedinCustomer = customerService.createCustomer(loggedinCustomer);

            navigateTo = "main.faces";
        } catch (Exception e) {
            addMessage(this.getClass().getName(), "doCreateCustomer", e);
        }
        return navigateTo;
    }

    public void doLogout() {
        loggedinCustomer = null;
        // Stop conversation
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    public void doUpdateAccount() {
        loggedinCustomer = customerService.updateCustomer(loggedinCustomer);
    }

    public boolean isLoggedIn() {
        return loggedinCustomer != null;
    }

    public Customer getLoggedinCustomer() {
        return loggedinCustomer;
    }

    public void setLoggedinCustomer(Customer loggedinCustomer) {
        this.loggedinCustomer = loggedinCustomer;
    }

    public void persistAndLogin(Customer newCustomer) {
        setLoggedinCustomer(customerService.createCustomer(newCustomer));
    }

}
