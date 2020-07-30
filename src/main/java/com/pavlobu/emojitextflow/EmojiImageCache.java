// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

package com.pavlobu.emojitextflow;

/**
 * The most code of this file is written by UltimateZero
 * UltimateZero github page: https://github.com/UltimateZero
 *
 * modified by Pavlo Buidenkov
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

import javafx.scene.image.Image;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class EmojiImageCache {
    private ConcurrentHashMap<String, WeakReference<Image>> map;
    private static EmojiImageCache INSTANCE;

    private EmojiImageCache() {
        map = new ConcurrentHashMap<>();
    }

    public Image getImage(String path) {
        WeakReference<Image> ref = map.get(path);
        if (ref == null || ref.get() == null) {
            ref = new WeakReference<>(new Image(path, true));
            map.put(path, ref);
        }
        return ref.get();
    }

    public static EmojiImageCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EmojiImageCache();
        }
        return INSTANCE;
    }
}
