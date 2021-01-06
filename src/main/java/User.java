import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

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
                return User.sendMail(request);
            case "verify code":
                return User.verifyCode(request);
            case "set password":
                return User.setPassword(request);
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
            String id = result.getString(1);
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
        HttpSession session = request.getSession();
        String name = request.getParameter("name");
        String password = request.getParameter("newPassword");
        String email = request.getParameter("email");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String id = request.getParameter("id");
        String role = request.getParameter("role");
        if(session.getAttribute("user_id")==null)
            return "{\"status\":false, \"error\":\"You are not logged in\"}";

        DatabaseConnector db = new DatabaseConnector();

        String emailDuplicateCheck = "SELECT email FROM user WHERE user.email = ?;";

        ResultSet emailResult = db.getData(emailDuplicateCheck,email);

        if(emailResult.next())
            return "{\"status\":false, \"error\":\"Email already registered\"}";
        if(!id.equals(session.getAttribute("user_id")) && !session.getAttribute("user_role").equals("admin"))
            return "{\"status\":false, \"error\":\"You are not authorized to perform this action\"}";
        String sql;
        ResultSet result;
        if(id.equals(session.getAttribute("user_id"))){
            sql = "select * from user where id=? and password=md5(?)";
            result = db.getData(sql, id, request.getParameter("currentPassword"));
        }
        else {
            sql = "select * from user where id=?";
            result = db.getData(sql, id);
        }

        if(result.next())
        {
            if(name==null || name.equals(""))
                name=result.getString("name");
            if(role==null || role.equals(""))
                role=result.getString("role");
            if(email==null || email.equals(""))
                email=result.getString("email");
            if(dateOfBirth==null || dateOfBirth.equals(""))
                dateOfBirth = result.getString("date_of_birth");
            sql = "UPDATE user set name = ?, password = md5(?), email = ?, date_of_birth = ?, role=? where id=?";
            if(password==null || password.equals("")) {
                password=result.getString("password");
                sql = "UPDATE user set name = ?, password = ?, email = ?, date_of_birth = ?, role=? where id=?";
            }
            int response = db.execute(sql,name,password,email,dateOfBirth, role, id);
            if(response!=0)
                return "{\"status\":true, \"result\":\"account updated successfully\", \"name\":\""+name+"\", \"role\":\""+role+"\",\"dateOfBirth\":\""+dateOfBirth+"\"}";
            return "{\"status\":false, \"error\":\"account could not be updated\"}";
        }
        return "{\"status\":false, \"error\":\"User Error\"}";


        /*
            INPUTS
            -----------------------------
            data received from front-end:
                => name
                => currentPassword
                => dateOfBirth
                => role
                => newPassword(can be null)
                => id: this is the id of user whose record is to update
                maybe some values are null so if a value is null/ empty string then that field must not be update
            data from session: user_role             ==========>>>>>>>No ye zabardasti kry user pr no field should be empty, mazak thori ho rha hai :/
            ---------------------------------
            OUTPUTS
            ---------------------------------
            Success:
                if user record updated successfully
                return
                    status=true, result="Profile updated successfully", name, role, dateOfBirth, id
            Error:
                1. if user is not logged in (session.user_id is null)
                    OR currentPasswordValidationFails OR session.user_id!=request.id and session.user_role!="admin" (means someone tries to update profile of others and he is not even admin)
                    return
                        status=false, error="You are not authorized for this action"
                2. if record is not updated
                    return status=false, error="Could not perform action"
        */
    }
    private static String logoutUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        HttpSession session = request.getSession();
        session.invalidate();
        return "{\"status\":true, \"result\":\"You are logged out successfully\"}";
        // simply empty the session
        // return status=true, result="user is logout successfully"
    }
    private static String createUser(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        //String role = request.getParameter("role");
        String dateOfBirth = request.getParameter("dateOfBirth");

        DatabaseConnector db = new DatabaseConnector();

        String usernameDuplicateCheck = "SELECT username FROM user WHERE user.username = ?;";

        ResultSet usernameResult = db.getData(usernameDuplicateCheck,username);
        if(usernameResult.next())
            return "{\"status\":false, \"error\":\"Username already taken\"}";

        String emailDuplicateCheck = "SELECT email FROM user WHERE user.email = ?;";

        ResultSet emailResult = db.getData(emailDuplicateCheck,email);
        if(emailResult.next())
            return "{\"status\":false, \"error\":\"Email already registered\"}";

        String sql = "INSERT INTO user (name, username, password, email, role, date_of_birth) VALUES (?,?,md5(?),?,?,?)";
        int response = db.execute(sql,name, username, password, email, "guest", dateOfBirth);
        System.out.println(response);
        if(response != 0)
            return "{\"status\":true, \"result\":\"Profile Created Successfully\"}";
        else
            return "{\"status\":false, \"error\":\"Profile Could not be created\"}";

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
    }
    private static String sendMail(HttpServletRequest request) throws SQLException, ClassNotFoundException {
        String email = request.getParameter("email");
        HttpSession session = request.getSession();
        DatabaseConnector db= new DatabaseConnector();
        String emailDuplicateCheck = "SELECT id, name FROM user WHERE user.email = ?;";
        ResultSet emailResult = db.getData(emailDuplicateCheck,email);
        if(emailResult.next()){
            String id = emailResult.getString("id");
            session.setAttribute("verify_id", id);
            Random rand = new Random();
            String code = String.valueOf(rand.nextInt(1000000-100000)+100000);
            session.setAttribute("verify_code", code);
            String msg = "Hey "+emailResult.getString("name")+"! Your Verification code is: "+code;
            String subject = "Forget Password Verification Code";
            if(MailSender.sendMail(email, subject, msg))
                return "{\"status\":true, \"result\":\"Verification code sent, check email\"}";
            else
                return "{\"status\":false, \"error\":\"Could not send email\"}";
        }
        else
            return "{\"status\":false, \"error\":\"Invalid Email\"}";
    }
    private  static String verifyCode(HttpServletRequest request) throws SQLException, ClassNotFoundException{
        HttpSession session = request.getSession();
        String code = request.getParameter("verify_code");

        if(code.equals(session.getAttribute("verify_code"))){
            return "{\"status\":true, \"result\":\"Verification done successfully\"}";
        }
        return "{\"status\":true, \"error\":\"Verification code didn't match!\"}";
    }
    private  static String setPassword(HttpServletRequest request) throws SQLException, ClassNotFoundException{
        String newPassword = request.getParameter("password");
        HttpSession session = request.getSession();
        DatabaseConnector db = new DatabaseConnector();

        String sql = "UPDATE user SET password = ? WHERE id = ?";

        int result = db.execute(sql,newPassword,session.getAttribute("verify_id"));

        if(result != 0){
            return "{\"status\":true, \"result\":\"New Password Updated Successfully\"}";
        }
        return "{\"status\":true, \"error\":\"Password could not be updated!\"}";
    }
}
