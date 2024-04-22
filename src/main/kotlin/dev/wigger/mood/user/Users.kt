package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheEntity
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "users", indexes = [Index(name = "username_index", columnList = "username", unique = true)])
class Users : PanacheEntity() {
    @Nullable
    var firstName: String? = null

    @Nullable
    var lastName: String? = null

    @Column(unique = true) @NotBlank
    lateinit var username: String

    @NotBlank
    lateinit var mail: String

    @NotBlank
    lateinit var password: String
}
