package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.Task;
import tfms.entity.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// В метод необходимо добавить защиту от смены статуса задачи находящейсая на пользователе отличном от отправляющего запрос
public class TaskUpdateStatus {
    private static final Logger log = Logger.getLogger(TaskUpdateStatus.class);
    public static String change(Task t){
        Connection connection = null;
        try {
            log.info("starting change task status. task id - " + t.getId() );
            connection = DataSource.getConnection();
            t.id = t.id.replace("\'","\'\'");

            if(t.getTask_status_id().equals("00000000-0000-0000-0000-000000000004")){
                //начало транзакции бегин
                Statement csBegin = connection.createStatement();
                csBegin.execute("begin;");


                //попытка смены статуса задачи
                String message1 = "UPDATE tfms.task t\n" +
                        "\tset task_status_id= '00000000-0000-0000-0000-000000000004'\n" +
                        "\tWHERE t.id = \'"+ t.getId() +"\'\n" +
                        "\tand t.task_status_id = '00000000-0000-0000-0000-000000000003' returning task_status_id;";
                CallableStatement csUpdate = connection.prepareCall(message1);
                ResultSet rs1 = csUpdate.executeQuery();
                rs1.next();
                String response_task_status_id = rs1.getString("task_status_id");
                //t.setTask_status_id(response_task_status_id);

                //сверка айди статусов.
                if(response_task_status_id.equals(t.getTask_status_id())){
                    log.info("change task status . task id - "+ t.getId() +" new status - " + t.getTask_status_id() );
                }else{
                    Statement csRollback1 = connection.createStatement();
                    csRollback1.execute("rollback;");
                    connection.close();
                    log.error("task status not changed. task_id - " + t.getId() );
                    return "task status not changed. task_id - " + t.getId();
                }


                //занесение информации о снятии задач в таблицу истории задач
                Statement csTask_status_history = connection.createStatement();
                csTask_status_history.execute("INSERT INTO tfms.task_status_history(task_id, status_id, start_time, user_id)\n" +
                        "                    values ('"+t.getId()+"', '00000000-0000-0000-0000-000000000004', now(), '"+ t.getAppointed_user() +"')\n"
                );
                log.info("info about change task status inserted in history. internal task_id - " + t.getId() + " user_id - " +t.getAppointed_user());


                //завершение транзакции
                Statement csCommit = connection.createStatement();
                csCommit.execute("commit;");
                log.info("status changed. new status - " + t.getTask_status_id() + " user id - " + t.getAppointed_user());
                connection.close();
                return "status changed. new status - " + t.getTask_status_id() + " user id - " + t.getAppointed_user();
            }else {
                if(t.getTask_status_id().equals("00000000-0000-0000-0000-000000000005")){
                    //начало транзакции бегин
                    Statement csBegin = connection.createStatement();
                    csBegin.execute("begin;");


                    //попытка смены статуса задачи
                    String message1 = "UPDATE tfms.task t\n" +
                            "\tset task_status_id= '00000000-0000-0000-0000-000000000005', final_user = '"+t.getAppointed_user()+"', appointed_user = NULL\n" +
                            "\tWHERE t.id = \'"+ t.getId() +"\'\n" +
                            "\tand t.task_status_id = '00000000-0000-0000-0000-000000000004' " +
                            "returning task_status_id;";
                    CallableStatement csUpdate = connection.prepareCall(message1);
                    ResultSet rs1 = csUpdate.executeQuery();
                    rs1.next();
                    String response_task_status_id = rs1.getString("task_status_id");
                    //t.setTask_status_id(response_task_status_id);

                    //сверка айди статусов.
                    if(response_task_status_id.equals(t.getTask_status_id())){
                        log.info("change task status . task id - "+ t.getId() +" new status - " + t.getTask_status_id() );
                    }else{
                        Statement csRollback1 = connection.createStatement();
                        csRollback1.execute("rollback;");
                        connection.close();
                        log.error("task status not changed. task_id - " + t.getId() );
                        return "task status not changed. task_id - " + t.getId();
                    }


                    //занесение информации о снятии задач в таблицу истории задач
                    Statement csTask_status_history = connection.createStatement();
                    csTask_status_history.execute("INSERT INTO tfms.task_status_history(task_id, status_id, start_time, user_id)\n" +
                            "                    values ('"+t.getId()+"', '00000000-0000-0000-0000-000000000005', now(), '"+ t.getAppointed_user() +"')\n"
                    );
                    log.info("info about change task status inserted in history. internal task_id - " + t.getId() + " user_id - " +t.getAppointed_user());


                    //завершение транзакции
                    Statement csCommit = connection.createStatement();
                    csCommit.execute("commit;");
                    log.info("status changed. new status - " + t.getTask_status_id() + " user id - " + t.getAppointed_user());
                    connection.close();
                    return "status changed. new status - " + t.getTask_status_id() + " user id - " + t.getAppointed_user();
                }else {
                    log.info("status not changed. required status - " + t.getTask_status_id() + " user id - " + t.getAppointed_user() + " task id - " + t.getId());
                    return "in this method only statuses \"10000000-0000-0000-0000-000000000004\" and \"10000000-0000-0000-0000-000000000005\" can be set";
                }
            }



        }catch (Exception e){
            //попытка сделать ролбэк
            try {
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                log.info("transaction task status changed rollback." + " task_id - " + t.getId() + " user_id - " + t.getAppointed_user());
            }catch (Exception e1){
                log.error("ERROR transaction task status changed rollback."+ " task_id - " + t.getId() + " user_id - " + t.getAppointed_user() + " \n"+e);
            }

            //попытка закрытия коннекта
            try {
                connection.close();
                log.info("connection close. " + " task_id - " + t.getId() + " user_id - " + t.getAppointed_user());
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null. task_id - " + t.getId() + " user_id - " + t.getAppointed_user() + " \n"+e);
            }
            log.error("ERROR status changed. task_id - " + t.getId() + " user_id - " + t.getAppointed_user() + " \n"+e);
            System.out.println(e);
            if(e.getMessage().equals("ResultSet not positioned properly, perhaps you need to call next.")){
                return "task status not changed. task_id - " + t.getId() + " user_id - " + t.getAppointed_user();
            }
            return e.getMessage();
        }
    }
}
