/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.console;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import nava.ui.MainFrame;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ConsolePanel extends JPanel implements ConsoleListener {
    
    int numLines = 10;
    int charsPerLine = 120;
    int topPadding = 10;
    int bottomPadding = 10;
    int leftPadding = 10;
    int rightPadding = 10;
    int fontHeight = 0;
    int fontWidth = 0;
    int lineSpacing = 3;
    // ArrayList<String> lines = new ArrayList<String>();
    ConsoleBuffer console;
    
    public void setConsoleBuffer(ConsoleBuffer consoleBuffer) {
        if (this.console != null) {
            console.removeConsoleListener(this);
        }
        this.console = consoleBuffer;        
        console.addConsoleListener(this);
        repaint();
    }
    
    public ConsolePanel() {
    }
    
    @Override
    public void paintComponent(Graphics graphics) {        
        
        if (console == null) {
            numLines = 0;
        } else {
            numLines = console.n;
            charsPerLine = console.maxLineChars;
        }
        
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g.setFont(MainFrame.fontDroidSansMono);
        fontHeight = g.getFontMetrics().getHeight();
        fontWidth = g.getFontMetrics().stringWidth("W");
        
        
        int width = leftPadding + rightPadding + (fontWidth * charsPerLine);
        int height = topPadding + bottomPadding + (fontHeight + lineSpacing) * numLines;
        Rectangle visibleRect = this.getVisibleRect();
        
        this.setPreferredSize(new Dimension(width, height));
        
        g.setColor(Color.white);
        g.fill(visibleRect);
        
        if (console == null) {
            return;
        }
        
        
        g.setFont(MainFrame.fontDroidSansMono);
        g.setColor(Color.black);
        
        int linesPerScreen = (int) (Math.ceil((double) visibleRect.height / (double) (fontHeight + lineSpacing))) + 1;

        //int startLine = (int) (((double) (visibleRect.y) / (double) (visibleRect.height)) * (double) (linesPerScreen));
        int startLine = (int) ((double) (visibleRect.y - fontHeight) / (double) (fontHeight + lineSpacing));
        int endLine = startLine + linesPerScreen;
        
        ArrayList<ConsoleRecord> records = console.getRecords(startLine, linesPerScreen);
        //ArrayList<ConsoleRecord> records = standardConsole.getRecords(0, standardConsole.n);
        /*
         * for (int i = 0; i < lines.size(); i++) { g.drawString(i+".
         * "+lines.get(i), leftPadding, i * (fontHeight + lineSpacing) +
         * fontHeight); }
         *
         */
        
        for (int i = 0; i < records.size(); i++) {
            
            g.setColor(getTypeColor(records.get(i).typeName));
            // g.drawString(records.get(i).lineNumber + ". " + records.get(i).text, leftPadding, records.get(i).lineNumber * (fontHeight + lineSpacing) + fontHeight);
            g.drawString(records.get(i).text, leftPadding, records.get(i).lineNumber * (fontHeight + lineSpacing) + fontHeight);
        }
    }
    
    @Override
    public void lineAddedEvent(int totalLines) {
        repaint();
    }
    
    public void clearScreen() {
        console = null;
        repaint();
    }
    ArrayList<String> types = new ArrayList<>();
    ArrayList<Color> colours = new ArrayList<>();

    public void setTypeColor(String type, Color color) {
        types.add(type);
        colours.add(color);
    }
    Color defaultColor = Color.black;

    public Color getTypeColor(String type) {
        int index = types.indexOf(type);
        if (index < 0) {
            return defaultColor;
        }
        return colours.get(index);
    }
}
