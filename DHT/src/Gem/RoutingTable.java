package Gem;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class RoutingTable 
{

    private final Node localNode;  
    private transient Bucket[] buckets;

    

    public RoutingTable(Node localNode )
    {
        this.localNode = localNode;
        this.initialize();
        this.insert(localNode);
    }

    public final void initialize()
    {
        this.buckets = new Bucket[ID.getIDLength()];
        for (int i = 0; i < ID.getIDLength(); i++)
        {
            buckets[i] = new Bucket(i);
        }
    }

    public synchronized final void insert(Contact c)
    {
        this.buckets[this.getBucketId(c.getNode().getNodeId())].insert(c);
    }

    
    public synchronized final void insert(Node n)
    {
        this.buckets[this.getBucketId(n.getNodeId())].insert(n);
    }

    
   
    public final int getBucketId(ID nid)
    {
        int bId = this.localNode.getNodeId().getDistance(nid) - 1;

        return bId < 0 ? 0 : bId;
    }

    
    
    public synchronized final List<Node> findClosest(ID target, int numNodesRequired)
    {
        TreeSet<Node> sortedSet = new TreeSet<>(new KeyComparator(target));
        sortedSet.addAll(this.getAllNodes());
        List<Node> closest = new ArrayList<>(numNodesRequired);
        int count = 0;
        for (Node n : sortedSet)
        {
            closest.add(n);
            if (++count == numNodesRequired)
            {
                break;
            }
        }
        return closest;
    }

    
    public synchronized final List<Node> getAllNodes()
    {
        List<Node> nodes = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            for (Contact c : b.getContacts())
            {
                nodes.add(c.getNode());
            }
        }

        return nodes;
    }

    
    public final List<Contact> getAllContacts()
    {
        List<Contact> contacts = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            contacts.addAll(b.getContacts());
        }

        return contacts;
    }

   
   
    public final Bucket[] getBuckets()
    {
        return this.buckets;
    }

   
    public final void setBuckets(Bucket[] buckets)
    {
        this.buckets = buckets;
    }

   
    public void setUnresponsiveContacts(List<Node> contacts)
    {
        if (contacts.isEmpty())
        {
            return;
        }
        for (Node n : contacts)
        {
            this.setUnresponsiveContact(n);
        }
    }

    
    
    public synchronized void setUnresponsiveContact(Node n)
    {
        int bucketId = this.getBucketId(n.getNodeId());
        this.buckets[bucketId].removeNode(n);
    }

    @Override
    public synchronized final String toString()
    {
        StringBuilder sb = new StringBuilder("\nPrinting Routing Table Started ///// \n");
        int totalContacts = 0;
        for (Bucket b : this.buckets)
        {
            if (b.numContacts() > 0)
            {
                totalContacts += b.numContacts();
                sb.append("# nodes in Bucket with depth ");
                sb.append(b.getDepth());
                sb.append(": ");
                sb.append(b.numContacts());
                sb.append("\n");
                sb.append(b.toString());
                sb.append("\n");
            }
        }

        sb.append("\nTotal Contacts: ");
        sb.append(totalContacts);
        sb.append("\n\n");

        sb.append("Printing Routing Table Ended///// ");

        return sb.toString();
    }

	

	

}
