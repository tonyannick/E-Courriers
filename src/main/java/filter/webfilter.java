package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebFilter(filterName = "AuthFilter", urlPatterns = { "*.xhtml" }, dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class webfilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest reqt = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession ses = reqt.getSession(false);
        String reqURI = reqt.getRequestURI();
        if (reqURI.indexOf("/login.xhtml") >= 0
                || (ses != null && ses.getAttribute("userName") != null)
                || reqURI.indexOf("/public/") >= 0
                || reqURI.contains("javax.faces.resource"))
            filterChain.doFilter(request, response);
        else
            resp.sendRedirect(reqt.getContextPath() + "/faces/login.xhtml");
    }
}
