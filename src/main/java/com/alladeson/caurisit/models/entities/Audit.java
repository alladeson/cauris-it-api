package com.alladeson.caurisit.models.entities;

//import  com.alladeson.caurisit.security.core.Auditable;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Audit/* extends Auditable */{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(columnDefinition = "DATETIME(6)")
    private LocalDateTime dateHeure;
    private Operation operation;
    private String code;
    private String description;
    @Lob
    private String valeurAvant;
    @Lob
    private String valeurApres;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValeurAvant() {
        return valeurAvant;
    }

    public void setValeurAvant(String valeurAvant) {
        this.valeurAvant = valeurAvant;
    }

    public String getValeurApres() {
        return valeurApres;
    }

    public void setValeurApres(String valeurApres) {
        this.valeurApres = valeurApres;
    }
}
