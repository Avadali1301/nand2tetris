import java.util.*;
import java.io.*;
import java.lang.*;
public class JackTokenizer {
// these variables just keep track of things for me
	private String fileContents = "";
	private String fileName;
	private int currentLine;
// I need to know variables, classes, and subroutines so that I can  tag them properly
	private ArrayList<String> keywords = new ArrayList<String>();
	private ArrayList<String> symbols = new ArrayList<String>();
	private ArrayList<String> tokens = new ArrayList<String>();
	private ArrayList<String> classNames = new ArrayList<String>();
	private ArrayList<String> variableList = new ArrayList<String>();
	private ArrayList<String> ops = new ArrayList<String>();
	private ArrayList<String> unaryOps = new ArrayList<String>();
	private ArrayList<String> subroutineNames = new ArrayList<String>();
	public JackTokenizer() {
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
	// adding the ops
		ops.add("+");
		ops.add("-");
		ops.add("*");
		ops.add("/");
		ops.add("&");
		ops.add("|");
		ops.add(">");
		ops.add("<");
		ops.add("=");
	// adding unary ops
		unaryOps.add("-");
		unaryOps.add("~");
	}
	public void whileGrammar() throws whileException{
	// a while loop is allowed to have no statements, so I don't need to check for that. 
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<whileStatement>")) {
				if(tokens.get(i + 3).contains("</expression>")) {
					throw new whileException("There was no expression in your while loop");
				}
			}
		}
	}
	public int whileStatement(int index) throws Exception{
	// I use the basic structure of a while loop to break it up into while, expressions, statements.
		String ln = fileContents.substring(index);
		int openB = 1;
		int closeB = 0;
		int openP = 1;
		int closeP = 0;
		int endIndex = 0;
		int dex = ln.indexOf("{");
		int paren1 = ln.indexOf("(");
		int i = dex + 1;
		int j = paren1 + 1;
		while (openB != closeB) {
			if(ln.substring(i, i + 1).equals("{"))  {
				openB++;
			}
			else if(ln.substring(i, i + 1).equals("}")) {
				closeB++;
			}
			i++;
		}
		while (openP != closeP) {
			if(ln.substring(j, j + 1).equals("("))  {
				openP++;
			}
			else if(ln.substring(j, j + 1).equals(")")) {
				closeP++;
			}
			j++;
		}
		int paren2 = j - 1;
		// i is the index of the closeBracket
		String line = ln.substring(dex + 1, i);
		tokens.add("<whileStatement>" + "\n" + "<keyword>" + "while" + "</keyword>");
		tokens.add("<symbol>" + "(" + "</symbol>");
		//System.out.println(ln.substring(paren1 + 1, paren2));
		expression(ln.substring(paren1 + 1, paren2));
		tokens.add("<symbol>" + ")" + "</symbol>");
		tokens.add("<symbol>" + "{" + "</symbol>");
		//System.out.print(line);
		statements(line, dex + 1 + index);
		tokens.add("<symbol>" + "}" + "</symbol>");
		tokens.add("</whileStatement>");
		return (index + i);
	}
	public void letGrammar() throws letException{
	// I check to see if the variable exists and if the variable is being set equal to a non-empty expression.
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<letStatement>")) {
				if(!tokens.get(i + 2).contains("<varName>")) {
					throw new letException("There is no variable by that name");
				}
				else {
					int j = i;
					while(!tokens.get(j).contains(";")) {
						if(tokens.get(j).contains("<expression>")) {
							if(tokens.get(j + 1).contains("</expression>")) {
								throw new letException("There is no expression in this let statement");
							}
						}
						j++;
					}
				}
			}
		}
	}
		
	public int letStatement(int index) {
	// break it up into let, varName, =, expression
		tokens.add("<letStatement>");
		//System.out.print(fileContents.substring(index));
		int dex = fileContents.substring(index).indexOf(";");
		String ln = fileContents.substring(index);
		int equals = fileContents.substring(index).indexOf("=");
		String line = fileContents.substring(equals + index, index + dex + 1);
		tokenize(ln.substring(0, equals + 1));
		expression(fileContents.substring(equals + index + 1, index + dex));
		tokenize(fileContents.substring(dex + index, index + dex + 1));
		tokens.add("</letStatement>");
		return index + dex + 1;
	}
	public void ifGrammar() throws ifException{
	// an if statement is allowed to have no statements, so I don't need to check for that. 
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<ifStatement>")) {
				if(tokens.get(i + 3).contains("</expression>")) {
					throw new ifException("There was no expression in your if statement");
				}
			}
		}
	}
	public int ifStatement(int index) throws Exception{
	// essentially same structure as while loop
		String ln = fileContents.substring(index);
		int openB = 1;
		int closeB = 0;
		int openP = 1;
		int closeP = 0;
		int endIndex = 0;
		int dex = ln.indexOf("{");
		int paren1 = ln.indexOf("(");
		int i = dex + 1;
		int j = paren1 + 1;
		while (openB != closeB) {
			if(ln.substring(i, i + 1).equals("{"))  {
				openB++;
			}
			else if(ln.substring(i, i + 1).equals("}")) {
				closeB++;
			}
			i++;
		}
		while (openP != closeP) {
			if(ln.substring(j, j + 1).equals("("))  {
				openP++;
			}
			else if(ln.substring(j, j + 1).equals(")")) {
				closeP++;
			}
			j++;
		}
		int paren2 = j - 1;
		// i is the index of the closeBracket
		String line = ln.substring(dex + 1, i);
		tokens.add("<ifStatement>" + "\n" + "<keyword>" + "if" + "</keyword>");
		tokens.add("<symbol>" + "(" + "</symbol>");
		//System.out.println(ln.substring(paren1 + 1, paren2));
		expression(ln.substring(paren1 + 1, paren2));
		tokens.add("<symbol>" + ")" + "</symbol>");
		tokens.add("<symbol>" + "{" + "</symbol>");
		//System.out.print(line);
		statements(line, dex + 1 + index);
		tokens.add("<symbol>" + "}" + "</symbol>");
		tokens.add("</ifStatement>");
		return (index + i);

	}
	public void elseGrammar() throws elseException{
	// an else statement is allowed to have no statements, so I don't need to check for that. 
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<elseStatement>")) {
				if(tokens.get(i + 3).contains("</expression>")) {
					throw new elseException("There was no expression in your else statement");
				}
			}
		}
	}
	public int elseStatement(int index) throws Exception{
	// same structure as if statement
		String ln = fileContents.substring(index);
		int openB = 1;
		int closeB = 0;
		int openP = 1;
		int closeP = 0;
		int endIndex = 0;
		int dex = ln.indexOf("{");
		int paren1 = ln.indexOf("(");
		int i = dex + 1;
		int j = paren1 + 1;
		while (openB != closeB) {
			if(ln.substring(i, i + 1).equals("{"))  {
				openB++;
			}
			else if(ln.substring(i, i + 1).equals("}")) {
				closeB++;
			}
			i++;
		}
		while (openP != closeP) {
			if(ln.substring(j, j + 1).equals("("))  {
				openP++;
			}
			else if(ln.substring(j, j + 1).equals(")")) {
				closeP++;
			}
			j++;
		}
		int paren2 = j - 1;
		// i is the index of the closeBracket
		String line = ln.substring(dex + 1, i);
		tokens.add("<elseStatement>" + "\n" + "<keyword>" + "else" + "</keyword>");
		tokens.add("<symbol>" + "(" + "</symbol>");
		//System.out.println(ln.substring(paren1 + 1, paren2));
		expression(ln.substring(paren1 + 1, paren2));
		tokens.add("<symbol>" + ")" + "</symbol>");
		tokens.add("<symbol>" + "{" + "</symbol>");
		//System.out.print(line);
		statements(line, dex + 1 + index);
		tokens.add("<symbol>" + "}" + "</symbol>");
		tokens.add("</elseStatement>");
		return (index + i);
	}
	public void doGrammar() throws doException{
	// a do statement must have a subroutine call
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<doStatement>")) {
				if(!tokens.get(i + 3).contains("<subroutineCall>")) {
					throw new doException("There was no subroutineCall in your do statement");
				}
			}
		}
	}
	public int doStatement(int index) {
	// it's just a subroutine call
		tokens.add("<doStatement>");
		int dex = fileContents.substring(index).indexOf(";");
		String line = fileContents.substring(index, index + dex + 1);
		tokenize(line);
		tokens.add("</doStatement>");
		return index + dex + 1;
	}
	public void returnGrammar() throws returnException{
	// I mean a return statement can return an empty expression and something so I don't really need to check for anything relating to the grammar here.
	}
	public int returnStatement(int index) {
	// simple break up into return, expression
		tokens.add("<returnStatement>");
		int dex = fileContents.substring(index).indexOf(";");
		String line = fileContents.substring(index, index + dex + 1);
		expression(line);
		tokens.add("</returnStatement>");
		return index + dex + 1;
	}
	public void statementsGrammar() throws statementsException{
	// just check if there are any subroutines or variable declarations in statements. 
		int wrappedStates = 0;
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<statements>") || wrappedStates > 0) {
				if(ln.contains("<statements>")) {
					wrappedStates++;
				}
				int j = i;
				while(!tokens.get(j).contains("</statements>")) {
					if(tokens.get(j).contains("<varDec>")) {
						throw new statementsException("variable declaration in statements");
					}
					else if(tokens.get(j).contains("<classVarDec>")) {
						throw new statementsException(" class variable declaration in statements");
					}
					else if(tokens.get(j).contains("<subroutineDec>")) {
						throw new statementsException("subroutine declaration in statements");
					}
					j++;
				}
			}
			if(ln.contains("</statements>")) {
				wrappedStates--;
			}
		}
	}
	public void statements(String line, int index) throws Exception{
	// I know that varDec and subroutines are not statements, but they are here for the sake of my statement grammar checker
		int i = index;
		tokens.add("<statements>");
		while(i < line.length() + index) {
			//System.out.print(fileContents.substring(i, i + 2));
			//System.out.print(line.substring(i, i + 1));
			//System.out.print(line);
		// just check for the first two letters and execute as neccessary.
			if(fileContents.substring(i, i + 2).equals("le")) {
				i = letStatement(i);
			}
			else if(fileContents.substring(i, i + 2).equals("wh")) {
				i = whileStatement(i);
			}
			else if(fileContents.substring(i, i + 2).equals("if")) {
				i = ifStatement(i);
			}
			else if(fileContents.substring(i, i + 2).equals("el")) {
				i = elseStatement(i);
			}
			else if(fileContents.substring(i, i + 2).equals("re")) {
				i = returnStatement(i);
			}
			else if(fileContents.substring(i, i + 2).equals("do")) {
				i = doStatement(i);
			}
			else if(fileContents.substring(i, i + 2).equals("va")){
				i = variables(i);
			}
			else if(fileContents.substring(i, i + 2).equals("st")){
				tokens.add("<classVarDec>");
				i = variables(i);
				tokens.add("</classVarDec>");
			}
			else if(fileContents.substring(i, i + 2).equals("fi")){
				tokens.add("<classVarDec>");
				i = variables(i);
				tokens.add("</classVarDec>");
			}
			else if(fileContents.substring(i, i + 2).equals("co")){
				i = subroutine(i);
			}
			else if(fileContents.substring(i, i + 2).equals("fu")){
				i = subroutine(i);
			}
			else if(fileContents.substring(i, i + 2).equals("me")){
				i = subroutine(i);
			}
			else {
				i++;
			}
		}
		tokens.add("</statements>");
	}
	public void expression(String line) {
	// just tokenizing a line
		tokens.add("<expression>");
		tokenize(line);
		tokens.add("</expression>");
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
    public void subroutineGrammar() throws subroutineException{
	// just checking if every part of the subroutine is there
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<subroutineDec>")) {
				ArrayList<Integer> vars = new ArrayList<Integer>();
				int statement = 0;
				int j = i;
				while(!tokens.get(j).contains("</subroutineDec>")) {
					if(tokens.get(j).contains("<varDec>")) {
						vars.add(j);
					}
					else if(tokens.get(j).contains("<statements>")) {
						statement = j;
					}
					j++;
				}
					for(int k = 0; k < vars.size(); k++) {
						if(vars.get(k) <= statement) {
							throw new subroutineException("variables must be declared before statements");
						}
					}
				if(tokens.get(i + 1).contains("method") || tokens.get(i + 1).contains("constructor") || tokens.get(i + 1).contains("function")){
					if(tokens.get(i + 2).contains("void") || tokens.get(i + 1).contains("<type>")) {
						if(tokens.get(i + 3).contains("<subroutineName>")) {
							if(tokens.get(i + 7).contains("<parameterList>")) {
								int f = i;
								while(!tokens.get(f).contains("</parameterList>")) {
									f++;
								}
								if(!tokens.get(f + 3).contains("<subroutineBody>")) {
									throw new subroutineException("subroutine must have a body");
								}
							}
							else {
								throw new subroutineException("subroutine missing parameter list");
							}
						}
						else {
							throw new subroutineException("subroutine is missing name");
						}
					}
					else {
						throw new subroutineException("subroutine has invalid type");
					}
				}
				else {
					throw new subroutineException("subroutine must be either a function, method, or constructor");
				}
			}
		}
	}
    public int subroutine(int index) throws Exception{
    // similar to a while loop except I have a parameter list instead of expression
    	tokens.add("<subroutineDec>");
    	String ln = fileContents.substring(index);
    	int paren1 = ln.indexOf("(");
    	//System.out.println(index);
    	int space1 = fileContents.substring(index).indexOf(" ") + index;
    	//System.out.println(space1);
    	int space2 = fileContents.substring(space1 + 1).indexOf(" ") + space1 + 1;
    	System.out.println(fileContents.substring(index, space2 + 1));
    	//System.out.println(space2);
    	tokenize(fileContents.substring(index, space2 + 1));
    	tokens.add("<subroutineName>");
    	// System.out.println(fileContents.substring(space2 + 1, paren1 + index));
    	tokenize(fileContents.substring(space2 + 1, paren1 + index));
    	tokens.add("</subroutineName>");
    	subroutineNames.add(fileContents.substring(space2, paren1 + index).trim());
    	int openB = 1;
		int closeB = 0;
		int openP = 1;
		int closeP = 0;
		int endIndex = 0;
		int dex = ln.indexOf("{");
		int i = dex + 1;
		int j = paren1 + 1;
		while (openB != closeB) {
			if(ln.substring(i, i + 1).equals("{"))  {
				openB++;
			}
			else if(ln.substring(i, i + 1).equals("}")) {
				closeB++;
			}
			i++;
		}
		while (openP != closeP) {
			if(ln.substring(j, j + 1).equals("("))  {
				openP++;
			}
			else if(ln.substring(j, j + 1).equals(")")) {
				closeP++;
			}
			j++;
		}
		int paren2 = j - 1;
		String line = ln.substring(dex + 1, i);
    	tokens.add("<symbol>" + "(" + "</symbol>");
    	tokens.add("<parameterList>");
    	tokenize(ln.substring(paren1 + 1, paren2));
    	tokens.add("</parameterList>");
    	tokens.add("<symbol>" + ")" + "</symbol>");
    	tokens.add("<symbol>" + "{" + "</symbol>");
    	tokens.add("<subroutineBody>");
    	int k = dex + 1 + index;
    	while(fileContents.substring(k).substring(0, 2).trim().equals("va")) {
    			k = variables(k);
    	}
    	statements(fileContents.substring(k, i + index), k);
    	tokens.add("</subroutineBody>");
    	tokens.add("<symbol>" + "}" + "</symbol>");
    	tokens.add("</subroutineDec>");
    	return (index + i);
    }
    public void classesGrammar() throws classesException{
	// I take care of classes grammar in the classes method
	}
    public void classes() throws Exception{
    // just tokenizing statements
    	tokens.add("<class>");
    	int openB = 1;
		int closeB = 0;
    	int brack = this.fileContents.indexOf("{");
    	int space = this.fileContents.indexOf(" ");
    	int space2 = this.fileContents.substring(space + 1).indexOf(" ") + space;
    	int i = brack + 1;
    	while (openB != closeB) {
			if(fileContents.substring(i, i + 1).equals("{"))  {
				openB++;
			}
			else if(fileContents.substring(i, i + 1).equals("}")) {
				closeB++;
			}
			i++;
		}
    	tokenize(this.fileContents.substring(0, space));
    	tokens.add("<className>" + fileContents.substring(space + 1, brack) + "</className>");
    	keywords.add(this.fileContents.substring(space + 1, brack).trim());
    	classNames.add(this.fileContents.substring(space + 1, brack).trim());
    	tokenize(this.fileContents.substring(brack, brack + 1));
    	int k = brack + 1;
    	while(fileContents.substring(k).substring(0, 2).trim().equals("st") || fileContents.substring(k).substring(0, 2).trim().equals("fi")) {
    		tokens.add("<classVarDec>");
    		k = variables(k);
    		tokens.add("</classVarDec>");
    	}
    	while(k < fileContents.length() - 2 && (fileContents.substring(k).substring(0, 2).trim().equals("co") || fileContents.substring(k).substring(0, 2).trim().equals("fu") || fileContents.substring(k).substring(0, 2).trim().equals("me"))) {
    	 	k = subroutine(k);
    	}
    	if(k < fileContents.length() - 2 && (fileContents.substring(k).substring(0, 2).trim().equals("st") || fileContents.substring(k).substring(0, 2).trim().equals("fi"))) {
    			throw new classesException("class variables must be declared before subroutines");
    	}
    	//statements(this.fileContents.substring(brack + 1, i - 1), brack + 1);
    	tokens.add("<symbol>" + "}" + "</symbol>");
    	tokens.add("</class>");
    }
    public void variablesGrammar() throws variablesException{
	// a do statement must have a subroutine call
		for(int i = 0; i < tokens.size(); i++) {
			String ln = tokens.get(i);
			if(ln.contains("<varDec>")) {
				if(!tokens.get(i + 2).contains("<type>")) {
					throw new variablesException("variable must have type");
				}
				else {
					if(!tokens.get(i + 3).contains("<identifier>")) {
						throw new variablesException("variable must have name");
					}
				}
			}
		}
	}
    public int variables(int index) {
    // separate into varType, type, varName
    	tokens.add("<varDec>");
		int dex = fileContents.substring(index).indexOf(";") + index;
		String line = fileContents.substring(index, dex + 1);
		int space = fileContents.substring(index).indexOf(" ") + index;
		//System.out.print(line);
		int space2 = fileContents.substring(space + 1).indexOf(" ") + space;
		tokenize(fileContents.substring(index, space2 + 1));
		variableList.add(fileContents.substring(space2 + 1, dex).trim());
		tokenize(fileContents.substring(space2 + 1, dex));
		tokenize(fileContents.substring(dex, dex + 1));
		tokens.add("</varDec>");
		return dex + 1;
    }
    public void subCall(String str) {
    // I call this in the tokenizer so that I can pass the string itself to check if it fits 1 of the 3 criteria
    	int openP = 1;
    	int dot = str.indexOf(".");
		int closeP = 0;
		int paren1 = str.indexOf("(");
		int j = paren1 + 1;
		while (openP != closeP) {
			if(str.substring(j, j + 1).equals("("))  {
				openP++;
			}
			else if(str.substring(j, j + 1).equals(")")) {
				closeP++;
			}
			j++;
		}
		tokens.add("<term>");
		tokens.add("<subroutineCall>");
		if(dot != -1 && variableList.contains(str.substring(0, dot))) {
			tokens.add("<identifier>\n<term>\n<varName>" + str.substring(0, dot) + "</varName>\n</term>\n</identifier>");
			tokens.add("<symbol>.</symbol>");
		}
		else if(dot != -1 && classNames.contains(str.substring(0, dot))) {
			tokens.add("<identifier>\n<className>" + str.substring(0, dot) + "</className>\n</identifier>");
			tokens.add("<symbol>.</symbol>");
		}
		tokens.add("<subroutineName>" + str.substring(dot + 1, paren1) + "</subroutineName>");
		tokens.add("<symbol>" + "(" + "</symbol>");
		tokens.add("<expressionList>");
		expression(str.substring(paren1 + 1, j - 1));
		tokens.add("</expressionList>");
		tokens.add("<symbol>" + ")" + "</symbol>");
		tokens.add("</subroutineCall>");
		tokens.add("</term>");
    }
	public void tokenize(String ln) {
	// keep working on this 
		int index = 0;
		String line = ln;
	// i break up the code on the delimeters
		boolean isString = false;
		boolean isChar = false;
		boolean inRoutine = false;
		while(index < ln.length()){
			line = ln.substring(index);
			if(line.substring(0, 1).equals("\"")) {
				isString = !isString;
				index += 1;
			}
			else if(line.substring(0, 1).equals('\'')) {
				isChar = !isChar;
				index += 1;
			}
			else if(line.substring(0, 1).equals(" ")) {
				index += 1;
			}
			else {
			// i essentially go through all the possible scenarios
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
					if(line.substring(0, mindex).equals("true") || line.substring(0, mindex).equals("false") || line.substring(0, mindex).equals("this") || line.substring(0, mindex).equals("null")) {
						tokens.add("<keyword>\n<keywordConstant>" + line.substring(0, mindex) + "</keywordConstant>\n</keyword>");
					}
					else if(classNames.contains(line.substring(0, mindex)) || line.substring(0, mindex).equals("int") || line.substring(0, mindex).equals("boolean") || line.substring(0, mindex).equals("char")) {
						if(mindex + 1 <= line.length() && classNames.contains(line.substring(0, mindex)) && line.substring(mindex, mindex + 1).equals(".")) {
						// if it's a subroutine call using a className
							int openP = 1;
    						int dot = line.indexOf(".");
							int closeP = 0;
							int paren1 = line.indexOf("(");
							int j = paren1 + 1;
							while (openP != closeP) {
								if(line.substring(j, j + 1).equals("("))  {
									openP++;
							}
								else if(line.substring(j, j + 1).equals(")")) {
									closeP++;
								}
								j++;
							}
							subCall(line.substring(0, j + 1));
							index += j - mindex;
						}
						else {
							tokens.add("<keyword>\n<type>" + line.substring(0, mindex) + "</type>\n</keyword>");
						}
					}
					else {
						tokens.add("<keyword>" + line.substring(0, mindex) + "</keyword>");
					}
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
					if(ops.contains(line.substring(0, 1))) {
						tokens.add("<symbol>\n<op>" + line.substring(0, 1) + "</op>\n</symbol>");
					}
					else if(unaryOps.contains(line.substring(0, 1))) {
						tokens.add("<symbol>\n<unaryOp>" + line.substring(0, 1) + "</unaryOp>\n</symbol>");
					}
					else {
						tokens.add("<symbol>" + line.substring(0, 1) + "</symbol>");
					}
					mindex++;
				}
				else if(subroutineNames.contains(line.substring(0, mindex))) {
					// if its a subroutine call by itself
							int openP = 1;
    						int dot = line.indexOf(".");
							int closeP = 0;
							int paren1 = line.indexOf("(");
							int j = paren1 + 1;
							while (openP != closeP) {
								if(line.substring(j, j + 1).equals("("))  {
									openP++;
							}
								else if(line.substring(j, j + 1).equals(")")) {
									closeP++;
								}
								j++;
							}
							subCall(line.substring(0, j + 1));
							index += j - mindex;
				}
				else {
					if(variableList.contains(line.substring(0, mindex))) {
						if(mindex + 1 <= line.length() && line.substring(mindex, mindex + 1).equals(".")) {
						// if its a subroutine call using a variable name
							int openP = 1;
    						int dot = line.indexOf(".");
							int closeP = 0;
							int paren1 = line.indexOf("(");
							int j = paren1 + 1;
							while (openP != closeP) {
								if(line.substring(j, j + 1).equals("("))  {
									openP++;
							}
								else if(line.substring(j, j + 1).equals(")")) {
									closeP++;
								}
								j++;
							}
							subCall(line.substring(0, j + 1));
							index += j - mindex;
						}
						else {
							tokens.add("<identifier>\n<varName>" + line.substring(0, mindex) + "</varName>\n</identifier>");
						}
					}
					else {
						tokens.add("<identifier>" + line.substring(0, mindex) + "</identifier>");
					}
				}
				index += mindex;
			}
		}
	}
	public void checkGrammar() throws Exception{
		statementsGrammar();
		whileGrammar();
		letGrammar();
		ifGrammar();
		doGrammar();
		elseGrammar();
		returnGrammar();
		variablesGrammar();
	}
// accessor methods
	public String getFileContents() {
		return this.fileContents;
	}
	public void setFileContents(String str) {
		this.fileContents = str;
	}
	public ArrayList<String> getTokens() {
		return this.tokens;
	}
	public ArrayList<String> getClassNames() {
		return this.classNames;
	}
	public static void main(String[] args) throws Exception{
	// I'm reading the directory and making sure that each jack file gets its own xml file
		String fileName = "";
		JackTokenizer jt = new JackTokenizer();
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
                    jt.setFileContents(jt.getFileContents() + currentLine.trim());

               	 }
               	}
               	if(jt.getFileContents().length() > 0) {
               	 	// System.out.println(jt.getFileContents());
               	 	jt.classes();
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
    jt.checkGrammar();
	}
}