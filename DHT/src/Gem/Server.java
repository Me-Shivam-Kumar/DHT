package Gem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import message.Message;
import message.MessageFactory;
import message.Receiver;

//A Class Used to Send and Receive Messages between Nodes
public class Server {
	private static final int DATAGRAM_BUFFER_SIZE = 64 * 1024;      
	private final DatagramSocket socket;
	private final Node node;
	private boolean isRunning;
	private final Timer timer;
	private Map<Integer,Receiver> receivers;
	private final Map<Integer,TimerTask> tasks;
	private final MessageFactory messageFactory;
	{
		isRunning=true;
		this.tasks=new HashMap<>();
		this.receivers=new HashMap<>();
		this.timer=new Timer(true);
	}
	
	public Server(int udpPort,Node node,MessageFactory mFactory) throws SocketException {
		this.socket=new DatagramSocket(udpPort);
		this.node=node;
		this.messageFactory = mFactory;
		this.startListener();
		
	}
	public synchronized void reply(Node to, Message msg, int comm) throws IOException
    {
        if (!isRunning)
        {
            throw new IllegalStateException("Kad Server is not running.");
        }
        sendMessage(to, msg, comm);
    }
	 public synchronized int sendMessage(Node to, Message msg, Receiver recv) throws IOException, ServerDownException
	    {
	        if (!isRunning)
	        {
	            throw new ServerDownException(this.node + " - Kad Server is not running.");
	        }
	        int comm = new Random().nextInt();
	        if (recv != null)
	        {
	            try
	            {
	                receivers.put(comm, recv);
	                TimerTask task = new TimeoutTask(comm, recv);
	                timer.schedule(task, 2000);
	                tasks.put(comm, task);
	            }
	            catch (IllegalStateException ex)
	            {
	               
	            }
	        }

	        
	        sendMessage(to, msg, comm);
	        return comm;
	    }
	 private void sendMessage(Node to,Message msg,int comm) throws IOException {
		
	        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(bout);)
	        {
	            
	            dout.writeInt(comm);
	            dout.writeByte(msg.code());
	            msg.toStream(dout);
	            dout.close();

	            byte[] data = bout.toByteArray();

	            if (data.length > DATAGRAM_BUFFER_SIZE)
	            {
	                throw new IOException("Message is too big");
	            }

	           
	            DatagramPacket pkt = new DatagramPacket(data, 0, data.length);
	            pkt.setSocketAddress(to.getSocketAddress());
	            socket.send(pkt);
	        }
	 }
	 private void startListener()
	    {
	        new Thread()
	        {
	            @Override
	            public void run()
	            {
	                listen();
	            }
	        }.start();
	    }
	 private void listen()
	    {
	        try
	        {
	            while (isRunning)
	            {
	                try
	                {
	                  
	                    byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE];
	                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	                    socket.receive(packet);
	                    
	                    try (ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
	                            DataInputStream din = new DataInputStream(bin);)
	                    {
	                        int comm = din.readInt();
	                        byte messCode = din.readByte();

	                        Message msg = messageFactory.createMessage(messCode, din);
	                        din.close();

	                        
	                        Receiver receiver;
	                        if (this.receivers.containsKey(comm))
	                        {
	                           
	                            synchronized (this)
	                            {
	                                receiver = this.receivers.remove(comm);
	                                TimerTask task = (TimerTask) tasks.remove(comm);
	                                if (task != null)
	                                {
	                                    task.cancel();
	                                }
	                            }
	                        }
	                        else
	                        {
	                            receiver = messageFactory.createReceiver(messCode, this);
	                        }

	                        if (receiver != null)
	                        {
	                            receiver.receive(msg, comm);
	                        }
	                    }
	                }
	                catch (IOException e)
	                {
	                   
	                    System.err.println("Server ran into a problem in listener method. Message: " + e.getMessage());
	                }
	            }
	        }
	        finally
	        {
	            if (!socket.isClosed())
	            {
	                socket.close();
	            }
	            this.isRunning = false;
	        }
	    }
	                
	                    
private synchronized void unregister(int comm){
	                        receivers.remove(comm);
	                        this.tasks.remove(comm);
}

public synchronized void shutdown()
{
    this.isRunning = false;
    this.socket.close();
    timer.cancel();
}	                   
class TimeoutTask extends TimerTask
{

    private final int comm;
    private final Receiver recv;

    public TimeoutTask(int comm, Receiver recv)
    {
        this.comm = comm;
        this.recv = recv;
    }

    @Override
    public void run()
    {
        if (!Server.this.isRunning)
        {
            return;
        }

        try
        {
            unregister(comm);
            recv.timeout(comm);
        }
        catch (IOException e)
        {
            System.err.println("Cannot unregister a receiver. Message: " + e.getMessage());
        }
    }
}

public void printReceivers()
{
    for (Integer r : this.receivers.keySet())
    {
        System.out.println("Receiver for comm: " + r + "; Receiver: " + this.receivers.get(r));
    }
}

public boolean isRunning()
{
    return this.isRunning;
}

}
	                   

	                    
	
