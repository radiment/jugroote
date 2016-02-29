package com.epam.jugroote.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;

public class GroovyHtmlConverter {
    public static String convertToScript(InputStream html) {
        State s = new State();
        s.reader = new InputStreamReader(html);
        s.builder = new StringBuilder();
        try {
            s.readCommon().finishWrite();
        } catch (IOException e) {
            throw new ConfigurationException("Error while converting: ", e);
        }
        return s.builder.toString();
    }

    private static void groovyCode(State s) {
        s.spaceInsert().append();
    }

    private static class State {
        InputStreamReader reader;
        StringBuilder builder;
        boolean tagStart;
        boolean writeMode;
        char ch;
        char prev;
        int space;
        int read;

        State append(String str) {
            builder.append(str);
            return this;
        }

        State append() {
            builder.append(ch);
            return this;
        }

        State append(char ch) {
            builder.append(ch);
            return this;
        }

        boolean oneRead() throws IOException {
            prev = ch;
            boolean result = (read = reader.read()) != -1;
            if (result) {
                ch = (char) read;
            }
            return result;
        }

        boolean read() throws IOException {
            while (oneRead() && ch == ' ') {
                space++;
            }
            return read != -1;
        }

        State spaceInsert() {
            return spaceInsert(true);
        }

        State spaceInsert(boolean reset) {
            for (int i = 0; i < space; i++) {
                builder.append(' ');
            }
            if (reset) {
                space = 0;
            }
            return this;
        }

        boolean last(char last) {
            return builder.charAt(builder.length() - 1) == last;
        }

        State ln() {
            if (writeMode) {
                builder.append("\\n");
            } else if (!last('\n')) {
                builder.append('\n');
            }
            return this;
        }

        State token() {
            return append(prev).append();
        }

        boolean checkToken(Predicate<State> predicate) throws IOException {
            if (!oneRead()) {
                builder.append(ch);
                return false;
            }
            if (predicate.test(this)) return true;

            token();
            return false;
        }

        State startTag() {
            tagStart = true;
            if (writeMode) {
                return spaceInsert().token();
            }
            writeMode = true;
            return startWrite().token();
        }

        private State startWrite() {
            return append("_writer.write(\"").spaceInsert();
        }

        State readTag() throws IOException {
            startTag();
            while (read()) {
                if (ch == '>') {
                    return append().endTag();
                } else if (ch == '$') {
                    possibleEvaluation(this, true);
                } else {
                    appendIt();
                }
            }
            return this;
        }

        State readCommon() throws IOException {
            while (read()) {
                if (ch == '<' && checkToken(state -> state.ch != ' ' && state.ch != '\n')) {
                    readTag();
                } else if (ch == '\n') {
                    ln();
                } else {
                    if (writeMode) {
                        finishWrite();
                    }
                    if (ch == '$') {
                        possibleEvaluation(this, false);

                    } else if (ch == '"') {
                        possibleString(this);

                    } else {
                        groovyCode(this);
                    }
                }
            }
            return this;
        }

        State appendIt() {
            if (ch == '\n') {
                ln();
            } else if (ch == ' ') {
                space++;
            } else if (ch == '"' && writeMode) {
                append("\\\"");
            } else {
                spaceInsert().append();
            }
            return this;
        }

        State endTag() {
            tagStart = false;
            return this;
        }

        State finishWrite() {
            if (writeMode) {
                builder.append("\")\n");
                writeMode = false;
            }
            return this;
        }

        State startCode() {
            if (tagStart) {
                finishWrite();
            }
            return spaceInsert();
        }

    }

    private static void possibleEvaluation(State s, boolean writeMode)
            throws IOException {
        if (!s.read()) return;

        if (s.ch != '{') {
            s.spaceInsert().append(s.prev).append();
            return;
        }

        if (writeMode) {
            s.append("\")\n");
        }
        s.append("_writer.write(");
        while (s.read()) {
            switch (s.ch) {
                case '"':
                    possibleString(s);
                    break;
                case '}': {
                    s.append(")\n");
                    if (writeMode) {
                        s.append("_writer.write(\"");
                    }
                    return;
                }
                default:
                    s.append();
            }
        }
    }

    private static void possibleString(State s)
            throws IOException {
        s.append();
        if (s.last('\\')) {
            return;
        }
        while (s.read()) {
            s.append();
            if (s.ch == '"' && !s.last('\\')) {
                return;
            }
        }
    }

    private static void possibleTagProcess(State s)
            throws IOException {
        char startCh = s.ch;
        if (!s.read()) return;

        if (s.prev != startCh || s.ch == '\n' || s.ch == ' ') {
            s.spaceInsert().append(startCh).append();
            return;
        }
        s.readTag();
    }

}
