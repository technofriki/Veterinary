package com.mokah.veterinary.features.owners.model;

import com.mokah.veterinary.features.adresses.model.Address;
import com.mokah.veterinary.features.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "owners")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @PrePersist
    public void generateExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID();
        }
    }

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "dni", nullable = false, length = 30, unique = true)
    private String dni;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
