package bean;


import model.User;
import sessionManager.SessionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@SessionScoped
public class HeaderBean implements Serializable {

    private static final long serialVersionUID = 2172844342523393288L;
    private User user;

    @PostConstruct
    public void initialisation(){
        HttpSession session = SessionUtils.getSession();
        user = new User();
        user.setUserName((String) session.getAttribute("userName"));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
