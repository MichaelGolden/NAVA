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
public class ConsolePanel extends JPanel {

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
    ConsoleBuffer standardConsole;

    public ConsolePanel() {
        standardConsole = new ConsoleBuffer(new ConsoleDatabase(), "app_123", "standard_out");
        /*
         * lines.add("With just days left in the year, lawmakers in two states
         * are making last-minute bids to pass marriage equality bills.");
         * lines.add("thumb up﻿ if damienwalters took u here, because he liked
         * this video."); lines.add("Congratulations to the lucky winners who
         * won one of 10 cheques worth R20,000 by entering the Kellogg’s®
         * All-Bran® Shopping Spree Competition. Thank you all for
         * participating!"); lines.add("Interwebs drama of the day: Randi
         * Zuckerberg, sister of Mark Zuckerberg, threw a fit when someone
         * tweeted a copy of a Zuckerberg family photo (see above) that Randi
         * herself had posted to Facebook."); lines.add("A company that once
         * claimed it wasn't tracking users when they were logged off, only to
         * turn around and admit that it was, just before someone reported that
         * Facebook in fact had applied for and received a patent on technology
         * that would do exactly that;"); lines.add("People will wonder what
         * gifts these people bought each other and why the boy with the very
         * pale face and the hoodie is leaning smugly against the kitchen
         * cabinets."); lines.add("An anonymous reader writes with news of a
         * study out of the Netherlands (abstract) about the link between
         * psychosis and marijuana use.");
         */

        new Thread() {

            public void run() {
                for (int i = 0;; i++) {
                    standardConsole.bufferedWrite("inserting line "+i, "app_123", "standard_out");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConsolePanel.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }
            }
        }.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setFont(MainFrame.fontDroidSansMono);
        fontHeight = g.getFontMetrics().getHeight();
        fontWidth = g.getFontMetrics().stringWidth("W");

        numLines = standardConsole.n;
        int width = leftPadding + rightPadding + (fontWidth * charsPerLine) + fontHeight;
        int height = topPadding + bottomPadding + (fontHeight + lineSpacing) * numLines;
        Rectangle visibleRect = this.getVisibleRect();

        this.setPreferredSize(new Dimension(width, height));



        g.setColor(Color.white);
        g.fill(visibleRect);

        g.setFont(MainFrame.fontDroidSansMono);
        g.setColor(Color.black);
        
        int linesPerScreen = (int) (Math.ceil((double) visibleRect.height / (double) (fontHeight + lineSpacing)))+1;

        //int startLine = (int) (((double) (visibleRect.y) / (double) (visibleRect.height)) * (double) (linesPerScreen));
        int startLine = (int) ((double) (visibleRect.y-fontHeight) / (double) (fontHeight + lineSpacing));
        int endLine = startLine + linesPerScreen;

        ArrayList<ConsoleRecord> records = standardConsole.getRecords(startLine, linesPerScreen);
        //ArrayList<ConsoleRecord> records = standardConsole.getRecords(0, standardConsole.n);
        /*
         * for (int i = 0; i < lines.size(); i++) { g.drawString(i+".
         * "+lines.get(i), leftPadding, i * (fontHeight + lineSpacing) +
         * fontHeight); }
         *
         */
        
        for (int i = 0; i < records.size(); i++) {
            g.drawString(records.get(i).lineNumber + ". " + records.get(i).text, leftPadding, records.get(i).lineNumber * (fontHeight + lineSpacing) + fontHeight);
        }
    }
}
