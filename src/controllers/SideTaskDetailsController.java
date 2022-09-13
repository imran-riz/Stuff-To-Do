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
import models.TaskToDo;
import util.IdGenerator;
import util.TaskCollection;
import values.Repeat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


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

    private TaskToDo taskToDo;

    public void initialize(TaskToDo taskToDo) {
        if (this.taskToDo != null) {
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

            this.taskToDo = taskToDo;

            String reminderTime = this.taskToDo.getReminderTime().format(DateTimeFormatter.ofPattern("hh:mm a"));

            this.taskTextArea.setText(this.taskToDo.getText());
            this.taskDueDatePicker.setValue(this.taskToDo.getDueDate());
            this.repeatComboBox.setValue(this.taskToDo.getRepeat());
            this.taskReminderDatePicker.setValue(this.taskToDo.getReminderDate());
            this.hrChoiceBox.setValue(reminderTime.substring(0, 2));
            this.minChoiceBox.setValue(reminderTime.substring(3, 5));
            this.dayPeriodChoiceBox.setValue(reminderTime.substring(6, 8).toUpperCase());
            this.notesTextArea.setText(this.taskToDo.getNotes());

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
            if (hr != 12)   hr += 12;
        }
        else if (hr == 12) {
            hr = 0;
        }

        LocalTime reminderTime = LocalTime.of(hr, min);


        // compare the details from the side view to the existing data of a Task, if they vary update the Task and other repeated Tasks
        if (!taskTextArea.equalsIgnoreCase(this.taskToDo.getText()) || !dueDate.equals(this.taskToDo.getDueDate()) || repeat != this.taskToDo.getRepeat() || !reminderDate.equals(this.taskToDo.getReminderDate()) || !reminderTime.equals(this.taskToDo.getReminderTime()) || !notes.equalsIgnoreCase(this.taskToDo.getNotes())) {
            this.taskToDo.setId(IdGenerator.generateNewId());
            this.taskToDo.setText(taskTextArea);
            this.taskToDo.setDueDate(dueDate);
            this.taskToDo.setRepeat(repeat);
            this.taskToDo.setReminderDate(reminderDate);
            this.taskToDo.setReminderTime(reminderTime);
            this.taskToDo.setNotes(notes);

            TaskCollection.getInstance().storeTasksToFile();
            HomePageController.loadTasksToListView();
        }
    }
}
