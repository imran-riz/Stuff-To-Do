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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Task;
import util.IdGenerator;
import util.TaskCollection;
import values.Repeat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class SideTaskDetailsController {
    public AnchorPane taskDetailsRoot;
    public VBox mainVbox, vbox1, vbox2 ;
    public HBox hbox1, hbox2 ;
    public TextArea taskTextArea;
    public DatePicker taskDueDatePicker, taskReminderDatePicker;
    public ComboBox<Repeat> repeatComboBox;
    public ComboBox<String> hrChoiceBox, minChoiceBox, dayPeriodChoiceBox;
    public TextArea notesTextArea;
    public Button saveBtn;

    private final ObservableList<Repeat> repeatValuesList = FXCollections.observableArrayList();
    private final ObservableList<String> hoursList = FXCollections.observableArrayList();
    private final ObservableList<String> minutesList = FXCollections.observableArrayList();
    private final ObservableList<String> dayPeriodsList = FXCollections.observableArrayList();

    private Task task;

    public void initialize(Task task) {
        if (this.task != null) {
            System.out.println("\nERROR: SideTaskDetailsController.initialize() -> The model Task can only be set once!");
        }
        else {
            this.initializeLists();

            this.taskTextArea.setWrapText(true);
            this.repeatComboBox.setItems(this.repeatValuesList);
            this.repeatComboBox.setValue(Repeat.NONE);
            this.hrChoiceBox.setItems(this.hoursList);
            this.hrChoiceBox.setVisibleRowCount(6);
            this.minChoiceBox.setItems(this.minutesList);
            this.minChoiceBox.setVisibleRowCount(6);
            this.dayPeriodChoiceBox.setItems(this.dayPeriodsList);
            this.notesTextArea.setWrapText(true);

            this.taskDueDatePicker.setEditable(false);
            this.taskReminderDatePicker.setEditable(false);

            this.task = task;

            String reminderTime = this.task.getReminderTime().format(DateTimeFormatter.ofPattern("hh:mm a"));

            this.taskTextArea.setText(this.task.getText());
            this.taskDueDatePicker.setValue(this.task.getDueDate());
            this.repeatComboBox.setValue(this.task.getRepeat());
            this.taskReminderDatePicker.setValue(this.task.getReminderDate());
            this.hrChoiceBox.setValue(reminderTime.substring(0, 2));
            this.minChoiceBox.setValue(reminderTime.substring(3, 5));
            this.dayPeriodChoiceBox.setValue(reminderTime.substring(6, 8).toUpperCase());
            this.notesTextArea.setText(this.task.getNotes());

            this.taskTextArea.setId("taskTextArea");
            this.vbox1.setId("miniContentBox") ;
            this.vbox2.setId("miniContentBox") ;
            this.hbox1.setId("miniContentBox") ;
            this.hbox2.setId("miniContentBox") ;
            this.notesTextArea.setId("taskNotesTextArea");
        }
    }


    private void initializeLists() {
        this.repeatValuesList.addAll(Repeat.NONE, Repeat.DAILY, Repeat.WEEKLY, Repeat.MONTHLY);

        for (int i = 1; i <= 12; i++) {

            if (i < 10) {
                this.hoursList.add("0".concat(Integer.toString(i)));
            }
            else {
                this.hoursList.add(Integer.toString(i));
            }
        }

        for (int i = 0; i < 60; i++) {

            if (i < 10) {
                this.minutesList.add("0".concat(Integer.toString(i)));
            }
            else {
                this.minutesList.add(Integer.toString(i));
            }
        }

        this.dayPeriodsList.addAll("AM", "PM");
    }


    public void saveChanges() {
        // first get all the data that's in the task details side view
        String taskTextArea = this.taskTextArea.getText().replace("\n", "");
        LocalDate dueDate = this.taskDueDatePicker.getValue();
        Repeat repeat = this.repeatComboBox.getValue();
        LocalDate reminderDate = this.taskReminderDatePicker.getValue();
        String strHr = this.hrChoiceBox.getValue();
        String strMin = this.minChoiceBox.getValue();
        String dayPeriod = this.dayPeriodChoiceBox.getValue();
        String notes = this.notesTextArea.getText();

        int hr = Integer.parseInt(strHr);
        int min = Integer.parseInt(strMin);

        if (dayPeriod.equalsIgnoreCase("PM")) {
            hr += 12;

            if (hr == 24) hr = 0;
        }

        LocalTime reminderTime = LocalTime.of(hr, min);


        // compare the details from the side view to the existing data of a Task, if they vary update the Task and other repeated Tasks
        if (!taskTextArea.equalsIgnoreCase(this.task.getText()) || !dueDate.equals(this.task.getDueDate()) || repeat != this.task.getRepeat() || !reminderDate.equals(this.task.getReminderDate()) || !reminderTime.equals(this.task.getReminderTime()) || !notes.equalsIgnoreCase(this.task.getNotes())) {
            this.task.setId(IdGenerator.generateNewId());
            this.task.setText(taskTextArea);
            this.task.setDueDate(dueDate);
            this.task.setRepeat(repeat);
            this.task.setReminderDate(reminderDate);
            this.task.setReminderTime(reminderTime);
            this.task.setNotes(notes);

            TaskCollection.getInstance().storeTasksToFile();
            HomePageController.loadTasksToListView();
        }
    }
}
