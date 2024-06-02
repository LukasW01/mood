package dev.wigger.mood.entry

import dev.wigger.mood.user.Users
import dev.wigger.mood.util.annotation.MoodAnnotation
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import jakarta.annotation.Nullable
import jakarta.json.bind.annotation.JsonbDateFormat
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "entry")
class Entry : PanacheEntityBase() {
    @Nullable
    var journal: String? = null

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    lateinit var id: UUID

    @MoodAnnotation @NotBlank
    lateinit var mood: String

    @NotNull @JsonbDateFormat(value = "yyyy-MM-dd")
    lateinit var date: LocalDate

    @NotBlank
    lateinit var color: String

    @ManyToOne(cascade = [CascadeType.REMOVE], targetEntity = Users::class) @JoinColumn(name = "user_id")
    lateinit var user: Users
}
