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
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "passwordresettoken")
@Data
@NoArgsConstructor
@Slf4j
public class PasswordResetToken implements Serializable {
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
		log.warn("PasswordResetToken(token,user,expiryDateTime)=<redacted>,{},{}: GDPR-AUDIT: Password reset operation performed for user {}", user, expiryDateTime, user);
		log.info("ENTERING: PasswordResetToken() method");
        this.token = token;
        this.user = user;
        this.expiryDateTime = expiryDateTime;
    }

    public boolean isTokenValid() {
		log.warn("ENTERING: isTokenValid() method");
        return getExpiryDateTime().isAfter(LocalDateTime.now());
    }

}
