package com.financialtracker.backend.Models.DL.ServicesImpl;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService  {
	private final String SECRET_KEY="thisissecretcodeagentanirudhspeakingdot.@!#$%^&*()_++_)(*&^%$#@!1234567654321";
	
	private Key getSignedKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
	public String generateToken(String username,String role) {
		Map<String, Object> cmap=new HashMap<String, Object>();
		cmap.put("usertype", role.toUpperCase());
		return createToken(cmap, username);
	}
	private String createToken(Map<String, Object> cmap,String email) {
		return Jwts.builder()
				.claims(cmap)
				.subject(email)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis()+1000*60*60*24))
				.signWith(getSignedKey())
				.compact();
	}
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith((SecretKey)getSignedKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	private <T> T extractClaim(String token,Function<Claims, T> resolver) {
		Claims claims=extractAllClaims(token);
		return resolver.apply(claims);
	}
	
	public String extractRole(String token) {
		Claims claims=extractAllClaims(token);
		return claims.get("usertype", String.class);
	}
	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	public String extractSubjectFromToken(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	public boolean isValidToken(String token) {
		try {
			return !isTokenExpired(token);
		}
		catch (Exception e) {
			return false;
		}
	}
}
