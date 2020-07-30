// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

import com.pavlobu.emojitextflow.Emoji;
import com.pavlobu.emojitextflow.EmojiParser;
import com.pavlobu.emojitextflow.EmojiImageCache;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * most code of this file is written by UltimateZero
 * UltimateZero github page: https://github.com/UltimateZero
 *
 * modified by Pavlo Buidenkov
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

import java.util.List;
import java.util.Map;

public class EmojiSearchListController {

	private static final boolean SHOW_MISC = false;
	@FXML
	private ScrollPane searchScrollPane;
	@FXML
	private FlowPane searchFlowPane;
	@FXML
	private TabPane tabPane;
	@FXML
	private TextField txtSearch;
	@FXML
	private ComboBox<Image> boxTone;

	@FXML
	void initialize() {
		if(!SHOW_MISC) {
			tabPane.getTabs().remove(tabPane.getTabs().size()-2, tabPane.getTabs().size());
		}
		ObservableList<Image> tonesList = FXCollections.observableArrayList();

		for(int i = 1; i <= 5; i++) {
			Emoji emoji = EmojiParser.getInstance().getEmoji(":thumbsup_tone"+i+":");
			Image image = EmojiImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex()));
			tonesList.add(image);
		}
		Emoji em = EmojiParser.getInstance().getEmoji(":thumbsup:"); //default tone
		Image image = EmojiImageCache.getInstance().getImage(getEmojiImagePath(em.getHex()));
		tonesList.add(image);
		boxTone.setItems(tonesList);
		boxTone.setCellFactory(e->new ToneCell());
		boxTone.setButtonCell(new ToneCell());
		boxTone.getSelectionModel().selectedItemProperty().addListener(e->refreshTabs());


		searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		searchFlowPane.prefWidthProperty().bind(searchScrollPane.widthProperty().subtract(5));
		searchFlowPane.setHgap(5);
		searchFlowPane.setVgap(5);

		txtSearch.textProperty().addListener(x-> {
			String text = txtSearch.getText();
			if(text.isEmpty() || text.length() < 2) {
				searchFlowPane.getChildren().clear();
				searchScrollPane.setVisible(false);
			} else {
				searchScrollPane.setVisible(true);
				List<Emoji> results = EmojiParser.getInstance().search(text);
				searchFlowPane.getChildren().clear();
				results.forEach(emoji ->searchFlowPane.getChildren().add(createEmojiNode(emoji)));
			}
		});


		for(Tab tab : tabPane.getTabs()) {
			ScrollPane scrollPane = (ScrollPane) tab.getContent();
			FlowPane pane = (FlowPane) scrollPane.getContent();
			pane.setPadding(new Insets(5));
			scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
			pane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(5));
			pane.setHgap(5);
			pane.setVgap(5);

			tab.setId(tab.getText());
			ImageView icon = new ImageView();
			icon.setFitWidth(20);
			icon.setFitHeight(20);
			switch (tab.getText().toLowerCase()) {
				case "frequently used":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":heart:").getHex())));
					break;
				case "people":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":smiley:").getHex())));
					break;
				case "nature":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":dog:").getHex())));
					break;
				case "food":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":apple:").getHex())));
					break;
				case "activity":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":soccer:").getHex())));
					break;
				case "travel":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":airplane:").getHex())));
					break;
				case "objects":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":bulb:").getHex())));
					break;
				case "symbols":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":atom:").getHex())));
					break;
				case "flags":
					icon.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(EmojiParser.getInstance().getEmoji(":flag_eg:").getHex())));
					break;
			}

			if(icon.getImage() != null) {
				tab.setText("");
				tab.setGraphic(icon);
			}

			tab.setTooltip(new Tooltip(tab.getId()));
			tab.selectedProperty().addListener(ee-> {
				if(tab.getGraphic() == null) return;
				if(tab.isSelected()) {
					tab.setText(tab.getId());
				} else {
					tab.setText("");
				}
			});
		}



		boxTone.getSelectionModel().select(0);
		tabPane.getSelectionModel().select(1);
	}

	private void refreshTabs() {
		Map<String, List<Emoji>> map = EmojiParser.getInstance().getCategorizedEmojis(boxTone.getSelectionModel().getSelectedIndex()+1);
		for(Tab tab : tabPane.getTabs()) {
			ScrollPane scrollPane = (ScrollPane) tab.getContent();
			FlowPane pane = (FlowPane) scrollPane.getContent();
			pane.getChildren().clear();
			String category = tab.getId().toLowerCase();
			if(map.get(category) == null) continue;
			map.get(category).forEach(emoji -> pane.getChildren().add(createEmojiNode(emoji)));
		}
	}

	private Node createEmojiNode(Emoji emoji) {
		StackPane stackPane = new StackPane();
		stackPane.setMaxSize(32, 32);
		stackPane.setPrefSize(32, 32);
		stackPane.setMinSize(32, 32);
		stackPane.setPadding(new Insets(3));
		ImageView imageView = new ImageView();
		imageView.setFitWidth(32);
		imageView.setFitHeight(32);
		try {
			imageView.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		stackPane.getChildren().add(imageView);

		Tooltip tooltip = new Tooltip(emoji.getShortname());
		Tooltip.install(stackPane, tooltip);
		stackPane.setCursor(Cursor.HAND);
		ScaleTransition st = new ScaleTransition(Duration.millis(90), imageView);

		stackPane.setOnMouseEntered(e-> {
			imageView.setEffect(new DropShadow());
			st.setToX(1.2);
			st.setToY(1.2);
			st.playFromStart();
			if(txtSearch.getText().isEmpty())
				txtSearch.setPromptText(emoji.getShortname());
		});
		stackPane.setOnMouseExited(e-> {
			imageView.setEffect(null);
			st.setToX(1.);
			st.setToY(1.);
			st.playFromStart();
		});
		return stackPane;
	}

	private String getEmojiImagePath(String hexStr) throws NullPointerException {
		return this.getClass().getResource("emoji_images/" + hexStr + ".png").toExternalForm();
	}

	class ToneCell extends ListCell<Image> {
		private final ImageView imageView;
		public ToneCell() {
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			imageView = new ImageView();
			imageView.setFitWidth(20);
			imageView.setFitHeight(20);
		}
		@Override
		protected void updateItem(Image item, boolean empty) {
			super.updateItem(item, empty);

			if(item == null || empty) {
				setText(null);
				setGraphic(null);
			} else {
				imageView.setImage(item);
				setGraphic(imageView);
			}
		}
	}
}
