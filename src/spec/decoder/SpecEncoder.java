package spec.decoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;


public class SpecEncoder 
{
    public static void main (String[] args)
     throws FileNotFoundException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        JsonObject metaChunkRoot = new JsonParser().parse(
                    new String(Crypto.ReadFile("encoder/flat/meta.json"), "UTF-8")
                ).getAsJsonObject();
        String gameID = metaChunkRoot.get("gameKey").getAsJsonObject().get("gameId").toString();
        String encryptionKey = metaChunkRoot.get("encryptionKey").getAsString();
        
        int fileIndex = 1;
        File file = null;
        byte[] realKey = Crypto.decrypt(Base64.decodeBase64(encryptionKey), gameID.getBytes());
        
        do
        {
            file = new File("encoder/flat/"+fileIndex+".chunk");
            if(file.exists())
            {
                byte[] plainData = Crypto.ReadFile("encoder/flat/"+fileIndex+".chunk");
                byte[] gzipedData = Crypto.compress(plainData);
                byte[] encryptedData = Crypto.encrypt(gzipedData, realKey);
                
                Crypto.WriteFile("encoder/compressed/"+fileIndex+".chunk", encryptedData);
            }
            fileIndex++;
        } while (file != null && file.exists());
    }
}
