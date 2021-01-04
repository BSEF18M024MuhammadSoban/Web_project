import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public static String performAction(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        String action = request.getParameter("action");
        switch(action) {
            case "login":
                return User.loginUser(request);
            case "signup":
                return User.createUser(request);
            case "logout":
                return User.logoutUser(request);
            case "sent forget password email":
                break;
            case "verify code":
                break;
            case "set password":
                break;
            case "update profile":
                return User.updateUser(request);
            case "delete user":
                return User.deleteUser(request);
        }
        return "{\"status\":false, \"error\":\"Invalid request\"}";
    }
    private static String loginUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        DatabaseConnector db = new DatabaseConnector();
        String sql = "select id, role from user where (username=? or email=?) and password=md5(?)";
        ResultSet result = db.getData(sql, username, username, password);

        if(result.next()) {
            int id = result.getInt(1);
            String role = result.getString(2);

            HttpSession session = request.getSession();
            session.setAttribute("user_id", id);
            session.setAttribute("user_role", role);

            return "{\"status\":true, \"result\":\"You are logged in\", \"role\":\""+role+"\"}";
        }

        return "{\"status\":false, \"error\":\"Invalid username/password\"}";
    }
    private static String deleteUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        String uid = request.getParameter("id");
        HttpSession session = request.getSession();

        if(session.getAttribute("user_role")==null || !session.getAttribute("user_role").equals("admin"))
            return "{\"status\":false, \"error\":\"You are not authorized to perform this action\"}";

        DatabaseConnector db = new DatabaseConnector();
        String sql = "delete from user where id=?";
        int response  = db.execute(sql, uid);

        if (response==1)
            return "{\"status\":true, \"result\":\"User Removed\", \"id\":"+uid+"}";
        else
            return "{\"status\":false, \"error\":\"User id is invalid\"}";
    }
    private static String updateUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        /*
            INPUTS
            -----------------------------
            data received from front-end:
                => name
                => password
                => dateOfBirth
                => role
                => id: this is the id of user whose record is to update
                maybe some values are null so if a value is null/ empty string then that field must not be update
            data from session: user_role
            ---------------------------------
            OUTPUTS
            ---------------------------------
            Success:
                if user record updated successfully
                return
                    status=true, result="Profile updated successfully", name, role, dateOfBirth, id
            Error:
                1. if user is not logged in (session.user_id is null)
                    OR session.user_id!=request.id and session.user_role!="admin" (means someone tries to update profile of others and he is not even admin)
                    return
                        status=false, error="You are not authorized for this action"
                2. if record is not updated
                    return status=false, error="Could not perform action"
        */
        return "";
    }
    private static String logoutUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        // simply empty the session
        // return status=true, result="user is logout successfully"
        return "";
    }
    private static String createUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        /*
            INPUTS
            -----------------------------
            data received from front-end:
                => name
                => password
                => dateOfBirth
            ---------------------------------
            OUTPUTS
            ---------------------------------
            Success:
                if user record added successfully
                login user(means setting user_role and user_id in session)
                return
                    status=true, result="Profile created successfully", role
            Error:
                1. username or email already exist(you may create verifier function for this function)
                    return status=false, error=username or email already exist
                2. if user is not created
                    return status=false, error=unable to perform action
        */
        return "";
    }
}
