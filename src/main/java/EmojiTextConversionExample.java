// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * most code of this file is written by UltimateZero
 * UltimateZero github page: https://github.com/UltimateZero
 *
 * modified by Pavlo Buidenkov
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

public class EmojiTextConversionExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL(this.getClass().getClassLoader().getResource("ExampleEmojiConversion.fxml").toExternalForm()));
			Parent root = loader.load();
			Scene scene = new Scene(root, 600, 300);
			primaryStage.setTitle("Emoji Conversion");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
