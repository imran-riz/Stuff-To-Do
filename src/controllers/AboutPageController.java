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

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import util.PageNavigator;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutPageController implements Initializable {
    public AnchorPane root ;
    public Button backBtn ;
    public Label headerLbl ;
    public TextArea aboutAppTextArea, licensesTextArea;
    public Hyperlink hyperlink;
    public ImageView appLogoImgView ;

    private final ImageView imageView = new ImageView(new Image("/resources/images/back_arrow_icon.png")) ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.backBtn.setShape(new Circle(25));
        this.backBtn.setGraphic(this.imageView);

        this.aboutAppTextArea.setEditable(false);
        this.licensesTextArea.setEditable(false);

        this.hyperlink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/imran-2003/Stuff-To-Do")) ;
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }) ;

        this.appLogoImgView.setImage(new Image("/resources/images/app_logo.png"));

        this.root.setId("root");
        this.aboutAppTextArea.setId("aboutTextArea");
        this.licensesTextArea.setId("licensesTextArea");
    }

    public void goBack() {
        PageNavigator.activatePage("HomePage");
    }
}
