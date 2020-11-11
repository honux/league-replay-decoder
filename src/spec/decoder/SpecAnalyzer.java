package spec.decoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SpecAnalyzer 
{
    public static void main (String[] args) throws FileNotFoundException, IOException, Exception
    {
        String[] extensions = new String[2];
        extensions[0] = "chunk";
        extensions[1] = "keyframe";
        int gameID = 330437331;
        
        for (String extension : extensions)
        {
            StringBuilder SQL = new StringBuilder();
            SQL.append("INSERT INTO `data`(`gameID`, `chunkID`,`type`, `owner`, `time`, `flag`, `size`, `data`) VALUES ");
            for (int fileIndex = 1; ; fileIndex++)
            {
                File file = new File("analyzer/"+fileIndex+"."+extension);
                if(file.exists())
                {
                    byte[] nonFormatedData = Crypto.ReadFile("analyzer/"+fileIndex+"."+extension);
                    Stream byteStream = new Stream(nonFormatedData);
                    Packet lastPacket = new Packet();
                    StringBuilder HTML = new StringBuilder();
                    
                    HTML.append("<html><head><script src=\"../jquery-2.1.1.min.js\"></script><script src=\"../test.js\"></script><style>.red{color:red;} .gray{color:#777} .packet-info{width:300px;float:left} .packet-hex{width:600px;float:left} .packet-char {width:300px;float:left}</style></head><body>");
                    while (byteStream.CanRead(1))
                    {
                        Packet packet = new Packet();
                        byte flag = byteStream.ReadByte();

                        if ((flag&0x80) != 0)
                        {
                            int timestamp = byteStream.ReadByte()&0xFF;
                            packet.time = lastPacket.time + (float)timestamp/1000;
                        }
                        else
                        {
                            packet.time = byteStream.ReadFloat();
                        }

                        if ((flag&0x10) != 0)
                        {
                             packet.size = byteStream.ReadByte()&0xFF;
                        }
                        else
                        {
                            packet.size = byteStream.ReadInt()&0xFFFFFFFF;
                        }

                        if ((flag&0x40) != 0)
                        {
                            packet.type = lastPacket.type;
                        }
                        else
                        {
                            packet.type = byteStream.ReadByte();
                        }

                        if ((flag&0x20) != 0)
                        {
                            packet.owner = lastPacket.owner+byteStream.ReadByte();
                        }
                        else
                        {
                            packet.owner = byteStream.ReadInt();                        
                        }

                        packet.data = byteStream.ReadData(packet.size);

                        byte[] owner = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(packet.owner).array();
                        HTML.append("<info><div><div class=\"packet-info\">");
                        HTML.append("Time: <code>");
                            HTML.append((int)(packet.time/60));
                            HTML.append(":");
                            HTML.append((int)(packet.time-((int)(packet.time/60))*60));
                            HTML.append(":");
                            HTML.append(Math.round((packet.time-(int)packet.time)*10000));
                            HTML.append(" (");
                            HTML.append(packet.time);
                        HTML.append(")</code><br>");
                        HTML.append("Owner: <code>");
                        for (int i = 0; i < owner.length; i++)
                        {
                            HTML.append(String.format("%02X ", owner[i]));
                        }
                        HTML.append("</code><br>");
                        
                        HTML.append("Type: <code class=\"red\">");
                            HTML.append(String.format("%02X ", packet.type));
                        HTML.append("</code><br>");
                        
                        HTML.append("Flag: <code>");
                            HTML.append(String.format("%02X ", flag));
                        HTML.append("</code><br>");
                        
                        HTML.append("Size: <code>");
                            HTML.append(packet.size);
                        HTML.append("</code><br>");
                        
                        HTML.append("</div><div class=\"packet-hex gray\"><code>");
                        
                        SQL.append("(");
                        SQL.append(gameID);
                        SQL.append(",");
                        SQL.append(fileIndex);
                        SQL.append(",");
                        SQL.append(packet.type&0xFF);
                        SQL.append(",'");
                        for (int i = 0; i < owner.length; i++)
                        {
                            SQL.append(String.format("%02X ", owner[i]));
                        }
                        SQL.append("',");
                        SQL.append(packet.time);
                        SQL.append(",");
                        SQL.append(flag&0xFF);
                        SQL.append(",");
                        SQL.append(packet.size);
                        SQL.append(",'");
                        int packetIndex;
                        for (packetIndex = 0; packetIndex < packet.data.length/16; packetIndex++)
                        {
                            String str = String.format("%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X ",
                             packet.data[packetIndex*16], packet.data[packetIndex*16+1], packet.data[packetIndex*16+2], packet.data[packetIndex*16+3], packet.data[packetIndex*16+4], 
                             packet.data[packetIndex*16+5], packet.data[packetIndex*16+6], packet.data[packetIndex*16+7], packet.data[packetIndex*16+8], packet.data[packetIndex*16+9],
                             packet.data[packetIndex*16+10], packet.data[packetIndex*16+11], packet.data[packetIndex*16+12], packet.data[packetIndex*16+13], packet.data[packetIndex*16+14],
                             packet.data[packetIndex*16+15]);
                            HTML.append(str);
                            HTML.append("<br>");
                            SQL.append(str);
                        }
                        packetIndex *= 16;
                        while (packetIndex < packet.data.length)
                        {
                            String str = String.format("%02X ", packet.data[packetIndex]);
                            HTML.append(str);
                            SQL.append(str);
                            packetIndex++;   
                        }
                        SQL.append("'),");
                        HTML.append("</code></div><div class=\"packet-char gray\"><code>");
                        String replaceAll = new String(packet.data, "ASCII").replaceAll("[\\p{C}\\p{Z}]", ".");
                        for (int i = 0; i < replaceAll.length(); i+=16)
                        {
                            HTML.append(replaceAll.substring(i, Math.min(i+15, replaceAll.length())));
                            HTML.append("<br>");
                        }
                        
                        /*
                        for (int i = 0; i < packet.data.length; i++)
                        {
                            if (i%16 == 0 && i != 0)
                            {
                                HTML += "<br>";
                            }
                            HTML += String.format("%c", (char)packet.data[i]);
                        }
                        */
                        HTML.append("</code></div><br style=\"clear:both;\"></div></info><hr><br>");
                        lastPacket = packet;
                    }
                    HTML.append("</body></html>");
                    
                    Crypto.WriteFile("analyzer/"+fileIndex+"."+extension+".html", HTML.toString().getBytes());
                    
                    System.out.println("Finished analyzing the "+fileIndex+"."+extension);
                }
                else
                {
                    break;
                }
            }
            SQL.delete(SQL.length()-1, SQL.length());
            SQL.append(";");
            Crypto.WriteFile("analyzer/"+extension+"-"+gameID+".sql", SQL.toString().getBytes());
        }
    }
}
