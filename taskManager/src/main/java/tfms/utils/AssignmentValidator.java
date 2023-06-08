package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AssignmentValidator {
    private static final Logger log = Logger.getLogger(AssignmentValidator.class);
    public static boolean validate(User u, Connection connection){
        try {
            log.info("starting validate assign for user. user_id - " + u.getId());
            u.id = u.id.replace("\'","\'\'");


            //получение кол-ва задач назначенных на пользователя
            String message = "select count(1) from tfms.task t\n" +
                    "        WHERE  appointed_user = \'"+u.getId()+"\';";
            CallableStatement cs = connection.prepareCall(message);
            ResultSet rs = cs.executeQuery();
            log.info("validate count assign for user. user_id - " + u.getId());
            rs.next();
            int response = Integer.parseInt(rs.getString("count"));



            //получение состояния пользователя
            String message1 = "SELECT user_status_id\n" +
                    "\tFROM tfms.\"user\" u\n" +
                    "\twhere u.id = \'"+u.getId()+"\'";
            CallableStatement cs1 = connection.prepareCall(message1);
            ResultSet rs1 = cs1.executeQuery();
            log.info("validate status assign for user. user_id - " + u.getId());
            rs1.next();
            String response1 = rs1.getString("user_status_id");


            if(response==0&&response1.equals("10000000-0000-4444-0000-000000000001")){
                return true;
            }
            else {
                return false;
            }


        }catch (Exception e){
            //попытка сделать ролбэк
            try {
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                connection.close();
                log.info("transaction validate assign for user rollback." + " user_id - " + u.getId());
            }catch (Exception e1){
                log.error("ERROR transaction validate assign for user rollback."+ " user_id - " + u.getId()+" \n"+e);
            }

            //попытка закрытия коннекта
            try {
                connection.close();
                log.info("connection close. " + " user_id - " + u.id);
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null. user_id - " + u.id+"\n"+e);
            }
            log.error("ERROR  validate assign for user. user_id - " + u.id+ " \n"+e);
            System.out.println(e);
            return false;
        }
    }
}

//begin
//        SELECT *
//        FROM tfms.task
//        where appointed_user = '10000000-0000-0000-0000-000000000002' for update
//
//        UPDATE tfms.task
//        SET appointed_user = null,
//        task_status_id = '00000000-0000-0000-0000-000000000002'
//        WHERE  appointed_user = '10000000-0000-0000-0000-000000000006';
//
//        commit