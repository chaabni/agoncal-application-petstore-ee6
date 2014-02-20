package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.UIScoped;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import javax.inject.Inject;
import org.agoncal.application.petstore.domain.Address;
import org.agoncal.application.petstore.domain.Customer;
import org.agoncal.application.petstore.web.AccountController;
import org.vaadin.maddon.BeanBinder;
import org.vaadin.maddon.fields.MTextField;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 * Floating window to login as existing customer, register a new customer, or edit 
 * existing customer details.
 * 
 * TODO refactor into smaller peaces
 */
@UIScoped
public class CustomerDetails extends Window {

    @Inject
    javax.enterprise.event.Event<CustomerDetailsChanged> customerDetailsEvent;

    @Inject
    AccountController accountController;

    private TextField existingLogint = new MTextField("Username");
    private PasswordField existingPw = new PasswordField("Password");
    
    private Button loginButton = new Button("Login",
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    accountController.doLogin(existingLogint.getValue(),
                            existingPw.getValue());
                    customerDetailsEvent.fire(new CustomerDetailsChanged());
                    close();
                }
            });

    private Customer customerEntity = new Customer();
    private Address addressEntity = new Address();

    /* Form fields for Customer */
    private TextField login = new MTextField("Username:");
    private TextField firstname = new MTextField("Firstname:");
    private TextField lastname = new MTextField("Lastname:");
    private TextField email = new MTextField("Email:");
    private DateField dateOfBirth = new DateField("Date of birth:");

    /* Form fields for Address */
    private TextField street1 = new MTextField("Street:");
    private TextField street2 = new MTextField("Street:");
    private TextField city = new MTextField("City:");
    private TextField state = new MTextField("State:");
    private TextField zipcode = new MTextField("Zip:");
    private TextField country = new MTextField("Country:");

    private PasswordField newPw = new PasswordField("Password:");
    private PasswordField newPw2 = new PasswordField("Retype password:");

    private Button createOrSave = new Button("Create Account");

    private BeanFieldGroup<Customer> customerGroup;
    private BeanFieldGroup<Address> addressGroup;

    private final Header customerDetailsHeader = new Header("New account:");

    private Button logout = new Button("Logout", new Button.ClickListener() {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            accountController.doLogout();
            setup();
        }
    });

    private VerticalLayout loginForm = new MVerticalLayout(new Header(
            "Login as existing customer:"),
            existingLogint, existingPw, loginButton).withMargin(false);

    public CustomerDetails() {
        setModal(true);
        setWidth(80, Unit.PERCENTAGE);
        setHeight(80, Unit.PERCENTAGE);
        setCaption("Customer settings");

        setContent(new MVerticalLayout(loginForm, logout, customerDetailsHeader,
                 login, firstname, lastname, email, dateOfBirth, newPw, newPw2,
                street1, street2, city, state, zipcode, country, createOrSave));

        addAttachListener(new AttachListener() {

            @Override
            public void attach(AttachEvent event) {
                setup();
            }
        });

        newPw2.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                String pw1 = newPw.getValue();
                final boolean invalid = pw1.length() < 3 || !pw1.equals(event.
                        getText());
                if (invalid) {
                    newPw2.setComponentError(new UserError(
                            "Passwords don't match or too short"));
                    customerEntity.setPassword(null);
                } else {
                    newPw2.setComponentError(null);
                    customerEntity.setPassword(pw1);
                }
                createOrSave.setEnabled(!invalid);
            }
        });

        createOrSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (accountController.isLoggedIn()) {
                    accountController.doUpdateAccount();
                    customerDetailsEvent.fire(new CustomerDetailsChanged());
                    close();
                } else if (customerGroup.isValid() && addressGroup.isValid()) {
                    accountController.persistAndLogin(customerEntity);
                    Notification.show("Customer details saved");
                    customerDetailsEvent.fire(new CustomerDetailsChanged());
                    close();
                } else {
                    Notification.show("Check marked fields to proceed",
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });

    }

    void setup() {
        loginForm.setVisible(!accountController.isLoggedIn());
        logout.setVisible(accountController.isLoggedIn());
        customerDetailsHeader.setValue(
                accountController.isLoggedIn() ? "Edit details" : "Create account");

        if (accountController.isLoggedIn()) {
            logout.setCaption("Logout user " + accountController.getLoggedinCustomer().getLogin());
            customerEntity = accountController.getLoggedinCustomer();
            addressEntity = customerEntity.getHomeAddress();
        }
        customerGroup = BeanBinder.bind(customerEntity, this);
        addressGroup = BeanBinder.bind(addressEntity, this);
        customerEntity.setHomeAddress(addressEntity);
        createOrSave.setCaption(
                accountController.isLoggedIn() ? "Update account" : "Create account");
        createOrSave.setEnabled(accountController.isLoggedIn());
    }

}
