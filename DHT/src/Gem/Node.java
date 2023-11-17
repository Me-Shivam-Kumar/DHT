package Gem;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import message.Streamable;

public class Node implements Streamable{
	
	private ID nodeId;
	private InetAddress inetAddress;
	private int port;
	public Node(ID id,InetAddress ip,int port) {
		this.nodeId=id;
		this.inetAddress=ip;
		this.port=port;
		this.nodeId.toString();
	}
	public Node(DataInputStream in ) throws IOException{
		this.fromStream(in);
		this.nodeId.toString();
	}
	public void setInetAddress(InetAddress addr)
    {
        this.inetAddress = addr;
    }
	
	public ID getNodeId()
    {
        return this.nodeId;
    }
	
	public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(this.inetAddress, this.port);
    }

	@Override
	public void toStream(DataOutputStream out) throws IOException {

       
        this.nodeId.toStream(out);
        byte[] a = inetAddress.getAddress();
        if (a.length != 4)
        {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got " + a.length);
        }
        out.write(a);
        out.writeInt(port);
		
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
        this.nodeId = new ID(in);
        byte[] ip = new byte[4];
        in.readFully(ip);
        this.inetAddress = InetAddress.getByAddress(ip);

        this.port = in.readInt();
		
	}
	
	public Node getNode() {
		
		return this;
	}
	
}
