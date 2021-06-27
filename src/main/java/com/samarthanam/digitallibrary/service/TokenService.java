package com.samarthanam.digitallibrary.service;

import static com.samarthanam.digitallibrary.enums.ServiceError.TOKEN_EXPIRED;
import static com.samarthanam.digitallibrary.enums.ServiceError.TOKEN_INVALID;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samarthanam.digitallibrary.exception.TokenCreationException;
import com.samarthanam.digitallibrary.exception.TokenExpiredException;
import com.samarthanam.digitallibrary.exception.TokenTemperedException;
import com.samarthanam.digitallibrary.model.AbstractToken;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenService {

    private final String issuer;
    private final String jwtSecret;
    private final Long jwtExpTTLMilli;
    private final Gson gson;
    private final JWTVerifier jwtVerifier;

    private final Algorithm algorithm;
    private final static String PAYLOAD_KEY = "payload";

    public TokenService(@Value("${com.digilibrary.service.jwt.issuer}") final String issuer,
            @Value("${com.digilibrary.service.jwt.secret}") final String jwtSecret,
            @Value("${com.digilibrary.service.jwt.exp.milli}") final Long jwtExpTTLMilli) {
        this.issuer = issuer;
        this.jwtSecret = jwtSecret;
        this.jwtExpTTLMilli = jwtExpTTLMilli;
        this.gson = new GsonBuilder().create();
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String createJwtToken(AbstractToken token) throws TokenCreationException {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpTTLMilli))
                    .withJWTId(String.valueOf(UUID.randomUUID()))
                    .withClaim(PAYLOAD_KEY, serializePayload(token))
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
            log.error("Error while creating jwt token: {}", exception);
            throw new TokenCreationException(TOKEN_INVALID);
        }

    }

    public Object decodeJwtToken(String token, Class c) throws TokenTemperedException, TokenExpiredException {

        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            Claim claim = claims.get("payload");
            return deserializePayload(claim.asString(), c);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException exception) {
            log.error("Jwt token expired");
            throw new TokenExpiredException(TOKEN_EXPIRED);
        } catch (JWTVerificationException | IllegalArgumentException exception) {
            //Invalid signature/claims
            log.error("Invalid token, exception: ", exception);
            throw new TokenTemperedException(TOKEN_INVALID);
        }
    }

    private String serializePayload(AbstractToken token) {
        return gson.toJson(token);
    }

    private Object deserializePayload(String payload, Class c) {
        return gson.fromJson(payload, c);
    }
}
