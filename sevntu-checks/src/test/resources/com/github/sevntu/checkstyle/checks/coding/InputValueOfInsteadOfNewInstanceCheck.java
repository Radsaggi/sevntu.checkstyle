
public class InputValueOfInsteadOfNewInstanceCheck
{
	
	private void generalExpressionsCheck()
	{
		int a = 5;
		Object c = new Integer(a); //!!
		Short d = new Short(5); //!!
		Float e = new Float(7.5f); //!!
		
		Integer f = new Integer(getInteger());
		Double g = new Double(getDouble());
		
		Byte b = new Byte(getByte() + 1);
		Long l = new Long("78912345676l");
	}
	
	private int getInteger() 
	{
		return 2;
	}
	
	private double getDouble() 
	{
		return 3.14;
	}
	
	private byte getByte() 
	{
		return 12;
	}
	
	public class Testing
	{
	    boolean returnTrue()
	    {
	        return true;
	    }
	}
}
