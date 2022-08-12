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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import models.Task;
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


public class HomePageController implements Initializable {

    public AnchorPane root, sideMenuAnchorPane, centerAnchorPane, detailsPane;
    public BorderPane borderPane;
    public Label welcomeLbl, headerLbl ;
    public Button aboutBtn, todayTasksBtn, plannedTasksBtn, completedTasksBtn, closeBtn;

    private FXMLLoader loader ;
    private AnchorPane newTaskFormRoot ;
    private NewTaskFormController newTaskFormController ;

    public static final StringProperty headerStrProperty = new SimpleStringProperty();

    private static final ListView<Pane> tasksListView = new ListView<>() ;

    public static final List<Task> tasksToday = new ArrayList<>() ;            // stores the Tasks for the present day
    public static final List<Task> allPlannedTasks = new ArrayList<>() ;       // stores all the planned tasks to do that are not complete

    private static final LocalDate TODAY_DATE = LocalDate.now() ;
    private static Task selectedTask;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.headerLbl.textProperty().bind(headerStrProperty);

        headerStrProperty.setValue(HomePageHeaders.TASKS_TODAY) ;

        this.todayTasksBtn.setOnAction(e -> {
            if (selectedTask != null) {
                hideTaskDetails();
            }

            showTasksForToday();
        });

        this.plannedTasksBtn.setOnAction(e -> {
            if (selectedTask != null) {
                hideTaskDetails();
            }

            showAllPlannedTasks();
        });

        this.completedTasksBtn.setOnAction(e -> {
            if (selectedTask != null) {
                hideTaskDetails();
            }

            showCompletedTasks();
        });

        this.aboutBtn.setOnAction(e -> this.showAboutPage());

        this.closeBtn.setOnAction(e -> this.hideTaskDetails());


        // include the ListView to the app
        AnchorPane.setLeftAnchor(tasksListView, 5.0);
        AnchorPane.setRightAnchor(tasksListView, 5.0);
        AnchorPane.setTopAnchor(tasksListView, 110.0);

        tasksListView.setPrefWidth(520.0);
        tasksListView.setPrefHeight(392.0);

        loadTasksToListView();

        this.centerAnchorPane.getChildren().add(tasksListView) ;


        // This is done to make the app responsive. When the side view containing the selected task's details is shown,
        // the appropriate new-task-form is loaded to fit the available space in the center of the border pane.
        this.centerAnchorPane.widthProperty().addListener(e -> {
            double currentWidth = this.centerAnchorPane.getWidth();

            if (currentWidth < 530) {
                this.loader = new FXMLLoader(getClass().getResource("/resources/fxml/new-task-form-small.fxml"));
            }
            else {
                this.loader = new FXMLLoader(getClass().getResource("/resources/fxml/new-task-form-large.fxml"));
            }

            try {
                this.centerAnchorPane.getChildren().remove(newTaskFormRoot) ;

                this.newTaskFormRoot = this.loader.load();

                this.newTaskFormController = this.loader.getController() ;

                AnchorPane.setLeftAnchor(this.newTaskFormRoot, 5.0) ;
                AnchorPane.setBottomAnchor(this.newTaskFormRoot, 10.0) ;
                AnchorPane.setRightAnchor(this.newTaskFormRoot, 5.0) ;

                this.centerAnchorPane.getChildren().add(this.newTaskFormRoot) ;

                this.newTaskFormController.initializeComponents();
            }
            catch (IOException ioEx) {
                System.out.println("\nERROR: HomePageController.initialize() -> exception when loading the new task form.");
                ioEx.printStackTrace() ;
            }
        });


        tasksListView.setOnMouseClicked(e -> {
            if (!tasksListView.getItems().isEmpty()) {
                String selectedTaskId = ((Label) tasksListView.getSelectionModel().getSelectedItem().getChildren().get(0)).getText();

                if (TaskCollection.getInstance().getTask(selectedTaskId) == selectedTask) {
                    this.hideTaskDetails();
                }
                else {
                    selectedTask = TaskCollection.getInstance().getTask(selectedTaskId);
                    this.showTaskDetails();
                }
            }
        });



        // load the fxml file with the AnchorPane containing components to add a new task
        try {
            this.loader = new FXMLLoader(getClass().getResource("/resources/fxml/new-task-form-large.fxml"));
            this.newTaskFormRoot = this.loader.load();

            AnchorPane.setLeftAnchor(this.newTaskFormRoot, 5.0) ;
            AnchorPane.setBottomAnchor(this.newTaskFormRoot, 10.0) ;
            AnchorPane.setRightAnchor(this.newTaskFormRoot, 5.0) ;

            this.newTaskFormController = this.loader.getController() ;

            this.centerAnchorPane.getChildren().add(this.newTaskFormRoot) ;

            this.newTaskFormController.initializeComponents();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.borderPane.setRight(null);

        this.sideMenuAnchorPane.setId("sideMenu");
        this.centerAnchorPane.setId("centerAnchorPane");
        tasksListView.setId("tasksListView");
        this.headerLbl.setId("header");
        this.todayTasksBtn.setId("sideMenuOptionBtn");
        this.plannedTasksBtn.setId("sideMenuOptionBtn");
        this.completedTasksBtn.setId("sideMenuOptionBtn");
        this.aboutBtn.setId("aboutBtn");
        this.detailsPane.setId("detailsPane");
        this.closeBtn.setId("closeBtn");
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
        List<Task> taskList = TaskCollection.getInstance().getAllTasks() ;
        taskList.sort(Comparator.comparing(task -> task.getDueDate()));

        allPlannedTasks.clear();
        tasksToday.clear();

        for (Task task: taskList) {
            if (task.getCompleted()) {
                if (task.getDueDate().equals(TODAY_DATE) || task.getDueDate().isAfter(TODAY_DATE)) {
                    allPlannedTasks.add(task) ;
                }
            }
            else {
                allPlannedTasks.add(task);
            }
        }

        tasksToday.addAll(taskList.stream()
                                .filter(task -> task.getDueDate().equals(TODAY_DATE))
                                .toList()) ;
    }


    /**
     * Displays the Tasks that are due on the present day in the home page's list view
     */
    private static void showTasksForToday() {
        headerStrProperty.setValue(HomePageHeaders.TASKS_TODAY) ;

        tasksListView.getItems().clear() ;

        for (Task task : tasksToday) {
            try {
                FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

                tasksListView.getItems().add(loader.load());

                TaskBoxController taskBoxController = loader.getController();
                taskBoxController.initialize(task);
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
        allPlannedTasks.sort(Comparator.comparing(task -> task.getDueDate()));

        headerStrProperty.setValue(HomePageHeaders.PLANNED_TASKS) ;

        tasksListView.getItems().clear() ;

        for (Task task : allPlannedTasks) {
            try {
                FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

                tasksListView.getItems().add(loader.load());

                TaskBoxController taskBoxController = loader.getController();
                taskBoxController.initialize(task);
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
        List<Task> tasksCompletedList = TaskCollection.getInstance().getAllTasks().stream().filter(task -> task.getCompleted()).toList() ;

        headerStrProperty.setValue(HomePageHeaders.COMPLETED_TASKS) ;

        tasksListView.getItems().clear() ;

        for (Task task : tasksCompletedList) {
            try {
                FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

                tasksListView.getItems().add(loader.load());

                TaskBoxController taskBoxController = loader.getController();
                taskBoxController.initialize(task);
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
        FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/side-task-details.fxml"));

        try {
            sideDetailsAnchorPane = loader.load();
            AnchorPane.setTopAnchor(sideDetailsAnchorPane, 40.0);

            this.detailsPane.getChildren().add(sideDetailsAnchorPane) ;

            this.borderPane.setRight(this.detailsPane) ;

            SideTaskDetailsController sideTaskDetailsController = loader.getController() ;
            sideTaskDetailsController.initialize(selectedTask);

        }
        catch (Exception e) {
            System.out.println("\nERROR: HomePageController.showTaskDetails() ->");
            e.printStackTrace();
        }

    }


    /**
     * Used to close the side view containing the selected task's details if open
     */
    private void hideTaskDetails() {
        this.borderPane.setRight(null) ;
        selectedTask = null ;
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
     * @param task the new Task that was created
     */
    public static void addNewTask(Task task) {
        allPlannedTasks.add(task) ;

        if (task.getDueDate().equals(TODAY_DATE)) {
            tasksToday.add(task) ;
        }

        switch (headerStrProperty.getValue()) {
            case HomePageHeaders.TASKS_TODAY -> showTasksForToday();
            case HomePageHeaders.PLANNED_TASKS -> showAllPlannedTasks();
            case HomePageHeaders.COMPLETED_TASKS -> showCompletedTasks();
        }
    }


    /**
     * Used to remove the root pane in TaskBox that corresponds to the Task the user has chosen to delete from the ListView
     * This is called from TaskBoxController.java
     * @param taskBoxRoot the root pane of the TaskBox
     * @param task the Task to be removed
     */
    public static void removeTaskBox(AnchorPane taskBoxRoot, Task task) {
        allPlannedTasks.remove(task);
        tasksToday.remove(task);

        tasksListView.getItems().remove(taskBoxRoot) ;
    }
}
