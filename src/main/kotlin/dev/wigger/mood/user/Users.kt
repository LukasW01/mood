package dev.wigger.mood.user

import io.quarkus.hibernate.orm.panache.PanacheEntity
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.GenericGenerator
import java.util.*


@Entity
@Table(name = "users", indexes = [Index(name = "username_index", columnList = "username", unique = true)])
class Users : PanacheEntityBase() {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    lateinit var id: UUID
    
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
