package Gem;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import message.Streamable;

public class ID implements Streamable {
 private final static int ID_LENGTH=160;
 private byte[] keyBytes;
 public ID(String data)
 {
     keyBytes = data.getBytes();
     if (keyBytes.length != ID_LENGTH / 8)
     {
         throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long.");
     }
 }

 public ID()
 {
     keyBytes = new byte[ID_LENGTH / 8];
     new Random().nextBytes(keyBytes);
 }
 
 public ID(byte[] bytes)
 {
     if (bytes.length != ID_LENGTH / 8)
     {
         throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long. Data Given: '" + new String(bytes) + "'");
     }
     this.keyBytes = bytes;
 }

 
 public ID(DataInputStream in) throws IOException
 {
     this.fromStream(in);
 }

 public byte[] getBytes()
 {
     return this.keyBytes;
 }

 public BigInteger getInt()
 {
     return new BigInteger(1, this.getBytes());
 }

 
 @Override
 public boolean equals(Object o)
 {
     if (o instanceof ID)
     {
         ID nid = (ID) o;
         return this.hashCode() == nid.hashCode();
     }
     return false;
 }

 @Override
 public int hashCode()
 {
     int hash = 7;
     hash = 83 * hash + Arrays.hashCode(this.keyBytes);
     return hash;
 }

 public ID xor(ID nid)
 {
     byte[] result = new byte[ID_LENGTH / 8];
     byte[] nidBytes = nid.getBytes();

     for (int i = 0; i < ID_LENGTH / 8; i++)
     {
         result[i] = (byte) (this.keyBytes[i] ^ nidBytes[i]);
     }

     ID resNid = new ID(result);

     return resNid;
 }



 public int getFirstSetBitIndex()
 {
     int prefixLength = 0;

     for (byte b : this.keyBytes)
     {
         if (b == 0)
         {
             prefixLength += 8;
         }
         else
         {
             int count = 0;
             for (int i = 7; i >= 0; i--)
             {
                 boolean a = (b & (1 << i)) == 0;
                 if (a)
                 {
                     count++;
                 }
                 else
                 {
                     break;   
                 }
             }
             prefixLength += count;
             break;
         }
     }
     return prefixLength;
 }

 
 public int getDistance(ID to)
 {
     
     return ID_LENGTH - this.xor(to).getFirstSetBitIndex();
 }

 @Override
 public void toStream(DataOutputStream out) throws IOException
 {
    
     out.write(this.getBytes());
 }

 @Override
 public final void fromStream(DataInputStream in) throws IOException
 {
     byte[] input = new byte[ID_LENGTH / 8];
     in.readFully(input);
     this.keyBytes = input;
 }

 public String hexRepresentation()
 {

     BigInteger bi = new BigInteger(1, this.keyBytes);
     return String.format("%0" + (this.keyBytes.length << 1) + "X", bi);
 }

 @Override
 public String toString()
 {
     return this.hexRepresentation();
 }
 public static int getIDLength() {
	 return ID_LENGTH;
 }

}


