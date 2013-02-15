package lw.app;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/bukken")
public class LwBukken extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
        response.setContentType("text/html; charset=UTF-8");
        String path = "/bukken.jsp";
        RequestDispatcher dispatch = request.getRequestDispatcher(path);
        dispatch.include(request, response);
    }
}