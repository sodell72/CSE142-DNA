// Sean O'Dell, CSE 142, Spring 2015, Section BG
// Programming Assignment #7, 05/26/15
//
// This program's behavior is to report information about DNA nucleotide sequences that may encode
// proteins and to create a text file with this information.

import java.util.*;
import java.io.*;

public class DNA {
public static final int MIN_CODON = 5;
public static final double VALID_CG_PERCENT = 30.0;
public static final int UNIQUE_NUCLEOTIDES = 4;
public static final int NUCLEOTIDES_PER_CODON = 3;
   public static void main(String[] args) throws FileNotFoundException {
      Scanner console = new Scanner(System.in);
      intro();
      System.out.print("Input file name? ");
      String inputFileName = console.nextLine();
      System.out.print("Output file name? ");
      String outputFileName = console.nextLine();
      Scanner fileScan = new Scanner(new File(inputFileName));
      PrintStream output = new PrintStream(new File(outputFileName));
      double[] masses = {135.128, 111.103, 151.128, 125.107};        // Defines particle masses in
      double junkMass = 100.000;                                     // [g/mol]
      determineOutput(masses, junkMass, output, fileScan);
   }
   
   // Prints introduction
   public static void intro() {
      System.out.println("This program reports information about DNA");
      System.out.println("nucleotide sequences that may encode proteins.");
   }
   
   // Determines and creates output file given the nucleotide and junk masses, printstream and file
   // scanner
   public static void determineOutput(double[] masses, double junkMass, 
                                        PrintStream output, Scanner fileScan) {
      while (fileScan.hasNextLine()) {
         String regionName = fileScan.nextLine();
         String line = fileScan.nextLine().toUpperCase();                         
         int[] nucCounts = countNucleotides(line);
         int junkCount = determineJunkCount(line);
         double totalMass = calculateTotalMass(nucCounts, masses, junkCount, junkMass);
         double[] massPercentages = calculateTotalMassPercentages(nucCounts, masses, totalMass);
         String[] codonList = createCodonsList(line);
         String proteinAnswer = isProtein(codonList, massPercentages);
         writeOutput(regionName, line, nucCounts, massPercentages, totalMass, codonList, 
                     proteinAnswer, output);
      }
   }
   
   // Prints output file given the region name, nucleotides line, nucleotide counts, mass 
   // percentages, total mass, list of codons, answer to if it is protein, and a print stream
   public static void writeOutput(String regionName, String line, int[] nucCounts, 
                                  double[] massPercentages, double totalMass, 
                                  String[] codonList, String proteinAnswer, 
                                  PrintStream output) {
      output.println("Region Name: " + regionName);
      output.println("Nucleotides: " + line);               
      output.println("Nuc. Counts: " + Arrays.toString(nucCounts));
      output.print("Total Mass%: " + Arrays.toString(massPercentages) + " of ");
      output.printf("%.1f\n", totalMass); 
      output.println("Codons List: " + Arrays.toString(codonList));
      output.println("Is Protein?: " + proteinAnswer);
      output.println();
   }

   // Counts number of each nucleotide given the nucleotides of a region
   public static int[] countNucleotides(String line) {
      int[] nucCounts = new int[UNIQUE_NUCLEOTIDES];
      for (int i = 0; i < line.length(); i++) {
         char nucleotide = line.charAt(i);
         if (nucleotide != '-') {
            int nucleotideIndex = getNucleotideIndex(nucleotide);
            nucCounts[nucleotideIndex]++;
         }
      }
      return nucCounts;
   }

   // Converts given nucleotide character to an index
   public static int getNucleotideIndex(char nucleotide) {
      if (nucleotide == 'A') {
         return 0;
      } else if (nucleotide == 'C') {
         return 1;
      } else if (nucleotide == 'G') {
         return 2;
      } else if (nucleotide == 'T') {
         return 3;
      }
      return -1;
   }
   
   // Determines the number of junk items in each region given the nucleotides of a region
   public static int determineJunkCount(String line) {
      int junkCount = 0;
      for (int i = 0; i < line.length(); i++) {
         char currentCharacter = line.charAt(i);
         if (currentCharacter == '-') {
            junkCount++;
         }
      }
      return junkCount;
   }
   
   // calculates the total mass of the the nucleotides given the number of each nucleotide, the
   // mass of each nucleotide, the number of junk items in the region, and the mass of a single
   // junk item
   public static double calculateTotalMass(int[] nucCounts, double[] masses, int junkCount,
                                           double junkMass) {
      double totalMass = 0;
      for (int i = 0; i < nucCounts.length; i++) {
         totalMass += masses[i] * nucCounts[i];
      }
      totalMass += (junkMass * junkCount);
      return totalMass;
   }
   
   // Calculates the total mass percentage and total mass, given the number of each
   // nucleotide in the region, the masses of each nucleotide type, and the total mass
   public static double[] calculateTotalMassPercentages(int[] nucCounts, double[] masses,
                                                        double totalMass) {
      double[] massPercentages = new double[masses.length];
      for (int i = 0; i < nucCounts.length; i++) {
         massPercentages[i] = Math.round((nucCounts[i] * masses[i] / totalMass * 100) * 10.0) /
                              10.0;
      }
      return massPercentages;
   }
   
   // Creates list of codons given an input of nucleotides for a region
   public static String[] createCodonsList(String line) {
      line = line.replace("-", "");
      String[] codonList = new String[line.length() / NUCLEOTIDES_PER_CODON];
      int count = 0;
      for (int i = 0; i < line.length(); i += NUCLEOTIDES_PER_CODON) {
         codonList[count] = line.substring(i, i + NUCLEOTIDES_PER_CODON);
         count++;
      }
      return codonList;
   }
   
   // Determines wheter a region is a protein given a list of codons, and the mass percentages of
   // nucleotides
   public static String isProtein(String[] codonList, double[] massPercentages) {
      String proteinAnswer = "NO";
      if (codonList[0].equals("ATG") && (codonList[codonList.length - 1].equals("TAA") ||
          codonList[codonList.length - 1].equals("TAG") ||
          codonList[codonList.length - 1].equals("TGA")) 
          && codonList.length >= MIN_CODON &&
          massPercentages[1] + massPercentages[2] >= VALID_CG_PERCENT) {
         proteinAnswer = "YES";
      }
      return proteinAnswer;
   }  
}