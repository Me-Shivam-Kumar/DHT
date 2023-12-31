package message;

import java.io.IOException;
import Gem.Server;
import Gem.Initt;

public class ConnectReceiver implements Receiver
{

    private final Server server;
    private final Initt localNode;

    public ConnectReceiver(Server server, Initt local)
    {
        this.server = server;
        this.localNode = local;
    }

    
    @Override
    public void receive(Message incoming, int comm) throws IOException
    {
        ConnectMessage mess = (ConnectMessage) incoming;
        
        this.localNode.getRoutingTable().insert(mess.getOrigin());

        AcknowledgeMessage msg = new AcknowledgeMessage(this.localNode.getNode());
        
        this.server.reply(mess.getOrigin(), msg, comm);
    }

    @Override
    public void timeout(int comm) throws IOException
    
    {
    	
    }
}
