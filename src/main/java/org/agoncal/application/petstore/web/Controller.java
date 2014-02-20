package org.agoncal.application.petstore.web;

import com.vaadin.ui.Notification;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    protected void addWarningMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
    }

    protected void addErrorMessage(String message) {
        Notification.show(message);
        System.err.println(message);
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    protected String getParam(String param) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> map = context.getExternalContext().getRequestParameterMap();
        return map.get(param);
    }

    protected Long getParamId(String param) {
        return Long.valueOf(getParam(param));
    }
}