package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TasksFromUserRemover {
    private static final Logger log = Logger.getLogger(TasksFromUserRemover.class);
    public static String remove(User u, Connection connection){
        try {
            log.info("starting remove task from user "+ u.getId());
            u.id = u.id.replace("\'","\'\'");


            //занесение информации о снятии задач в таблицу истории задач
            Statement csTask_status_history = connection.createStatement();
            csTask_status_history.execute("INSERT INTO tfms.task_status_history(\n" +
                    "\ttask_id, status_id, start_time, user_id)\n" +
                    "\t\n" +
                    "\t\n" +
                    "\tselect t.id, '00000000-0000-0000-0000-000000000002' , now(), 'ffffffff-ffff-ffff-ffff-ffffffffffff'\n" +
                    "\tfrom tfms.task  t \n" +
                    "\twhere appointed_user = \'" +u.getId()+ "\';");
            log.info("info about remove task from user inserted in history. internal user_id - " + u.getId());


            //обновление информации о снятии задач в таблицу задач
            Statement csTask = connection.createStatement();
            csTask_status_history.execute("UPDATE tfms.task\n" +
                    "        SET appointed_user = null,\n" +
                    "        task_status_id = '00000000-0000-0000-0000-000000000002'\n" +
                    "        WHERE  appointed_user = \'" +u.getId()+ "\';");
            log.info("task remove from user. internal. user_id - " + u.getId());



            return "0";
        }catch (Exception e){
            //попытка сделать ролбэк
            try {
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                connection.close();
                log.info("transaction remove task from user rollback." + " user_id - " + u.getId());
            }catch (Exception e1){
                log.error("ERROR transaction remove task from user rollback."+ " user_id - " + u.id+" \n"+e);
            }

            //попытка закрытия коннекта
            try {
                connection.close();
                log.info("connection close. " + " user_id - " + u.id);
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null. user_id - " + u.id+"\n"+e);
            }
            log.error("ERROR remove task from user. user_id - " + u.id+ " \n"+e);
            System.out.println(e);
            return e.getMessage();
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