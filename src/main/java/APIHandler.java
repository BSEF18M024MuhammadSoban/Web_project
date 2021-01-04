import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;


public class APIHandler extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // it defines response type is json
        response.setContentType("application/json");
        // every request must have actionType
        String actionType = request.getParameter("actionType");
        try {
            switch (actionType) {
                case "user": {
                    String jsonResponse = User.performAction(request);
                    response.getWriter().println(jsonResponse);
                }
                break;
            }
        }
        catch (SQLException e) {
            response.getWriter().println("{\"status\":false, \"error\":\"Connection Error!\"}");
        }
        catch (ClassNotFoundException e) {
            response.getWriter().println("{\"status\":false, \"error\":\"Server Error!\"}");
        }
    }
}