
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RsaKeyGenerator {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        KeyPair keyPair = generator.generateKeyPair();

        String privateKey = Base64.getMimeEncoder(64, new byte[]{'\n'})
                .encodeToString(keyPair.getPrivate().getEncoded());

        String publicKey = Base64.getMimeEncoder(64, new byte[]{'\n'})
                .encodeToString(keyPair.getPublic().getEncoded());

        Files.writeString(
                Path.of("keys/private.pem"),
                "-----BEGIN PRIVATE KEY-----\n"
                        + privateKey
                        + "\n-----END PRIVATE KEY-----\n"
        );

        Files.writeString(
                Path.of("keys/public.pem"),
                "-----BEGIN PUBLIC KEY-----\n"
                        + publicKey
                        + "\n-----END PUBLIC KEY-----\n"
        );

        System.out.println("RSA key pair generated successfully!");
    }
}
