package swd392.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityJwtConfiguration {
    //@Value("${app.security.access-token-private-key}")
    private String privateKeyString = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDFfz7eV+Xyw3097bvESJU4hwiGxBERR/K3RgZr9uIVBuG1xGv0ZJJd1Kv0+767gvpOebUZkHT4I9ii0fNaS+Kp3qvcyU34cyBo6XjVu55vr1kAbx3bfO/p+scVSlHKIY9ZkP+gUranjSHuT2asQwpVGeaTeVODcQPqkV542Ti/plqgLXyzCXhON/k8oYAYs6yeC7yKiDS4UeOtS6VTjpDSsdZwwghNZlg3BtSXS8lho/qWZvDfIL3RC24V7ViN4BnGGIBKrAevDXp2Xxdy6t6kyLG/AEzXfS8vwC07SklU63NW7fZKLPCenYU20dE2FFWsM04BqjoUdeA5//pB7Gs1AgMBAAECggEAArrGCPKDb8fvsXlg7LLJWYokHiFMn2IQulg7DO6RGttwgZvugK5AogiQogefk726yZTnXf8KT/8+t2sjdVGaJk41GDMx4SpveCnZFXg0b/Kyc9dVT9KmCevQOAYnmFJOVQbo9nz70NftFdlLh387h16qHDvFNHAF8qvtO2hxSJhvS3NbnAKYLaSW2zAovkPP0HWQ0gLT+VLOIWej8DZ2NmAxhMgnWeM/6jU9JwnzERkUlu8vnQkFsKJzakSQUCcyR8LFoT5QADl5491LnAx5T4DWoH0Junj0FRVQpORt6IkoT/zBYLCKw+f+0aorXBfYYqA3DVTOKTC2eWNvQ0bWuQKBgQDn5nt81z/tSd2ZBlVhCIEW/xsxYoc45UyceAZrEBmR6fmDedcQksFe2TOxAQhaevHtwILfcAb9596Cb0GsqsJYg531sbVQv9ByQdlUmzi+Ccjvljnx9cWK8MyEstcNjJYVlMYKx6lp0ekZ8N+SXjs0krpEW/odJHorAtrnFTpz7QKBgQDaBX4UEhPhKQTtJjIShBsalGMsu26ivrAg1OZ7o14IT5P7Y71bKVHGU5G5iw3ZZcMquCuPVCEa4r01qcK0ImXKTJ4mfhBPkJW1OlFqXYsDvw34MMIaqeWA5gXtdgEh6knhKCdPShf7bqLQ0zGToOOyVFGU1EQ60HSznrAU7EB7aQKBgBWc6Y1tEkwvQjPs3zA39rAJ/qLPqAKpfEZo+VApI/jxcYvLYzvsMZGBdMveaMTmCH48HvZsHnqzjY1iiSBqmcpeGe64LUnlVqwPbw3vmY48ozmRKr1eRpb4clc7w8dDfwlr4DlI+oUeRzdft7aQibwQoMOWPu1QZQJ1Namr1NLRAoGBAMXRhfwgCvmZhmbG6VfzlcQgL3w+9Lul2Zlwk1BLgt53lgXbEczNSO1ylh6MFRzUQKlaQevioccLRDKUvZ1EOKe9rSGv8IzA25V8PLMN/TyA9ePkGbqnvR2fmHFFSPHE6xPoowJGcEwqroBbn/gbW35LE1q+3smeqsTvj0Tst/VBAoGBAJlJ6rppMsJ/L0I7GCe9gt3+uotzYEX6fQHdAOaJWkFeiwRCF1o3tQfl4arr7vWQ886RwiDtdf5TivHqkpoCwOuhuXwEl6Clq4VhnJJvLeC8T89HUXyzYifQ5duIYqg1yAVwxsAeHJCDvIqwGbtcb0i1P4XX9WIiLthzM2XOfE42";

    //@Value("${app.security.access-token-public-key}")
    private String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxX8+3lfl8sN9Pe27xEiVOIcIhsQREUfyt0YGa/biFQbhtcRr9GSSXdSr9Pu+u4L6Tnm1GZB0+CPYotHzWkviqd6r3MlN+HMgaOl41bueb69ZAG8d23zv6frHFUpRyiGPWZD/oFK2p40h7k9mrEMKVRnmk3lTg3ED6pFeeNk4v6ZaoC18swl4Tjf5PKGAGLOsngu8iog0uFHjrUulU46Q0rHWcMIITWZYNwbUl0vJYaP6lmbw3yC90QtuFe1YjeAZxhiASqwHrw16dl8XcurepMixvwBM130vL8AtO0pJVOtzVu32Sizwnp2FNtHRNhRVrDNOAao6FHXgOf/6QexrNQIDAQAB";

    @Bean
    public RSAKey generateRSAKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(privateKeyString);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(publicKeyString);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(generateRSAKey().toRSAPublicKey()).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefault());
        return jwtDecoder;
    }

    @Bean
    public JwtEncoder jwtEncoder() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(generateRSAKey())));
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimDelimiter(",");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
