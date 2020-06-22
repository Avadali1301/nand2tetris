import java.io.*;
import java.util.*;
import java.lang.Math;
public class Assembler {
  private static Map<String, String> destMap = new HashMap<String, String>();
  private static Map<String, String> compMap = new HashMap<String, String>();
  private static Map<String, String> jumpMap = new HashMap<String, String>();
  private static ArrayList<String> assemblyLines = new ArrayList<String>();
  private static Map<String, String> ram = new HashMap<String, String>();
  private static boolean newLine;
  private static int ramInc;
  public static String aInstruction(String currentLine) throws IOException{
  // Determines if I create newLine
    newLine = true;
  // Checks if line is just whitespace
    if(currentLine.length() == 0) {
      newLine = false;
      return "";
    }
  // checks if line is a-intruction
    else if(currentLine.substring(0, 1).equals("@")) {
      // check if the address is an int, if so, I just return its value in binary
        String ramId = currentLine.substring(1, currentLine.length());
        if(isInteger(ramId)) {
          return toBinary(Integer.parseInt(ramId));
        }
        else {
        // if the address is a symbol and is not in my map, I add it to the map
            if(!(ram.containsKey(currentLine.substring(1, currentLine.length())))) {
                ram.put(currentLine.substring(1, currentLine.length()), toBinary(ram.size() - 7 - ramInc));
            }
          // debugging stuff
        //    System.out.println(currentLine.substring(1, currentLine.length()));
        //    System.out.println(ram.size() - 1);
          //  System.out.println(ram.get(currentLine.substring(1, currentLine.length())));
            return ram.get(currentLine.substring(1, currentLine.length()));
        }
      }
  // checks if line is a comment
    else if(currentLine.substring(0, 2).equals("//")) {
      newLine = false;
      return "";
    }
  // for the scope of this project, only other option is for the line to be c-instruction
    else {
  // If there is no =, dest is null
  // If there is no ;, jump is null
          String destBits;
          String compBits;
          String jumpBits;
          int equalIndex = currentLine.indexOf("=");
          int semiColonIndex = currentLine.indexOf(";");
          if(equalIndex == -1 && semiColonIndex != -1){
            destBits = "null";
            compBits = currentLine.substring(0, semiColonIndex);
            jumpBits = currentLine.substring(semiColonIndex + 1, currentLine.length());
          }
          else if (equalIndex == -1 && semiColonIndex == -1){
          /*  destBits = "null";
            compBits = currentLine;
            jumpBits = "null";*/
            newLine = false;
            throw new IOException("For the scope of this project, one of your lines is incorrect becaues it contains neither a semicolon nor an equals sign");
          }
          else if(equalIndex != -1 && semiColonIndex == -1){
            destBits = currentLine.substring(0, equalIndex);
            jumpBits = "null";
            compBits = currentLine.substring(equalIndex + 1, currentLine.length());
          }
          else {
            destBits = currentLine.substring(0, equalIndex);
            compBits = currentLine.substring(equalIndex + 1, semiColonIndex);
            jumpBits = currentLine.substring(semiColonIndex + 1, currentLine.length());
          }
          return cInstruction(destBits, compBits, jumpBits);
        }
    }

    public static String cInstruction(String destBits, String compBits, String jumpBits) {
    // the order of output is comp - dest - jump
      String destOut = destMap.get(destBits);
      String compOut =  compMap.get(compBits);
      String jumpOut = jumpMap.get(jumpBits);
      return compOut + destOut + jumpOut;
    }
    public static String toBinary(int n) {
      // returns string because int will trim leading zeros
        int updatedN = n;
        String output = "";
        if(n > Math.pow(2, 15) - 1) {
          throw new ArithmeticException("You tried to use a number that is too large for two's complement");
        }
        else if(n < -Math.pow(2, 15)) {
          throw new ArithmeticException("You tried to use a number that is too small for two's complement");
        }
    // I start at the most significant bit, and I just implement a greedy algorithm
        for(int i = 15; i >= 0; i--) {
            int currentBit = (int)Math.pow(2, i);
            if(updatedN >= currentBit) {
                output += "1";
                updatedN -= currentBit;
            }
            else {
                output += "0";
            }
        }
        if(n < 0) {
            output = toBinary(65536 + n);
        }
        return output;
    }
    public static boolean isInteger(String n) {
    // checks if an error is thrown when I parse an int
        boolean out = true;
        try {
            Integer.parseInt(n);
        }
        catch(NumberFormatException e) {
            out = false;
        }
        catch(NullPointerException e) {
            out = false;
        }
        return out;
    }
    public static void main(String[] args) {
  // add all of the comp, dest, and jump values to their respective maps
    compMap.put("0", "1110101010");
    compMap.put("1", "1110111111");
    compMap.put("-1", "1110111010");
    compMap.put("D", "1110001100");
    compMap.put("A", "1110110000");
    compMap.put("!D", "1110001101");
    compMap.put("!A", "1110110001");
    compMap.put("-D", "1110001111");
    compMap.put("-A", "1110110011");
    compMap.put("D+1", "1110011111");
    compMap.put("A+1", "1110110111");
    compMap.put("D-1", "1110001110");
    compMap.put("A-1", "1110110010");
    compMap.put("D+A", "1110000010");
    compMap.put("D-A", "1110010011");
    compMap.put("A-D", "1110000111");
    compMap.put("D&A", "1110000000");
    compMap.put("D|A", "1110010101");
// End of a = 0 comp instructions
    compMap.put("M", "1111110000");
    compMap.put("!M", "1111110001");
    compMap.put("-M", "1111110011");
    compMap.put("M+1", "1111110111");
    compMap.put("M-1", "1111110010");
    compMap.put("D+M", "1111000010");
    compMap.put("D-M", "1111010011");
    compMap.put("M-D", "1111000111");
    compMap.put("D&M", "1111000000");
    compMap.put("D|M", "1111010101");
// End of a = 1 comp instructions
    destMap.put("null", "000");
    destMap.put("M", "001");
    destMap.put("D", "010");
    destMap.put("MD", "011");
    destMap.put("A", "100");
    destMap.put("AM", "101");
    destMap.put("AD", "110");
    destMap.put("AMD", "111");
// End of dest instructions
    jumpMap.put("null", "000");
    jumpMap.put("JGT", "001");
    jumpMap.put("JEQ", "010");
    jumpMap.put("JGE", "011");
    jumpMap.put("JLT", "100");
    jumpMap.put("JNE", "101");
    jumpMap.put("JLE", "110");
    jumpMap.put("JMP", "111");
// End of jmp instructions
// I need to add the predefined symbols to my map
    for(int i = 0; i < 16; i++) {
        String defaultRamKey = "R" + Integer.toString(i);
        ram.put(defaultRamKey, toBinary(i));
    }
    ram.put("SP", toBinary(0));
    ram.put("LCL", toBinary(1));
    ram.put("ARG", toBinary(2));
    ram.put("THIS", toBinary(3));
    ram.put("THAT", toBinary(4));
    ram.put("SCREEN", toBinary(16384));
    ram.put("KBD", toBinary(24576));
      try {
      // this just reads from the file
        File assemblyFile = new File(args[0]);
        Scanner scan = new Scanner(assemblyFile);
        String newFile = args[0].substring(0, args[0].length() - 4) + ".hack";
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        String currentLine = null;
        boolean inBlock = false;
        while(scan.hasNextLine()) {
        // remove all spaces from lines
          currentLine = scan.nextLine().replaceAll(" ", "");
        // check that line is not empty and is not a comment
        // account for block comments
        if(currentLine.length() != 0){
          if(currentLine.contains("*/")) {
            inBlock = false;
          }
          else if(currentLine.substring(0, 2).equals("/*") || inBlock) {
            inBlock = true;
          }
          else if(!currentLine.substring(0, 2).equals("//")){
          // make sure that line is a label
            if(currentLine.substring(0, 1).equals("(") && currentLine.substring(currentLine.length() - 1, currentLine.length()).equals(")")) {
            // add lable to symbols list and increment ramInc
            // make sure not to add symbol line to assemblyLines because it does not get translated
              ram.put(currentLine.substring(1, currentLine.length() - 1), toBinary(assemblyLines.size()));
              ramInc += 1;
            }
            else {
            // if line is not a symbol, just add it to the assembly lines
              assemblyLines.add(currentLine);
            }
          }
          }
      }
      for (int i = 0; i < assemblyLines.size(); i++) {
    // write my lines to new hack file
        writer.write(aInstruction(assemblyLines.get(i)));
        if(newLine)
          writer.newLine();
      }
      writer.close();
    }
      catch(IOException e) {
        System.out.println("Error");
      }

    }
}
