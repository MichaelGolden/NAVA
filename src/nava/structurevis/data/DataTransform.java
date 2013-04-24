package nava.structurevis.data;

import java.io.Serializable;
import java.text.DecimalFormat;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;

/**
 *
 * @author Michael Golden
 */
public class DataTransform implements Serializable {
    private static final long serialVersionUID = -7745486856947485491L;

    public enum TransformType implements Serializable {

        LINEAR, // transform data between 0 and 1
        NORMSINV1, // inverse normal distribution one-tailed
        EXPLOG, // transform data using e^(logx), useful for p-values
        SQRT, // tranform by square root
        SQUARED; // transform by squaring
        //IDENTITY;;/ 

        @Override
        public String toString() {
            switch (this) {
                case LINEAR:
                    return "Linear";
                case NORMSINV1:
                    return "Inverse normal distribution";
                case EXPLOG:
                    return "Exponential logarithm";
                //case IDENTITY:
                    //return "Identity";
                case SQRT:
                    return "Square root";
                case SQUARED:
                    return "Squared";
                default:
                    return "";
            }
        }
    };
    public double min;
    public double max;
    public TransformType type;

    public DataTransform(double min, double max, TransformType type) {
        this.min = min;
        this.max = max;
        this.type = type;
    }

    public float transform(float x) {
        return (float) transform((double) x);
    }

    public double transform(double x) {
        switch (type) {
            //case IDENTITY:
                //return x;
            case LINEAR:
                return (x - min) / (max - min);
            case NORMSINV1:
                double minz = StatUtils.getInvCDF(min/2,false);
                double maxz = StatUtils.getInvCDF(max/2,false);
                //double t = (StatUtils.getInvCDF(x/2,false)-mint)/(maxt-mint);
                double t = (StatUtils.getInvCDF(x/2,false)-minz)/(maxz-minz);
               // System.out.println("mint " + mint+"\tmaxt "+maxt);
                //System.out.println(x+"\t"+t);
                return t;
            case EXPLOG:
                double q = Math.log10(1 / 255.0); // calcuate last value in colour range to display
                double minp = (Math.log10(max) - Math.log10(min));
                double scale = q / minp / 1.75;
                double f = Math.exp((Math.log10(x) - Math.log10(min)) * scale);
                return 1 - f;
            case SQRT:
                return (Math.sqrt(x) - Math.sqrt(min)) / Math.sqrt(max - min);
            case SQUARED:
                return (Math.pow(x, 2) - Math.pow(min, 2)) / Math.pow(max - min, 2);
        }
        return 0;
    }

    public double inverseTransform(double y) {
        switch (type) {
            //case IDENTITY:
                //return y;
            case LINEAR:
                return (y * (max - min)) + min;
            case NORMSINV1:
                double minz = StatUtils.getInvCDF(min/2,false);
                double maxz = StatUtils.getInvCDF(max/2,false);
                //(StatUtils.getInvCDF(x/2,false)-mint)/(maxt-mint)
                // normalz returns p-value
                //StatUtils.erfc(y);
                //System.out.println("y*(maxz-minz))+minz "+maxz+"\t"+minz);
               // System.out.println(RankingAnalyses.NormalZ(minz));
               // System.out.println(RankingAnalyses.NormalZ(maxz));
              //  System.out.println(RankingAnalyses.NormalZ(0));
              //  System.out.println(RankingAnalyses.NormalZ(1));
              //  System.out.println(RankingAnalyses.NormalZ(0.5));
                //return RankingAnalyses.NormalZ((y*(maxz-minz))+minz);
                return RankingAnalyses.NormalZ((y*(maxz-minz))+minz);
               // return  StatUtils.erfc((y*(maxz-minz))+minz)*2;
            case EXPLOG:
                double q = Math.log10(1 / 255.0); // calcuate last value in colour range to display
                double minp = (Math.log10(max) - Math.log10(min));
                double scale = q / minp / 1.75;
                return Math.min(Math.pow(10, (Math.log(1 - y) / scale) + Math.log10(min)), max);
            case SQRT:
                return Math.pow((y * Math.sqrt(max - min)) + Math.sqrt(min), 2);
            case SQUARED:
                return Math.sqrt((y * Math.pow(max - min,2)) + Math.pow(min,2));
        }
        return 0;
    }
    
    
    static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    static DecimalFormat exponentialFormat = new DecimalFormat("0.00E0");
    
    public String getFormattedString(double val, int fractionDigits)
    {
        switch(type)
        {
            case LINEAR:
                return decimalFormat.format(val);
            case EXPLOG:
                return exponentialFormat.format(val);
            case NORMSINV1:
                return exponentialFormat.format(val);
            default:
                return decimalFormat.format(val);
        }
    }
}
