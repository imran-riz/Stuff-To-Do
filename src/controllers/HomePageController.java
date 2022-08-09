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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;


public class HomePageController implements Initializable {

    public AnchorPane root, sideMenuAnchorPane, centerAnchorPane, detailsPane;
    public BorderPane borderPane;
    public Label welcomeLbl, headerLbl ;
    public Button aboutBtn, todayTasksBtn, plannedTasksBtn, completedTasksBtn, closeBtn;

    private static final ListView<Pane> tasksListView = new ListView<>() ;

    public static StringProperty headerTxt = new SimpleStringProperty();

    private FXMLLoader loader ;
    private AnchorPane newTaskFormRoot ;
    private NewTaskFormController newTaskFormController ;

    private Task selectedTask;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.showTasksForToday();
        }
        catch (IOException e) {
            System.out.println("\nERROR: HomePageController.initialize() -> exception when loading the task boxes.");
            e.printStackTrace() ;
        }

        headerTxt.setValue("Tasks For Today") ;

        this.headerLbl.textProperty().bind(headerTxt);

        // include the ListView to the app
        AnchorPane.setLeftAnchor(tasksListView, 5.0);
        AnchorPane.setRightAnchor(tasksListView, 5.0);
        AnchorPane.setTopAnchor(tasksListView, 110.0);

        tasksListView.setPrefWidth(520.0);
        tasksListView.setPrefHeight(392.0);

        this.centerAnchorPane.getChildren().add(tasksListView) ;

        // this is done to make the app responsive
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

                if (TaskCollection.getInstance().getTask(selectedTaskId) == this.selectedTask) {
                    this.hideTaskDetails();
                }
                else {
                    this.selectedTask = TaskCollection.getInstance().getTask(selectedTaskId);
                    this.showTaskDetails();
                }
            }
        });



        // load the fxml file with the AnchorPane containing components to add a new task for the first time
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


    public void showTasksForToday() throws IOException {
        List<Task> tasksTodayList = TaskCollection.getInstance().getAllTasks().stream().filter(task -> task.getDueDate().equals(LocalDate.now())).toList() ;

        // check if the details for a task is open. If so, close it
        if (this.selectedTask != null) {
            hideTaskDetails();
        }

        headerTxt.setValue("Tasks For Today") ;

        tasksListView.getItems().clear() ;

        for (Task task : tasksTodayList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/task-box.fxml"));

            tasksListView.getItems().add(loader.load());

            TaskBoxController taskBoxController = loader.getController();
            taskBoxController.initialize(task);
        }
    }


    public void showAllPlannedTasks() throws IOException {
        List<Task> allTasks = TaskCollection.getInstance().getAllTasks() ;
        allTasks.sort(Comparator.comparing(task -> task.getDueDate()));

        // check if the details for a task is open. If so, close it
        if (this.selectedTask != null) {
            hideTaskDetails();
        }

        headerTxt.setValue("Planned") ;

        tasksListView.getItems().clear() ;

        for (Task task : allTasks) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/task-box.fxml"));

            tasksListView.getItems().add(loader.load());

            TaskBoxController taskBoxController = loader.getController();
            taskBoxController.initialize(task);
        }
    }


    public void showCompletedTasks() throws IOException {
        List<Task> tasksCompletedList = TaskCollection.getInstance().getAllTasks().stream().filter(task -> task.getCompleted()).toList() ;

        // check if the details for a task is open. If so, close it
        if (this.selectedTask != null) {
            hideTaskDetails();
        }

        headerTxt.setValue("Tasks Completed") ;

        tasksListView.getItems().clear() ;

        for (Task task : tasksCompletedList) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/task-box.fxml"));

            tasksListView.getItems().add(loader.load());

            TaskBoxController taskBoxController = loader.getController();
            taskBoxController.initialize(task);
        }
    }

    /**
     * Is called when the user clicks on a ListItem.
     * Will show the RIGHT pane in the BorderPane that will contain the details of the selected task.
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
            sideTaskDetailsController.initialize(this.selectedTask);

        }
        catch (Exception e) {
            System.out.println("\nERROR: HomePageController.showTaskDetails() ->");
            e.printStackTrace();
        }

    }


    public void hideTaskDetails() {
        this.borderPane.setRight(null) ;
        this.selectedTask = null ;
    }


    /**
     * Shows the about-page.fxml file in the window
     */
    public void showAboutPage() {
        PageNavigator.activatePage("AboutPage");
    }


    public static void addTaskToListView(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource("/resources/fxml/task-box.fxml"));

            tasksListView.getItems().add(loader.load());

            TaskBoxController taskBoxController = loader.getController();
            taskBoxController.initialize(task);
        }
        catch (Exception e) {
            System.out.println("\nERROR: HomePageController.addTaskToListView() -> Failed to add task to listview.") ;
            e.printStackTrace() ;
        }
    }


    public static void removeTaskBox(AnchorPane taskBoxRoot) {
        tasksListView.getItems().remove(taskBoxRoot) ;
    }
}
