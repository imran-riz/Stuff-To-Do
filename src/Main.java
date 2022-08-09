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


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.Page;
import util.PageNavigator;
import util.TaskCollection;

import java.io.IOException;
import java.util.Objects;


public class Main extends Application {

    public static void main(String[] args) {
        TaskCollection.getInstance().loadTasksFromFile() ;
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(new Pane()) ;

        this.setUpPages() ;
        PageNavigator.setMainScene(scene) ;
        PageNavigator.activatePage("HomePage");

        stage.setTitle("S T U F F   T O   D O");
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/resources/images/app_logo.png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void setUpPages() throws IOException {
        Pane homePagePane = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/resources/fxml/pages/home-page.fxml"))) ;
        Pane aboutPagePane = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/resources/fxml/pages/about-page.fxml"))) ;

        Page homePage = new Page(homePagePane, "/resources/styles/home-page-stylesheet.css") ;
        Page aboutPage = new Page(aboutPagePane, "/resources/styles/about-page-stylesheet.css") ;

        PageNavigator.add("HomePage", homePage) ;
        PageNavigator.add("AboutPage", aboutPage) ;
    }
}
