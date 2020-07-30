// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

package com.pavlobu.emojitextflow;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Created by Pavlo (Paul) Buidenkov on 2020-07-27
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

public class EmojiTextFlowParameters {

    private TextAlignment textAlignment;
    private Font font;
    private Color textColor;
    private double emojiScaleFactor;

    public EmojiTextFlowParameters() {
        this.font = Font.getDefault();
    }

    public void setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public double getEmojiFitWidth() {
        return font.getSize() * emojiScaleFactor;
    }

    public double getEmojiFitHeight() {
        return font.getSize() * emojiScaleFactor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setEmojiScaleFactor(double emojiScaleFactor) {
        this.emojiScaleFactor = emojiScaleFactor;
    }

    public double getEmojiScaleFactor() {
        return emojiScaleFactor;
    }
}
