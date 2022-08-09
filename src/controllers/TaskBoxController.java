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

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import models.Task;
import util.TaskCollection;


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
            });

            this.deleteBtn.setShape(new Circle(15.0)) ;
            this.deleteBtn.setGraphic(this.imageView);

            this.textLbl.widthProperty().addListener((obs, oldVal, newVal) -> {
                this.line.setStartX(newVal.doubleValue());
            });

            this.line.setVisible(this.task.getCompleted());

            this.taskBoxRoot.setId("taskBoxRoot");
            this.textLbl.setId("taskText");
            this.dueDateLbl.setId("dueDate");
        }
    }


    public void delete() {
        TaskCollection.getInstance().removeTask(this.task);
        HomePageController.removeTaskBox(this.taskBoxRoot);
    }


    public Task getModel() {
        return this.task ;
    }



}
