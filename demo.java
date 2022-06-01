import map.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

class dmap//class to hold concurrent hashmap object and its corresponding methods for convinient access of threads
{

	public static concurmap dobj = new concurmap();

	public static void dput(Object k,Object v)
	{
		dobj.put(k,v);
	}

	public static void dget(Object k)
	{
		dobj.get(k);
	}

	public static void dis()
	{
		dobj.display();
	}

	public static void dremove(Object k)
	{
		dobj.remove(k);
	}

}

class mythread1 extends Thread//this thread is used to put values into the concurrent hashmap object
{
	Object k,v;
	mythread1(Object k,Object v)
	{
		this.k = k;
		this.v = v;
	}
	public void run()
	{
		dmap.dput(k,v);
	}
}

class mythread2 extends Thread//this thread is used to get values into the concurrent hashmap object
{
	Object k;
	mythread2(Object k)
	{
		this.k = k;
	}
	public void run()
	{
		dmap.dget(k);
	}

}

class mythread3 extends Thread//this thread is used to remove values into the concurrent hashmap object
{
	Object k;
	mythread3(Object k)
	{
		this.k = k;
	}
	public void run()
	{
		dmap.dremove(k);
	}

}

class demo extends TimerTask
{

		mythread1 tput1 = new mythread1( -1.5 , 10000 );

		mythread2 tget1 = new mythread2( -6.5F );
		mythread2 tget2 = new mythread2( 1.5 );
		mythread2 tget3 = new mythread2( 6.5F );
		mythread2 tget4 = new mythread2( -1.5 );

		mythread3 tremove1 = new mythread3( -6.5F );
		mythread3 tremove2 = new mythread3( 6.5F );

	public static void main(String[] args)
	{

		// set scheduler here to run all the threads in race condition
		long ONCE_PER_DAY = 1000*60*60*24;
     	Calendar calendar = Calendar.getInstance();
     	calendar.set(Calendar.HOUR_OF_DAY, 11);//hour of the day 0-24
     	calendar.set(Calendar.MINUTE, 5);//min of the day 0-60
     	calendar.set(Calendar.SECOND, 00);//sec of the day 0-60
     	Date time = calendar.getTime();
     	TimerTask check  = new demo();
     	Timer timer = new Timer();
     	timer.scheduleAtFixedRate(check, time ,ONCE_PER_DAY);

     	//some random values to fill the concurrent hashmap object
     	dmap.dput( -6.5F , "ZOHO" );
     	dmap.dput( 1.5 , true );
     	dmap.dput( 6.5F , 999l );
     	dmap.dput( 'D' , "Dhoni" );
     	dmap.dput( 777 , 12.000001 );


				
	}

	@Override    
	// run method of timer task
	public void run()
	{

    	tput1.start();

    	tremove1.start();

    	tremove2.start();

    	tget4.start();
    
    	tget3.start();
    
    	tget2.start();

    	tget1.start();

    	dmap.dis();// to print the values present in the concurrent hashmap object

    }
}

