package kotlindevelop.member.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(
    AuditingEntityListener::class
)
data class Member @Builder constructor(
    @field:Column(unique = true) private var loginId: String,
    private var pw: String,
    private var name: String,
    private var mImage: String,
    private var email: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private val id: Long? = null

    @Column(columnDefinition = "TEXT")
    private var refreshToken: String? = null

    @CreatedDate
    private val createdAt: LocalDateTime? = null

    @LastModifiedDate
    private val modifiedAt: LocalDateTime? = null

    @OneToMany(mappedBy = "maker", cascade = [CascadeType.ALL], orphanRemoval = true)
    @ToString.Exclude
    var productList: List<Product> = ArrayList<Product>()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    @ToString.Exclude
    var ordersList: List<Orders> = ArrayList<Orders>()


    fun changeLoginId(loginId: String) {
        this.loginId = loginId
    }


    fun changePw(pw: String) {
        this.pw = pw
    }

    fun changeName(name: String) {
        this.name = name
    }


    fun changeEmail(email: String) { // 잠시 수정
        this.email = email
    }


    fun changeMImage(mImage: String) {
        this.mImage = mImage
    }

    fun updateRefreshToken(refreshToken: String?) {
        this.refreshToken = refreshToken
    }
}