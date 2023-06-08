package tfms.utils;

import org.apache.log4j.Logger;
import tfms.entity.Task;
import tfms.main.MainClass;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class CompetenceToTaskAppointer extends Thread{
    private Task t;
    private static final Logger log = Logger.getLogger(CompetenceToTaskAppointer.class);
    Connection connection = null;

    public CompetenceToTaskAppointer(Task t) {
        this.t = t;
    }

    public void run(){
        try {

            log.info("starting add task competence external id -  internal id - "+t.id);
            connection = DataSource.getConnection();
            t.id = t.id.replace("\'","\'\'");

            //начало транзакции бегин
            Statement csBegin = connection.createStatement();
            csBegin.execute("begin;");

            // назначение компетенций на задачу
            String message = "call tfms.task_competence_appoint(\'"+t.id+"\'::uuid)";
            Statement cs = connection.createStatement();
            cs.executeUpdate(message);
            log.info("task competence added to db. external id - internal id - "+t.id);

            //занесение информации о назначении задачи в таблицу истории задач
            Statement csTask_status_history = connection.createStatement();
            csTask_status_history.execute("INSERT INTO tfms.task_status_history(\n" +
                    "\t task_id, status_id, start_time, user_id)\n" +
                    "\tVALUES ( \'"+ t.getId() +"\', '00000000-0000-0000-0000-000000000001', now(), \'ffffffff-ffff-ffff-ffff-ffffffffffff\');");
            log.info("info about available task inserted in history. internal task_id - "+ t.getId() + " user_id - \'ffffffff-ffff-ffff-ffff-ffffffffffff\'");

            //коммит
            Statement csCommit = connection.createStatement();
            csCommit.execute("commit;");

            connection.close();


        }catch (Exception e){
            try {
                //коммит
                Statement csRollback = connection.createStatement();
                csRollback.execute("rollback;");
                connection.close();
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null external id - internal task id - "+t.id+"\n"+e);
            }
            try {
                connection.close();
            }catch (Exception e1){
                log.error("ERROR close connection or connection is null external id - internal task id - "+t.id+"\n"+e);
            }
            log.error("ERROR add task competence to db internal task id - "+t.id+"\n"+e);
        }



    }
}
