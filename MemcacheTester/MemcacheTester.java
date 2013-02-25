import java.util.*;
import java.net.*;
import java.lang.*;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import net.rubyeye.xmemcached.*;
import net.rubyeye.xmemcached.utils.*;

public class MemcacheTester
{
    public static void main(String[] args) throws Exception
    {
	XMemcachedClientBuilder builder = null;
	if(args.length == 4)
	{
	    System.out.println("Using command line ip");
            builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(args[3] + ":11211"));
	}
        else
	{
	    System.out.println("Args[3] was empty, using hardcoded ips");
	    //Hardcode your ips into the line below
            builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.111.109:11211 192.168.111.117:11211 192.168.111.118:11211 192.168.111.119:11211"));
	    System.out.println("Done creating builder....");
	}
	System.out.println("Building client...");
	MemcachedClient client = builder.build();
	String key = args[0];
	String value = args[1];
	String command = args[2];
	if(command.equals("put"))
	{
	    System.out.println("Doing put...");
	    Object response = client.set(key, 0, value);
	    System.out.println("Response: " + response);
        }
	else if(command.equals("get"))
	{
	    Object res = client.get(key);
 	    System.out.println("Got response: " + res);
	} 
	else if(command.equals("delete"))
	{
	    System.out.println("Delete returned: " + client.delete(key));
	    System.out.println("Done deleting");
	}
	else if(command.equals("replace"))
	{
	    client.replace(key, 0, value);
	    System.out.println("Done replacing");
	}
	else if(command.equals("cas"))
	{
	    GetsResponse<Object> getsResp = client.gets(key);
	    System.out.println("GetsResp: " + getsResp);
	    Object returnVal = client.cas(key, 0, value, getsResp.getCas());
	    System.out.println("ReturnVal: " + returnVal);
	}
	else if(command.equals("casfail"))
	{
	    GetsResponse<Object> getsResp = client.gets(key);
            System.out.println("GetsResp: " + getsResp);
	    System.out.println("Set response: " + client.set(key, 0, "casfail"));
            Object returnVal = client.cas(key, 0, value, getsResp.getCas());
            System.out.println("ReturnVal: " + returnVal);
	}
    }
}
