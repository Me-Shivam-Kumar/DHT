package message;
import java.io.DataInputStream;
import java.io.IOException;
import Gem.Server;
import Gem.Initt;
public class MessageFactory 
{
    private final Initt localNode;
  
    public MessageFactory(Initt local)
    {
        this.localNode = local;
    }

    public Message createMessage(byte code, DataInputStream in) throws IOException
    {
        switch (code)
        {
            case AcknowledgeMessage.CODE:
                return new AcknowledgeMessage(in);
            case ConnectMessage.CODE:
                return new ConnectMessage(in);
            case NodeLookupMessage.CODE:
                return new NodeLookupMessage(in);
            case NodeReplyMessage.CODE:
                return new NodeReplyMessage(in);
        }
		return null;
    }

    public Receiver createReceiver(byte code, Server server)
    {
        switch (code)
        {
            case ConnectMessage.CODE:
                return new ConnectReceiver(server, this.localNode);
            case NodeLookupMessage.CODE:
                return new NodeLookupReceiver(server, this.localNode);
            
        }
		return null;
    }
}
