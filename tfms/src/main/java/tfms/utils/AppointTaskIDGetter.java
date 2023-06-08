package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.Task;
import tfms.entity.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class AppointTaskIDGetter {

    private static final Logger log = Logger.getLogger(AppointTaskIDGetter.class);
    public static String get(User u){
        Connection connection = null;
        try {
            log.debug("starting get appointed task id. internal user id -  " + u.getId());
            connection = DataSource.getConnection();
            u.id = u.id.replace("\'","\'\'");
            String message = "SELECT id\n" +
                    "\tFROM tfms.task t where t.appointed_user = \'"+u.getId()+"\'  limit 1;";
            CallableStatement cs = connection.prepareCall(message);
            ResultSet rs = cs.executeQuery();
            log.debug("appointed task id received from db. internal user id -" + u.getId());
            rs.next();

            if(rs.getString("id").equals("null")){
                return "no tasks appointed";
            }
            String appointedTaskID = rs.getString("id");

            log.debug("appointed task received from db. internal id -" + appointedTaskID);
            connection.close();
            return appointedTaskID;

        }catch (Exception e){
            try {
                connection.close();
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null internal user id -"  + u.getId() + "\n"+e);
            }

            log.error("ERROR received appointed task id from db. internal user id -" + u.getId() +"\n"+e);
            System.out.println(e);
            if(e.getMessage().equals("ResultSet not positioned properly, perhaps you need to call next.")){
                return "no tasks appointed. internal user id" + u.getId() ;
            }
            return e.getMessage();
        }
    }

}
