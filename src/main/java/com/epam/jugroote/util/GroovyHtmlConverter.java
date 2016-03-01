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
            if (space == 0) return this;

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
                spaceInsert();
                builder.append(ch);
                return false;
            }
            if (predicate.test(this)) return true;

            spaceInsert().token();
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
                } else if (ch == '$' && checkToken(state -> state.ch == '{')) {
                    readEvaluation();
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
                    if (ch == '$' && checkToken(state -> state.ch == '{')) {
                        readEvaluation();
                    } else if (ch == '"' && !last('\\')) {
                        append().readString();
                    } else {
                        spaceInsert().append();
                    }
                }
            }
            return this;
        }

        State readString() throws IOException {
            while (read()) {
                spaceInsert().append();
                if (ch == '"' && !last('\\')) {
                    return this;
                }
            }
            return this;
        }

        State readEvaluation() throws IOException {
            if (writeMode) {
                append("\")\n");
            }
            append("_writer.write(");
            while (read()) {
                if (ch == '"' && !last('\\')) {
                    append().readString();
                } else if (ch == '}' && !last('\\')) {
                    append(")\n");
                    if (writeMode) {
                        startWrite();
                    }
                    return this;
                } else {
                    append();
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

    }

}
