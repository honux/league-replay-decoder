package spec.decoder;


public class Packet
{
    public int size;
    public int owner;
    public byte type;
    public float time;
    public byte[] data;
    
    
    public Packet ()
    {
        this.size = 0;
        this.time = 0;
        this.type = 0;
        this.owner = 0;
        this.data = null;
    }
}