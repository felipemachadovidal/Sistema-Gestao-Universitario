package br.com.unifor.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    var username: String? = null

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String? = null

    @Column(nullable = false)
    var role: String = "ADMIN"
}