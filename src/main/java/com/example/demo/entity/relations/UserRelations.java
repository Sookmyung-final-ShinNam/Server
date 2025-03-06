package com.example.demo.entity.relations;

import com.example.demo.entity.BaseEntity;
import com.example.demo.entity.base.Token;
import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public class UserRelations extends BaseEntity {

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Token token;

}