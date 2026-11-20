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
	private static final Logger log = LoggerFactory.getLogger(PasswordResetToken.class);
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
		log.error(">>> Entering PasswordResetToken(token=<redacted>,user={},expiryDateTime={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime);
        this.token = token;
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): this.token → <redacted>", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime);
        this.user = user;
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): this.user → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime, (this.user == null ? "null" : "[id=" + this.user.getId() + ", name=" + this.user.getName() + ", countryCode=" + this.user.getCountryCode() + "]"));
        this.expiryDateTime = expiryDateTime;
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): this.expiryDateTime → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime, this.expiryDateTime);
		log.warn("<<< Exiting PasswordResetToken(token=<redacted>,user={},expiryDateTime={})", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime);
    }

    public boolean isTokenValid() {
		log.error(">>> Entering isTokenValid()");
        boolean result = getExpiryDateTime().isAfter(LocalDateTime.now());
		log.warn("<<< Exiting isTokenValid()");
        return result;
    }

}
