package Gem;

public class ServerDownException extends RoutingException
{

    public ServerDownException()
    {
        super();
    }

    public ServerDownException(String message)
    {
        super(message);
    }
}
