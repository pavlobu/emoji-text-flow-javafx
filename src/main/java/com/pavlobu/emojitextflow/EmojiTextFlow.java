// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

package com.pavlobu.emojitextflow;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Created by Pavlo (Paul) Buidenkov on 2020-07-27
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

public class EmojiTextFlow extends TextFlow {
    private static final Logger logger = LoggerFactory.getLogger(EmojiTextFlow.class);

    private EmojiTextFlowParameters parameters;

    public EmojiTextFlow() {
        initializeDefaultParametersObject();
    }

    private void initializeDefaultParametersObject() {
        parameters = new EmojiTextFlowParameters();
        parameters.setEmojiScaleFactor(1D);
        parameters.setTextAlignment(TextAlignment.CENTER);
        parameters.setFont(Font.font("System", FontWeight.NORMAL, 35));
        parameters.setTextColor(Color.BLACK);
    }

    public EmojiTextFlow(EmojiTextFlowParameters parameters) {
        this.parameters = parameters;
        if (parameters.getTextAlignment() != null) {
            this.setTextAlignment(parameters.getTextAlignment());
        }
    }

    public void parseAndAppend(String message) {
        Queue<Object> obs = EmojiParser.getInstance().toEmojiAndText(message);
        while (!obs.isEmpty()) {
            Object ob = obs.poll();
            if (ob instanceof String) {
                addTextNode((String) ob);
                continue;
            }
            if (ob instanceof Emoji) {
                Emoji emoji = (Emoji) ob;
                try {
                    addEmojiImageNode(createEmojiImageNode(emoji));
                } catch (NullPointerException e) {
                    logger.error("Image with hex code: " + emoji.getHex() + " appear not to exist in resources path");
                    e.printStackTrace();
                    addTextNode(emoji.getUnicode());
                }
            }
        }

    }

    private ImageView createEmojiImageNode(Emoji emoji) throws NullPointerException {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(parameters.getEmojiFitWidth());
        imageView.setFitHeight(parameters.getEmojiFitHeight());
        imageView.setImage(EmojiImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
        return imageView;
    }

    private void addTextNode(String text) {
        Text textNode = new Text();
        textNode.setText(text);
        textNode.setFont(parameters.getFont());
        if (parameters.getTextColor() != null) {
            textNode.setFill(parameters.getTextColor());
        }

        this.getChildren().add(textNode);
    }

    private void addEmojiImageNode(ImageView emojiImageNode) {
        this.getChildren().add(emojiImageNode);
    }

    private String getEmojiImagePath(String hexStr) throws NullPointerException {
        return this.getClass().getClassLoader().getResource("emoji_images/" + hexStr + ".png").toExternalForm();
    }
}
