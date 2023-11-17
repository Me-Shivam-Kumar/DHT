package Gem;
import message.Receiver;
import java.io.IOException;
import message.AcknowledgeMessage;
import message.ConnectMessage;
import message.Message;
public class ConnectOperation implements Receiver
{

    public static final int MAX_CONNECT_ATTEMPTS = 5;       
    private final Server server;
    private final Initt localNode;
    private final Node bootstrapNode;
    private boolean error;
    private int attempts;

    public ConnectOperation(Server server, Initt local, Node bootstrap)
    {
        this.server = server;
        this.localNode = local;
        this.bootstrapNode = bootstrap;
    }

    public synchronized void execute() throws IOException
    {
        try
        {
           this.error = true;
            this.attempts = 0;
            Message m = new ConnectMessage(this.localNode.getNode());
            server.sendMessage(this.bootstrapNode, m, this);
            int totalTimeWaited = 0;
            int timeInterval = 50;     
            while (totalTimeWaited < 2000)
            {
                if (error)
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
            if (error)
            {
                throw new RoutingException("ConnectOperation: Bootstrap node did not respond: " + bootstrapNode);
            }

            NodeLookupOperation lookup = new NodeLookupOperation(this.server, this.localNode, this.localNode.getNode().getNodeId());
            lookup.execute();
           
        }
        catch (InterruptedException e)
        {
            System.err.println("Connect operation was interrupted. ");
        }
    }

    
    @Override
    public synchronized void receive(Message incoming, int comm)
    {
       this.localNode.getRoutingTable().insert(this.bootstrapNode);
        error = false;
        notify();
    }

    
    public synchronized void timeout(int comm) throws IOException
    {
        if (++this.attempts < MAX_CONNECT_ATTEMPTS)
        {
            this.server.sendMessage(this.bootstrapNode, new ConnectMessage(this.localNode.getNode()), this);
        }
        else
        {
            notify();
        }
    }
}
