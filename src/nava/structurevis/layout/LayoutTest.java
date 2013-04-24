/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis.layout;

import fr.orsay.lri.varna.exceptions.ExceptionModeleStyleBaseSyntaxError;
import fr.orsay.lri.varna.exceptions.ExceptionParameterError;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class LayoutTest {

    public static void main(String[] args) throws Exception {
        int[] pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString("(((((...)))))");
        //ArrayList<Point2D.Double> coordinates = NAView.naview_xy_coordinates(pairedSites);
       ArrayList<Point2D.Double> coordinates = RadiateView.radiateview_xy_coordinates(pairedSites, true);
        for(Point2D.Double coord : coordinates)
        {
            System.out.println(coord);
        }
        System.out.println();

    }
}
