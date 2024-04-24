package dev.wigger.mood.entry

import dev.wigger.mood.user.Users
import dev.wigger.mood.util.annotation.MoodEnum
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.annotation.Nullable
import jakarta.json.bind.annotation.JsonbDateFormat
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
class Entry : PanacheEntityBase() {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    lateinit var id: UUID

    @Nullable
    var journal: String? = null

    @MoodEnum @NotBlank
    lateinit var mood: String

    @NotNull @JsonbDateFormat(value = "yyyy-MM-dd")
    lateinit var date: Date

    @NotBlank
    lateinit var color: String

    @ManyToOne(cascade = [CascadeType.REMOVE]) @JoinColumn(name = "user_id")
    lateinit var user: Users
}
