import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;


public class APIHandler extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // it defines response type is json
        response.setContentType("application/json");
        // every request must have actionType
        ServletConfig config=getServletConfig();
        DatabaseConnector.initDatabaseStuff(config.getInitParameter("MySQL_HOST"), config.getInitParameter("MySQL_USER"), config.getInitParameter("MySQL_PASSWORD"), config.getInitParameter("MySQL_DATABASE"));
        MailSender.initMailSender(config.getInitParameter("MAIL_HOST"), config.getInitParameter("MAIL_PORT"), config.getInitParameter("MAIL_USERNAME"), config.getInitParameter("MAIL_PASSWORD"));
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
            response.getWriter().println("{\"status\":false, \"result\":\"Connection Error\", \"error\":\""+e.getMessage()+"\"}");
        }
        catch (ClassNotFoundException e) {
            response.getWriter().println("{\"status\":false, \"error\":\"Server Error!\"}");
        }
    }
}