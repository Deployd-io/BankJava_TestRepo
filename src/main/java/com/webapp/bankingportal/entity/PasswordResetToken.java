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
		log.debug(">>> Entering PasswordResetToken(token=<redacted>,user={},expiryDateTime={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", expiryDateTime);
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): user → {}", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", expiryDateTime, "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]");
        this.token = token;
        this.user = user;
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): this.token → <redacted>", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", expiryDateTime);
        this.expiryDateTime = expiryDateTime;
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): this.expiryDateTime → {}", (user == null ? "null" : "[id=" + user.getId() + ", name=" + user.getName() + ", countryCode=" + user.getCountryCode() + "]"), expiryDateTime, this.expiryDateTime);
		log.debug("PasswordResetToken(token=<redacted>,user={},expiryDateTime={}): this.user → {}", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", expiryDateTime, "[id=" + this.user.getId() + ", name=" + this.user.getName() + ", email=" + this.user.getEmail() + ", countryCode=" + this.user.getCountryCode() + ", phoneNumber=" + this.user.getPhoneNumber() + ", address=" + this.user.getAddress() + "]");
		log.debug("<<< Exiting PasswordResetToken(token=<redacted>,user={},expiryDateTime={})", "[id=" + user.getId() + ", name=" + user.getName() + ", email=" + user.getEmail() + ", countryCode=" + user.getCountryCode() + ", phoneNumber=" + user.getPhoneNumber() + ", address=" + user.getAddress() + "]", expiryDateTime);
    }

    public boolean isTokenValid() {
		log.debug(">>> Entering isTokenValid()");
        boolean result = getExpiryDateTime().isAfter(LocalDateTime.now());
		log.debug("<<< Exiting isTokenValid()");
        return result;
    }

}
