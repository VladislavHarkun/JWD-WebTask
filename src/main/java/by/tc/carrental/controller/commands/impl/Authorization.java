package by.tc.carrental.controller.commands.impl;

import by.tc.carrental.controller.commands.Command;
import by.tc.carrental.model.entity.User;
import by.tc.carrental.model.service.ServiceException;
import by.tc.carrental.model.service.ServiceProvider;
import by.tc.carrental.model.service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class Authorization implements Command {
    private static final String LOGIN_PARAM = "login";
    private static final String PASSWORD_PARAM = "password";
    private static final String LOAD_MAIN_PAGE = "Controller?command=loadmainpage";
    private static final String AUTH_ATTRIBUTE = "authorization";
    private static final String AUTH_PAGE_PATH = "jsp/user/authorization.jsp";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter(LOGIN_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);

        User user = new User(login, password);
        ServiceProvider serviceProvider = ServiceProvider.getInstance();
        UserService userService = serviceProvider.getUserService();

        User authorizedUser = null;

        try {
            authorizedUser = userService.authorization(user);
            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute(AUTH_ATTRIBUTE, true);
            response.sendRedirect(LOAD_MAIN_PAGE);

        } catch (ServiceException e) {
            request.setAttribute("message", e.getMessage());
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(AUTH_PAGE_PATH);
            requestDispatcher.forward(request, response);
        }
    }
}
