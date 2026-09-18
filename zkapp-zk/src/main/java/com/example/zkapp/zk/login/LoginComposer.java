package com.example.zkapp.zk.login;

import com.example.zkapp.core.config.ZkAppProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;

/**
 * Controla la pagina de login comun del Starter ({@code /login.zul}).
 * <p>
 * El formulario se envia como una peticion HTML nativa (no AJAX) directamente a
 * {@code POST /login}, procesada por Spring Security: esta pagina no realiza autenticacion por si
 * misma, unicamente la presenta e informa del resultado de un intento anterior mediante los
 * parametros {@code error}/{@code logout}. El token CSRF se resuelve directamente en el ZUL
 * mediante EL ({@code request.getAttribute('_csrf')}): los componentes {@code native} no tienen
 * contrapartida en el servidor, por lo que no pueden conectarse (wire) a este Composer.
 */
public class LoginComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire("#titleLabel")
    private Label titleLabel;

    @Wire("#errorLabel")
    private Label errorLabel;

    @Wire("#logoutLabel")
    private Label logoutLabel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        ZkAppProperties properties = SpringUtil.getApplicationContext().getBean(ZkAppProperties.class);
        titleLabel.setValue(properties.getEffectiveUiTitle());

        HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();

        if (request.getParameter("error") != null) {
            errorLabel.setVisible(true);
        }
        if (request.getParameter("logout") != null) {
            logoutLabel.setVisible(true);
        }
    }
}
