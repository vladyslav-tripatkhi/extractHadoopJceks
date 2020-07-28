import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyStore;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;

import com.facebook.presto.jdbc.PrestoDriver;

public class extractJceks {
    public static void main(String[] args) {
        String filePath = args[0];
        char [] password = args[1].toCharArray();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            KeyStore ks = KeyStore.getInstance("JCEKS");
            ks.load(fis, password);
            SecretKey accessKey = (SecretKey) ks.getKey("fs.s3a.access.key", password);
            String accessKeyAsHex = new BigInteger(1, accessKey.getEncoded()).toString(16);

            SecretKey secretKey = (SecretKey) ks.getKey("fs.s3a.secret.key", password);
            String secretAsHex = new BigInteger(1, secretKey.getEncoded()).toString(16);

            System.out.println("[credentials]");
            System.out.println("aws_access_key_id=" + hexToAscii(accessKeyAsHex));
            System.out.println("aws_secret_access_key=" + hexToAscii(secretAsHex));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}
