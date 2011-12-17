
public class COBase {
	public Object parent;
/*
	public void registerDispose(Object x) {
		
	}
	
	*/
	
	 void log(String x)
	   {
	      try
	      {
	         throw new Exception("Who called me?");
	      }
	      catch( Exception e )
	      {
	         System.out.println( e.getStackTrace()[1].getClassName() + 
	                             "." +
	                             e.getStackTrace()[1].getMethodName() + 
	                             " : " +
	                             x);
	      }
	   }
	}