/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agoncal.application.petstore.ui;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import javax.annotation.PostConstruct;
import org.vaadin.maddon.label.RichText;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 *
 */
@CDIView(value = "")
public class WelcomeView extends MVerticalLayout implements View {

    @PostConstruct
    void setup() {
        addComponent(new RichText().withMarkDownResource("/welcome.md"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
