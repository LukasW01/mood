package dev.wigger.mood.model

import dev.wigger.mood.util.annotation.MoodEnum
import io.quarkus.hibernate.orm.panache.PanacheEntity
import jakarta.annotation.Nullable
import jakarta.json.bind.annotation.JsonbDateFormat
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

@Entity
@Table(name = "entry")
class Entry: PanacheEntity() {
    @MoodEnum @NotBlank
    lateinit var mood: String
    @Nullable
    var journal: String? = null
    @NotNull @JsonbDateFormat(value = "yyyy-MM-dd")
    lateinit var date: Date
    @NotBlank 
    lateinit var color: String
    @ManyToOne(cascade = [CascadeType.REMOVE]) @JoinColumn(name = "user_id")
    lateinit var user: Users
}


