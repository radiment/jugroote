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
                    case '$': possibleEvaluation(reader, builder, ch, false); break;
                    case '"': possibleString(reader, builder, ch); break;
                    default: builder.append(ch);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationException("Error while converting: ", e);
        }
        return builder.toString();
    }

    private static void possibleEvaluation(InputStreamReader reader, StringBuilder builder, char startCh,
                                           boolean writeMode)
            throws IOException {
        int read = reader.read();
        if (read == -1) return;

        char ch = (char) read;
        if (ch != '{') {
            builder.append(startCh).append(ch);
            return;
        }

        if (writeMode) {
            builder.append("\") ");
        }
        builder.append("_writer.write(");
        while ((read = reader.read()) != -1) {
            ch = (char) read;
            switch (ch) {
                case '"': possibleString(reader, builder, ch); break;
                case '}': {
                    builder.append(")");
                    if (writeMode) {
                        builder.append(" _writer.write(\"");
                    }
                    return;
                }
                default:
                    builder.append(ch);
            }
        }
    }

    private static void possibleString(InputStreamReader reader, StringBuilder builder, char startCh)
            throws IOException {
        builder.append(startCh);
        if (lastIs(builder, '\\')) {
            return;
        }
        int read;
        char ch;
        while ((read = reader.read()) != -1) {
            ch = (char) read;
            builder.append(ch);
            if (ch == '"' && !lastIs(builder, '\\')) {
                return;
            }
        }
    }

    private static boolean lastIs(StringBuilder builder, char c) {
        return builder.charAt(builder.length() - 1) == c;
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
                case '\n':
                    builder.append("\\n"); break;
                case '"':
                    builder.append("\\\""); break;
                case '$': possibleEvaluation(reader, builder, ch, true); break;
                default:
                    builder.append(ch);
            }
        }
    }
}
