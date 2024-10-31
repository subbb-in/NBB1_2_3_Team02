package edu.example.kotlindevelop.domain.member.dto

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.kotlindevelop.domain.member.entity.Member
import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

class MemberDTO {

    data class CreateRequestDto(
        @field:NotBlank(message = "로그인 ID는 필수 입력 값 입니다")
        var loginId: String,
        @field:NotBlank(message = "이메일은 필수 입력 값 입니다")
        var email: String,
        @field:NotBlank(message = "비밀번호는 필수 입력 값 입니다")
        var pw: String,
        @field:NotBlank(message = "닉네임은 필수 입력 값 입니다")
        var name: String,
        @JsonProperty("mImage")
        var mImage: String? = null
    ) {
        fun toEntity(): Member {
            // Member 객체를 초기화
            return Member(
                loginId = loginId,
                email = email,
                pw = pw,
                name = name,
                mImage = mImage
            )
        }
    }


    data class StringResponseDto( var message: String)

    data class Update (
        var id: Long,
        var loginId: String? = null,
        var pw: String? = null,
        var name: String? = null,
        var mImage: String? = null,
        var email: String? = null
    )


    data class LoginRequestDto (
        @field:NotBlank(message = "로그인 ID를 입력해주세요")
        var loginId: String,
        @NotBlank(message = "비밀번호를 입력해주세요")
        var pw: String
    )


    data class LoginResponseDto(
        var id: Long,
        var loginId: String,
        var name: String,
        var mImage: String,
        var accessToken: String? = null,
        var refreshToken: String? = null
    ) {
        constructor(member: Member) : this(
            // id는 AutoIncrement 로 생성하기 때문에 Null허용 객체 이지만, Response 과정에서는 null일 가능성이 없기 때문에 대입 값으로 0 설정
            id = member.id?:0,
            loginId = member.loginId,
            name = member.name,
            mImage = member.mImage ?: "default_image.png"
        )

    }


    data class RefreshAccessTokenRequestDto ( var refreshToken: String )
    data class RefreshAccessTokenResponseDto( var accessToken: String, var message: String)

    data class Delete (
        var loginId: String,
        var pw: String
    )

    data class Response(
        var id: Long,
        var loginId: String,
        var email: String,
        var pw: String,
        var name: String,
        var mImage: String,
        var createdAt: LocalDateTime,
        var modifiedAt: LocalDateTime
    ) {
        constructor(member: Member) : this(
            id = member.id ?: throw IllegalArgumentException("Member ID cannot be null"),
            loginId = member.loginId,
            pw = member.pw,
            name = member.name,
            mImage = member.mImage ?: "default_image.png",
            createdAt = member.createdAt ?: LocalDateTime.now(),
            modifiedAt = member.modifiedAt ?: LocalDateTime.now(),
            email = member.email
        )
    }

    data class ChangeImage (
        var id: Long,
        var mImage: MultipartFile
    )

    data class LogoutResponseDto ( var message: String)


    data class FindPWRequestDto (
         var loginId: String,
         var email: String
    )
}
//    @Data
        //    @AllArgsConstructor
        //    public static class ProductGet {
        //         Long id;
        //         List<Product> productList = new ArrayList<>();
        //    }
        //
        //    @Data
        //    @AllArgsConstructor
        //    public static class OrdersGet {
        //         List<Orders> ordersList = new ArrayList<>();
        //    }
