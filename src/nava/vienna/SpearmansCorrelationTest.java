/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.util.ArrayList;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SpearmansCorrelationTest {
   
    double corr;
    double pval;
    int n;
    
    public static SpearmansCorrelationTest calculate(ArrayList<Double> x, ArrayList<Double> y)
    {
        double [][] data = new double[x.size()][2];
        for(int i = 0 ; i < x.size() ; i++)
        {
            data[i][0] = x.get(i);
            data[i][1] = y.get(i);
        }
        
         SpearmansCorrelationTest res = new SpearmansCorrelationTest();
        
        if(x.size() > 2)
        {
            SpearmansCorrelation test = new SpearmansCorrelation(new Array2DRowRealMatrix(data), new NaturalRanking(NaNStrategy.REMOVED));
            res.corr = test.getCorrelationMatrix().getEntry(0, 1);
            res.pval = test.getRankCorrelation().getCorrelationPValues().getEntry(0, 1);
            res.n = x.size();
        }
        
        return res;
    }
    
    
}
