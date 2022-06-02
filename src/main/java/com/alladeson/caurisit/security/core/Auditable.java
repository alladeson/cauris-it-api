package com.alladeson.caurisit.security.core;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedBy
    @Column(updatable = false)
    protected String/*Long*/ createdBy;

    @CreatedDate
    @Column(updatable = false, columnDefinition = "DATETIME(6)")
    protected LocalDateTime createdAt;

    @LastModifiedBy
    @Column()
    protected String/*Long*/ lastModifiedBy;

    @LastModifiedDate
    @Column(columnDefinition = "DATETIME(6)")
    protected LocalDateTime/*Instant*/ lastModifiedAt;

    public String/*Long*/ getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String/*Long*/ createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String/*Long*/ getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String/*Long*/ lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime/*Instant*/ getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime/*Instant*/ lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}
