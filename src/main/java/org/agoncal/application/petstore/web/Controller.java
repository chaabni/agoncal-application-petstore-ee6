package org.agoncal.application.petstore.web;

import com.vaadin.ui.Notification;
import java.util.logging.Logger;
import javax.inject.Inject;
import static org.agoncal.application.petstore.util.ExceptionUtils.getRootCause;
import static org.agoncal.application.petstore.util.ExceptionUtils.isApplicationException;
import org.agoncal.application.petstore.util.Loggable;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */

@Loggable
public abstract class Controller {

    // ======================================
    // =             Attributes             =
    // ======================================

    @Inject
    private transient Logger logger;

    // ======================================
    // =          Protected Methods         =
    // ======================================

    protected void addMessage(String sourceClass, String sourceMethod, Throwable throwable) {
        Throwable cause = getRootCause(throwable);
        if (isApplicationException(cause)) {
            addWarningMessage(cause.getMessage());
        } else {
            addErrorMessage(throwable.getMessage());
            logger.throwing(sourceClass, sourceMethod, throwable);
        }
    }

    protected void addInformationMessage(String message) {
        Notification.show(message);
    }

    protected void addWarningMessage(String message) {
        Notification.show(message, Notification.Type.WARNING_MESSAGE);
    }

    protected void addErrorMessage(String message) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
        System.err.println(message);
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

}