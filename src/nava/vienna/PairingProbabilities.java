/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PairingProbabilities 
{
    public static double [] single = {0.337, 0.207, 0.202, 0.254};
    public static double [][] pairing = {{0.007, 0.004, 0.011, 0.127},
                                        {0.006, 0.005, 0.283, 0.005},
                                        {0.023, 0.275, 0.006, 0.045},
                                        {0.132, 0.004, 0.060, 0.001}};
    public static char [] dna = {'A', 'C', 'G', 'T', 'U'};
    
    public static double get(char c1)
    {
        int i1 = 0;
        for(int i = 0 ; i < dna.length ; i++)
        {
            if(dna[i] == c1)
            {
                i1 = i;
                break;
            }
        }
        
        //System.out.println(c1+"\t"+single[i1]);
        return single[Math.min(i1, 3)];
    }
    
    public static double get(char c1, char c2)
    {        
        int i1 = 0;
        int i2 = 0;
        for(int i = 0 ; i < dna.length ; i++)
        {
            if(dna[i] == c1)
            {
                i1 = i;
            }
            if(dna[i] == c2)
            {
                i2 = i;
            }
        }
        return pairing[Math.min(i1, 3)][Math.min(i2, 3)];
    }
}
