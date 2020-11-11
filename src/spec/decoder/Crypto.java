package spec.decoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class Crypto 
{
    public static byte[] decrypt (byte[] encrypted, byte[] key) 
     throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException 
    {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "Blowfish"));

        return cipher.doFinal(encrypted);
    }
    
    public static byte[] encrypt (byte[] decryptedData_, byte[] key_)
     throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key_, "Blowfish"));

        return cipher.doFinal(decryptedData_);
    }

    public static byte[] decompress (byte[] compressed)
     throws IOException 
    {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;

        while((n = gis.read(buffer)) != -1)
        {
            baos.write(buffer, 0, n);
        }

        return baos.toByteArray();
    }
    
    public static byte[] compress (byte[] flatData_) 
     throws IOException
    {
        if (flatData_ == null || flatData_.length == 0) 
        {
            return null;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream(flatData_.length);
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(flatData_);
        gzip.close();

        byte[] compressedBytes = out.toByteArray(); 

        return out.toByteArray();
    }
    
    public static byte[] ReadFile (String filePath_) throws FileNotFoundException, IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath_));
        byte[] buffer = new byte[1024];
        int n;

        while((n = bis.read(buffer)) != -1)
        {
            baos.write(buffer, 0, n);
        }

        bis.close();
        return baos.toByteArray();
    }
    
    public static void WriteFile (String fileName_, byte[] bytes_) throws IOException 
    {
        if(fileName_.lastIndexOf("/") > 0)
        {
            new File(fileName_).getParentFile().mkdirs();
        }

        FileOutputStream fileOut = new FileOutputStream(fileName_);
        fileOut.write(bytes_);
        fileOut.close();
    }
}
