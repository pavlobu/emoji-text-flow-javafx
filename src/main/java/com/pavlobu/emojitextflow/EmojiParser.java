// Copyright (c) 2020, Pavlo Buidenkov. All rights reserved.
// Use of this source code is governed by a BSD 3-Clause License
// that can be found in the LICENSE file.

package com.pavlobu.emojitextflow;

import mjson.Json;

import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The most code of this file is written by UltimateZero
 * UltimateZero github page: https://github.com/UltimateZero
 *
 * modified by Pavlo Buidenkov
 * Pavlo Buidenkov github page: https://github.com/pavlobu
 */

public class EmojiParser {
    private static final boolean USE_ASCII = true;
    private static final HashMap<String, EmojiEntry> emojiList = new HashMap<>();
    private static final HashMap<String, String> asciiToShortname = new HashMap<>();
    private static final HashMap<String, String> shortnameToUnicode = new HashMap<>();
    private static final HashMap<String, String> unicodeToShortname = new HashMap<>();
    private static final HashMap<String, String> unicodeToHex = new HashMap<>();

    private static Pattern ASCII_PATTERN;
    private static Pattern UNICODE_PATTERN;
    private static Pattern SHORTNAME_PATTERN;

    private static EmojiParser INSTANCE = null;

    public static EmojiParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EmojiParser();
        }
        return INSTANCE;
    }

    private static final String EMOJIONE_JSON_FILE = "emoji.json";
    private static final String EMOJIONE_KEY_NAME = "name";
    private static final String EMOJIONE_KEY_SHORTNAME = "shortname";
    private static final String EMOJIONE_KEY_UNICODE_ALT = "unicode_alt";
    private static final String EMOJIONE_KEY_UNICODE = "unicode";
    private static final String EMOJIONE_KEY_ALIASES = "aliases";
    private static final String EMOJIONE_KEY_ALIASES_ASCII = "aliases_ascii";
    private static final String EMOJIONE_KEY_KEYWORDS = "keywords";
    private static final String EMOJIONE_KEY_CATEGORY = "category";
    private static final String EMOJIONE_KEY_EMOJI_ORDER = "emoji_order";

    private static final String EMOJIONE_MODIFIER = "modifier";


    private EmojiParser() {
        URL url = EmojiParser.class.getClassLoader().getResource(EMOJIONE_JSON_FILE);
        Json j = Json.read(url);
        Map<String, Json> map = j.asJsonMap();
        map.forEach((key, value) -> {
            Map<String, Json> valueMap = value.asJsonMap();
            String name = valueMap.get(EMOJIONE_KEY_NAME).asString();
            String shortname = valueMap.get(EMOJIONE_KEY_SHORTNAME).asString();
            List<String> unicodes = valueMap.get(EMOJIONE_KEY_UNICODE_ALT).asList().stream().map(o -> (String) o).collect(Collectors.toList());
            unicodes.add(valueMap.get(EMOJIONE_KEY_UNICODE).asString());
            List<String> aliases = valueMap.get(EMOJIONE_KEY_ALIASES).asList().stream().map(o -> (String) o).collect(Collectors.toList());
            List<String> aliases_ascii = valueMap.get(EMOJIONE_KEY_ALIASES_ASCII).asList().stream().map(o -> (String) o).collect(Collectors.toList());
            List<String> keywords = valueMap.get(EMOJIONE_KEY_KEYWORDS).asList().stream().map(o -> (String) o).distinct().collect(Collectors.toList());
            String category = valueMap.get(EMOJIONE_KEY_CATEGORY).asString();
            int emojiOrder = valueMap.get(EMOJIONE_KEY_EMOJI_ORDER).asInteger();

            EmojiEntry entry = new EmojiEntry();
            entry.setName(name);
            entry.setShortname(shortname);
            entry.setUnicodes(unicodes);
            entry.setAliases(aliases);
            entry.setAliasesAscii(aliases_ascii);
            entry.setKeywords(keywords);
            entry.setCategory(category);
            entry.setEmojiOrder(emojiOrder);
            EmojiParser.emojiList.put(shortname, entry);
        });

        EmojiParser.emojiList.forEach((shortname, entry) -> {
            entry.getUnicodes().forEach(unicode -> {
                if (unicode == null || unicode.isEmpty()) return;
                unicodeToHex.put(convert(unicode), unicode);
                shortnameToUnicode.put(shortname, convert(unicode));
                unicodeToShortname.put(convert(unicode), shortname);
            });
            entry.getAliasesAscii().forEach(ascii -> {
                asciiToShortname.put(ascii, shortname);
            });

        });

        ASCII_PATTERN = Pattern.compile(String.join("|", asciiToShortname.keySet().stream().map(o -> Pattern.quote(o)).collect(Collectors.toList())));
        SHORTNAME_PATTERN = Pattern.compile(String.join("|", emojiList.keySet().stream().collect(Collectors.toList())));
        UNICODE_PATTERN = Pattern.compile(String.join("|", unicodeToHex.keySet().stream().map(u -> Pattern.quote(u)).collect(Collectors.toList())));
    }

    public Queue<Object> toEmojiAndText(String str) {
        Queue<Object> queue = new ArrayDeque<>();
        String unicodeStr = shortnameToUnicode(str);
        Matcher matcher = UNICODE_PATTERN.matcher(unicodeStr);
        int lastEnd = 0;
        while (matcher.find()) {
            String lastText = unicodeStr.substring(lastEnd, matcher.start());
            if (!lastText.isEmpty())
                queue.add(lastText);
            String m = matcher.group();
            String hexStr = emojiList.get(unicodeToShortname.get(m)).getLastUnicode();
            if (hexStr == null || hexStr.isEmpty()) {
                queue.add(m);
            } else {
                queue.add(new Emoji(unicodeToShortname.get(m), m, hexStr));
            }
            lastEnd = matcher.end();
        }
        String lastText = unicodeStr.substring(lastEnd);
        if (!lastText.isEmpty())
            queue.add(lastText);
        return queue;
    }

    public String shortnameToUnicode(String str) {
        String output = replaceWithFunction(str, SHORTNAME_PATTERN, (shortname) -> {
            if (shortname == null || shortname.isEmpty() || (!emojiList.containsKey(shortname))) {
                return shortname;
            }
            if (emojiList.get(shortname).getUnicodes().isEmpty()) {
                return shortname;
            }

            String unicode = emojiList.get(shortname).getLastUnicode().toUpperCase();
            return convert(unicode);
        });

        if (USE_ASCII) {
            output = replaceWithFunction(output, ASCII_PATTERN, (ascii) -> {
                String shortname = asciiToShortname.get(ascii);
                String unicode = emojiList.get(shortname).getLastUnicode().toUpperCase();
                return convert(unicode);
            });
        }

        return output;
    }

    public String unicodeToShortname(String str) {
        String output = replaceWithFunction(str, UNICODE_PATTERN, (unicode) -> {
            if (unicode == null || unicode.isEmpty() || (!unicodeToShortname.containsKey(unicode))) {
                return unicode;
            }
            return unicodeToShortname.get(unicode);
        });

        return output;
    }

    public String shortnameToAscii(String str) {
        String output = replaceWithFunction(str, SHORTNAME_PATTERN, (shortname) -> {
            if (shortname == null || shortname.isEmpty() || (!emojiList.containsKey(shortname))) {
                return shortname;
            }
            if (emojiList.get(shortname).getAliasesAscii().isEmpty()) {
                return shortname;
            }
            return emojiList.get(shortname).getAliasesAscii().get(0);
        });

        return output;
    }

    public String asciiToShortname(String str) {
        String output = replaceWithFunction(str, ASCII_PATTERN, (ascii) -> {
            if (ascii == null || ascii.isEmpty() || (!asciiToShortname.containsKey(ascii))) {
                return ascii;
            }
            return asciiToShortname.get(ascii);
        });

        return output;
    }

    public String unicodeToAscii(String str) {
        return shortnameToAscii(unicodeToShortname(str));
    }

    public String asciiToUnicode(String str) {
        return shortnameToUnicode(asciiToShortname(str));
    }

    public List<String> getCategories() {
        return emojiList.values().stream().map(e -> e.getCategory()).distinct().collect(Collectors.toList());
    }

    public Map<String, List<Emoji>> getCategorizedEmojis(int tone) {
        Map<String, List<Emoji>> map = new HashMap<>();
        getTonedEmojis(tone).forEach(emojiEntry -> {
            if (emojiEntry.getCategory().equals(EMOJIONE_MODIFIER)) return;
            for (int i = 1; i <= 6; i++) {
                if (i == tone) continue;
                if (emojiEntry.getShortname().endsWith("_tone" + i + ":"))
                    return;
            }
            List<Emoji> list = map.computeIfAbsent(emojiEntry.getCategory(), k -> new ArrayList<>());
            Emoji emoji = new Emoji(emojiEntry.getShortname(), convert(emojiEntry.getLastUnicode()),
                    emojiEntry.getLastUnicode());
            emoji.setEmojiOrder(emojiEntry.getEmojiOrder());
            list.add(emoji);
        });

        map.values().forEach(list -> list.sort(Comparator.comparing(Emoji::getEmojiOrder)));

        return map;
    }

    public List<EmojiEntry> getTonedEmojis(int tone) {
        List<EmojiEntry> allToned = new ArrayList<>();
        List<EmojiEntry> selectedTone = new ArrayList<>();
        List<EmojiEntry> defaultTone = new ArrayList<>();
        emojiList.values().forEach(emojiEntry -> {
            for (int i = 1; i <= 5; i++) {
                if (emojiEntry.getShortname().endsWith("_tone" + i + ":")) {
                    allToned.add(emojiEntry);
                    if (emojiEntry.getShortname().endsWith(tone + ":")) {
                        selectedTone.add(emojiEntry);
                    }
                    String withoutTone = emojiEntry.getShortname().substring(0, emojiEntry.getShortname().length() - 7) + ":";
                    EmojiEntry emojiEntryWithoutTone = emojiList.get(withoutTone);
                    if (!defaultTone.contains(emojiEntryWithoutTone)) {
                        defaultTone.add(emojiEntryWithoutTone);
                    }
                }
            }
        });
        List<EmojiEntry> allEmojis = new ArrayList<>(emojiList.values());
        allEmojis.removeAll(allToned);
        allEmojis.removeAll(defaultTone);
        if (tone == 6) { //default
            allEmojis.addAll(defaultTone);
        } else {
            allEmojis.addAll(selectedTone);
        }
        return allEmojis;

    }

    private String replaceWithFunction(String input, Pattern pattern, Function<String, String> func) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(input);
        int lastEnd = 0;
        while (matcher.find()) {
            String lastText = input.substring(lastEnd, matcher.start());
            builder.append(lastText);
            builder.append(func.apply(matcher.group()));
            lastEnd = matcher.end();
        }
        builder.append(input.substring(lastEnd));
        return builder.toString();
    }

    private String convert(String unicodeStr) {
        if (unicodeStr.isEmpty()) return unicodeStr;
        String[] parts = unicodeStr.split("-");
        StringBuilder buff = new StringBuilder();
        for (String s : parts) {
            int part = Integer.parseInt(s, 16);
            if (part >= 0x10000 && part <= 0x10FFFF) {
                int hi = (int) (Math.floor((part - 0x10000) / 0x400) + 0xD800);
                int lo = ((part - 0x10000) % 0x400) + 0xDC00;
                buff.append(new String(Character.toChars(hi)) + new String(Character.toChars(lo)));
            } else {
                buff.append(new String(Character.toChars(part)));
            }
        }
        return buff.toString();
    }

    public List<Emoji> search(String text) {
        return emojiList.values().stream().filter(emojiEntry -> (emojiEntry.getShortname().contains(text)
                || emojiEntry.getAliases().contains(text) || emojiEntry.getAliasesAscii().contains(text))
                || emojiEntry.getName().contains(text)).map(emojiEntry ->
                new Emoji(emojiEntry.getShortname(), convert(emojiEntry.getLastUnicode()), emojiEntry.getLastUnicode())).collect(Collectors.toList());
    }

    public Emoji getEmoji(String shortname) {
        EmojiEntry entry = emojiList.get(shortname);
        if (entry == null) return null;
        Emoji emoji = new Emoji(entry.getShortname(), convert(entry.getLastUnicode()), entry.getLastUnicode());
        return emoji;
    }

    class EmojiEntry {
        private String name;
        private String shortname;
        private List<String> unicodes;
        private List<String> aliases;
        private List<String> aliasesAscii;
        private List<String> keywords;
        private String category;
        private int emojiOrder;

        public EmojiEntry() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortname() {
            return shortname;
        }

        public void setShortname(String shortname) {
            this.shortname = shortname;
        }

        public List<String> getUnicodes() {
            return unicodes;
        }

        public void setUnicodes(List<String> unicodes) {
            this.unicodes = unicodes;
        }

        public String getLastUnicode() {
            if (unicodes.isEmpty()) return null;
            return unicodes.get(unicodes.size() - 1);
        }

        public List<String> getAliases() {
            return aliases;
        }

        public void setAliases(List<String> aliases) {
            this.aliases = aliases;
        }

        public List<String> getAliasesAscii() {
            return aliasesAscii;
        }

        public void setAliasesAscii(List<String> aliasesAscii) {
            this.aliasesAscii = aliasesAscii;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getEmojiOrder() {
            return emojiOrder;
        }

        public void setEmojiOrder(int emojiOrder) {
            this.emojiOrder = emojiOrder;
        }
    }

}
