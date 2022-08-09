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

import models.Task;
import values.Repeat;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataExchange {

    public static void writeToFile(String pathToFile, List<Task> listOfTasks) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(pathToFile) ;

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream) ;

        for (Task task: listOfTasks) {
            objectOutputStream.writeObject(task.getId());
            objectOutputStream.writeObject(task.getText()) ;
            objectOutputStream.writeObject(task.getCompleted()) ;
            objectOutputStream.writeObject(task.getDueDate()) ;
            objectOutputStream.writeObject(task.getReminderDate()) ;
            objectOutputStream.writeObject(task.getReminderTime()) ;
            objectOutputStream.writeObject(task.getRepeat());
            objectOutputStream.writeObject(task.getNotes());
        }

        objectOutputStream.close() ;
        fileOutputStream.close() ;
    }


    public static List<Task> readFromFile(String pathToFile) throws Exception {
        List<Task> listOfTasks = new ArrayList<>() ;

        try {
            FileInputStream fileInputStream = new FileInputStream(pathToFile) ;
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream) ;

            while (true) {
                Task task = new Task() ;

                task.setId((String) objectInputStream.readObject()) ;
                task.setText((String) objectInputStream.readObject()) ;
                task.setCompleted((Boolean) objectInputStream.readObject()) ;
                task.setDueDate((LocalDate) objectInputStream.readObject()) ;
                task.setReminderDate((LocalDate) objectInputStream.readObject()) ;
                task.setReminderTime((LocalTime) objectInputStream.readObject()) ;
                task.setRepeat((Repeat) objectInputStream.readObject());
                task.setNotes((String) objectInputStream.readObject());

                listOfTasks.add(task) ;
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