
package ro.msg.learning.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

@Configuration
public class JwtConfig {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtConfig(
            @Value("${jwt.private-key-path}") String privateKeyPath,
            @Value("${jwt.public-key-path}") String publicKeyPath
    ) throws Exception {

        String privatePem = readKey(privateKeyPath, "PRIVATE");
        String publicPem = readKey(publicKeyPath, "PUBLIC");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(
                new PKCS8EncodedKeySpec(
                        Base64.getDecoder().decode(privatePem)
                )
        );

        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(
                new X509EncodedKeySpec(
                        Base64.getDecoder().decode(publicPem)
                )
        );
    }

    private String readKey(String keyPath, String keyType)
            throws IOException {

        String pem = Files.readString(
                Path.of(keyPath),
                StandardCharsets.UTF_8
        );

        return pem.replace("-----BEGIN " + keyType + " KEY-----", "")
                .replace("-----END " + keyType + " KEY-----", "")
                .replaceAll("\\s", "");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        var jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public RSAPublicKey rsaPublicKey() {
        return publicKey;
    }
}
