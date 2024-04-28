package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users", indexes = [Index(name = "username_index", columnList = "username", unique = true), Index(name = "mail_index",
    columnList = "mail", unique = true)])
class Users : PanacheEntityBase() {
    @Nullable
    var firstName: String? = null

    @Nullable
    var lastName: String? = null
    
    @NotNull
    var isVerified: Boolean = false

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(unique = true) @NotBlank
    lateinit var username: String

    @NotBlank
    lateinit var mail: String

    @NotBlank
    lateinit var password: String

    @NotNull
    lateinit var token: UUID
    
    @NotNull
    lateinit var dateJoined: LocalDateTime
    
    var resetToken: UUID? = null
}
