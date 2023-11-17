package Gem;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public class Bucket 
{
    private final int depth;
    private final TreeSet<Contact> contacts;
    {
        contacts = new TreeSet<>();
    }

    public Bucket(int depth )
    {
        this.depth = depth;
       
    }
    public synchronized void insert(Contact c)
    {
        if (this.contacts.contains(c))
        {
 
            Contact tmp = this.removeFromContacts(c.getNode());
            tmp.setSeenNow();
            this.contacts.add(tmp);
        }
        else
        {
            if (contacts.size() >= 5 )
            {
                //Bucket Full
            }
            else
            {
                this.contacts.add(c);
            }
        }
    }

    public synchronized void insert(Node n)
    {
        this.insert(new Contact(n));
    }

   
    public synchronized boolean containsContact(Contact c)
    {
        return this.contacts.contains(c);
    }

   
    public synchronized boolean containsNode(Node n)
    {
        return this.containsContact(new Contact(n));
    }

  
    public synchronized boolean removeContact(Contact c)
    {
        
        if (!this.contacts.contains(c))
        {
            return false;
        }
        this.contacts.remove(c);
        return true;
    }

    private synchronized Contact getFromContacts(Node n)
    {
        for (Contact c : this.contacts)
        {
            if (c.getNode().equals(n))
            {
                return c;
            }
        }
        throw new NoSuchElementException("Contact does not exist in the contacts list.");
    }

    private synchronized Contact removeFromContacts(Node n)
    {
        for (Contact c : this.contacts)
        {
            if (c.getNode().equals(n))
            {
                this.contacts.remove(c);
                return c;
            }
        }

        throw new NoSuchElementException("Node does not exist");
    }

   
    public synchronized boolean removeNode(Node n)
    {
        return this.removeContact(new Contact(n));
    }

    
    public synchronized int numContacts()
    {
        return this.contacts.size();
    }

    
    public synchronized int getDepth()
    {
        return this.depth;
    }

    
    public synchronized List<Contact> getContacts()
    {
        final ArrayList<Contact> ret = new ArrayList<>();

        
        if (this.contacts.isEmpty())
        {
            return ret;
        }

        for (Contact c : this.contacts)
        {
            ret.add(c);
        }

        return ret;
    }

  

    @Override
    public synchronized String toString()
    {
        StringBuilder sb = new StringBuilder("Bucket at depth: ");
        sb.append(this.depth);
        sb.append("\n Nodes: \n");
        for (Contact n : this.contacts)
        {
            sb.append("Node: ");
            sb.append(n.getNode().getNodeId().toString());
            sb.append(")");
            sb.append("\n");
        }

        return sb.toString();
    }
}
