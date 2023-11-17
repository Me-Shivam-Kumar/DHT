package Gem;

public class Contact implements Comparable<Contact>
{
    private final Node n;
    private long lastSeen;
    public Contact(Node n)
    {
        this.n = n;
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    public Node getNode()
    {
        return this.n;
    }

    public void setSeenNow()
    {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    public long lastSeen()
    {
        return this.lastSeen;
    }

    @Override
    public boolean equals(Object c)
    {
        if (c instanceof Contact)
        {
            return ((Contact) c).getNode().equals(this.getNode());
        }

        return false;
    }

    @Override
    public int compareTo(Contact o)
    {
        if (this.getNode().equals(o.getNode()))
        {
            return 0;
        }

        return (this.lastSeen() > o.lastSeen()) ? 1 : -1;
    }

    @Override
    public int hashCode()
    {
        return this.getNode().hashCode();
    }

}
