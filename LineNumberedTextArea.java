import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

public class LineNumberedTextArea extends JPanel {
    private final JTextPane textPane;
    private final JTextArea lineNumbers;

    public LineNumberedTextArea() {
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 15));
        textPane.setEditorKit(new StyledEditorKit());
        textPane.setDocument(new DefaultStyledDocument());

        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        lineNumbers = new JTextArea("1");
        lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 15));
        lineNumbers.setBackground(Color.LIGHT_GRAY);
        lineNumbers.setEditable(false);

        Border border = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
        lineNumbers.setBorder(border);

        scrollPane.setRowHeaderView(lineNumbers);

        // 添加文本改变监听器，用于更新行号
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });
    }

    private void updateLineNumbers() {
        SwingUtilities.invokeLater(() -> {
            int totalLines = getLineCount();

            StringBuilder numbers = new StringBuilder();

            for (int i = 1; i <= totalLines; i++) {
                numbers.append(i).append("\n");
            }

            lineNumbers.setText(numbers.toString());
        });
    }

    // 获取 JTextPane 中的文本
    public String getText() {
        return textPane.getText();
    }

    // 设置 JTextPane 中的文本
    public void setText(String text) {
        textPane.setText(text);
        updateLineNumbers();
    }

    public int getLineCount() {
        int totalLines = 1;
        int offset = 0;

        try {
            while ((offset = Utilities.getRowEnd(textPane, offset) + 1) < textPane.getDocument().getLength()) {
                totalLines++;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return totalLines;
    }

}
