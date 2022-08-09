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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TaskCollection {

    private final static TaskCollection instance = new TaskCollection() ;

    private final String PATH_TO_DIRECTORY = System.getProperty("user.home") + "\\.to-do-app" ;
    private final String PATH_TO_FILE = PATH_TO_DIRECTORY + "/tasks-info.ser" ;

    private List<Task> listOfTasks = new ArrayList<>() ;


    private TaskCollection() {}


    public static TaskCollection getInstance() {
        return instance ;
    }

    public  List<Task> getAllTasks() {
        return this.listOfTasks ;
    }

    public void addTask(Task task) {
        this.listOfTasks.add(task) ;
        this.storeTasksToFile() ;
    }

    public void removeTask(Task task) {
        this.listOfTasks.remove(task) ;
        this.storeTasksToFile() ;
    }

    public Task getTask(String id) {
        return this.listOfTasks.stream()
                .filter(task -> id.equalsIgnoreCase(task.getId()))
                .findAny()
                .orElse(null);
    }


    public void loadTasksFromFile() {
        try {
            this.listOfTasks = DataExchange.readFromFile(this.PATH_TO_FILE) ;
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            System.out.println("INFO: TaskCollection.loadTasksFromFile() -> No saved data");
        }
        catch (FileNotFoundException fileNotFoundException) {
            System.out.println("\nINFO: TaskCollection.readDataFromFile() -> File not found! Creating directory...");

            File file = new File(PATH_TO_DIRECTORY);

            if (file.mkdirs()) {
                System.out.println("INFO: TaskCollection.readDataFromFile() -> Directory created! Creating file in the directory...");

                File file2 = new File(PATH_TO_FILE);

                try {
                    if (file2.createNewFile()) {
                        System.out.println("INFO: TaskCollection.readDataFromFile() -> File created! Reading from file again...");
                        this.loadTasksFromFile();
                    }
                }
                catch (IOException ioException) {
                    System.out.println("\nERROR: TaskCollection.loadTasksFromFile() -> ") ;
                    ioException.printStackTrace() ;
                }
            }
        }
        catch (Exception e) {
            System.out.println("\nERROR: TaskCollection.loadTasksFromFile() ->") ;
            e.printStackTrace() ;
        }
    }


    public void storeTasksToFile() {
        try {
            DataExchange.writeToFile(this.PATH_TO_FILE, this.listOfTasks);
        }
        catch (Exception e) {
            System.out.println("\nERROR: TaskCollection.storeTasksToFile() -> ") ;
            e.printStackTrace() ;
        }
    }
}
