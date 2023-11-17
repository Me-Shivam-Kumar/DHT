package Gem;

import java.io.IOException;
import java.net.InetAddress;
import message.MessageFactory;

public class Initt {
	private String ownerId;
	private RoutingTable routingTable;
	private final transient Node localNode;
	private final transient Server server;
	private final int udpPort;
    private final transient MessageFactory messageFactory; 
    
	public Initt(String ownerId, Node localNode, int udpPort,  RoutingTable routingTable/* ,KadConfiguration config*/) throws IOException
    {
        this.ownerId = ownerId;
        this.udpPort = udpPort;
        this.localNode = localNode;
        this.messageFactory = new MessageFactory(this);
        this.routingTable = routingTable;
        this.server = new Server(udpPort, this.localNode,this.messageFactory);
        
    }
   

    public Initt(String ownerId, Node node, int udpPort) throws IOException
    {
        this(
                ownerId,
                node,
                udpPort,
                new RoutingTable(node)
        );
    }

    public Initt(String ownerId, ID defaultId, int udpPort) throws IOException
    {
        this(
                ownerId,
                new Node(defaultId, InetAddress.getLocalHost(), udpPort),
                udpPort
        );
    }

    public synchronized final void bootstrap(Node n) throws IOException, RoutingException
    {
        System.nanoTime();
        ConnectOperation op = new ConnectOperation(this.server, this, n);
        op.execute();
        System.nanoTime();
        
    }

    public String getOwnerId()
    {
        return this.ownerId;
    }
    public Node getNode()
    {
        return this.localNode;
    }

   
    public Server getServer()
    {
        return this.server;
    }
    
    public int getPort()
    {
        return this.udpPort;
    }

    public void shutdown(final boolean saveState) throws IOException
    {
         
        this.server.shutdown();

    }

    public RoutingTable getRoutingTable()
    {
        return this.routingTable;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\n\nPrinting Kad State for instance with owner: ");
        sb.append(this.ownerId);
        sb.append("\n\n");

        sb.append("\n");
        sb.append("Local Node");
        sb.append(this.localNode);
        sb.append("\n");

        sb.append("\n");
        sb.append("Routing Table: ");
        sb.append(this.getRoutingTable());
        sb.append("\n");

        sb.append("\n");
        sb.append("DHT: ");
        
        sb.append("\n");

        sb.append("\n\n\n");

        return sb.toString();
    }
}


