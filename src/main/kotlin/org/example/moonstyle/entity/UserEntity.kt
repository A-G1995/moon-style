package org.example.moonstyle.entity

import jakarta.persistence.*
import org.example.moonstyle.DigitsToEnglishConverter
import org.springframework.data.annotation.CreatedDate
import java.util.Date


@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email"]),
        UniqueConstraint(columnNames = ["nationalNumber"])
    ]
)
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,   // ✅ important fix here
    
    @Column(nullable = false, unique = true)
    val email: String,
    
    @Column(nullable = false)
    val password: String,
    
    @Column(nullable = false)
    val fullName: String,
    
    @Column(nullable = false, length = 11)
    @Convert(converter = DigitsToEnglishConverter::class)
    val phoneNumber: String,
    
    @Column(nullable = false, unique = true,  length = 10)
    @Convert(converter = DigitsToEnglishConverter::class)
    val nationalNumber: String,
    
    @Column(nullable = false)
    val isAdmin: Boolean = false,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: Date = Date()

)