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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.Task;
import util.TaskCollection;
import values.HomePageHeaders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class TaskBoxController {
    public AnchorPane taskBoxRoot ;
    public CheckBox completedCheckBox ;
    public Label textLbl, dueDateLbl ;
    public Button deleteBtn ;
    public Line line ;

    private final Label idLbl = new Label();
    private final ImageView imageView = new ImageView(new Image("/resources/images/delete_icon.png")) ;


    private Task task ;


    public void initialize(Task task) {
        if (this.task != null) {
            throw new IllegalStateException("\nERROR: TaskBoxController.initModel() -> The model can only be initialized once") ;
        }
        else {
            this.task = task ;

            this.idLbl.setText(this.task.getId());
            this.idLbl.setVisible(false) ;
            this.taskBoxRoot.getChildren().add(0, this.idLbl) ;

            this.textLbl.setWrapText(false);
            this.textLbl.textProperty().bind(this.task.getTaskTextStringProperty());
            this.dueDateLbl.textProperty().bind(this.task.getDueDateStringProperty());

            this.completedCheckBox.setSelected(this.task.getCompleted()) ;
            this.completedCheckBox.setOnAction(e -> {
                boolean selected = this.completedCheckBox.isSelected() ;

                this.task.setCompleted(selected);
                this.line.setVisible(selected);

                TaskCollection.getInstance().storeTasksToFile();

                if (selected) {
                    if (HomePageController.headerStrProperty.getValue().equals(HomePageHeaders.PLANNED_TASKS)) {
                        if (this.task.getDueDate().isBefore(LocalDate.now())) {
                            HomePageController.removeTaskBox(this.taskBoxRoot, this.task);
                        }
                    }
                }
                else {
                    if (HomePageController.headerStrProperty.getValue().equals(HomePageHeaders.COMPLETED_TASKS)) {
                        HomePageController.removeTaskBox(this.taskBoxRoot, this.task);
                        HomePageController.addNewTask(this.task);
                    }
                }
            });

            this.deleteBtn.setShape(new Circle(15.0)) ;
            this.deleteBtn.setGraphic(this.imageView);

            this.textLbl.widthProperty().addListener((obs, oldVal, newVal) -> this.line.setStartX(newVal.doubleValue()));

            this.line.setVisible(this.task.getCompleted());

            this.taskBoxRoot.setId("taskBoxRoot");
            this.textLbl.setId("taskText");
            this.dueDateLbl.setId("dueDate");
        }
    }


    public void delete() {
        List<Task> listOfSameTaskRepeated = TaskCollection.getInstance().getAllTasksThatRepeat(this.task);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION) ;
        alert.setTitle("Delete Task");
        alert.getDialogPane().setContent(new Label("Are you absolutely sure that you wish to delete this task? Once deleted, it can't be undone."));
        alert.getDialogPane().getButtonTypes().clear();
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES) ;

        Optional<ButtonType> result = alert.showAndWait() ;

        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (listOfSameTaskRepeated.size() == 1) {
                TaskCollection.getInstance().removeTask(this.task);
                System.out.println("\nINFO: Deleted! " + this.task.getText() + " - " + this.task.getId());
            }
            else {
                listOfSameTaskRepeated = listOfSameTaskRepeated.stream()
                        .filter(t -> t.getDueDate().equals(this.task.getDueDate()) || t.getDueDate().isAfter(this.task.getDueDate()))
                        .toList();

                for (Task t : listOfSameTaskRepeated) {
                    TaskCollection.getInstance().removeTask(t);
                    System.out.println("\nINFO: Deleted! " + this.task.getText() + " - " + this.task.getId());
                }
            }

            HomePageController.loadTasksToListView();
        }
    }
}
