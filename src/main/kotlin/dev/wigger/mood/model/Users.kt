package dev.wigger.mood.model

import io.quarkus.hibernate.orm.panache.PanacheEntity
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "users", indexes = [Index(name = "username_index", columnList = "username", unique = true)])
class Users: PanacheEntity() {
    @Column(unique = true) @NotBlank @Max(50)
    lateinit var username: String
    @NotBlank @Max(256)
    lateinit var mail: String
    @Nullable
    var firstName: String? = null
    @Nullable
    var lastName: String? = null
    @NotBlank @Max(128)
    lateinit var password: String
}
