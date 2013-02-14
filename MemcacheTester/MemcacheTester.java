import java.util.*;
import net.spy.memcached.MemcachedClient;
import java.net.*;
import java.lang.*;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;


public class MemcacheTester
{
    public static void main(String[] args)
    {
	List<InetSocketAddress> ipList = new ArrayList<InetSocketAddress>();
	if(args.length == 3)
	{
	    ipList.add(new InetSocketAddress(args[2], 11211));
	}
        else
	{
	    ipList.add(new InetSocketAddress("192.168.111.105", 11211));
            ipList.add(new InetSocketAddress("192.168.111.106", 11211));
            ipList.add(new InetSocketAddress("127.0.0.1", 11211));
 	    ipList.add(new InetSocketAddress("192.168.111.107", 11211));
	}
	MemcachedClient client = null;
	try
	{
	    client = new MemcachedClient(ipList);
	}
	catch(Exception e)
	{
	    System.out.println("Exception!");
	}
	String key = args[0];
	if(args[1].equals("put"))
	{
	    Future<Boolean> response = client.set(key, 1200, key);
            try
            {
                Boolean successful = response.get();
	        System.out.println("Successful? - " + successful);
            }
            catch(InterruptedException e)
            {
                System.out.println("Exception");
	    }
            catch(ExecutionException e)
            {
	        System.out.println("Exception!");
	    }
        }
	if(args[1].equals("get"))
	{
	    Object res = client.get(key);
 	    System.out.println("Got response: " + res);
	} 
    }
}
