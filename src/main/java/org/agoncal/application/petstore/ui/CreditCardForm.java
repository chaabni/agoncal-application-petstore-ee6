package org.agoncal.application.petstore.ui;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import java.util.Arrays;
import org.agoncal.application.petstore.domain.CreditCard;
import org.agoncal.application.petstore.domain.CreditCardType;
import org.vaadin.maddon.BeanBinder;
import org.vaadin.maddon.ListContainer;
import org.vaadin.maddon.fields.MTextField;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

/*
 * A form to input credit card details
 */
public class CreditCardForm extends MVerticalLayout {

    private Header billingDetails = new Header("Billing details:");
    private TextField creditCardNumber = new MTextField("Credit card number");
    private OptionGroup creditCardType = new OptionGroup("Type");
    private TextField creditCardExpDate = new MTextField("Expires");
    private BeanFieldGroup<CreditCard> fieldGroup;

    public CreditCardForm() {
        setMargin(false);
        creditCardExpDate.setInputPrompt("MM/YY");
        creditCardExpDate.setWidth(6, Unit.EM);
        creditCardType.setContainerDataSource(new ListContainer(Arrays.asList(
                CreditCardType.values())));
        addComponents(billingDetails,
                new MHorizontalLayout().withMargin(false).
                with(creditCardNumber, creditCardExpDate),
                creditCardType);
    }

    public CreditCardForm setCreditCard(CreditCard card) {
        this.fieldGroup = BeanBinder.bind(card, this).withEagarValidation();
        return this;
    }

    public boolean isValid() {
        return fieldGroup != null && fieldGroup.isValid();
    }

}
