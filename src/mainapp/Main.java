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


package mainapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Page;
import models.Task;
import util.IdGenerator;
import util.PageNavigator;
import util.TaskCollection;
import values.Repeat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


public class Main extends Application {

    public static void main(String[] args) {
        TaskCollection.getInstance().loadTasksFromFile() ;
        checkForRepeatingTasks();
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("S T U F F   T O   D O");
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/resources/images/app_logo.png"))));
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            if (!TaskCollection.getInstance().getAllTasks().isEmpty()) {
                List<Task> tasksToday = TaskCollection.getInstance().getAllTasks().stream()
                                                                                  .filter(task -> task.getDueDate().equals(LocalDate.now()))
                                                                                  .toList() ;

                for (Task task: tasksToday) {
                    task.cancelReminderAlert();
                }
            }

            System.exit(0);
        });

        Scene scene = new Scene(new Pane()) ;

        this.setUpPages() ;
        PageNavigator.setMainScene(scene) ;
        PageNavigator.activatePage("HomePage");

        stage.setScene(scene);
        stage.show();
    }

    private void setUpPages() throws IOException {
        Pane homePagePane = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/resources/fxml/pages/home-page.fxml"))) ;
        Pane aboutPagePane = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/resources/fxml/pages/about-page.fxml"))) ;

        Page homePage = new Page(homePagePane, "/resources/styles/home-page-stylesheet.css") ;
        Page aboutPage = new Page(aboutPagePane, "/resources/styles/about-page-stylesheet.css") ;

        PageNavigator.add("HomePage", homePage) ;
        PageNavigator.add("AboutPage", aboutPage) ;
    }


    private static void checkForRepeatingTasks() {
        List<Task> taskList = TaskCollection.getInstance().getAllTasks().stream()
                                .filter(task -> !task.getRepeat().equals(Repeat.NONE) && task.getDueDate().isBefore(LocalDate.now()) && !task.getCompleted())
                                .toList();

        for (Task task: taskList) {
            Task newTask, lastOccurrenceOfTask ;
            String newTaskID ;
            LocalDate newTaskDue = LocalDate.now();
            LocalDate newTaskReminder = LocalDate.now();

            List<Task> listOfSameTask = TaskCollection.getInstance().getAllTasksThatRepeat(task) ;

            lastOccurrenceOfTask = listOfSameTask.get(listOfSameTask.size()-1) ;

            System.out.println(lastOccurrenceOfTask.getId());

            if (!lastOccurrenceOfTask.getDueDate().equals(LocalDate.now())) {
                newTaskID = IdGenerator.generateIdForRepeatedTask(lastOccurrenceOfTask) ;

                if (task.getRepeat().equals(Repeat.DAILY)) {
                    newTask = new Task(newTaskID, task.getText(), false, newTaskDue, newTaskReminder, task.getReminderTime(), task.getRepeat(), task.getNotes());
                    TaskCollection.getInstance().addTask(newTask);
                }
                else if (task.getRepeat().equals(Repeat.WEEKLY)) {
                    if (newTaskDue.getDayOfWeek().equals(task.getDueDate().getDayOfWeek())) {
                        newTask = new Task(newTaskID, task.getText(), false, newTaskDue, newTaskReminder, task.getReminderTime(), task.getRepeat(), task.getNotes());
                        TaskCollection.getInstance().addTask(newTask);
                    }
                }
                else if (task.getRepeat().equals(Repeat.MONTHLY)) {
                    if (newTaskDue.getDayOfMonth() == task.getDueDate().getDayOfMonth()) {
                        newTask = new Task(newTaskID, task.getText(), false, newTaskDue, newTaskReminder, task.getReminderTime(), task.getRepeat(), task.getNotes());
                        TaskCollection.getInstance().addTask(newTask);
                    }
                }
            }
        }
    }
}
