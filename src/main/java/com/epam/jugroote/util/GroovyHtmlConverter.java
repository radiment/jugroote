package com.epam.jugroote.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GroovyHtmlConverter {
    public static String convertToScript(InputStream html) {
        InputStreamReader reader = new InputStreamReader(html);
        StringBuilder builder = new StringBuilder();
        int read;
        try {
            while ((read = reader.read()) != -1) {
                char ch = (char) read;
                switch (ch) {
                    case '<': possibleTagProcess(reader, builder, ch); break;
                    default: builder.append(ch);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException("Error while converting: ", e);
        }
        return builder.toString();
    }

    private static void possibleTagProcess(InputStreamReader reader, StringBuilder builder, char startCh)
            throws IOException {
        int read = reader.read();
        if (read == -1) return;

        char ch = (char) read;
        switch (ch) {
            case '\n':
            case ' ': builder.append(startCh).append(ch); return;
            default: builder.append("_writer.write(\"").append(startCh).append(ch);
        }
        while ((read = reader.read()) != -1) {
            ch = (char) read;
            switch (ch) {
                case '>': builder.append(ch).append("\")"); return;
                default:
                    builder.append(ch);
            }
        }
    }
}
