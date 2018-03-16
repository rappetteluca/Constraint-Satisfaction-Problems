import java.util.*;

/**
 * This class represents an assignment for a CSP problem.
 * The assignment may be partial or complete.
 */
public class Assignment {
  /**
   * This private map is used to track the current variable assignments.
   */
  Map<Variable, String> assignments = null;

  /**
   * This private map is used to track the restricted domain of variables
   */
  Map<Variable, List<String>> domain = null;


  /**
   * Creates a new blank assignment.
   */
  static Assignment blank() {
    Assignment blank = new Assignment();
    blank.assignments = new HashMap<Variable, String>();
    blank.domain = new HashMap<Variable,List<String>>();
    return blank;
  }

  /**
   * Assigns a value to a variable and returns a new
   * assignment representing this state.
   */
  public Assignment assign(Variable v, String val) 
  {
    Assignment n = new Assignment();
    n.assignments = new HashMap<Variable, String>(assignments);
    n.assignments.put(v, val);
    n.domain = new HashMap<Variable,List<String>>(domain);

    // Restrict the domain to only a single value
    List<String> varDomain = new LinkedList<String>();
    varDomain.add(val);
    n.restrictDomain(v, varDomain);

    return n;
  }

  /**
   * Gets the valuable of a variable.
   * @param v The variable to retrieve
   * @return The assigned value or null if it is unassigned.
   */
  public String getValue(Variable v) 
  {
    return assignments.get(v);
  }

  /**
   * Assigns a restricted domain to a variable.
   */
  public void restrictDomain(Variable v, List<String> varDomain) {
    domain.put(v,varDomain);
  }

  /**
   * Returns the restricted domain of a variable.
   */
  public List<String> getDomain(Variable v) {
    return domain.get(v);
  }

  /**
   * Returns the number of assignments made
   */
  public int size() {
    return assignments.size();
  }
}