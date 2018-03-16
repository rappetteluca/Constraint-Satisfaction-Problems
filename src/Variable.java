import java.util.*;

public class Variable 
{
	public int crosswordNumber;
	public int charLength = 0;
	public int rowPos;
	public int colPos;
	public LinkedList<String> domain;
	public int downWord;
	public String value;
		
	
	public Variable (int crosswordNum, int startingX, int startingY, int flag)
	{
		value = new String("NO_VALUE");
		rowPos = startingX;
		colPos = startingY;
		crosswordNumber = crosswordNum;
		domain = new LinkedList<String>();
		downWord = flag;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (downWord > 0)
		{
			sb.append(crosswordNumber + "-down = " + value + " (" + domain.size() + " values possible)");
		}
		else
		{
			sb.append(crosswordNumber + "-across = " + value + " (" + domain.size() + " values possible)");
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		if(getClass() == o.getClass())
		{
			Variable vb = (Variable) o;
			return crosswordNumber == vb.crosswordNumber && rowPos == vb.rowPos && colPos == vb.colPos && downWord == vb.downWord;
		}
		return false;
	}
}