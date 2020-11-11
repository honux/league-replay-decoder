package spec.decoder;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class Stream 
{
    public enum Endian
    {
        LITTLE_ENDIAN,
        BIG_ENDIAN
    };
    private byte[] data;
    private int index;
    private Endian endian;
    
    public Stream (byte[] data_)
    {
        this.data = data_;
        this.index = 0;
        this.endian = Endian.LITTLE_ENDIAN;
    }
    
    public Stream (byte[] data_, Endian endian_)
    {
        this.data = data_;
        this.index = 0;
        this.endian = endian_;
    }
    
    public boolean CanRead (int length_)
    {
        if (this.index+length_ > this.data.length)
        {
            return false;
        }
        return true;
    }
    
    public byte[] ReadData (int length_) throws Exception
    {
        if (!CanRead(length_))
        {
            throw new Exception("Stream out of bound");
        }
        
        this.index += length_;
        return Arrays.copyOfRange(this.data, this.index-length_, this.index);
    }
    
    public float ReadFloat () throws Exception
    {
        byte[] rawData = ReadData(4);
        if (this.endian == Endian.LITTLE_ENDIAN)
        {
            rawData = ReverseBytes(rawData);
        }
        
        return ByteBuffer.wrap(rawData).getFloat();
    }
    
    public int ReadInt () throws Exception
    {
        byte[] rawData = ReadData(4);
        if (this.endian == Endian.LITTLE_ENDIAN)
        {
            rawData = ReverseBytes(rawData);
        }
        
        return ByteBuffer.wrap(rawData).getInt();
    }
    
    public short ReadShort () throws Exception
    {
        byte[] rawData = ReadData(2);
        if (this.endian == Endian.LITTLE_ENDIAN)
        {
            rawData = ReverseBytes(rawData);
        }
        
        return ByteBuffer.wrap(rawData).getShort();
    }
    
    public byte ReadByte () throws Exception
    {
        byte[] rawData = ReadData(1);
        
        return rawData[0];
    }
    
    public char ReadChar () throws Exception
    {
        return (char)ReadByte();
    }
    
    private byte[] ReverseBytes (byte[] data_)
    {
        for (int index = 0; index < data_.length / 2; index++)
        {
            byte temp = data_[index];
            data_[index] = data_[data_.length - 1 - index];
            data_[data_.length - 1 - index] = temp;
        }
        
        return data_;
    }
}
