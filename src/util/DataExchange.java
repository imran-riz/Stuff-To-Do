/*
 * Copyright (c) 2022 by Imran R.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package util;

import models.TaskToDo;
import values.Repeat;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataExchange {

    public static void writeToFile(String pathToFile, List<TaskToDo> listOfTasks) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(pathToFile) ;

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream) ;

        for (TaskToDo taskToDo : listOfTasks) {
            objectOutputStream.writeObject(taskToDo.getId());
            objectOutputStream.writeObject(taskToDo.getText()) ;
            objectOutputStream.writeObject(taskToDo.getCompleted()) ;
            objectOutputStream.writeObject(taskToDo.getDueDate()) ;
            objectOutputStream.writeObject(taskToDo.getReminderDate()) ;
            objectOutputStream.writeObject(taskToDo.getReminderTime()) ;
            objectOutputStream.writeObject(taskToDo.getRepeat());
            objectOutputStream.writeObject(taskToDo.getNotes());
        }

        objectOutputStream.close() ;
        fileOutputStream.close() ;
    }


    public static List<TaskToDo> readFromFile(String pathToFile) throws Exception {
        List<TaskToDo> listOfTasks = new ArrayList<>() ;

        try {
            FileInputStream fileInputStream = new FileInputStream(pathToFile) ;
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream) ;

            while (true) {
                String taskId = ((String) objectInputStream.readObject()) ;
                String taskText = ((String) objectInputStream.readObject()) ;
                Boolean taskCompleted = ((Boolean) objectInputStream.readObject()) ;
                LocalDate taskDueDate = ((LocalDate) objectInputStream.readObject()) ;
                LocalDate taskReminderDate = ((LocalDate) objectInputStream.readObject()) ;
                LocalTime taskReminderTime = ((LocalTime) objectInputStream.readObject()) ;
                Repeat taskRepeat = ((Repeat) objectInputStream.readObject());
                String taskNotes = ((String) objectInputStream.readObject());

                TaskToDo taskToDo = new TaskToDo(taskId, taskText, taskCompleted, taskDueDate, taskReminderDate, taskReminderTime, taskRepeat, taskNotes) ;

                listOfTasks.add(taskToDo) ;
            }
        }
        catch (EOFException eofException) {
            System.out.println("\nINFO: DataExchange.readFromFile() -> End of file reached!") ;
        }
        catch (ClassNotFoundException ce) {
            ce.printStackTrace() ;
        }

        return listOfTasks ;
    }
}
