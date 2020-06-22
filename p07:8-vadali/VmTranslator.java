import java.io.*;
import java.util.*;
import java.lang.Math;
/*
    Date Started: 17 April 2020
    Programmer: Avi Vadali
    Purpose: Translate vm language commands into assembly code
*/
public class VmTranslator {
    // this variable is so that I don't jump back to previous returnAddresses
    private static int callCount = 0;
    private static String funcName = "";
    private static Map<String, String> stackArith = new HashMap<String, String>();
    // this variable is so that I don't jump back to previous labels when I am checking for equality
    private static int jumpNum = 0;
    // I don't need to check for comments because this method automatically does it by not including an "else" when it checks for pushes and pulls
    public static void incrementCall() {
        callCount += 1;
    }
    public static String stackOps(String currentLine, String fileName) {
        String output = "";
        String begin = currentLine.substring(0, 2);
        String num = "";
        // takes care of all pushes
        if(begin.equals("pu")) {
            String nextPart = currentLine.substring(5, 8);
            if (nextPart.equals("sta")) {
                num = currentLine.substring(12);
                output = "@" + fileName + "." + num +  "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";

            }
            else if(nextPart.equals("con")) {
                num = currentLine.substring(14);
                output = "@"  + num +  "\nD=A\n@SP\nA=M\nM=D \n@SP\nM=M+1\n";

            }
            else if(nextPart.equals("loc")) {
                num = currentLine.substring(11);
                output = "@LCL\nD=M\n@" + num + "\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
            }
            else if(nextPart.equals("arg")) {
                num = currentLine.substring(14);
                output = "@ARG\nD=M\n@" + num + "\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
            }
            else if(nextPart.equals("tem")) {
                num = currentLine.substring(11);
                output = "@5\nD=A\n@" + num +  "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
                
            }
            else if(nextPart.equals("thi")) {
                num = currentLine.substring(10);
                output = "@THIS\nD=M\n@" + num +  "\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
                
            }
            else if(nextPart.equals("tha")) {
                num = currentLine.substring(10);
                output = "@THAT\nD=M\n@" + num +  "\nA=A+D\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";

            }
            else if(nextPart.equals("poi")) {
                num = currentLine.substring(13);
                if(num.equals("0")) {
                    output = "@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
                }
                else if(num.equals("1")) {
                    output = "@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n";
                }
                
                
            }
        }
        // takes care of all pops
        else if(begin.equals("po")){
            String nextPart = currentLine.substring(4, 7);
            if (nextPart.equals("sta")) {
                num = currentLine.substring(11);
                output = "@" + fileName + "." + num +  "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
            }
            else if(nextPart.equals("loc")) {
                num = currentLine.substring(10);
                output = "@LCL\nD=M\n@" + num +  "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";

            }
            else if(nextPart.equals("arg")) {
                num = currentLine.substring(13);
                output = "@ARG\nD=M\n@" + num +  "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
                
            }
            else if(nextPart.equals("tem")) {
                num = currentLine.substring(9);
                output = "@R5\nD=A\n@" + num + "\nD=D+A\n@SEG\nM=D\n@SP\nAM=M-1\nD=M\n@SEG\nA=M\nM=D\n";
            }
            else if(nextPart.equals("thi")) {
                num = currentLine.substring(9);
                output = "@THIS\nD=M\n@" + num + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
                
            }
            else if(nextPart.equals("tha")) {
                num = currentLine.substring(9);
                output = "@THAT\nD=M\n@" + num + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
            }
            else if(nextPart.equals("poi")) {
                num = currentLine.substring(12);
                if(num.equals("0")) {
                    output = "@THIS\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
                }
                else if(num.equals("1")) {
                    output = "@THAT\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
                }
                
            }

        }
 // program flow
        else if(begin.equals("la")) {
            output = "(" +  funcName + "$" + currentLine.substring(6) + ")\n";
        }
        else if(begin.equals("go")) {
            output = "@" + funcName + "$" + currentLine.substring(5) + "\n0;JMP\n";
        }
        else if(begin.equals("if")) {
            output = "@SP\nAM=M-1\nD=M\n@" + funcName + "$" + currentLine.substring(8) + "\nD;JNE\n";
        }
// subroutine
        else if(begin.equals("fu")) {
            // create a label for the function, push as many zeros as needed, and update stack pointer
            int space1 = currentLine.indexOf(" ");
            int space2 = currentLine.substring(space1 + 1).indexOf(" ") + space1 + 1;
            funcName = currentLine.substring(space1 + 1, space2);
            output += "(" + funcName + ")\n@SP\nA=M\n";
            int numParam = Integer.parseInt(currentLine.substring(space2 + 1));
            for(int i = 0; i < numParam; i++) {
                output += "M=0\nA=A+1\n";
            }
            output += "D=A\n@SP\nM=D\n";
        }
        else if(begin.equals("ca")) {
            // sve my returnAddress and my locals, and then create a label to come back to
            int space1 = currentLine.indexOf(" ");
            int space2 = currentLine.substring(space1 + 1).indexOf(" ") + space1 + 1;
            String callName = currentLine.substring(space1 + 1, space2);
            int numParam = Integer.parseInt(currentLine.substring(space2 + 1));
            output = "@RETURN_ADDRESS" + callCount + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n@ARG\nD=M\n@SP\n" +
                    "A=M\nM=D\n@SP\nM=M+1\n@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n\n@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n" + 
                        "@SP\nD=M\n@5\nD=D-A\n@" + numParam + "\nD=D-A\n@ARG\nM=D\n@SP\nD=M\n@LCL\nM=D\n@" + callName + "\n0;JMP\n(RETURN_ADDRESS" + callCount + ")\n";
            incrementCall();
        }
        else if(begin.equals("re")) {
            // restore all of my locals, update my arg, make sure that the return value is on top of the stack, and jump back to returnAddress
            output = "@LCL\nD=M\n@FRAME\nM=D\n@5\nA=D-A\nD=M\n@RET\nM=D\n@ARG\nD=M\n@0\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n@ARG\nD=M\n@SP\nM=D+1\n" +
              "@FRAME\nD=M-1\nAM=D\nD=M\n@THAT\nM=D\n" + 
              "@FRAME\nD=M-1\nAM=D\nD=M\n@THIS\nM=D\n" + 
              "@FRAME\nD=M-1\nAM=D\nD=M\n@ARG\nM=D\n" + 
              "@FRAME\nD=M-1\nAM=D\nD=M\n@LCL\nM=D\n" + 
              "@RET\nA=M\n0;JMP\n";
        }
        return output;
    }

    public static void main(String[] args) {
        String fileName = "";
    // add all of my arithmetic and logical operations to my map
    // just concatenate the lines together and then use \n to do a line break

    // binary arithmetic/logical operations
        stackArith.put("add", "@SP\nAM=M-1\nD=M\nA=A-1\nM=M+D\n");
        stackArith.put("sub", "@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n");
        stackArith.put("and", "@SP\nAM=M-1\nD=M\nA=A-1\nM=M&D\n");
        stackArith.put("or", "@SP\nAM=M-1\nD=M\nA=A-1\nM=M|D\n");

    // unary arithmetic/logical operations
        stackArith.put("neg", "@SP\nA=M-1\nM=-M\n");
        stackArith.put("not", "@SP\nA=M-1\nM=!M\n");

    // boolean equivalence commands
        stackArith.put("eq", "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@FALSE" + jumpNum + "\n" + "D;" + "JNE" + "\n" +
                "@SP\n" + "A=M-1\n" + "M=-1\n" + "@CONT" + jumpNum + "\n" + "0;JMP\n" + "(FALSE" + jumpNum + ")\n" + "@SP\n" +
                "A=M-1\n" + "M=0\n" + "(CONT" + jumpNum + ")\n");
        stackArith.put("lt", "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@FALSE" + jumpNum + "\n" + "D;" + "JGE" + "\n" +
                "@SP\n" + "A=M-1\n" + "M=-1\n" + "@CONT" + jumpNum + "\n" + "0;JMP\n" + "(FALSE" + jumpNum + ")\n" + "@SP\n" +
                "A=M-1\n" + "M=0\n" + "(CONT" + jumpNum + ")\n");
        stackArith.put("gt", "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@FALSE" + jumpNum + "\n" + "D;" + "JLE" + "\n" +
                "@SP\n" + "A=M-1\n" + "M=-1\n" + "@CONT" + jumpNum + "\n" + "0;JMP\n" + "(FALSE" + jumpNum + ")\n" + "@SP\n" +
                "A=M-1\n" + "M=0\n" + "(CONT" + jumpNum + ")\n");
        try {
        // This chunk of code reads in the lines of the file and begins the process of translation. 
            fileName = args[0].substring(0, args[0].length() - 3);
            ArrayList<File> vmFiles = new ArrayList<File>();
            File assemblyFile = null;
            if(fileName.substring(0, 1).equals("/")) {
                File directory = new File(args[0]);
        // debugging code
             //   System.out.print(directory.getName());
             //   System.out.print(directory);
                File[] files = directory.listFiles();
                assemblyFile = new File(directory.getName());
                for(File f : files) {
                    if(f.isFile() && f.getName().substring(f.getName().length() - 3).equals(".vm")) {
                        vmFiles.add(f);
                    }
                }
            }
            else {
                vmFiles.add(new File(args[0]));
                assemblyFile = new File(fileName);
            }
            String newFile = assemblyFile.getName() + ".asm";
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            writer.write("@256\nD=A\n@SP\nM=D\n");
            writer.write(stackOps("call Sys.init 0", vmFiles.get(0).getName().substring(0, vmFiles.get(0).getName().length() - 3)));
            String currentLine;
            for(File f : vmFiles) {
                Scanner scan = new Scanner(f);
                while(scan.hasNextLine()) {
                currentLine = scan.nextLine();
                if(currentLine.length() == 0) {
                    writer.write("\n");
                }
                else if(stackArith.containsKey(currentLine)) {
                    writer.write(stackArith.get(currentLine));
    // this checks if I used an equality statement, because if I did, I need to increment my counter so my labels don't get unintentionally called.
                    if(currentLine.equals("eq") || currentLine.equals("lt") || currentLine.equals("gt")) {
                        jumpNum++;
                    }
                }
                else {
                    writer.write(stackOps(currentLine, f.getName().substring(0, f.getName().length() - 3)));
                }
        }
            }
        writer.close();
      }
      catch(IOException e) {
        System.out.println("Error");
    }
}
}

