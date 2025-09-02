package org.example.moonstyle.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.util.Date


@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    
    @Column(nullable = false, unique = true)
    val email: String,
    
    @Column(nullable = false)
    val password: String,
    
    @Column(nullable = false)
    val fullName: String,
    
    
    val phoneNumber: String,
    val nationalNumber: String,
    
    @Column(nullable = false)
    val isAdmin: Boolean = false,
    
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    var createdAt: Date = Date()

)