package com.rest.documentManager.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter @Setter
@Table(name = "profiles")
public class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    public Profile(){
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String instancia;

    public Profile(String descricao, String instancia) {
        this.descricao = descricao;
        this.instancia = instancia;
    }
}
