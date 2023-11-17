package message;

import java.io.IOException;
import java.util.List;

import Gem.Server;
import Gem.Initt;
import Gem.Node;


public class NodeLookupReceiver implements Receiver 
{

    private final Server server;
    private final Initt localNode;
   

    public NodeLookupReceiver(Server server, Initt local)
    {
        this.server = server;
        this.localNode = local; 
    }

    public void receive(Message incoming, int comm) throws IOException
    {
        NodeLookupMessage msg = (NodeLookupMessage) incoming;
        Node origin = msg.getOrigin();
        this.localNode.getRoutingTable().insert(origin);
        List<Node> nodes = this.localNode.getRoutingTable().findClosest(msg.getLookupId(), 5);
        Message reply = new NodeReplyMessage(this.localNode.getNode(), nodes);
        if (this.server.isRunning())
        {
            this.server.reply(origin, reply, comm);
        }
    }

	@Override
	public void timeout(int conversationId) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
