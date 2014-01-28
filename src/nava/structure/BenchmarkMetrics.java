package nava.structure;


public class BenchmarkMetrics {
	/**
	 * Given an array of paired sites corresponding to the real structure 
	 * and an array corrsponding to the predicted structure returns the sensitivity.
	 * @param realPairedSites
	 * @param predictedPairedSites
	 * @return
	 */
	public static double calculateSensitivity (int [] realPairedSites, int [] predictedPairedSites)
	{/* The sensitivity for a predicted structure
		is the percentage of base pairs in the experimental structure that are also present in
		the predicted structure.*/
		
		/*double totalRealBasePairs = 0;		
		double correctlyPredicted = 0;
		for(int i = 0 ; i < realPairedSites.length ; i++)
		{
			if(realPairedSites[i] >= i + 1 && realPairedSites[i] != 0)
			{
				totalRealBasePairs++;
				if(realPairedSites[i] == predictedPairedSites[i])
				{
					correctlyPredicted++;
				}
			}
		}
		

		//System.out.println(correctlyPredicted);
		//System.out.println(totalRealBasePairs);
		
		//return correctlyPredicted / totalRealBasePairs;
		
		double [] ret = getValues(realPairedSites, predictedPairedSites);
		return ret[0] / (ret[0]+ret[3]);*/
		
		double count = 0;
		double total = 0;
		for(int i = 0 ; i < realPairedSites.length ; i++)
		{
			if(realPairedSites[i] != 0)
			{
				total++;
				if(realPairedSites[i] == predictedPairedSites[i])
				{
					count++;
				}
			}
		}
		
		return count / total;
	}
	
	/**
	 * Given an array of paired sites corresponding to the real structure 
	 * and an array corresponding to the predicted structure returns the PPV.
	 * @param realPairedSites
	 * @param predictedPairedSites
	 * @return
	 */
	public static double calculatePPV (int [] realPairedSites, int [] predictedPairedSites)
	{	
		/* The PPV is the percentage of base
		pairs in the predicted structure that are in the experimental structure.*/
		
		/*
		double correctlyPredicted = 0; // true positives
		double incorrectlyPredicted = 0; // false positives
		for(int i = 0 ; i < realPairedSites.length ; i++)
		{
			if(realPairedSites[i] >= i + 1 && realPairedSites[i] != 0 && realPairedSites[i] == predictedPairedSites[i])
			{
				correctlyPredicted++;
			}
			else
			if(predictedPairedSites[i] >= i + 1 && predictedPairedSites[i] != 0 && realPairedSites[i] != predictedPairedSites[i])
			{
				incorrectlyPredicted++;
			}
		}
		
		//return correctlyPredicted / (correctlyPredicted + incorrectlyPredicted);

		double [] ret = getValues(realPairedSites, predictedPairedSites);
		return ret[0] / (ret[0]+ret[2]);*/
		double count = 0;
		double total = 0;
		for(int i = 0 ; i < realPairedSites.length ; i++)
		{
			if(predictedPairedSites[i] != 0)
			{
				total++;
				if(predictedPairedSites[i] == realPairedSites[i])
				{
					count++;
				}
			}
		}
		
		return count / total;
	}
	
	/**
	 * Given an array of paired sites corresponding to the real structure 
	 * and an array corresponding to the predicted structure returns the F-score.
	 * @param realPairedSites
	 * @param predictedPairedSites
	 * @return
	 */
	public static double calculateFScore (int [] realPairedSites, int [] predictedPairedSites)
	{
		double sensitivity = calculateSensitivity(realPairedSites, predictedPairedSites);
		double ppv = calculatePPV(realPairedSites, predictedPairedSites);
		
		double fscore =  (2 * sensitivity * ppv)/(sensitivity+ppv);
		return Double.isNaN(fscore) ? 0 : fscore;
	}
}
