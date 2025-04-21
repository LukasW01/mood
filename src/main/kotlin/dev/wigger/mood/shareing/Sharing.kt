package dev.wigger.mood.shareing

import dev.wigger.mood.entry.Entry
import dev.wigger.mood.user.Users
import dev.wigger.mood.util.enums.Permissions
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
import org.jetbrains.annotations.NotNull
import java.time.ZonedDateTime

@Entity
@Table(name = "sharing")
class Sharing : PanacheEntityBase() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    
    @CreationTimestamp
    var createdAt: ZonedDateTime? = null

    @UpdateTimestamp
    var updatedAt: ZonedDateTime? = null

    @Transient
    var entry: List<Entry>? = null

    @NotNull @Enumerated(EnumType.ORDINAL)
    lateinit var permissions: Permissions

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Users::class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    lateinit var user: Users

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Users::class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "delegator_id")
    lateinit var delegator: Users
}
