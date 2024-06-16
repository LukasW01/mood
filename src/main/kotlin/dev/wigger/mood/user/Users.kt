package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import io.quarkus.security.jpa.Password
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users", indexes = [Index(name = "mail_index", columnList = "mail", unique = true)])
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
    var resetToken: UUID? = null
    var sharingToken: UUID? = null

    @NotBlank @Email
    lateinit var mail: String

    @NotBlank @Password
    lateinit var password: String

    @NotNull
    lateinit var verifyToken: UUID
    
    @CreationTimestamp
    lateinit var dateJoined: LocalDateTime
}
