package message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import Gem.Node;
import Gem.ID;


public class NodeLookupMessage implements Message
{

    private Node origin;
    private ID lookupId;

    public static final byte CODE = 0x05;

    
    public NodeLookupMessage(Node origin, ID lookup)
    {
        this.origin = origin;
        this.lookupId = lookup;
    }

    public NodeLookupMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        this.origin = new Node(in);
        this.lookupId = new ID(in);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        this.origin.toStream(out);
        this.lookupId.toStream(out);
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    public ID getLookupId()
    {
        return this.lookupId;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "NodeLookupMessage[origin=" + origin + ",lookup=" + lookupId + "]";
    }
}
