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


package models;

import controllers.HomePageController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import util.IdGenerator;
import util.TaskCollection;
import values.Repeat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Task {

    private String id;
    private String text;
    private Boolean completed;
    private LocalDate dueDate, reminderDate;
    private LocalTime reminderTime;
    private Repeat repeat;
    private String notes;

    private final StringProperty taskTextProperty = new SimpleStringProperty();
    private final StringProperty dueDateProperty = new SimpleStringProperty();
    private Timeline timeline ;

    public Task() {
    }

    public Task(String id, String text, Boolean completed, LocalDate dueDate, LocalDate reminderDate, LocalTime reminderTime, Repeat repeat, String notes) {
        this.id = id;
        this.text = text;
        this.completed = completed;
        this.dueDate = dueDate;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.repeat = repeat;
        this.notes = notes;

        this.taskTextProperty.setValue(this.text);
        this.setDueDateProperty() ;

        if (!this.completed)
            this.scheduleReminderAlert();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.taskTextProperty.setValue(this.text);
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;

        if (this.completed && this.repeat != Repeat.NONE) {
            String newTaskID;
            LocalDate newTaskDue, newTaskReminder;

            newTaskID = IdGenerator.generateIdForRepeatedTask(this) ;

            if (TaskCollection.getInstance().getTask(newTaskID) == null) {
                if (this.repeat == Repeat.DAILY) {
                    newTaskDue = this.dueDate.plusDays(1);
                    newTaskReminder = this.reminderDate.plusDays(1);
                }
                else if (this.repeat == Repeat.WEEKLY) {
                    newTaskDue = this.dueDate.plusWeeks(1);
                    newTaskReminder = this.reminderDate.plusWeeks(1);
                }
                else {
                    newTaskDue = this.dueDate.plusMonths(1);
                    newTaskReminder = this.reminderDate.plusMonths(1);
                }

                Task newTask = new Task(newTaskID, this.text, false, newTaskDue, newTaskReminder, this.reminderTime, this.repeat, this.notes);

                TaskCollection.getInstance().addTask(newTask);

                HomePageController.addNewTask(newTask);
            }
        }
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        this.setDueDateProperty() ;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;

        if (this.timeline != null) {
            this.timeline.stop();
        }

        if (!this.completed)
            this.scheduleReminderAlert();
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public StringProperty getTaskTextStringProperty() {
        return this.taskTextProperty;
    }

    private void setDueDateProperty() {
        Period period = Period.between(LocalDate.now(), this.dueDate) ;
        int numDays = period.getDays();

        this.dueDateProperty.setValue("Due: " + this.dueDate.toString());

        if (numDays == 0) {
            this.dueDateProperty.setValue("Today");
        }
        else if (numDays == 1) {
            this.dueDateProperty.setValue("Tomorrow");
        }
        else if (numDays == -1) {
            this.dueDateProperty.setValue("Yesterday");
        }
    }

    public StringProperty getDueDateStringProperty() {
        return this.dueDateProperty ;
    }


    public void cancelReminderAlert() {

        if (this.timeline != null) {
            this.timeline.stop();
        }
    }


    private void scheduleReminderAlert() {
        if (this.reminderDate.equals(LocalDate.now())) {
            if (LocalTime.now().isBefore(this.reminderTime)) {
                System.out.println("\nINFO: Setting reminder alert for: " + this.getText() + " || Task id: " + this.id);

                this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) ;

                    if (currentTime.equals(this.reminderTime.toString())) {

                        Notifications notifications = Notifications.create()
                                .title("Stuff To Do Today!")
                                .text(this.text)
                                .graphic(null)
                                .hideAfter(Duration.seconds(20))
                                .position(Pos.TOP_LEFT)
                                .darkStyle();

                        notifications.show();

                        this.timeline.stop();
                    }
                }));

                this.timeline.setCycleCount(Animation.INDEFINITE);
                this.timeline.play();
            }
        }
    }
}
