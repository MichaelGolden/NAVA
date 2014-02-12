/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.List;
import nava.vienna.Utils;
import nava.vienna.inverserna.EvaluationType.ParetoRanking;
import nava.vienna.inverserna.criteria.InverseRNAUtils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class GCcontentEvaluator extends Evaluator {
    
    public GCcontentEvaluator(EvaluationTypeRegister typeRegister)
    {
        super(typeRegister);               
    }

    @Override
    public ArrayList<EvaluationValue> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationValue> evaluatedCriteria = new ArrayList<>();    
        evaluatedCriteria.add(new EvaluationValue(InverseRNAUtils.calculateGC(tc.sequence), typeRegister.getType("gc_content_all_sites", tc.target, ParetoRanking.LOWER_IS_BETTER, false,0,1)));
        evaluatedCriteria.add(new EvaluationValue(InverseRNAUtils.calculatePairedSitesGCContent(tc.target.pairedSites, tc.sequence), typeRegister.getType("gc_content_paired_sites", tc.target, ParetoRanking.LOWER_IS_BETTER, true,0,1)));
        evaluatedCriteria.add(new EvaluationValue(InverseRNAUtils.calculatedUnpairedGCContent(tc.target.pairedSites, tc.sequence), typeRegister.getType("gc_content_unpaired_sites", tc.target, ParetoRanking.LOWER_IS_BETTER, true,0,1)));
        return evaluatedCriteria;        
    }
}
