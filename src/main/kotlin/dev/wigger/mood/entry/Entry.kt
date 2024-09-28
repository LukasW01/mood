package dev.wigger.mood.entry

import dev.wigger.mood.user.Users
import dev.wigger.mood.util.annotation.HexColor
import dev.wigger.mood.util.annotation.Mood
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.annotation.Nullable
import jakarta.json.bind.annotation.JsonbDateFormat
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "entry")
class Entry : PanacheEntityBase() {
    @Nullable
    var journal: String? = null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @NotBlank @Mood
    lateinit var mood: String

    @NotNull @JsonbDateFormat(value = "yyyy-MM-dd")
    lateinit var date: LocalDate

    @NotBlank @HexColor
    lateinit var color: String

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Users::class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    lateinit var user: Users
}
