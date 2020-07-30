// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

import com.pavlobu.emojitextflow.EmojiTextFlow;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.text.*;

/**
 * created by Pavlo Buidenkov
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

public class EmojiTextConversionController {

	@FXML
	private TextArea txtInput;
	@FXML
	private EmojiTextFlow flowOutput;

	@FXML
	void initialize() {
		flowOutput.setPadding(new Insets(10));
		txtInput.setFont(Font.font(16));
		txtInput.textProperty().addListener(e-> {
			flowOutput.getChildren().clear();
			flowOutput.parseAndAppend(txtInput.getText());
		});

		txtInput.setText(":smile: :D :) Hey \n" +
				"is this :pray: working?\n" +
				"Cool!! :thumbsup: :fireworks: :tada: :confetti_ball: \n");
	}
}
