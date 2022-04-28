package com.alladeson.caurisit.security.auth.jwt;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@JsonAutoDetect
public class JwtAuthenticationResponse {

	private String token;
    private String tokenType = "Bearer";
    @Temporal(value = TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date expiryToken;

    public JwtAuthenticationResponse(String token, Date expiryToken) {
		super();
		this.token = token;
		this.expiryToken = expiryToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

	/**
	 * @return the expiryToken
	 */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
	public Date getExpiryToken() {
		return expiryToken;
	}

	/**
	 * @param expiryToken the expiryToken to set
	 */
	public void setExpiryToken(Date expiryToken) {
		this.expiryToken = expiryToken;
	}
    
    
    
}
