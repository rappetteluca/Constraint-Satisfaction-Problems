import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Solve 
{
	protected LinkedList<Variable> wordVariables;
	protected LinkedList<Constraint> wordConstraints;
	protected ArrayList<String>[] dividedDictionary;
	private Character[][] grid;
	private HashMap<Pair,Variable> variablePositions;
	public int numRows;
	public int numCols;
	public int recursiveCalls = 0;

	public static void main(String[] args) 
	{
		if (args.length > 2)
		{
			new Solve(args[0], args[1], args[2]);
		}
		else
		{
			new Solve(args[0], args[1]);
		}

	}
	
	public Solve(String puzzleFile, String wordFile, String flag)
	{
		System.out.println("Lucas Rappette is in CS 452 so there is no support for arc consistency, sorry!");
		new Solve(puzzleFile, wordFile);
	}
	
	public Solve(String puzzleFile, String wordFile)
	{
		try
		{
			wordVariables = new LinkedList<Variable>();
			wordConstraints = new LinkedList<Constraint>();
			variablePositions = new HashMap<Pair, Variable>();
			makeGrid(puzzleFile);
			evaluateGridVariablesandConstraints();
			buildDictionaryAndDomains(wordFile);
			System.out.println(wordVariables.size() + " words");
			System.out.println(wordConstraints.size() + " constraints");
			System.out.println();
			System.out.println("Initial Assignment and domain sizes:");
			for (Variable v : wordVariables)
			{
				System.out.println(v.toString());
			}
			
			Assignment solution = findSolution();
			if (solution != null)
			{
				System.out.print("\n\nSUCCESS! ");
				System.out.println("Solution found after " + recursiveCalls + " recursive calls to search.\n");
				for (Variable v : wordVariables)
				{
					v.value = solution.getValue(v);
					if(v.downWord > 0)
					{
						for(int i = 0; i < v.charLength; i++)
						{
							grid[v.rowPos + i][v.colPos] = v.value.charAt(i);
						}
					}
					else
					{
						for (int i = 0; i < v.charLength; i++)
						{
							grid[v.rowPos][v.colPos + i] = v.value.charAt(i);
						}
					}
				}
				
				printGrid();
			}
		}
		catch (Exception E)
		{
			System.out.println("Incorrect Puzzle Format.");
		}
	}
	
	public Assignment findSolution()
	{
		BackTracker search = new BackTracker(this);
		Assignment soln = search.findSolution();
		recursiveCalls = search.recursiveCalls;
		return soln;
	}
	
	public boolean consistentAssignment(Assignment assign, Variable v) 
	{
	    for (Constraint c : wordConstraints) 
	    {
	      if (!c.consistent(assign)) 
	    	  return false;
	    }

	    return true;
	}
	
	public boolean satisfiedByAssignment(Assignment asign) 
	{
		if (wordVariables.size() > asign.size()) 
		{ 
			return false; 
		}

		for (Constraint c : wordConstraints) 
		{
			if (!c.satisfied(asign)) 
		    {
				return false;
		    }
		}
		return true;
	 }
	
	public void buildDictionaryAndDomains(String fileName)
	{
		if (numRows > numCols)
		{
			dividedDictionary = new ArrayList[numRows+1];
		}
		else
		{
			dividedDictionary = new ArrayList[numCols+1];
		}
		for (int i = 0; i < dividedDictionary.length; i++)
		{
			dividedDictionary[i] = new ArrayList<String>();
		}
		try
		{
			File target = new File("./inputData/" + fileName);
			BufferedReader in = new BufferedReader(new FileReader(target));
			String word = in.readLine();
			while(word != null)
			{
				if (!word.equalsIgnoreCase(""))
				{
					if (word.length() < dividedDictionary.length)
					dividedDictionary[word.length()].add(word);
				}
				
				word = in.readLine();
			}
			in.close();
			
			for (Variable v : wordVariables)
			{
				v.domain = new LinkedList<String>(dividedDictionary[v.charLength]);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Bad Dictionary File Name.");
		}
	}

	public void makeGrid(String fileName)
	{
		try
		{
			File target = new File("./inputData/" + fileName);
			Scanner scan = new Scanner(target);
			numRows = scan.nextInt();
			numCols = scan.nextInt();
			scan.close();
			grid = new Character[numRows][numCols];
			
			BufferedReader in = new BufferedReader(new FileReader(target));
			in.readLine();
			String rowRead = in.readLine();
			int rowNumber = 0;
			while(rowRead != null)
			{
				if (rowNumber == numRows)
					break;
				
				parseRow(rowRead, rowNumber);
				rowRead = in.readLine();
				rowNumber++;
			}
			in.close();
			
		}
		catch (Exception e)
		{
			System.out.println("Bad Puzzle File Name.");
		}
	}

	private void printGrid() 
	{
		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < numCols; j++)
			{
				if (grid[i][j] != 'X')
				{
					System.out.print(grid[i][j]);
					System.out.print(" ");
				}
				else
				{
					System.out.print("  ");
				}
			}
			System.out.println();
		}
		
	}

	private void evaluateGridVariablesandConstraints() 
	{
		for (Variable v : wordVariables)
		{
			if(v.downWord > 0)
			{
				int i = v.rowPos;
				int j = v.colPos;
				while(i < numRows && grid[i][j] != 'X')
				{
					Variable interSectingWord = null;
					int y = j;
					Pair temp = new Pair(i, y, 0);
					while (!variablePositions.containsKey(temp) && y >= 0)
					{
						if (grid[i][y] != 'X')
						{
							temp = new Pair(i, y, 0);
							//System.out.println("Down pairs (Return ACCROSS): " + temp.toString());
							y--;
						}
						else
						{
							break;
						}
					}
					if (variablePositions.containsKey(temp) == true && v != null)
					{
						interSectingWord = variablePositions.get(temp);
						if (interSectingWord != null)
						{
							Constraint c = new Constraint();
							c.variables.add(v);
							c.variables.add(interSectingWord);
							c.conditionals.add(new Intersection( (v.rowPos + i), (v.colPos - interSectingWord.colPos), v, interSectingWord));
							c.conditionals.add(new Intersection((v.colPos - interSectingWord.colPos), (v.rowPos + i), interSectingWord, v));
							if(!wordConstraints.contains(c))
							{
								wordConstraints.add(c);
							}
						}
					}
					v.charLength++;
					i++;
				}
			}
			else
			{
				int i = v.rowPos;
				int j = v.colPos;
				
				while(j < numCols && grid[i][j] != 'X')
				{
					Variable interSectingWord = null;
					Pair temp = new Pair(i, j, 1);
					int x = i;
					while (!variablePositions.containsKey(temp) && x >= 0)
					{
						if(grid[x][j] != 'X')
						{
							temp = new Pair(x, j, 1);
							//System.out.println("Accross Pairs (Return DOWN): " + temp.toString());
							x--;
						}
						else
						{
							break;
						}
					}
					
					if (variablePositions.containsKey(temp) && v != null);
					{
						interSectingWord = variablePositions.get(temp);
						if (interSectingWord != null)
						{
							Constraint c = new Constraint();
							c.conditionals.add(new Intersection((interSectingWord.colPos), (v.rowPos), v, interSectingWord));
							c.conditionals.add(new Intersection((v.rowPos), (interSectingWord.colPos), interSectingWord, v));
							c.variables.add(v);
							c.variables.add(interSectingWord);
							if(!wordConstraints.contains(c))
							{
								wordConstraints.add(c);
							}
						}
					}
					v.charLength++;
					j++;
				}
			}
		}
		
	}

	private void parseRow(String rowRead, int rowNumber) 
	{
		int columnNumber = 0;
		try
		{
			Reader toParse = new StringReader(rowRead);
			StreamTokenizer parser = new StreamTokenizer(toParse);
			parser.eolIsSignificant(true);
			parser.lowerCaseMode(false);
			parser.parseNumbers();
			parser.ordinaryChar(' ');
			parser.ordinaryChar('_');
			parser.nextToken();
			while (columnNumber < numCols && parser.ttype != parser.TT_EOL)
			{
				if (parser.ttype != ' ')
				{
					if (parser.ttype == parser.TT_NUMBER)
					{
						if (isAcrossVariable(rowNumber, columnNumber))
						{
							Variable newVar = new Variable((int)parser.nval, rowNumber, columnNumber, 0);
							wordVariables.add(newVar);
							variablePositions.put(new Pair(rowNumber, columnNumber, 0), newVar);
						}
						if (isDownVariable(rowNumber, columnNumber))
						{
							Variable newVar1 = new Variable((int)parser.nval, rowNumber, columnNumber, 1);
							wordVariables.add(newVar1);
							variablePositions.put(new Pair(rowNumber, columnNumber, 1), newVar1);
						}
						grid[rowNumber][columnNumber] = (char) parser.nval; //Not REALLY needed
					}
					else if (parser.ttype == '_')
					{
						grid[rowNumber][columnNumber] = (char) parser.ttype;
					}
					else
					{
						grid[rowNumber][columnNumber] = parser.sval.charAt(0);
					}
					columnNumber++;
				}
				parser.nextToken();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean isAcrossVariable(int rowNumber, int columnNumber) 
	{
		if(columnNumber == 0)
		{
			return true;
		}
		if(grid[rowNumber][columnNumber-1] == 'X')
		{
			return true;
		}
		
		return false;
	}

	private boolean isDownVariable(int rowNumber, int columnNumber) 
	{
		if (rowNumber == 0)
		{
			return true;
		}
		if (grid[rowNumber-1][columnNumber] == 'X')
		{
			return true;
		}
		
		return false;
	}
	
	/**
	   * Map that stores the constraints
	   * on any given variable.
	   */
	  Map<Variable, List<Constraint>> varConstraints = null;

	  /**
	   * Returns all the constraints that rely on a given variable.
	   */
	  public List<Constraint> variableConstraints(Variable v) {
	    if (varConstraints != null) return varConstraints.get(v);
	    varConstraints = new HashMap<Variable, List<Constraint>>();

	    for (Constraint c : wordConstraints) 
	    {
	      List<Variable> vars = c.reliesOn();
	      for (Variable constrVar : vars) {
	        // Add the constraint if we have a mapping
	        if (varConstraints.containsKey(constrVar)) {
	          varConstraints.get(constrVar).add(c);

	        // Create a mapping between the variable and this constraint
	        } else {
	          List<Constraint> constr = new LinkedList<Constraint>();
	          constr.add(c);
	          varConstraints.put(constrVar, constr);
	        }
	      }
	    }
	    return varConstraints.get(v);
	  }
	  
	public List<String> domainValues(Assignment assign, Variable v) 
	{
	    List<String> domain = assign.getDomain(v);
	    if (domain != null) 
	    {
	    	return domain;
	    }
	    else
	    	return v.domain;
	}
	
	public class Intersection
	{		
		//Describes an intersection of a character between two variables
		int charPositionSource;
		int charPositionTarget;
		Variable charTarget;
		Variable charSource;
			
		public Intersection(int posSource, int posTarget, Variable source, Variable v)
		{
			charPositionSource = posSource;
			charPositionTarget = posTarget;
			charSource = source;
			charTarget = v;
		}
		
		@Override
		public String toString()
		{
			if (charTarget.downWord > 0)
			{
				return new String("Intersection of Variable " + charSource.crosswordNumber + ((charSource.downWord > 0) ? "-down" : "-across") + " with Variable " + charTarget.crosswordNumber + "-down at char(" + charPositionSource + ")");
			}
			else
			{
				return new String("Intersection of Variable " + charSource.crosswordNumber + ((charSource.downWord > 0) ? "-down" : "-across") + " with Variable " + charTarget.crosswordNumber + "-across at char(" + charPositionSource + ")");
			}
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (this.getClass() == o.getClass())
			{
				Intersection compare = (Intersection) o;
				if (charPositionSource == compare.charPositionSource)
				{
					if (charPositionTarget == compare.charPositionTarget)
					{
						if(charSource.equals(compare.charSource) && charTarget.equals(compare.charTarget))
						{
							return true;
						}
					}
				}
				if (charPositionSource == compare.charPositionTarget)
				{
					if (charPositionTarget == compare.charPositionSource)
					{
						if(charSource.equals(compare.charTarget) && charTarget.equals(compare.charSource))
						{
							return true;
						}
					}
				}
			}
			
			return false;
		}
			
	}
	
	public class Constraint 
	{
	    public List<Variable> variables = new ArrayList<Variable>();
	    public List<Intersection> conditionals = new ArrayList<Intersection>();
	    
	    public boolean satisfied(Assignment asign) 
	    {
	      LinkedList<String> seen = new LinkedList<String>();
	      for (Variable v : variables) 
	      {
	        String val = asign.getValue(v);
	        if (val == null || seen.contains(val))  
	        {
	          return false;
	        }
	        seen.add(val);
	      }
	      for (Intersection c : conditionals)
		  {
	    	  if(asign.getValue(c.charSource).charAt(c.charPositionSource) != asign.getValue(c.charTarget).charAt(c.charPositionTarget))
	    	  {
	    		  return false;
	    	  }
		  }
	   return true;
	   }

	    public boolean consistent(Assignment asign) 
	    {
	      LinkedList<String> seen = new LinkedList<String>();
	      LinkedList<String> avail = new LinkedList<String>();
	      int constraintDomain = 0;

	      for (Variable v : variables) 
	      {
	        // Check if this variable adds to the domain of the constraint
	        for (String val : domainValues(asign, v)) 
	        {
	          if (!avail.contains(val)) {
	            constraintDomain++;
	            avail.add(val);
	          }
	        }

	        // Check for a duplicate value
	        String val = asign.getValue(v);
	        if (val != null) 
	        {
	          if (seen.contains(val))  
	          {
	            return false;
	          }
	          seen.add(val);
	        }
	      }
	      
	      for (Intersection c : conditionals)
		  {
	    	  if(asign.getValue(c.charSource) != null && asign.getValue(c.charTarget) != null)
	    	  {
	    		  if(asign.getValue(c.charSource).charAt(c.charPositionSource) != asign.getValue(c.charTarget).charAt(c.charPositionTarget))
	    		  {
	    			  return false;
	    		  }
	    	  }
		  }

	      // Check if there are not enough values
	      if (variables.size() > constraintDomain) 
	      {
	        return false;
	      }
	      
	      
	      return true;
	    }

		public List<Variable> reliesOn() 
	    {
	      return variables;
	    }
	    
	    @Override
	    public boolean equals(Object o)
	    {
	    	if (this.getClass() == o.getClass())
	    	{
	    		Constraint ct = (Constraint) o;
	    		if (variables.size() != ct.variables.size() || conditionals.size() != ct.conditionals.size())
	    		{
	    			return false;
	    		}
	    		for (Variable v : variables)
	    		{
	    			if(!ct.variables.contains(v))
	    			{
	    				return false;
	    			}
	    		}
	    		for (Intersection i : conditionals)
	    		{
	    			if(!ct.conditionals.contains(i))
	    			{
	    				return false;
	    			}
	    		}
	    		
	    		return true;
	    	}
	    	return false;
	    }
	    @Override
	    public String toString()
	    {
	    	StringBuilder sb = new StringBuilder(0);
	    	sb.append("Constraint Variables: \n");
	    	for (Variable v : variables)
	    	{
	    		sb.append(v.toString() + "\n");
	    	}
	    	for (Intersection i : conditionals)
	    	{
	    		sb.append(i.toString() + "\n");
	    	}
	    	sb.append("END CONSTRAINT \n");
	    	return sb.toString();
	    }
	    
	}
	
	public class Pair
	{
		public int xCoord;
		public int yCoord;
		public int downWord;
		
		public Pair(int x, int y, int flag)
		{
			xCoord = x;
			yCoord = y;
			downWord = flag;
		}
		
		@Override
		public int hashCode()
		{
			//Attempt to normalize
			int i = ((23 * ((downWord > 0) ? 1 : 0) + (17 * xCoord) + (31 * yCoord)));
			return i;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (getClass() == o.getClass())
			{
				Pair p = (Pair) o;
				return (xCoord == p.xCoord && yCoord == p.yCoord && ((downWord - p.downWord) == 0));
			}
			return false;
		}
		
		public String toString()
		{
			if (downWord == 0)
			{
				return new String("(" + xCoord + "," + yCoord + ") : " + "ACROSS");
			}
			else
			{
				return new String("(" + xCoord + "," + yCoord + ") : " + "DOWN");
			}
		}
	
	}
}
