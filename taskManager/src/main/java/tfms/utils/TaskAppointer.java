package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TaskAppointer {
    private static final Logger log = Logger.getLogger(TaskAppointer.class);
    public static String create(User u){
        Connection connection = null;
        try {
            log.info("starting request available task ");
            connection = DataSource.getConnection();
            u.id = u.id.replace("\'","\'\'");

            //начало транзакции бегин
            Statement csBegin = connection.createStatement();
            csBegin.execute("begin;");

            //проверка допустимости назначения задачи
            if (AssignmentValidator.validate(u,connection)){
            }else {
                log.info("can not assignee task on user " + u.getId());
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                connection.close();
                log.info("transaction appoint rollback. connection close." + " user_id - " + u.getId());
                return "can not assignee task on user " + u.getId() + ". user already has task or user status is no active";
            }


            //получение айди доступной задачи
            String message = "select \"tfms\".get_suitable_task(\'"+ u.id +"\'::uuid);";
            CallableStatement cs = connection.prepareCall(message);
            ResultSet rs = cs.executeQuery();
            log.info("available task received. user_id - " + u.id);
            rs.next();
            String response = rs.getString("get_suitable_task");
            log.info("available task received. internal task_id - " + response + " user_id - " + u.id );

            //если подходящая задача не найдена
            if(response == null){
                log.info("available task not found. internal id - "+ response + " user_id - " + u.id );
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                connection.close();
                log.info("transaction appoint rollback. connection close." + " user_id - " + u.id);
                return "available task not found. internal id - "+ response;
            }

            //попытка назначения пользователя на найденную задачу Если задачу успели перехватить - апдейта не будет
            String message1 = "UPDATE tfms.task\n" +
                    "\tSET  appointed_user=\'"+ u.id +"\', task_status_id='00000000-0000-0000-0000-000000000003'\n" +
                    "\tWHERE id = \'"+response+"\'" +
                    "and appointed_user is null " +
                    "returning id" +
                    ";";
            CallableStatement csUpdate = connection.prepareCall(message1);
            ResultSet rs1 = csUpdate.executeQuery();
            rs1.next();
            String response1 = rs1.getString("id");

            //сверка айди задач. айди доступной задачи должно совпасть с айди назначенной
            if(response1.equals(response)){
                log.info("available task appointed. internal id - "+ response + " user_id - " + u.id );
            }else{
                Statement csRollback1 = connection.createStatement();
                csRollback1.execute("rollback;");
                connection.close();
                log.error("transaction appoint rollback. connection close. available task_id not equal updated task_id internal task_id - "+ response + " user_id - " + u.id );
                return "transaction appoint rollback. available task_id not equal updated task_id external id - ";
            }

            //занесение информации о назначении задачи в таблицу истории задач
            Statement csTask_status_history = connection.createStatement();
            csTask_status_history.execute("INSERT INTO tfms.task_status_history(\n" +
                    "\t task_id, status_id, start_time, user_id)\n" +
                    "\tVALUES ( \'"+ response +"\', '00000000-0000-0000-0000-000000000003', now(), \'"+u.id+"\');");
            log.info("info about available task inserted in history. internal task_id - "+ response + " user_id - " + u.id);

            //завершение транзакции
            Statement csCommit = connection.createStatement();
            csCommit.execute("commit;");
            log.info("transaction task appoint success. internal task_id - "+ response + " user_id - " + u.id);
            connection.close();
            return response;
        }catch (Exception e){
            //попытка сделать ролбэк
            try {
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                log.info("transaction appoint rollback." + " user_id - " + u.id);
            }catch (Exception e1){
                log.error("ERROR transaction appoint rollback."+ " user_id - " + u.id+" \n"+e);
            }

            //попытка закрытия коннекта
            try {
                connection.close();
                log.info("connection close. " + " user_id - " + u.id);
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null. user_id - " + u.id+"\n"+e);
            }
            log.error("ERROR appoint task on user. user_id - " + u.id+ " \n"+e);
            System.out.println(e);
            return e.getMessage();
        }
    }
}
