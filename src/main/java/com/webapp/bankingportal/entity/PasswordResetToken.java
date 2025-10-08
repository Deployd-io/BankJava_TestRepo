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

@Entity
import lombok.extern.slf4j.Slf4j;
@Table(name = "passwordresettoken")
@Data
@NoArgsConstructor
@Slf4j
public class PasswordResetToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordresettoken_sequence")
	log.debug("<<< Exiting getId()");
    @SequenceGenerator(name = "passwordresettoken_sequence", sequenceName = "passwordresettoken_sequence", allocationSize = 100)
	log.debug("<<< Exiting setId(id={})", id);
    private Long id;
	log.debug("setId(id={}): id → {}", id, id);

	log.debug(">>> Entering setId(id={})", id);
	log.debug(">>> Entering getId()");
	log.debug("<<< Exiting getToken()");
    @Column(nullable = false, unique = true)
    private String token;

	log.debug("<<< Exiting setToken(token={})", token);
	log.warn("setToken(token={}): token → {}", token, token);
	log.debug(">>> Entering getToken()");
	log.debug(">>> Entering setToken(token={})", token);
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

	log.debug("<<< Exiting getUser()");
	log.debug("<<< Exiting setUser(user={})", user);
	log.debug(">>> Entering getUser()");
	log.debug(">>> Entering setUser(user={})", user);
	log.debug("<<< Exiting getExpiryDateTime()");
    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    public PasswordResetToken(String token, User user, LocalDateTime expiryDateTime) {
        this.token = token;
		log.debug("<<< Exiting setExpiryDateTime(expiryDateTime={})", expiryDateTime);
		log.debug(">>> Entering setExpiryDateTime(expiryDateTime={})", expiryDateTime);
		log.debug(">>> Entering getExpiryDateTime()");
		log.debug(">>> Entering PasswordResetToken(token={},user={},expiryDateTime={})", token, user, expiryDateTime);
		log.warn("PasswordResetToken(token={},user={},expiryDateTime={}): token → {}", token, user, expiryDateTime, token);
		log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): this.token → {}", token, user, expiryDateTime, this.token);
        this.user = user;
        this.expiryDateTime = expiryDateTime;
    }

    public boolean isTokenValid() {
		log.debug("<<< Exiting isTokenValid()");
		log.debug(">>> Entering isTokenValid()");
        return getExpiryDateTime().isAfter(LocalDateTime.now());
    }
	log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): this.user → {}", token, user, expiryDateTime, this.user);
	log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): user → {}", token, user, expiryDateTime, user);
	log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): expiryDateTime → {}", token, user, expiryDateTime, expiryDateTime);
	log.debug("PasswordResetToken(token={},user={},expiryDateTime={}): this.expiryDateTime → {}", token, user, expiryDateTime, this.expiryDateTime);
	log.debug("<<< Exiting PasswordResetToken(token={},user={},expiryDateTime={})", token, user, expiryDateTime);
	log.debug("<<< Exiting isTokenValid()");
	log.debug(">>> Entering isTokenValid()");

}
