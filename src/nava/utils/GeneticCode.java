package nava.utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class GeneticCode {

    public static final String aminoAcids = "FLIMVSPTAY*HQNKDECWRG";
    /*
     * 0 == Phe F 1 == Leu L 2 == Ile I 3 == Met M 4 == Val V 5 == Ser S 6 ==
     * Pro P 7 == Thr T 8 == Ala A 9 == Tyr Y 10 == Stop * 11 == His H 12 == Gln
     * Q 13 == Asn N 14 == Lys K 15 == Asp D 16 == Glu E 17 == Cys C 18 == Trp W
     * 19 == Arg R 20 == Gly G
     */
    public static final int[] universalCode = {14,/*
         * AAA
         */ 13,/*
         * AAC
         */ 14,/*
         * AAG
         */ 13,/*
         * AAT
         */
        7, /*
         * ACA
         */ 7, /*
         * ACC
         */ 7, /*
         * ACG
         */ 7, /*
         * ACT
         */
        19, /*
         * AGA
         */ 5, /*
         * AGC
         */ 19, /*
         * AGG
         */ 5, /*
         * AGT
         */
        2, /*
         * ATA
         */ 2, /*
         * ATC
         */ 3, /*
         * ATG
         */ 2, /*
         * ATT
         */
        12,/*
         * CAA
         */ 11,/*
         * CAC
         */ 12,/*
         * CAG
         */ 11,/*
         * CAT
         */
        6, /*
         * CCA
         */ 6, /*
         * CCC
         */ 6, /*
         * CCG
         */ 6, /*
         * CCT
         */
        19,/*
         * CGA
         */ 19,/*
         * CGC
         */ 19,/*
         * CGG
         */ 19,/*
         * CGT
         */
        1, /*
         * CTA
         */ 1, /*
         * CTG
         */ 1, /*
         * CTC
         */ 1, /*
         * CTT
         */
        16,/*
         * GAA
         */ 15,/*
         * GAC
         */ 16,/*
         * GAG
         */ 15,/*
         * GAT
         */
        8, /*
         * GCA
         */ 8, /*
         * GCC
         */ 8, /*
         * GCG
         */ 8, /*
         * GCT
         */
        20,/*
         * GGA
         */ 20,/*
         * GGC
         */ 20,/*
         * GGG
         */ 20,/*
         * GGT
         */
        4, /*
         * GTA
         */ 4, /*
         * GTC
         */ 4, /*
         * GTG
         */ 4, /*
         * GTT
         */
        10,/*
         * TAA
         */ 9, /*
         * TAC
         */ 10,/*
         * TAG
         */ 9, /*
         * TAT
         */
        5, /*
         * TCA
         */ 5, /*
         * TCC
         */ 5, /*
         * TCG
         */ 5, /*
         * TCT
         */
        10,/*
         * TGA
         */ 17,/*
         * TGC
         */ 18,/*
         * TGG
         */ 17,/*
         * TGT
         */
        1, /*
         * TTA
         */ 0, /*
         * TTC
         */ 1, /*
         * TTG
         */ 0 /*
     * TTT
     */};

    public static char getAAfromCodon(String codon) {
        String newCodon = codon.toUpperCase();
        newCodon = newCodon.replaceAll("A", "0");
        newCodon = newCodon.replaceAll("C", "1");
        newCodon = newCodon.replaceAll("G", "2");
        newCodon = newCodon.replaceAll("T", "3");

        try {
            int c = Integer.parseInt(newCodon, 4);
            if (c >= 0 && c < universalCode.length) {
                int aa = universalCode[c];
                return aminoAcids.charAt(aa);
            }
        } catch (NumberFormatException ex) {
        }

        return 'X';
    }

    public static String translateNucleotideSequence(String nucleotideSequence) {
        String translation = "";
        for (int i = 0; i < nucleotideSequence.length(); i += 3) {
            translation += getAAfromCodon(nucleotideSequence.substring(i, Math.min(nucleotideSequence.length(), i + 3)));
        }
        return translation;
    }
}
