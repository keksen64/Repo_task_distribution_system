package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.Task;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class TaskGetter {

    private static final Logger log = Logger.getLogger(TaskGetter.class);
    public static String get(Task t){
        Connection connection = null;
        try {
            log.debug("starting get task. internal id -  " + t.id);
            connection = DataSource.getConnection();
            t.id = t.id.replace("\'","\'\'");
            String message = "SELECT id, appointed_user, priority, register_time, task_status_id, task_type_id, body, external_id\n" +
                    "\tFROM tfms.task t where t.id = \'"+t.id+"\'  limit 1;";
            CallableStatement cs = connection.prepareCall(message);
            ResultSet rs = cs.executeQuery();
            log.debug("task received from db. internal id -" + t.id);
            rs.next();

            if(rs.getString("id").equals("null")){
                return "tusk id - "+t.id+" not exist";
            }
            t.setAppointed_user(rs.getString("appointed_user"));
            t.setPriority(Integer.parseInt(rs.getString("priority")));
            t.setRegister_time(rs.getString("register_time"));
            t.setTask_status_id(rs.getString("task_status_id"));
            t.setTask_type(rs.getString("task_type_id"));
            t.setBody(rs.getString("body"));
            t.setExternal_id(rs.getString("external_id"));

            log.debug("task received from db. internal id -" + t.id);
            connection.close();
            return t.toJSONString();

        }catch (Exception e){
            try {
                connection.close();
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null internal id -"  + t.id + "\n"+e);
            }

            log.error("ERROR received from db. internal id -" + t.id +"\n"+e);
            System.out.println(e);
            if(e.getMessage().equals("ResultSet not positioned properly, perhaps you need to call next.")){
                return "task with internal id" + t.id + " not exist.";
            }
            return e.getMessage();
        }
    }

}
