package dev.wigger.mood.shareing

import dev.wigger.mood.user.Users
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import org.hibernate.annotations.UpdateTimestamp
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

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Users::class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    lateinit var user: Users

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Users::class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "delegator_id")
    lateinit var delegator: Users
}
