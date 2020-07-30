// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

import com.pavlobu.emojitextflow.EmojiTextFlow;
import com.pavlobu.emojitextflow.EmojiTextFlowParameters;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * most code of this file is written by UltimateZero
 * UltimateZero github page: https://github.com/UltimateZero
 *
 * modified by Pavlo Buidenkov
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

public class ExampleJavaFXEmojiTextFlow extends Application {

    final static double EMOJI_SCALE_FACTOR = 1D; // used to adjust emoji size and position in relation to other text
    final static int textSize = 50;

    // NOTE: you should create your own EmojiTextFlowParameters object in your code somewhere
    // it is not a must for this object to be  or be initialized in { } block!
    EmojiTextFlowParameters emojiTextFlowParameters;
    {
        emojiTextFlowParameters = new EmojiTextFlowParameters();
        emojiTextFlowParameters.setEmojiScaleFactor(EMOJI_SCALE_FACTOR);
        emojiTextFlowParameters.setTextAlignment(TextAlignment.CENTER);
        emojiTextFlowParameters.setFont(Font.font("Verdana", FontWeight.BOLD, textSize));
        emojiTextFlowParameters.setTextColor(Color.BLACK);
    }


    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, this is JavaFXEmojiTextFlow demo " + javafxVersion + ", running on Java " + javaVersion + ".");

        TextField textField = new TextField();
        ListView list = new ListView();
        Button printMessageButton = new Button("CLICK ME TO PRINT TEXT IN LIST");

        printMessageButton.setOnAction(e -> {
            processUserTextInput(textField, list);
        });

        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                processUserTextInput(textField, list);
            }
        });

        list.getItems().add(l);
        list.getItems().add(textField);
        list.getItems().add(printMessageButton);

        parseMessageToEmojTextFlowAndAddItToList("Hello, :smile: it is me :lizard:", list);

        StackPane stackPane = new StackPane();
        StackPane.setMargin(list, new Insets(8, 8, 8, 8));

        stackPane.getChildren().add(list);

        Scene scene = new Scene(stackPane, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    private void processUserTextInput(TextField textField, ListView list) {
        parseMessageToEmojTextFlowAndAddItToList(textField.getText(), list);
        textField.clear();
    }


    private void parseMessageToEmojTextFlowAndAddItToList(String message, ListView list) {
        EmojiTextFlow dialogContainer = new EmojiTextFlow(emojiTextFlowParameters);
        dialogContainer.parseAndAppend(message);

        list.getItems().add(3, dialogContainer);
    }

    public static void main(String[] args) {
        launch();
    }

}