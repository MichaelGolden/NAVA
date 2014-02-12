/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna.criteria;

import nava.vienna.inverserna.EvaluationType;
import nava.vienna.inverserna.EvaluationValue;
import nava.vienna.inverserna.TargetStructure;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SimpleCriterionValue2 extends EvaluationValue<SimpleCriterionValue> {

    public SimpleCriterionValue2(double value, EvaluationType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public int compareTo(SimpleCriterionValue o) {
        return Double.compare(this.value, o.value);
    }
}
