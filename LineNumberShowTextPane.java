import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LineNumberShowTextPane extends JTextPane {
    public LineNumberShowTextPane() {
        super();
    }

    public void drawLineNumber(Graphics line) {
        // 先设置页边距，然后再用Graphics画一个矩形，后面就在这个矩形上画行号
        setMargin(new Insets(0, 35, 0, 0));
        line.setColor(new Color(180, 180, 180));
        line.fillRect(0, 0, 30, getHeight());
        // 获取行数
        StyledDocument doc = getStyledDocument();
        Element elem = doc.getDefaultRootElement();
        int row = elem.getElementCount();
        line.setColor(new Color(90, 90, 90));
        line.setFont(getFont());
        for (int i = 1; i <= row; i++) {
            line.drawString(i + "", 2, getY(i));
        }
    }

    // 重写paint，JTextPane自带监听，真不戳，不像隔壁JTextArea要加一堆监听器
    @Override
    public void paint(Graphics line) {
        super.paint(line);
        StyleConstants.setFontSize(getInputAttributes(), 16);
        drawLineNumber(line);
    }

    // 获取要画的Y轴，防止他和行里的文本对不上
    public int getY(int row) {
        int Y;
        Y = row * 23 - 6;
        return Y;
    }
}
