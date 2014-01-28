/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.util.Objects;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class ParetoParameter {
        
    public String parameterName;
    public static enum ParetoRanking {HIGHER_IS_BETTER, LOWER_IS_BETTER};
    public ParetoRanking ranking = ParetoRanking.HIGHER_IS_BETTER;
    public double value;
    
    public ParetoParameter(String parameterName, ParetoRanking ranking, double value)
    {
        this.parameterName = parameterName;
        this.ranking = ranking;
        this.value = value;
    }

    @Override
    public String toString() {
        return value+"";
        //return "ParetoParameter{" + "parameterName=" + parameterName + ", ranking=" + ranking + ", value=" + value + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParetoParameter other = (ParetoParameter) obj;
        if (!Objects.equals(this.parameterName, other.parameterName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.parameterName);
        return hash;
    }

    
}
