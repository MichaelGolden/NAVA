/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import nava.vienna.Utils;
import nava.vienna.inverserna.criteria.InverseRNAUtils;
import nava.vienna.inverserna.criteria.SimpleCriterion;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class GCcontentEvaluator extends Evaluator {

    @Override
    public ArrayList<EvaluationCriterion> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationCriterion> evaluatedCriteria = new ArrayList<>();
        evaluatedCriteria.add(new SimpleCriterion("gc_content_all_sites", InverseRNAUtils.calculateGC(tc.sequence), EvaluationCriterion.ParetoRanking.LOWER_IS_BETTER, false,0,1));
        evaluatedCriteria.add(new SimpleCriterion("gc_content_paired_sites", InverseRNAUtils.calculatePairedSitesGCContent(tc.target.pairedSites, tc.sequence), EvaluationCriterion.ParetoRanking.LOWER_IS_BETTER, true,0,1));
        evaluatedCriteria.add(new SimpleCriterion("gc_content_unpaired_sites", InverseRNAUtils.calculatedUnpairedGCContent(tc.target.pairedSites, tc.sequence), EvaluationCriterion.ParetoRanking.LOWER_IS_BETTER, true,0,1));
        return evaluatedCriteria;        
    }
}
