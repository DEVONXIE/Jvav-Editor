import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SyntaxHighlight implements DocumentListener {
    public HashMap<String, Color> keywords;
    public Style keywordStyle;
    public Style normalStyle;

    public SyntaxHighlight(LineNumberShowTextPane editor) {
        keywords = new javaKeywords();
        keywordStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        normalStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        StyleConstants.setForeground(normalStyle, Color.BLACK);

    }

    public char getCharAt(Document doc, int pos) throws BadLocationException {

        return doc.getText(pos, 1).charAt(0);

    }

    public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {

        char ch = getCharAt(doc, pos);

        if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
            return true;
        }

        return false;

    }

    public int getWordStartIndex(Document doc, int pos) throws BadLocationException {
        for (; pos > 0 && isWordCharacter(doc, pos - 1); pos--)
            ;
        return pos;
    }

    public int getWordEndIndex(Document doc, int pos) throws BadLocationException {
        for (; isWordCharacter(doc, pos); pos++)
            ;
        return pos;
    }

    public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
        int end = getWordEndIndex(doc, pos);
        String word = doc.getText(pos, end - pos);
        if (keywords.containsKey(word)) {
            StyleConstants.setForeground(keywordStyle, keywords.get(word));
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, end - pos, keywordStyle));
        } else {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, end - pos, normalStyle));
        }
        return end;
    }

    public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {

        int start = getWordStartIndex(doc, pos);

        int end = getWordEndIndex(doc, pos + len);

        char ch;

        while (start < end) {

            ch = getCharAt(doc, start);

            if (Character.isLetter(ch) || ch == '_') {

                start = colouringWord(doc, start);

            } else {

                SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));

                ++start;

            }

        }

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {

            colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());

        } catch (BadLocationException e1) {

            e1.printStackTrace();

        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {

            // 因为删除后光标紧接着影响的单词两边, 所以长度就不需要了

            colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);

        } catch (BadLocationException e1) {

            e1.printStackTrace();

        }

    }

    private class ColouringTask implements Runnable {

        private StyledDocument doc;

        private Style style;

        private int pos;

        private int len;

        public ColouringTask(StyledDocument doc, int pos, int len, Style style) {

            this.doc = doc;

            this.pos = pos;

            this.len = len;

            this.style = style;

        }

        public void run() {

            try {
                doc.setCharacterAttributes(pos, len, style, true);

            } catch (Exception e) {
            }

        }

    }

}
