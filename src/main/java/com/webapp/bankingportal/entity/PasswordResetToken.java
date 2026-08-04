package com.webapp.bankingportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "passwordresettoken")
@Data
@NoArgsConstructor
public class PasswordResetToken implements Serializable {
	public static final Logger log = LoggerFactory.getLogger(PasswordResetToken.class);
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordresettoken_sequence")
    @SequenceGenerator(name = "passwordresettoken_sequence", sequenceName = "passwordresettoken_sequence", allocationSize = 100)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    public PasswordResetToken(String token, User user, LocalDateTime expiryDateTime) {
		log.debug(">>> Entering PasswordResetToken(token={},user={},expiryDateTime={})", token, user, expiryDateTime);
		log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): user → {}", token, user, expiryDateTime, user);
        this.token = token;
        this.user = user;
		log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): this.token → {}", token, user, expiryDateTime, this.token);
        this.expiryDateTime = expiryDateTime;
		log.debug("<<< Exiting PasswordResetToken(token={},user={},expiryDateTime={})", token, user, expiryDateTime);
    }

    public boolean isTokenValid() {
		log.debug(">>> Entering isTokenValid()");
        return getExpiryDateTime().isAfter(LocalDateTime.now());
    }

}
