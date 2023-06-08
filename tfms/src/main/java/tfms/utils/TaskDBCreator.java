package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.Task;

import java.sql.*;

public class TaskDBCreator {
    private static final Logger log = Logger.getLogger(TaskDBCreator.class);
    public static String create(Task t){
        Connection connection = null;
        try {

            log.info("starting add task to db. external id -  " + t.getExternal_id());
            connection = DataSource.getConnection();
            t.task_type = t.task_type.replace("\'","\'\'");
            t.body = t.body.replace("\'","\'\'");

            //начало транзакции бегин
            Statement csBegin = connection.createStatement();
            csBegin.execute("begin;");

            // добавление экземпляра задачи
            String message = "call \"tfms\".task_register("+ t.priority +",\'"+ t.task_type +"\'::uuid,\'"+ t.body +"\'::text,\'00000000-0000-1111-0000-000000000000\'::uuid,\'"+t.external_id+"\');";
            CallableStatement cs = connection.prepareCall(message);
            ResultSet rs = cs.executeQuery();
            log.info("task added to db. external id -"  + t.getExternal_id());
            rs.next();
            String response = rs.getString("id");
            t.id = response;

            //занесение информации о назначении задачи в таблицу истории задач
            Statement csTask_status_history = connection.createStatement();
            csTask_status_history.execute("INSERT INTO tfms.task_status_history(\n" +
                    "\t task_id, status_id, start_time, user_id)\n" +
                    "\tVALUES ( \'"+ t.getId() +"\', '00000000-0000-0000-0000-000000000000', now(), \'ffffffff-ffff-ffff-ffff-ffffffffffff\');");
            log.info("info about available task inserted in history. internal task_id - "+ t.getId() + " user_id - \'ffffffff-ffff-ffff-ffff-ffffffffffff\'");

            //завершение транзакции
            Statement csCommit = connection.createStatement();
            csCommit.execute("commit;");
            connection.close();

            // назначение компетенций на задачу в отдельном коннекшн
            CompetenceToTaskAppointer ctta = new CompetenceToTaskAppointer(t);
            ctta.start();
            log.info("task added to db. external id - "  + t.getExternal_id() + " internal id - " + response);
            return response;

        }catch (Exception e){
            try {
                //роллбэк
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                connection.close();
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null external id - " + t.getExternal_id()+"\n"+e);
            }

            try {
                connection.close();
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null external id - " + t.getExternal_id()+"\n"+e);
            }

            log.error("ERROR add task to db external id - " + t.getExternal_id()+"\n"+e);
            System.out.println(e);
            return e.getMessage();
        }
    }
}
