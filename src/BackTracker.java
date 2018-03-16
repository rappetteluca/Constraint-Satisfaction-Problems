import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class BackTracker 
{
	Solve definitions;
	Assignment start;
	int recursiveCalls;
	
	public BackTracker(Solve problem)
	{
		this(problem, Assignment.blank());
	}
	public BackTracker(Solve problem, Assignment initial)
	{
		definitions = problem;
		start = initial;
		recursiveCalls = 0;
	}
	
	public Assignment findSolution()
	{
		return recursiveSolve(start);
	}
	
	private Assignment recursiveSolve(Assignment assign) 
	{
		recursiveCalls++;
	    if (definitions.satisfiedByAssignment(assign)) 
	    {
	      return assign;
	    }

	    // Get an unassigned variable
	    Variable v = unassignedVar(assign);
	    if (v == null) return null;
	    // Get the domain values for a variable
	    List<String> wordDomain = definitions.domainValues(assign, v);
	    for (String word : wordDomain) 
	    {
	      // Make a new assignment
	      Assignment newAssign = assign.assign(v, word);

	      // Check the consistency
	      if (!definitions.consistentAssignment(newAssign, v)) { 
	        continue; 
	      }
	      // Recurse
	      newAssign = recursiveSolve(newAssign);
	      if (newAssign != null) return newAssign;
	    }

	    // Failed
	    return null;
	  }

	 protected Variable unassignedVar(Assignment assign) 
	 {
		 int minDomain = Integer.MAX_VALUE;
		 Variable minVar = null;
		 List<Variable> vars = definitions.wordVariables;
		 
		 if (vars.size() == assign.size()) 
		    		return null;
		 
		 for (Variable v : vars) 
		 {
			 if (assign.getValue(v) == null) 
			 {
		        int domSize = definitions.domainValues(assign, v).size();
		        if (domSize < minDomain) 
		        {
		          minDomain = domSize;
		          minVar = v;
		        }
		        if (domSize == minDomain)
		        {
		        	//Compare minVar and V in the number of constraints present
		        	int currVarConstr = definitions.variableConstraints(minVar).size();
		        	int newVarConstr = definitions.variableConstraints(v).size();
		        	
		        	if (currVarConstr < newVarConstr)
		        	{
		        		minVar = v;
		        	}
		        }
		      }
		  }
		 return minVar;
	}
}
