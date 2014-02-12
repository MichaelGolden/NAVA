/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import nava.structurevis.Data1DDialog;
import nava.utils.GraphicsUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ScatterPlot extends JPanel {

    private static final long serialVersionUID = 4663901953519840762L;
    
    public ArrayList<Double> x = null;
    public ArrayList<Double> y = null;
    
    public double xmin = 0;
    public double xmax = 1;
    public double ymin = 0;
    public double ymax = 1;
    public double xrange = 1;
    public double yrange = 1;
    
    DecimalFormat xFormatDecimal = new DecimalFormat("0.00000");
    DecimalFormat yFormatDecimal = new DecimalFormat("0.00000");
    DecimalFormat xFormatScientific = new DecimalFormat("0.00E00");
    DecimalFormat yFormatScientific = new DecimalFormat("0.00E00");
    
    public String xlab = "x-axis";
    public String ylab = "y-axis";
    
    Font labelFont = new Font("Sans serif", Font.PLAIN, 12);
    Font axisLabelFont = new Font("Sans serif", Font.PLAIN, 10);
    
    
   double circleSize = 7;
   
    public String getFormattedString(double val)
    {
        if(Math.abs(val) < 0.001)
        {
            return xFormatScientific.format(val);
        }
        else
        {
            return xFormatDecimal.format(val);
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {

        autoSetAxes();
        
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double graphBorderLeft = 100;
        double graphBorderRight = 10;
        double graphBorderTop = 20;
        double graphBorderBottom = 45;
      
        double graphInsetBorder = 10;
        double graphWidth = getWidth()-(graphBorderLeft+graphBorderRight);
        double graphHeight = getHeight()-(graphBorderTop+graphBorderBottom);
        
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.gray);
        g.draw(new Rectangle2D.Double(graphBorderLeft, graphBorderTop, graphWidth, graphHeight));
        
        g.setFont(labelFont);
        g.setColor(Color.black);
        GraphicsUtils.drawStringCentred(g, graphBorderLeft+(graphWidth/2), graphBorderTop+30+graphHeight, xlab);
        AffineTransform orig = g.getTransform();
        g.rotate(-Math.PI/2);        
        g.drawString(ylab, (float)-(graphBorderTop+(graphHeight/2)+g.getFontMetrics().stringWidth(ylab)/2), 20f);
       // GraphicsUtils.drawStringVerticallyCentred(g, graphBorderTop+(graphHeight/2), graphBorderLeft, ylab);
        g.setTransform(orig);
        
        g.setFont(axisLabelFont);
        double xmarks = 5;
        for(int i = 0 ; i < xmarks ; i++)
        {
            double frac = i/(xmarks-1);
            double xpos = graphBorderLeft+graphInsetBorder + frac*(graphWidth-graphInsetBorder*2);
            double ypos =  graphBorderTop+graphHeight+7;
            double val = xmin+frac*(xrange);
            GraphicsUtils.drawStringCentred(g, xpos, ypos, getFormattedString(val)+"");
        }
        
        double ymarks = 5;
        double yaxisLabelWidth = 0;
        for(int i = 0 ; i < ymarks ; i++)
        {
            double frac = i/(ymarks-1);
            double val = ymin+frac*(yrange);
            yaxisLabelWidth = Math.max(yaxisLabelWidth, g.getFontMetrics().stringWidth(getFormattedString(val)));
        }
        for(int i = 0 ; i < ymarks ; i++)
        {
            double frac = i/(ymarks-1);
            double xpos = graphBorderLeft-5-yaxisLabelWidth;
            double ypos =  graphBorderTop+graphInsetBorder+((1-frac)*(graphHeight-graphInsetBorder*2));
            double val = ymin+frac*(yrange);
            GraphicsUtils.drawStringVerticallyCentred(g, xpos, ypos, getFormattedString(val) +"");
        }
        
        if(x != null && y != null)
        {
            for(int i = 0 ; i < x.size() ; i++)
            {
                double xpos =  graphBorderLeft+graphInsetBorder + ((x.get(i)-xmin) / (xrange))*(graphWidth-graphInsetBorder*2);
                double ypos =  graphBorderTop+graphInsetBorder + (graphHeight-graphInsetBorder*2) - ((y.get(i)-ymin) / (yrange))*(graphHeight-graphInsetBorder*2);
                Ellipse2D.Double circle = new Ellipse2D.Double(xpos-(circleSize/2), ypos-(circleSize/2), circleSize, circleSize);
                g.setColor(new Color(255,0,0,175));
                g.fill(circle);
            }
        }
    }
    
    public void setData(ArrayList<Double> x, ArrayList<Double> y)
    {
        this.x = x;
        this.y = y;
        xmin = Double.MAX_VALUE;
        xmax = Double.MIN_VALUE;
        ymin = Double.MAX_VALUE;
        ymax = Double.MIN_VALUE;
        xrange = 0;
        yrange = 0;
        autoSetAxes();
        repaint();
    }
    
    public void autoSetAxes()
    {
        if(x != null && y != null)
        {        
            for(int i = 0 ; i < x.size() ; i++)
            {
                xmin = Math.min(xmin, x.get(i));
                xmax = Math.max(xmax, x.get(i));
                ymin = Math.min(ymin, y.get(i));
                ymax = Math.max(ymax, y.get(i));           
            }

            xrange = xmax - xmin;
            yrange = ymax - ymin;
            if(xrange == 0)
            {
                xrange = 2;
                xmin = xmin-1;
                xmax = xmax+1;
            }
            if(yrange == 0)
            {
                yrange = 2;
                ymin = ymin-1;
                ymax = ymax+1;
            }
        }
    }
    
    public static void main(String args[]) {
        
        JFrame frame = new JFrame();
        frame.setSize(640, 480);
        ScatterPlot plot = new ScatterPlot();
        plot.x = new ArrayList<>();
        plot.y = new ArrayList<>();
        for(int i = 0 ; i < 50 ; i++)
        {
            plot.x.add(new Double(i));
            plot.y.add(new Double(i*i+i*2+3*i*Math.sin(i/2)));
        }
        plot.repaint();
        frame.add(plot, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
