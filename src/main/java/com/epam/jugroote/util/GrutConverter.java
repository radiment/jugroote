package com.epam.jugroote.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;

public class GrutConverter {
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
        StringBuilder indent = new StringBuilder();
        boolean tagStart;
        boolean writeMode;
        char ch;
        char prev;
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
            return read(" \t");
        }

        boolean read(CharSequence skip) throws IOException {
            while (oneRead() && skip.chars().anyMatch(value -> value == ch)) {
                indent.append(ch);
            }
            return read != -1;
        }

        State spaceInsert() {
            return spaceInsert(true);
        }

        State spaceInsert(boolean reset) {
            if (indent.length() == 0) return this;

            builder.append(indent);
            if (reset) {
                indent.setLength(0);
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
            indent.setLength(0);
            return this;
        }

        State token() {
            append(prev);
            return ch == '\n' ? ln() : append();
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
                } else if (!tryEvaluation()) {
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
                } else if (ch == '@' && checkToken(state -> state.ch == '{')) {
                    readTemplatePaste();
                } else {
                    if (writeMode) {
                        finishWrite();
                    }
                    if (ch == '"' && !last('\\')) {
                        append().readString();
                    } else if (!tryEvaluation()) {
                        spaceInsert().append();
                    }
                }
            }
            return this;
        }

        State readTemplatePaste() throws IOException {
            if (!writeMode) {
                append("_writer.write(\"");
            }
            while (read()) {
                if (ch == '}' && !last('\\') && checkToken(state -> state.ch == '@')) {
                    return this;
                } else if (!tryEvaluation()) {
                    appendIt();
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

        boolean tryEvaluation() throws IOException {
            if (ch == '$' && checkToken(state -> state.ch == '{')) {
                readEvaluation();
                return true;
            }
            return false;
        }

        State readEvaluation() throws IOException {
            if (writeMode) {
                spaceInsert().append("\")\n");
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
            } else if (ch == ' ' || ch == '\t') {
                indent.append(ch);
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
