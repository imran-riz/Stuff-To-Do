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

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.TaskToDo;
import util.TaskCollection;
import values.HomePageHeaders;
import values.Repeat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class TaskBoxController {
    public AnchorPane taskBoxRoot ;
    public HBox hbox1, hbox2, hbox3;
    public CheckBox completedCheckBox ;
    public Label textLbl, dueDateLbl, reminderTimeLbl, repeatLbl ;
    public Button deleteBtn ;
    public Line line ;
    public ImageView calendarImgView, notificationImgView, eventRepeatImgView ;

    private final Label idLbl = new Label();
    private final ImageView imageView = new ImageView(new Image("/resources/images/delete_icon.png")) ;


    private TaskToDo taskToDo;


    public void initialize(TaskToDo taskToDo) {
        this.calendarImgView.setImage(new Image("/resources/images/calendar_icon.png"));
        this.notificationImgView.setImage(new Image("/resources/images/notification_icon.png"));
        this.eventRepeatImgView.setImage(new Image("/resources/images/event_repeat_icon.png"));

        if (this.taskToDo != null) {
            throw new IllegalStateException("\nERROR: TaskBoxController.initModel() -> The model can only be initialized once") ;
        }
        else {
            this.taskToDo = taskToDo;

            this.idLbl.setText(this.taskToDo.getId());
            this.idLbl.setVisible(false) ;
            this.taskBoxRoot.getChildren().add(0, this.idLbl) ;

            this.textLbl.setWrapText(false);

            this.textLbl.textProperty().bind(this.taskToDo.getTaskTextStringProperty());
            this.dueDateLbl.textProperty().bind(this.taskToDo.getDueDateStringProperty());
            this.reminderTimeLbl.textProperty().bind(this.taskToDo.getReminderTimeStringProperty());
            this.repeatLbl.textProperty().bind(this.taskToDo.getRepeatStringProperty());

            this.completedCheckBox.setSelected(this.taskToDo.getCompleted()) ;
            this.completedCheckBox.setOnAction(e -> {
                boolean selected = this.completedCheckBox.isSelected() ;

                this.taskToDo.setCompleted(selected);
                this.line.setVisible(selected);

                TaskCollection.getInstance().storeTasksToFile();

                if (selected) {
                    if (HomePageController.headerStrProperty.getValue().equals(HomePageHeaders.PLANNED_TASKS)) {
                        if (this.taskToDo.getDueDate().isBefore(LocalDate.now())) {
                            HomePageController.removeTaskBox(this.taskBoxRoot, this.taskToDo);
                        }
                    }
                }
                else {
                    if (HomePageController.headerStrProperty.getValue().equals(HomePageHeaders.COMPLETED_TASKS)) {
                        HomePageController.removeTaskBox(this.taskBoxRoot, this.taskToDo);
                        HomePageController.addNewTask(this.taskToDo);
                    }
                }
            });

            this.deleteBtn.setShape(new Circle(15.0)) ;
            this.deleteBtn.setGraphic(this.imageView);

            this.textLbl.widthProperty().addListener((obs, oldVal, newVal) -> this.line.setStartX(newVal.doubleValue()));

            this.line.setVisible(this.taskToDo.getCompleted());

            if (this.taskToDo.getRepeat().equals(Repeat.NONE)) {
                this.hbox3.setVisible(false);
            }

            this.taskBoxRoot.setId("taskBoxRoot");
            this.textLbl.setId("taskText");
            this.dueDateLbl.setId("taskDueDateText");
            this.reminderTimeLbl.setId("taskReminderText");
            this.repeatLbl.setId("taskRepeatText");
        }
    }


    public void delete() {
        List<TaskToDo> listOfSameTaskRepeated = TaskCollection.getInstance().getAllTasksThatRepeat(this.taskToDo);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION) ;
        alert.setTitle("Delete Task");
        alert.getDialogPane().setContent(new Label("Are you absolutely sure that you wish to delete this task? Once deleted, it can't be undone."));
        alert.getDialogPane().getButtonTypes().clear();
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES) ;

        Optional<ButtonType> result = alert.showAndWait() ;

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (listOfSameTaskRepeated.size() == 1) {
                TaskCollection.getInstance().removeTask(this.taskToDo);
                System.out.println("\nINFO: Deleted! " + this.taskToDo.getText() + " - " + this.taskToDo.getId());
            }
            else {
                listOfSameTaskRepeated = listOfSameTaskRepeated.stream()
                        .filter(t -> t.getDueDate().equals(this.taskToDo.getDueDate()) || t.getDueDate().isAfter(this.taskToDo.getDueDate()))
                        .collect(Collectors.toList());

                for (TaskToDo t : listOfSameTaskRepeated) {
                    TaskCollection.getInstance().removeTask(t);
                    System.out.println("\nINFO: Deleted! " + this.taskToDo.getText() + " - " + this.taskToDo.getId());
                }
            }

            HomePageController.loadTasksToListView();
        }
    }
}
