package spec.decoder;

import com.google.gson.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;

public class SpecDecoder
{
    public static void main (String[] args)
     throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchAlgorithmException, BadPaddingException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, IOException
    {
        if (args.length != 2)
        {
            args = new String[2];
            File f = new File("decoder/compressed/meta.json");
            if(f.exists() && 1==2)
            {
                JsonObject metaChunkRoot = new JsonParser().parse(
                    new String(Crypto.ReadFile("decoder/compressed/meta.json"), "UTF-8")
                ).getAsJsonObject();
                args[0] = metaChunkRoot.get("gameKey").getAsJsonObject().get("gameId").toString();
                args[1] = metaChunkRoot.get("encryptionKey").getAsString();
            }
            else
            {
                args[0] = new String("22923174");
                args[1] = new String("WxDWXTCgXrzcWruUO5An8b8zIPP87smL");
            }
        }
        String gameID = args[0];
        String encryptionKey = args[1];
        
        byte[] realKey = Crypto.decrypt(Base64.decodeBase64(encryptionKey), gameID.getBytes());
        
        int fileIndex = 1;
        while (true)
        {
            File f = new File("decoder/compressed/"+fileIndex+".chunk");
            if(f.exists())
            {
                byte[] compressedData = Crypto.ReadFile("decoder/compressed/"+fileIndex+".chunk");
                byte[] uncompressedData = Crypto.decompress(Crypto.decrypt(compressedData, realKey));
                
                Crypto.WriteFile("decoder/plain/"+gameID+"/"+fileIndex+".chunk", uncompressedData);
            }
            else
            {
                break;
            }
            f = null;
            fileIndex++;
        }
        
        fileIndex = 1;
        while (true)
        {
            File f = new File("decoder/compressed/"+fileIndex+".keyframe");
            if(f.exists())
            {
                byte[] compressedData = Crypto.ReadFile("decoder/compressed/"+fileIndex+".keyframe");
                byte[] uncompressedData = Crypto.decompress(Crypto.decrypt(compressedData, realKey));
                
                Crypto.WriteFile("decoder/plain/"+gameID+"/"+fileIndex+".keyframe", uncompressedData);
            }
            else
            {
                break;
            }
            f = null;
            fileIndex++;
        }
    }
}
