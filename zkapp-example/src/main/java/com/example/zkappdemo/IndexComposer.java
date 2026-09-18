package com.example.zkappdemo;

import com.example.zkapp.core.api.CurrentUserService;
import com.example.zkapp.core.api.SessionService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;

/**
 * Demuestra el uso de la API publica del Starter ({@link CurrentUserService},
 * {@link SessionService}) desde el codigo de la aplicacion consumidora. No reimplementa ninguna
 * infraestructura: unicamente la consume.
 */
public class IndexComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire("#usernameLabel")
    private Label usernameLabel;

    @Wire("#rolesLabel")
    private Label rolesLabel;

    @Wire("#sessionIdLabel")
    private Label sessionIdLabel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        CurrentUserService currentUserService = SpringUtil.getApplicationContext().getBean(CurrentUserService.class);
        SessionService sessionService = SpringUtil.getApplicationContext().getBean(SessionService.class);

        usernameLabel.setValue(currentUserService.getUsername() + " (" + currentUserService.getDisplayName() + ")");
        rolesLabel.setValue(String.join(", ", currentUserService.getRoles()));
        sessionIdLabel.setValue(sessionService.getSessionId());
    }
}
