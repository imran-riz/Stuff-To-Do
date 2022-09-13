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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import models.TaskToDo;
import util.IdGenerator;
import util.TaskCollection;
import values.Repeat;

import java.time.LocalDate;
import java.time.LocalTime;


public class NewTaskFormController {

    public FlowPane root ;
    public HBox hbox1, hbox2, hbox3, hbox4, hbox5;
    public TextField newTaskTextFld;
    public DatePicker reminderDatePicker, dueDatePicker;
    public ComboBox<String> reminderHr, reminderMin, reminderDayPeriod;
    public ComboBox<Repeat> repeatBox;
    public Button addNewTaskBtn;

    private final ObservableList<Repeat> repeatValues = FXCollections.observableArrayList();
    private final ObservableList<String> hours = FXCollections.observableArrayList();
    private final ObservableList<String> minutes = FXCollections.observableArrayList();
    private final ObservableList<String> dayPeriods = FXCollections.observableArrayList();

    private final Alert alert = new Alert(Alert.AlertType.ERROR);
    private final ImageView imageView = new ImageView(new Image("/resources/images/add_icon.png")) ;


    public void initializeComponents() {
        this.initializeValues();

        this.repeatBox.setItems(this.repeatValues);
        this.repeatBox.setValue(Repeat.NONE);
        this.reminderHr.setItems(this.hours);
        this.reminderHr.setVisibleRowCount(6);
        this.reminderHr.setValue("--");
        this.reminderMin.setItems(this.minutes);
        this.reminderMin.setVisibleRowCount(6);
        this.reminderMin.setValue("--");
        this.reminderDayPeriod.setItems(this.dayPeriods);
        this.reminderDayPeriod.setValue("--");

        this.dueDatePicker.setEditable(false);
        this.reminderDatePicker.setEditable(false);

        this.imageView.setFitWidth(20);
        this.imageView.setFitHeight(20);
        this.addNewTaskBtn.setGraphic(this.imageView);

        this.root.setId("newTaskRoot");
        this.newTaskTextFld.setId("newTaskTxtFld");
        this.addNewTaskBtn.setId("addTaskBtn");
    }


    private void initializeValues() {
        this.repeatValues.addAll(Repeat.NONE, Repeat.DAILY, Repeat.WEEKLY, Repeat.MONTHLY);

        for (int i = 1; i <= 12; i++) {

            if (i < 10) {
                this.hours.add("0".concat(Integer.toString(i)));
            }
            else {
                this.hours.add(Integer.toString(i));
            }
        }

        for (int i = 0; i < 60; i++) {

            if (i < 10) {
                this.minutes.add("0".concat(Integer.toString(i)));
            }
            else {
                this.minutes.add(Integer.toString(i));
            }
        }

        this.dayPeriods.addAll("AM", "PM");
    }

    /**
     * Used to create a new task using the details the user has added.
     * The task will be saved locally to file and the ListView is updated.
     */
    public void addNewTask() {
        String taskText = this.newTaskTextFld.getText();
        LocalDate taskDueDate = this.dueDatePicker.getValue();
        Repeat taskRepeat = this.repeatBox.getValue();
        LocalDate taskReminderDate = this.reminderDatePicker.getValue();
        String reminderHr = this.reminderHr.getValue();
        String reminderMin = this.reminderMin.getValue();
        String reminderDayPeriod = this.reminderDayPeriod.getValue();


        if (taskText.isBlank() || taskDueDate == null || taskReminderDate == null || reminderHr.equals("--") || reminderMin.equals("--") || reminderDayPeriod.equals("--")) {
            this.alert.getDialogPane().setContent(new Label("All the details must be filled for the new task must be filled!"));
            this.alert.showAndWait();
        }
        else {
            String taskId = IdGenerator.generateNewId();
            int hours, minutes;

            hours = Integer.parseInt(reminderHr);
            minutes = Integer.parseInt(reminderMin);


            if (reminderDayPeriod.equalsIgnoreCase("PM")) {
                if (hours != 12) hours += 12;
            }
            else if (hours == 12) {
                hours = 0 ;
            }

            TaskToDo taskToDo = new TaskToDo(taskId, taskText, false, taskDueDate, taskReminderDate, LocalTime.of(hours, minutes), taskRepeat, "");
            TaskCollection.getInstance().addTask(taskToDo);

            HomePageController.addNewTask(taskToDo);
            System.out.println("\nINFO: New task created! Id for " + taskText + " : " +  taskId);

            this.newTaskTextFld.setText("");
            this.dueDatePicker.setValue(null);
            this.repeatBox.setValue(Repeat.NONE);
            this.reminderDatePicker.setValue(null);
            this.reminderHr.setValue("--");
            this.reminderMin.setValue("--");
            this.reminderDayPeriod.setValue("--");
        }
    }
}
