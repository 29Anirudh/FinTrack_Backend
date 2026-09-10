package com.financialtracker.backend.Models.POJO;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(check = {
		@CheckConstraint(name = "CHK_STATUS",constraint = "status in ('ADMIN','USER')")
},
uniqueConstraints = {
		@UniqueConstraint(columnNames = "email",name = "UNQ_EMAIL"),
		@UniqueConstraint(columnNames = "username",name = "UNQ_USERNAME")
}

)

public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userid;
	@OneToMany(mappedBy = "user")
	private List<Account> accounts;
	@Column(length=20,unique = true)
	private String username;
	@Column(length = 20)
	private String name;
	@Column(length = 30,unique = true)
	private String email;
	@Column(length = 80)
	private String password;
	private LocalDate dateofbirth;
	@Column(length = 5)
	private String status;
	@Column(nullable = false)
	private boolean isVerified=false;
	private String otp;
	private LocalDateTime otpExpiredTime;

	@OneToMany(mappedBy = "sender",cascade = CascadeType.REMOVE)
	private List<FriendRequests> sentRequests;
	@OneToMany(mappedBy = "receiver",cascade = CascadeType.REMOVE)
	private List<FriendRequests> receivedRequests;

	@OneToMany (mappedBy ="user1", cascade = CascadeType.REMOVE)
	private List<Friendships> friendshipsInitiated;
	@OneToMany (mappedBy = "user2",cascade = CascadeType.REMOVE)
	private List<Friendships> friendshipsAccepted;

	@CreationTimestamp
	@Column(nullable = false,updatable = false)
	private LocalDateTime createdAt;
	public boolean isValidAge(LocalDate dob) {
		return dob.isBefore(LocalDate.now().minusYears(18));
	}
}
