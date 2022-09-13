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


package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import models.TaskToDo;
import util.PageNavigator;
import util.TaskCollection;
import values.HomePageHeaders;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class HomePageController implements Initializable {

    public AnchorPane root, sideMenuAnchorPane, centerAnchorPane, detailsPane;
    public VBox centerVBox;
    public BorderPane borderPane;
    public Label welcomeLbl, headerLbl ;
    public Button aboutBtn, todayTasksBtn, plannedTasksBtn, completedTasksBtn ;

    public static final StringProperty headerStrProperty = new SimpleStringProperty();

    private static final ListView<Pane> tasksListView = new ListView<>() ;

    public static final List<TaskToDo> TASKS_TODAY = new ArrayList<>() ;            // stores the Tasks for the present day
    public static final List<TaskToDo> ALL_PLANNED_TASKS = new ArrayList<>() ;      // stores all the planned tasks to do that are not complete

    private static final LocalDate TODAY_DATE = LocalDate.now() ;
    private static TaskToDo selectedTaskToDo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.headerLbl.textProperty().bind(headerStrProperty);

        headerStrProperty.setValue(HomePageHeaders.TASKS_TODAY) ;

        initializeDetailsPane();

        this.todayTasksBtn.setOnAction(e -> {
            if (selectedTaskToDo != null) {
                hideTaskDetails();
            }

            showTasksForToday();
        });

        this.plannedTasksBtn.setOnAction(e -> {
            if (selectedTaskToDo != null) {
                hideTaskDetails();
            }

            showAllPlannedTasks();
        });

        this.completedTasksBtn.setOnAction(e -> {
            if (selectedTaskToDo != null) {
                hideTaskDetails();
            }

            showCompletedTasks();
        });

        this.aboutBtn.setOnAction(e -> this.showAboutPage());


        // include the ListView that displays the tasks to the home page
        loadTasksToListView();
        this.centerVBox.setFillWidth(true);
        this.centerVBox.getChildren().add(tasksListView);
        VBox.setVgrow(tasksListView, Priority.ALWAYS);

        tasksListView.setOnMouseClicked(e -> {
            if (!tasksListView.getItems().isEmpty()) {
                if (tasksListView.getSelectionModel().getSelectedItem() != null) {
                    String selectedTaskId = ((Label) tasksListView.getSelectionModel().getSelectedItem().getChildren().get(0)).getText();

                    if (TaskCollection.getInstance().getTask(selectedTaskId) == selectedTaskToDo) {
                        this.hideTaskDetails();
                    }
                    else {
                        selectedTaskToDo = TaskCollection.getInstance().getTask(selectedTaskId);
                        this.showTaskDetails();
                    }
                }
            }
        });


        // load the fxml file with the AnchorPane containing components to add a new task
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/new-task-form.fxml"));
            Pane newTaskFormRoot = loader.load();

            NewTaskFormController newTaskFormController = loader.getController();

            this.centerVBox.getChildren().add(newTaskFormRoot) ;

            newTaskFormController.initializeComponents();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.sideMenuAnchorPane.setId("sideMenu");
        this.centerAnchorPane.setId("centerAnchorPane");
        tasksListView.setId("tasksListView");
        this.headerLbl.setId("header");
        this.todayTasksBtn.setId("sideMenuOptionBtn");
        this.plannedTasksBtn.setId("sideMenuOptionBtn");
        this.completedTasksBtn.setId("sideMenuOptionBtn");
        this.aboutBtn.setId("aboutBtn");
        this.detailsPane.setId("detailsPane");
    }


    public static void loadTasksToListView() {
        tasksListView.getItems().clear();

        initializeTaskLists();

        if (headerStrProperty.getValue().equalsIgnoreCase(HomePageHeaders.TASKS_TODAY)) {
            showTasksForToday();
        }
        else if (headerStrProperty.getValue().equalsIgnoreCase(HomePageHeaders.PLANNED_TASKS)) {
            showAllPlannedTasks();
        }
        else {
            showCompletedTasks();
        }
    }


    /**
     * Loads the Tasks to the Lists 'allPlannedTasks' and 'tasksToday'
     */
    private static void initializeTaskLists() {
        List<TaskToDo> taskList = TaskCollection.getInstance().getAllTasks() ;
        taskList.sort(Comparator.comparing(task -> task.getDueDate()));

        ALL_PLANNED_TASKS.clear();
        TASKS_TODAY.clear();

        for (TaskToDo taskToDo : taskList) {
            if (taskToDo.getCompleted()) {
                if (taskToDo.getDueDate().equals(TODAY_DATE) || taskToDo.getDueDate().isAfter(TODAY_DATE)) {
                    ALL_PLANNED_TASKS.add(taskToDo) ;
                }
            }
            else {
                ALL_PLANNED_TASKS.add(taskToDo);
            }
        }

        TASKS_TODAY.addAll(taskList.stream()
                                .filter(task -> task.getDueDate().equals(TODAY_DATE))
                                .collect(Collectors.toList())) ;
    }


    private void initializeDetailsPane() {
        Button closeBtn;

        this.detailsPane = new AnchorPane();
        this.detailsPane.setPrefWidth(250);
        this.detailsPane.setPrefHeight(600);
        
        closeBtn = new Button("X");
        closeBtn.setPrefWidth(25);
        closeBtn.setPrefHeight(25);
        closeBtn.setId("closeBtn");
        closeBtn.setOnAction(e -> this.hideTaskDetails());
        AnchorPane.setTopAnchor(closeBtn, 10.0);
        AnchorPane.setRightAnchor(closeBtn, 10.0);

        this.detailsPane.getChildren().add(closeBtn);
    }


    /**
     * Displays the Tasks that are due on the present day in the home page's list view
     */
    private static void showTasksForToday() {
        headerStrProperty.setValue(HomePageHeaders.TASKS_TODAY) ;

        tasksListView.getItems().clear() ;

        for (TaskToDo taskToDo : TASKS_TODAY) {
            try {
                FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

                tasksListView.getItems().add(loader.load());

                TaskBoxController taskBoxController = loader.getController();
                taskBoxController.initialize(taskToDo);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Displays all the tasks that have been planned and yet to be completed in the home page's list view
     */
    private static void showAllPlannedTasks()  {
        ALL_PLANNED_TASKS.sort(Comparator.comparing(task -> task.getDueDate()));

        headerStrProperty.setValue(HomePageHeaders.PLANNED_TASKS) ;

        tasksListView.getItems().clear() ;

        for (TaskToDo taskToDo : ALL_PLANNED_TASKS) {
            try {
                FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

                tasksListView.getItems().add(loader.load());

                TaskBoxController taskBoxController = loader.getController();
                taskBoxController.initialize(taskToDo);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     *  Displays all the Tasks that have been completed
     */
    private static void showCompletedTasks() {
        List<TaskToDo> tasksCompletedList = TaskCollection.getInstance().getAllTasks().stream().filter(task -> task.getCompleted()).collect(Collectors.toList()) ;

        headerStrProperty.setValue(HomePageHeaders.COMPLETED_TASKS) ;

        tasksListView.getItems().clear() ;

        for (TaskToDo taskToDo : tasksCompletedList) {
            try {
                FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

                tasksListView.getItems().add(loader.load());

                TaskBoxController taskBoxController = loader.getController();
                taskBoxController.initialize(taskToDo);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Only called when the user selects a task from the home page's ListView.
     * Displays a side view on the right of the border pane.
     * The side view contains the details of the selected task.
     */
    private void showTaskDetails() {
        AnchorPane sideDetailsAnchorPane ;
        FXMLLoader loader ;

        try {
            loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/side-task-details.fxml"));

            sideDetailsAnchorPane = loader.load();
            AnchorPane.setTopAnchor(sideDetailsAnchorPane, 40.0);

            this.detailsPane.getChildren().add(sideDetailsAnchorPane) ;

            SideTaskDetailsController sideTaskDetailsController = loader.getController() ;
            sideTaskDetailsController.initialize(selectedTaskToDo);
        }
        catch (Exception e) {
            System.out.println("\nERROR: HomePageController.showTaskDetails() ->");
            e.printStackTrace();
        }

        this.borderPane.setRight(this.detailsPane);
    }


    /**
     * Used to close the side view containing the selected task's details if open
     */
    private void hideTaskDetails() {
        this.borderPane.setRight(null) ;
        selectedTaskToDo = null ;
    }


    /**
     * Shows the about-page.fxml file in the window
     */
    private void showAboutPage() {
        PageNavigator.activatePage("AboutPage");
    }


    /**
     * Used to add a new task to the Lists
     * The new task will then be displayed in the appropriate ListView depending on which tasks are being shown
     * @param taskToDo the new Task that was created
     */
    public static void addNewTask(TaskToDo taskToDo) {
        ALL_PLANNED_TASKS.add(taskToDo) ;

        if (taskToDo.getDueDate().equals(TODAY_DATE)) {
            TASKS_TODAY.add(taskToDo) ;
        }

        switch (headerStrProperty.getValue()) {
            case HomePageHeaders.TASKS_TODAY: showTasksForToday();
                                                break;
            case HomePageHeaders.PLANNED_TASKS: showAllPlannedTasks();
                                                break;
            case HomePageHeaders.COMPLETED_TASKS: showCompletedTasks();
                                                break;
        }
    }


    /**
     * Used to remove the root pane in TaskBox that corresponds to the Task the user has chosen to delete from the ListView
     * This is called from TaskBoxController.java
     * @param taskBoxRoot the root pane of the TaskBox
     * @param taskToDo the Task to be removed
     */
    public static void removeTaskBox(AnchorPane taskBoxRoot, TaskToDo taskToDo) {
        ALL_PLANNED_TASKS.remove(taskToDo);
        TASKS_TODAY.remove(taskToDo);

        tasksListView.getItems().remove(taskBoxRoot) ;
    }
}
