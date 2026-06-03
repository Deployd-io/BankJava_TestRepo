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
		log.debug(">>> Entering getUsernameFromToken(token={})", token);
		log.debug("<<< Exiting getUsernameFromToken(token={})", token);
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
		log.debug(">>> Entering generateToken(userDetails={})", userDetails);
        log.info("Generating token for user: " + userDetails.getUsername());
		log.debug("<<< Exiting generateToken(userDetails={})", userDetails);
        return doGenerateToken(userDetails,
                new Date(System.currentTimeMillis() + expiration));
    }

    @Override
    public String generateToken(UserDetails userDetails, Date expiry) {
        log.info("Generating token for user: " + userDetails.getUsername());
		log.debug("<<< Exiting generateToken(userDetails={},expiry={})", userDetails, expiry);
        return doGenerateToken(userDetails, expiry);
    }
    private Key key() {
		log.debug(">>> Entering key()");
        byte[] keyBytes = Decoders.BASE64.decode(secret);
		log.debug("<<< Exiting key()");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String doGenerateToken(UserDetails userDetails, Date expiry) {
		log.debug(">>> Entering doGenerateToken(userDetails={},expiry={})", userDetails, expiry);
		log.debug("<<< Exiting doGenerateToken(userDetails={},expiry={})", userDetails, expiry);
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(key(), SignatureAlgorithm.HS512).compact();
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
		log.debug(">>> Entering loadUserByUsername(accountNumber={})", accountNumber);
        val user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber)));

		log.debug("<<< Exiting loadUserByUsername(accountNumber={})", accountNumber);
        return withUsername(accountNumber).password(user.getPassword()).build();
    }

    @Override
    public Date getExpirationDateFromToken(String token)
            throws InvalidTokenException {
			log.debug(">>> Entering getExpirationDateFromToken(token={})", token);
		log.debug("<<< Exiting getExpirationDateFromToken(token={})", token);
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
            throws InvalidTokenException {
        val claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private JwtParser parser() {
		log.debug(">>> Entering parser()");
		log.debug("<<< Exiting parser()");
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build();
    }
    private Claims getAllClaimsFromToken(String token) throws InvalidTokenException {
		log.debug(">>> Entering getAllClaimsFromToken(token={})", token);
        try {
           // return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			log.debug("<<< Exiting getAllClaimsFromToken(token={})", token);
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
		log.debug(">>> Entering saveToken(token={})", token);
		long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) != null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_ALREADY_EXISTS_ERROR.getMessage());
        }
		log.info("saveToken(token)={}: find query executed in {} ms", token, (System.currentTimeMillis() - start));

        val account = accountRepository.findByAccountNumber(
                getUsernameFromToken(token));
				log.info("saveToken(token)={}: find query executed in {} ms", token, (System.currentTimeMillis() - start));

        log.info("Saving token for account: " + account.getAccountNumber());

        val tokenObj = new Token(
                token,
                getExpirationDateFromToken(token),
                account);

        tokenRepository.save(tokenObj);
		log.debug("<<< Exiting saveToken(token={})", token);
		log.info("saveToken(token)={}: save query executed in {} ms", token, (System.currentTimeMillis() - start));
    }

    @Override
    public void validateToken(String token) throws InvalidTokenException {
		log.debug(">>> Entering validateToken(token={})", token);
		long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) == null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_NOT_FOUND_ERROR.getMessage());
        }
		log.info("validateToken(token)={}: find query executed in {} ms", token, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting validateToken(token={})", token);
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
		log.debug(">>> Entering invalidateToken(token={})", token);
		long start = System.currentTimeMillis();
        if (tokenRepository.findByToken(token) != null) {
            tokenRepository.deleteByToken(token);
        }
		log.info("invalidateToken(token)={}: find query executed in {} ms", token, (System.currentTimeMillis() - start));
		log.info("invalidateToken(token)={}: delete query executed in {} ms", token, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting invalidateToken(token={})", token);
    }

}
