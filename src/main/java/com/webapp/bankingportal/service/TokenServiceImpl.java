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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

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
		log.warn("<<< Exiting getUsernameFromToken(token=<redacted>)");
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
		log.error(">>> Entering generateToken(userDetails={})", userDetails);
        log.info("Generating token for user: " + userDetails.getUsername());
		log.warn("<<< Exiting generateToken(userDetails={})", userDetails);
        return doGenerateToken(userDetails,
                new Date(System.currentTimeMillis() + expiration));
    }

    @Override
    public String generateToken(UserDetails userDetails, Date expiry) {
        log.info("Generating token for user: " + userDetails.getUsername());
        return doGenerateToken(userDetails, expiry);
    }
    private Key key() {
		log.error(">>> Entering key()");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
		log.warn("<<< Exiting key()");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String doGenerateToken(UserDetails userDetails, Date expiry) {
		log.error(">>> Entering doGenerateToken(userDetails={},expiry={})", userDetails, expiry);
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(key(), SignatureAlgorithm.HS512).compact();
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        val user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber)));

		log.error(">>> Entering loadUserByUsername(accountNumber={})", accountNumber);
		log.info("loadUserByUsername(accountNumber)={}: find query executed in {} ms", accountNumber, (System.currentTimeMillis() - start));
        return withUsername(accountNumber).password(user.getPassword()).build();
    }

    @Override
    public Date getExpirationDateFromToken(String token)
            throws InvalidTokenException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
            throws InvalidTokenException {
        val claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private JwtParser parser() {
		log.error(">>> Entering parser()");
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build();
    }
    private Claims getAllClaimsFromToken(String token) throws InvalidTokenException {
		log.error(">>> Entering getAllClaimsFromToken(token=<redacted>)");
        try {
           // return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			log.warn("<<< Exiting getAllClaimsFromToken(token=<redacted>)");
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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
		log.info("saveToken(token)=<redacted>: find query executed in {} ms", (System.currentTimeMillis() - start));
        if (tokenRepository.findByToken(token) != null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_ALREADY_EXISTS_ERROR.getMessage());
        }

        val account = accountRepository.findByAccountNumber(
                getUsernameFromToken(token));

        log.info("Saving token for account: " + account.getAccountNumber());

        val tokenObj = new Token(
                token,
                getExpirationDateFromToken(token),
                account);

		log.info("saveToken(token)=<redacted>: save query executed in {} ms", (System.currentTimeMillis() - start));
        tokenRepository.save(tokenObj);
		log.warn("<<< Exiting saveToken(token=<redacted>)");
    }

    @Override
    public void validateToken(String token) throws InvalidTokenException {
		log.error(">>> Entering validateToken(token=<redacted>)");
		long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) == null) {
			log.info("validateToken(token)=<redacted>: find query executed in {} ms", (System.currentTimeMillis() - start));
		log.warn("<<< Exiting validateToken(token=<redacted>)");
            throw new InvalidTokenException(ApiMessages.TOKEN_NOT_FOUND_ERROR.getMessage());
        }
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
		log.error(">>> Entering invalidateToken(token=<redacted>)");
		long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) != null) {
            tokenRepository.deleteByToken(token);
        }
		log.info("invalidateToken(token)=<redacted>: find query executed in {} ms", (System.currentTimeMillis() - start));
		log.warn("<<< Exiting invalidateToken(token=<redacted>)");
    }

}
