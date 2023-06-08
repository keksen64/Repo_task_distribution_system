package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserStatusSetter {
    private static final Logger log = Logger.getLogger(UserStatusSetter.class);
    public static String create(User u){
        Connection connection = null;
        try {
            log.info("starting change user status. user id - " + u.getId() );
            connection = DataSource.getConnection();
            u.id = u.id.replace("\'","\'\'");
            u.user_status_id = u.user_status_id.replace("\'","\'\'");

            if(u.getUser_status_id().equals("active")){
                u.setUser_status_id("10000000-0000-4444-0000-000000000001");
            }
            if(u.getUser_status_id().equals("idle")){
                u.setUser_status_id("10000000-0000-4444-0000-000000000002");
            }

            //начало транзакции бегин
            Statement csBegin = connection.createStatement();
            csBegin.execute("begin;");


            //попытка смены статуса пользователя
            String message1 = "UPDATE tfms.\"user\"\n" +
                    "\tset user_status_id= \'" +u.getUser_status_id()+ "\'\n" +
                    "\tWHERE id = '" +u.getId()+ "'\n" +
                    "\treturning id, user_status_id" +
                    ";";
            CallableStatement csUpdate = connection.prepareCall(message1);
            ResultSet rs1 = csUpdate.executeQuery();
            rs1.next();
            String response_user_id = rs1.getString("id");
            String response_user_status_id = rs1.getString("user_status_id");
            u.setUser_status_id(response_user_status_id);

            //сверка айди пользователей.
            if(response_user_id.equals(u.getId())){
                log.info("change user status . user id - "+ u.getId()  );
            }else{
                Statement csRollback1 = connection.createStatement();
                csRollback1.execute("rollback;");
                connection.close();
                log.error("user status not changed. user_id - " + u.getId() );
                return "user status not changed. user_id - " + u.getId();
            }

            if(u.getUser_status_id().equals("10000000-0000-4444-0000-000000000002")){
                TasksFromUserRemover.remove(u,connection);
            }


            //завершение транзакции
            Statement csCommit = connection.createStatement();
            csCommit.execute("commit;");
            log.info("status changed. new status - " + u.getUser_status_id() + "user id - " + u.getId());
            connection.close();
            return "status changed. new status - " + u.getUser_status_id() + " user id - " + u.getId();
        }catch (Exception e){
            //попытка сделать ролбэк
            try {
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                log.info("transaction status changed rollback." + " user_id - " + u.id);
            }catch (Exception e1){
                log.error("ERROR transaction status changed rollback."+ " user_id - " + u.id+" \n"+e);
            }

            //попытка закрытия коннекта
            try {
                connection.close();
                log.info("connection close. " + " user_id - " + u.id);
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null. user_id - " + u.id+"\n"+e);
            }
            log.error("ERROR status changed. user_id - " + u.id+ " \n"+e);
            System.out.println(e);
            if(e.getMessage().equals("ResultSet not positioned properly, perhaps you need to call next.")){
                return "user with internal id " + u.getId() + " not exist.";
            }
            return e.getMessage();
        }
    }
}
