package com.webapp.bankingportal.service;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.entity.Token;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.TokenRepository;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
	private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AccountRepository accountRepository;

    @Override
    public String getUsernameFromToken(String token) throws InvalidTokenException {
		log.error(">>> Entering getUsernameFromToken(token=<redacted>)");
        String result = getClaimFromToken(token, Claims::getSubject);
		log.warn("<<< Exiting getUsernameFromToken(token=<redacted>)");
        return result;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
		log.error(">>> Entering generateToken(userDetails={})", userDetails);
        String result = doGenerateToken(userDetails,
                new Date(System.currentTimeMillis() + expiration));
		log.warn("<<< Exiting generateToken(userDetails={})", userDetails);
        return result;
    }

    @Override
    public String generateToken(UserDetails userDetails, Date expiry) {
        return doGenerateToken(userDetails, expiry);
    }
    private Key key() {
		log.error(">>> Entering key()");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key result = Keys.hmacShaKeyFor(keyBytes);
		log.error("key(): result → {}", result);
		log.warn("<<< Exiting key()");
        return result;
    }
    private String doGenerateToken(UserDetails userDetails, Date expiry) {
		log.error(">>> Entering doGenerateToken(userDetails={},expiry={})", userDetails, expiry);
        String result = Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(key(), SignatureAlgorithm.HS512).compact();
		log.warn("<<< Exiting doGenerateToken(userDetails={},expiry={})", userDetails, expiry);
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
		log.error(">>> Entering loadUserByUsername(accountNumber={})", accountNumber);
        long start = System.currentTimeMillis();
        val user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber)));
						log.error("loadUserByUsername(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));

        UserDetails result = withUsername(accountNumber).password(user.getPassword()).build();
		log.warn("<<< Exiting loadUserByUsername(accountNumber={})", accountNumber);
        return result;
    }

    @Override
    public Date getExpirationDateFromToken(String token)
            throws InvalidTokenException {
			log.error(">>> Entering getExpirationDateFromToken(token=<redacted>)");
        Date result = getClaimFromToken(token, Claims::getExpiration);
		log.warn("<<< Exiting getExpirationDateFromToken(token=<redacted>)");
        return result;
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
            throws InvalidTokenException {
			log.error(">>> Entering getClaimFromToken(token=<redacted>,claimsResolver={})", claimsResolver);
        val claims = getAllClaimsFromToken(token);
		log.error("getClaimFromToken(token=<redacted>,claimsResolver={}): claims → {}", claimsResolver, claims);
        T result = claimsResolver.apply(claims);
		log.warn("<<< Exiting getClaimFromToken(token=<redacted>,claimsResolver={})", claimsResolver);
        return result;
    }
    private JwtParser parser() {
		log.error(">>> Entering parser()");
        JwtParser result = Jwts.parserBuilder()
                .setSigningKey(key())
                .build();
		log.warn("<<< Exiting parser()");
        return result;
    }
    private Claims getAllClaimsFromToken(String token) throws InvalidTokenException {
		log.error(">>> Entering getAllClaimsFromToken(token=<redacted>)");
        try {
           // return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            Claims result = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
			log.warn("<<< Exiting getAllClaimsFromToken(token=<redacted>)");
            return result;
          //  return parser().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // Delete expired token
            invalidateToken(token);

            throw new InvalidTokenException(ApiMessages.TOKEN_EXPIRED_ERROR.getMessage());

        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_UNSUPPORTED_ERROR.getMessage());

        } catch (MalformedJwtException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_MALFORMED_ERROR.getMessage());

        } catch (SignatureException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_SIGNATURE_INVALID_ERROR.getMessage());

        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_EMPTY_ERROR.getMessage());
        }
    }

    @Override
    public void saveToken(String token) throws InvalidTokenException {
		log.error(">>> Entering saveToken(token=<redacted>)");
        long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) != null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_ALREADY_EXISTS_ERROR.getMessage());
        }
		log.error("saveToken(token)=<redacted>: find query executed in {} ms", (System.currentTimeMillis() - start));

        val account = accountRepository.findByAccountNumber(
                getUsernameFromToken(token));

        val tokenObj = new Token(
                token,
                getExpirationDateFromToken(token),
                account);

        tokenRepository.save(tokenObj);
		log.error("saveToken(token)=<redacted>: save query executed in {} ms", (System.currentTimeMillis() - start));
    log.warn("<<< Exiting saveToken(token=<redacted>)");
    }

    @Override
    public void validateToken(String token) throws InvalidTokenException {
		log.error(">>> Entering validateToken(token=<redacted>)");
        long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) == null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_NOT_FOUND_ERROR.getMessage());
        }
		log.error("validateToken(token)=<redacted>: find query executed in {} ms", (System.currentTimeMillis() - start));
    log.warn("<<< Exiting validateToken(token=<redacted>)");
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
		log.error(">>> Entering invalidateToken(token=<redacted>)");
        long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) != null) {
            tokenRepository.deleteByToken(token);
			log.error("invalidateToken(token)=<redacted>: delete query executed in {} ms", (System.currentTimeMillis() - start));
			log.error("invalidateToken(token)=<redacted>: find query executed in {} ms", (System.currentTimeMillis() - start));
    log.warn("<<< Exiting invalidateToken(token=<redacted>)");
        }
    }

}
