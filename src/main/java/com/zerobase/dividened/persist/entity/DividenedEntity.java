package com.zerobase.dividened.persist.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zerobase.dividened.model.Dividened;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "DIVIDENED")
@Getter
@ToString
@NoArgsConstructor
@Table(
    uniqueConstraints = {
        @UniqueConstraint (
            columnNames = { "companyId", "date" }
        )
    }
)
public class DividenedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime date;

    private String dividened;

    public DividenedEntity(Long companyId, Dividened dividened) {
        this.companyId = companyId;
        this.date = dividened.getDate();
        this.dividened = dividened.getDividened();
    }
}
