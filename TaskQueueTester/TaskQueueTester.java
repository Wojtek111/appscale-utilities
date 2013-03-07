import com.google.appengine.api.taskqueue.TaskQueuePb;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueAddRequest;
import com.google.appengine.api.taskqueue.TaskQueuePb.TaskQueueAddRequest.Header;

import java.util.UUID;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.google.appengine.repackaged.com.google.io.protocol.ProtocolMessage;
import com.google.apphosting.utils.remoteapi.RemoteApiPb.Request;
import com.google.apphosting.utils.remoteapi.RemoteApiPb.Response;

public class TaskQueueTester
{
    static int port = 64839;
    static String host = "192.168.111.125";
    static int MAX_TOTAL_CONNECTIONS = 200;
    static int MAX_CONNECTIONS_PER_ROUTE = 20;
    static int MAX_CONNECTIONS_PER_ROUTE_LOCALHOST = 80;
    static String APPDATA_HEADER = "APPDATA";
    static String appId = "TQ";
    static String method = "Add";
    static String PROTOCOL_BUFFER_HEADER = "ProtocolBufferType";
    static String PROTOCOL_BUFFER_VALUE = "Request";
    static String SERVICE_NAME = "taskqueue";
    static int INPUT_STREAM_SIZE = 10240;
    static ByteArrayResponseHandler handler = new ByteArrayResponseHandler();
    public static void main(String[] args)
    {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), port));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
        cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        String url = "http://" + host + ":" + port + "/";
        HttpHost localhost = new HttpHost(url);
        cm.setMaxForRoute(new HttpRoute(localhost), MAX_CONNECTIONS_PER_ROUTE_LOCALHOST);
        DefaultHttpClient client = new DefaultHttpClient(cm);
        System.out.println("Posting to url: " + url);
        HttpPost post = new HttpPost(url);
        post.addHeader(PROTOCOL_BUFFER_HEADER, PROTOCOL_BUFFER_VALUE);
        String tag = appId;
        post.addHeader(APPDATA_HEADER, tag);

        Request remoteRequest = new Request();
        remoteRequest.setMethod(method);
        remoteRequest.setServiceName(SERVICE_NAME);
        TaskQueuePb.TaskQueueAddRequest request = getRequest(); 
        remoteRequest.setRequestAsBytes(request.toByteArray());

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try
        {
            bao.write(remoteRequest.toByteArray());
            ByteArrayEntity entity = new ByteArrayEntity(bao.toByteArray());
            post.setEntity(entity);
            bao.close();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        Response remoteResponse = new Response();
        try
        {
            //ByteArrayResponseHandler handler = new ByteArrayResponseHandler();
            byte[] bytes = client.execute(post, handler);
            remoteResponse.parseFrom(bytes);
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        TaskQueuePb.TaskQueueAddResponse response = new TaskQueuePb.TaskQueueAddResponse();
        response.parseFrom(remoteResponse.getResponseAsBytes());
        System.out.println("Response: " + response);
    }
    public static TaskQueuePb.TaskQueueAddRequest getRequest()
    {
         TaskQueuePb.TaskQueueAddRequest request = new TaskQueuePb.TaskQueueAddRequest();
         request.setQueueName("default");
         request.setTaskName("task-" + UUID.randomUUID().toString());
         request.setEtaUsec(100l);
         request.setMethod(1);
         request.setUrl("http://192.168.111.125:8080/taskqueuetester?key=key");
         TaskQueuePb.TaskQueueAddRequest.Header header = new TaskQueuePb.TaskQueueAddRequest.Header();
         header.setKey("X-Appengine-Current-Namespace");
         header.setValue("");
         request.addHeader(header);
         request.setMode(0);
         return request;
    }

    static class ByteArrayResponseHandler implements ResponseHandler<byte[]>
    {

        public byte[] handleResponse( HttpResponse response ) throws ClientProtocolException, IOException
        {
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                InputStream inputStream = entity.getContent();
                try
                {
                    return inputStreamToArray(inputStream);
                }
                finally
                {
                    entity.getContent().close();
                }
            }
            return new byte[] {};
        }
    }

    private static byte[] inputStreamToArray( InputStream in )
    {
        int len;
        int size = INPUT_STREAM_SIZE;
        byte[] buf = null;
        try
        {
            if (in instanceof ByteArrayInputStream)
            {
                size = in.available();
                buf = new byte[size];
                len = in.read(buf, 0, size);
            }
            else
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                buf = new byte[size];
                while ((len = in.read(buf, 0, size)) != -1)
                {
                    bos.write(buf, 0, len);
                }
                buf = bos.toByteArray();

            }
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return buf;
    }

}
