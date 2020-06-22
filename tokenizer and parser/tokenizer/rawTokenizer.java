import java.util.*;
import java.io.*;
import java.lang.*;
public class rawTokenizer {
	private String fileContents = "";
// this is used to keep track of what token the user is currently trying to get
	private int currentToken;
	private ArrayList<String> keywords = new ArrayList<String>();
	private ArrayList<String> symbols = new ArrayList<String>();
	private ArrayList<String> tokens = new ArrayList<String>();
	public rawTokenizer() {
	// initializing my keywords
		keywords.add("class");
		keywords.add("constructor");
		keywords.add("function");
		keywords.add("method");
		keywords.add("field");
		keywords.add("static");
		keywords.add("var");
		keywords.add("int");
		keywords.add("char");
		keywords.add("boolean");
		keywords.add("void");
		keywords.add("true");
		keywords.add("false");
		keywords.add("null");
		keywords.add("this");
		keywords.add("let");
		keywords.add("do");
		keywords.add("if");
		keywords.add("else");
		keywords.add("while");
		keywords.add("return");
	// initializing my symbols
		symbols.add("{");
		symbols.add("}");
		symbols.add("(");
		symbols.add(")");
		symbols.add("[");
		symbols.add("]");
		symbols.add(".");
		symbols.add(",");
		symbols.add(";");
		symbols.add("+");
		symbols.add("-");
		symbols.add("*");
		symbols.add("/");
		symbols.add("&");
		symbols.add("|");
		symbols.add("<");
		symbols.add(">");
		symbols.add("=");
		symbols.add("~");
	}
	public boolean isInteger(String n) {
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
	public void tokenize(String ln) {
		int index = 0;
		String line = ln;
	// i break up the code on the delimeters
		boolean isString = false;
		boolean isChar = false;
		while(index < ln.length()){
			line = ln.substring(index);
			// check if the token is in a string
			// we know that if we encounter a double-quote, either it's the start or the end of a string, so we just reverse the boolean value of the variable
			if(line.substring(0, 1).equals("\"")) {
				isString = !isString;
				index += 1;
			}
			// check if the token is in a char
			// we know that if we encounter a single-quote, either it's the start or the end of a char, so we just reverse the boolean value of the variable
			else if(line.substring(0, 1).equals('\'')) {
				isChar = !isChar;
				index += 1;
			}
			// skip it if it's a space
			else if(line.substring(0, 1).equals(" ")) {
				index += 1;
			}
			else {
			// I essentially go through all the possible scenarios
			// I can brute-force it because the possibilites are few (not to diminish the Jack Language).
				int mindex = line.length();
				for(String s : symbols) {
					if(line.indexOf(s) >= 0 && line.indexOf(s) < mindex) {
						mindex = line.indexOf(s);
					}
				}
				if(line.indexOf(" ") >= 0 && line.indexOf(" ") < mindex) {
					mindex = line.indexOf(" ");
				}
				if(line.indexOf("\"") >= 0 && line.indexOf("\"") < mindex) {
					mindex = line.indexOf("\"");
				}
				if(keywords.contains(line.substring(0, mindex))) {
						tokens.add("<keyword>" + line.substring(0, mindex) + "</keyword>");
				}
				else if(isString) {
					tokens.add("<term>\n<stringConstant>" + line.substring(0, mindex) + "</stringConstant>\n</term>");
				}
				else if(isChar) {
					tokens.add("<term>\n" + line.substring(0, mindex) + "\n</term>");
				}
				else if(isInteger(line.substring(0, mindex))) {
					tokens.add("<term>\n<integerConstant>" + line.substring(0, mindex) + "</integerConstant>\n</term>");
				}
				else if(symbols.contains(line.substring(0, 1))) {
					tokens.add("<symbol>" + line.substring(0, 1) + "</symbol>");
					mindex++;
				}
				else {
						tokens.add("<identifier>" + line.substring(0, mindex) + "</identifier>");
				}
				index += mindex;
			}
		}
	}
	public void setFileContents(String str) {
		this.fileContents = str;
	}
	// accessors
	public int getCurrentToken() {
		return this.currentToken;
	}
	public String getFileContents() {
		return this.fileContents;
	}
	public ArrayList<String> getTokens() {
		return this.tokens;
	}
	// just gets the next token in the list
	public String nextToken() {
		currentToken++;
		return this.tokens.get(currentToken - 1);
	}
	public static void main(String[] args) {
		String fileName = "";
		rawTokenizer jt = new rawTokenizer();
		ArrayList<String> tokens = jt.getTokens();
		try{
        // This chunk of code reads in the lines of the file and begins the process of translation. 
            fileName = args[0].substring(0, args[0].length() - 5);
            ArrayList<File> jackFiles = new ArrayList<File>();
            File parsedFile = null;
            if(fileName.substring(0, 1).equals("/")) {
                File directory = new File(args[0]);
        // debugging code
             //   System.out.print(directory.getName());
             //   System.out.print(directory);
                File[] files = directory.listFiles();
                parsedFile = new File(directory.getName());
                for(File f : files) {
                    if(f.isFile() && f.getName().substring(f.getName().length() - 5).equals(".jack")) {
                        jackFiles.add(f);
                    }
                }
            }
            else {
                jackFiles.add(new File(args[0]));
                parsedFile = new File(fileName);
            }
            String currentLine;
            int j = 0;
            for(File f : jackFiles) {
            // going through each file in the directory, scanning it, tokenizing/parsing it, and writing the parsed tokens to the new xml file
            	String newFile = f.getName().substring(0, f.getName().length() - 5) + ".xml";
            	// System.out.println(newFile);
            	BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                Scanner scan = new Scanner(f);
                while(scan.hasNextLine()) {
                	currentLine = scan.nextLine();
               	 if(currentLine.length() != 0 && !currentLine.contains("//")) {
               	 // I'm just stripping away the excess whitespace and turning the code into a long string
                    jt.setFileContents(jt.getFileContents() + currentLine.trim());

               	 }
               	}
               	if(jt.getFileContents().length() > 0) {
               	 	// System.out.println(jt.getFileContents());
               	 	jt.tokenize(jt.getFileContents());
               	 }
               	 for(int i = j; i < tokens.size(); i++) {
               	 	writer.write(tokens.get(i));
               	 	writer.newLine();
               	 }
               	 j = tokens.size();
               	jt.setFileContents("");
               	writer.close();
            }
        }
      catch(IOException e) {
        System.out.println("Error");
    }
    // System.out.print(jt.nextToken());
	}
}