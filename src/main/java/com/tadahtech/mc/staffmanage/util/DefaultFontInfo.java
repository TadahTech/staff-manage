package com.tadahtech.mc.staffmanage.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class DefaultFontInfo {

    private static final char COLOR_CHAR = '\u00a7';

    private static final Map<Character, Integer> LENGTHS = ImmutableMap.<Character, Integer>builder()
      .put(' ', 4)
      .put('!', 2)
      .put('"', 5)
      .put('#', 6)
      .put('$', 6)
      .put('%', 6)
      .put('&', 6)
      .put('\'', 3)
      .put('(', 5)
      .put(')', 5)
      .put('*', 5)
      .put('+', 6)
      .put(',', 2)
      .put('-', 6)
      .put('.', 2)
      .put('/', 6)
      .put('0', 6)
      .put('1', 6)
      .put('2', 6)
      .put('3', 6)
      .put('4', 6)
      .put('5', 6)
      .put('6', 6)
      .put('7', 6)
      .put('8', 6)
      .put('9', 6)
      .put(':', 2)
      .put(';', 2)
      .put('<', 5)
      .put('=', 6)
      .put('>', 5)
      .put('?', 6)
      .put('@', 7)
      .put('A', 6)
      .put('B', 6)
      .put('C', 6)
      .put('D', 6)
      .put('E', 6)
      .put('F', 6)
      .put('G', 6)
      .put('H', 6)
      .put('I', 4)
      .put('J', 6)
      .put('K', 6)
      .put('L', 6)
      .put('M', 6)
      .put('N', 6)
      .put('O', 6)
      .put('P', 6)
      .put('Q', 6)
      .put('R', 6)
      .put('S', 6)
      .put('T', 6)
      .put('U', 6)
      .put('V', 6)
      .put('W', 7)
      .put('X', 6)
      .put('Y', 6)
      .put('Z', 6)
      .put('[', 4)
      .put('\\', 6)
      .put(']', 4)
      .put('^', 6)
      .put('_', 7)
      .put('a', 6)
      .put('b', 6)
      .put('c', 6)
      .put('d', 6)
      .put('e', 6)
      .put('f', 5)
      .put('g', 6)
      .put('h', 6)
      .put('i', 2)
      .put('j', 6)
      .put('k', 5)
      .put('l', 3)
      .put('m', 6)
      .put('n', 6)
      .put('o', 6)
      .put('p', 6)
      .put('q', 6)
      .put('r', 6)
      .put('s', 6)
      .put('t', 4)
      .put('u', 6)
      .put('v', 6)
      .put('w', 7)
      .put('x', 6)
      .put('y', 6)
      .put('z', 6)
      .put('{', 5)
      .put('|', 2)
      .put('}', 5)
      .put('~', 7)
      .put('⌂', 6)
      .put('Ç', 6)
      .put('ü', 6)
      .put('é', 6)
      .put('â', 6)
      .put('ä', 6)
      .put('à', 6)
      .put('å', 6)
      .put('ç', 6)
      .put('ê', 6)
      .put('ë', 6)
      .put('è', 6)
      .put('ï', 4)
      .put('î', 6)
      .put('ì', 3)
      .put('Ä', 6)
      .put('Å', 6)
      .put('É', 6)
      .put('æ', 6)
      .put('Æ', 6)
      .put('ô', 6)
      .put('ö', 6)
      .put('ò', 6)
      .put('û', 6)
      .put('ù', 6)
      .put('ÿ', 6)
      .put('Ö', 6)
      .put('Ü', 6)
      .put('ø', 6)
      .put('£', 6)
      .put('Ø', 6)
      .put('×', 4)
      .put('ƒ', 6)
      .put('á', 6)
      .put('í', 3)
      .put('ó', 6)
      .put('ú', 6)
      .put('ñ', 6)
      .put('Ñ', 6)
      .put('ª', 6)
      .put('º', 6)
      .put('¿', 6)
      .put('®', 7)
      .put('¬', 6)
      .put('½', 6)
      .put('¼', 6)
      .put('¡', 2)
      .put('«', 6)
      .put('»', 6)
      .build();

    private char character;
    private int length;

    DefaultFontInfo(char character, int length) {
        this.character = character;
        this.length = length - 1;
    }

    public static DefaultFontInfo getDefaultFontInfo(char ch) {
        return new DefaultFontInfo(ch, LENGTHS.getOrDefault(ch, 5));
    }

    public char getCharacter() {
        return this.character;
    }

    public int getLength() {
        return this.length;
    }

    public int getBoldLength() {
        return this.length + 1;
    }

    public static int getPixelLength(String string) {
        if (string == null || string.isEmpty()) {
            return 0;
        }

        int messageSize = 0;
        boolean isColorCode = false;
        boolean isBold = false;

        for (char ch : string.toCharArray()) {
            if (ch == COLOR_CHAR) {
                isColorCode = true;
                continue;
            }

            if (isColorCode) {
                isColorCode = false;
                isBold = ch == 'l' || ch == 'L'; // Check if it is bold
                continue;
            }

            DefaultFontInfo fontInfo = DefaultFontInfo.getDefaultFontInfo(ch);
            messageSize += isBold ? fontInfo.getBoldLength() : fontInfo.getLength();
            messageSize++;
        }

        return messageSize;
    }
}
