package Gem;

import message.Receiver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import message.Message;
import message.NodeLookupMessage;
import message.NodeReplyMessage;

public class NodeLookupOperation implements Receiver
{
    private static final String UNASKED = "UnAsked";
    private static final String AWAITING = "Awaiting";
    private static final String ASKED = "Asked";
    private static final String FAILED = "Failed";
    private final Server server;
    private final Initt localNode;
    private final Map<Node, String> nodes;
    private final Map<Integer, Node> messagesTransiting;
    private final Message lookupMessage;  
    private final Comparator comparator;

    {
        messagesTransiting = new HashMap<>();
    }

    
    public NodeLookupOperation(Server server, Initt localNode, ID lookupId)
    {
        this.server = server;
        this.localNode = localNode;
        this.lookupMessage=new NodeLookupMessage(localNode.getNode(), lookupId);
        this.comparator = new KeyComparator(lookupId);
        this.nodes = new TreeMap(this.comparator);
    }

    public synchronized void execute() throws IOException, RoutingException
    {
        try
        {
           nodes.put(this.localNode.getNode(), ASKED);
            this.addNodes(this.localNode.getRoutingTable().getAllNodes());

           
            int totalTimeWaited = 0;
            int timeInterval = 10;   
            while (totalTimeWaited <2000)
            {
                if (!this.askNodesorFinish())
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
            this.localNode.getRoutingTable().setUnresponsiveContacts(this.getFailedNodes());

        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Node> getClosestNodes()
    {
        return this.closestNodes(ASKED);
    }

    
    public void addNodes(List<Node> list)
    {
        for (Node o : list)
        {
            if (!nodes.containsKey(o))
            {
                nodes.put(o, UNASKED);
            }
        }
    }

    private boolean askNodesorFinish() throws IOException
    {
        
        if (10 <= this.messagesTransiting.size())
        {
            return false;
        }

        
        List<Node> unasked = this.closestNodesNotFailed(UNASKED);

        if (unasked.isEmpty() && this.messagesTransiting.isEmpty())
        {
          
            return true;
        }

        
        for (int i = 0; (this.messagesTransiting.size() < 10) && (i < unasked.size()); i++)
        {
            Node n = (Node) unasked.get(i);

            int comm = server.sendMessage(n, lookupMessage, this);

            this.nodes.put(n, AWAITING);
            this.messagesTransiting.put(comm, n);
        }

        return false;
    }

   
    private List<Node> closestNodes(String status)
    {
        List<Node> closestNodes = new ArrayList<>(5);
        int remainingSpaces = 5;

        for (Map.Entry e : this.nodes.entrySet())
        {
            if (status.equals(e.getValue()))
            {
                closestNodes.add((Node) e.getKey());
                if (--remainingSpaces == 0)
                {
                    break;
                }
            }
        }

        return closestNodes;
    }

    
    private List<Node> closestNodesNotFailed(String status)
    {
        List<Node> closestNodes = new ArrayList<>(5);
        int remainingSpaces = 5;

        for (Map.Entry<Node, String> e : this.nodes.entrySet())
        {
            if (!FAILED.equals(e.getValue()))
            {
                if (status.equals(e.getValue()))
                {
                    
                    closestNodes.add(e.getKey());
                }

                if (--remainingSpaces == 0)
                {
                    break;
                }
            }
        }

        return closestNodes;
    }

    
    @Override
    public synchronized void receive(Message incoming, int comm) throws IOException
    {
        if (!(incoming instanceof NodeReplyMessage))
        {
            
            return;
        }
       
        NodeReplyMessage msg = (NodeReplyMessage) incoming;
        Node origin = msg.getOrigin();
        this.localNode.getRoutingTable().insert(origin);

        this.nodes.put(origin, ASKED);

        this.messagesTransiting.remove(comm);
        this.addNodes(msg.getNodes());
        this.askNodesorFinish();
    }

    
  
    public synchronized void timeout(int comm) throws IOException
    {
        Node n = this.messagesTransiting.get(comm);

        if (n == null)
        {
            return;
        }
        this.nodes.put(n, FAILED);
        this.localNode.getRoutingTable().setUnresponsiveContact(n);
        this.messagesTransiting.remove(comm);

        this.askNodesorFinish();
    }

    public List<Node> getFailedNodes()
    {
        List<Node> failedNodes = new ArrayList<>();

        for (Map.Entry<Node, String> e : this.nodes.entrySet())
        {
            if (e.getValue().equals(FAILED))
            {
                failedNodes.add(e.getKey());
            }
        }

        return failedNodes;
    }
}
