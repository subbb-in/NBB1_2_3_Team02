package edu.example.kotlindevelop.domain.member.controller

import edu.example.kotlindevelop.global.security.SecurityUser
import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.service.MemberService
import jakarta.mail.internet.MimeMessage
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/members")
class MemberController (
    val memberService: MemberService,
    val resourceLoader: ResourceLoader,
    val mailSender: JavaMailSender
){

    //회원가입
    @PostMapping("/register")
    fun register(
        @Validated @RequestBody request: MemberDTO.CreateRequestDto,
        bindingResult: BindingResult
    ): ResponseEntity<MemberDTO.CreateResponseDto> {
        if (bindingResult.hasErrors()) {
            val errorMessage = bindingResult.allErrors.joinToString(", ") { error ->
                when (error) {
                    is FieldError -> error.defaultMessage.toString()
                    else -> error.defaultMessage.toString()
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MemberDTO.CreateResponseDto(errorMessage))
        }

        val createResponseDto = memberService.create(request)

        return ResponseEntity.ok(createResponseDto)
    }



    //로그인
    @PostMapping("/login")
    fun login(@Validated @RequestBody request: MemberDTO.LoginRequestDto): ResponseEntity<MemberDTO.LoginResponseDto> {
        // 인증 성공
        val responseDto = memberService.checkLoginIdAndPassword(request.loginId, request.pw)

        val id = responseDto.id ?: throw IllegalArgumentException("Member ID cannot be null")
        val loginId = responseDto.loginId

        val accessToken = memberService.generateAccessToken(id, loginId)
        val refreshToken = memberService.generateRefreshToken(id, loginId)

        memberService.setRefreshToken(id, refreshToken)

        responseDto.accessToken = accessToken
        responseDto.refreshToken = refreshToken

        return ResponseEntity.ok(responseDto)
    }


    @PostMapping("/refreshAccessToken")
    fun login(@RequestBody request: MemberDTO.RefreshAccessTokenRequestDto): ResponseEntity<MemberDTO.RefreshAccessTokenResponseDto> {
        val accessToken: String = memberService.refreshAccessToken(request.refreshToken)
        val responseDto: MemberDTO.RefreshAccessTokenResponseDto =
            MemberDTO.RefreshAccessTokenResponseDto(accessToken, "새로운 AccessToken 발급")

        return ResponseEntity.ok<MemberDTO.RefreshAccessTokenResponseDto>(responseDto)
    }


    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<MemberDTO.LogoutResponseDto> {
        memberService.setRefreshToken(user.id, "null")

        return ResponseEntity.ok(MemberDTO.LogoutResponseDto("로그아웃 되었습니다"))
    }



     //나의 회원 정보 조회화기
    @GetMapping("/")
    fun getMyPage(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<MemberDTO.Response> {
        val id: Long = user.id ?: throw IllegalArgumentException("User ID cannot be null")
        return ResponseEntity.ok(memberService.read(id))
    }

    //다른 유저의 회원정보 조회하기
    @GetMapping("/{id}")
    fun read(@PathVariable id: Long): ResponseEntity<MemberDTO.Response> {
        return ResponseEntity.ok(memberService.read(id))
    }


//    //내 정보 수정하기
//    @PutMapping("/")
//    fun modify(
//        @AuthenticationPrincipal user: SecurityUser,
//        @Validated @RequestBody dto: MemberDTO.Update
//    ): ResponseEntity<MemberDTO.Update> {
//        val id: Long = user.getId()
//        dto.setId(id)
//        return ResponseEntity.ok<T>(memberService.update(dto))
//    }
//
//    //주문 삭제하기
//    @DeleteMapping("/")
//    fun delete(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<Map<String, String>> {
//        val id: Long = user.getId()
//        memberService.delete(id)
//        return ResponseEntity.ok(java.util.Map.of("result", "success"))
//    }
//
//    @PutMapping("/update-image")
//    fun modifyImage(
//        @AuthenticationPrincipal user: SecurityUser,
//        @RequestParam("mImage") mImage: MultipartFile?
//    ): ResponseEntity<ChangeImage> {
//        val dto: ChangeImage = ChangeImage()
//        dto.setId(user.getId())
//        dto.setMImage(mImage)
//        return ResponseEntity.ok<T>(memberService.changeImage(dto, mImage))
//    }

    // 이미지 서빙 엔드포인트
    @GetMapping("/upload/{filename:.+}")
    fun serveImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = resourceLoader.getResource("file:upload/$filename")
        return ResponseEntity.ok(file)
    }

    //아이디 찾기
    @GetMapping("/findId")
    fun findId(@RequestParam email: String): ResponseEntity<String> {
        val loginId: String = memberService.findByEmail(email)

        return ResponseEntity.ok(loginId)
    }

    //비밀번호 찾기
    @PostMapping("/findPW")
    fun findPw(@RequestBody request: MemberDTO.FindPWRequestDto): ResponseEntity<String> {
        val templatePassword: String = memberService.setTemplatePassword(request.loginId, request.email )

        val title = "데브코스 팀2 아이디/비밀번호 찾기 인증 이메일 입니다."
        val from = "seodo1e1205@gmail.com"
        val to: String = request.email
        val content =
            (System.getProperty("line.separator") +
                    System.getProperty("line.separator") +
                    "임시 비밀번호로 로그인 후 꼭 새로운 비밀번호로 설정해주시기 바랍니다."
                    + System.getProperty("line.separator") +
                    System.getProperty("line.separator") +
                    "임시비밀번호는 " + templatePassword + " 입니다. "
                    + System.getProperty("line.separator"))

        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val messageHelper: MimeMessageHelper = MimeMessageHelper(message, true, "UTF-8")

            messageHelper.setFrom(from)
            messageHelper.setTo(to)
            messageHelper.setSubject(title)
            messageHelper.setText(content)

            mailSender.send(message)
        } catch (e: Exception) {
            log.debug(e)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("임시 비밀번호 전송을 실패하였습니다")
        }
        return ResponseEntity.ok("임시 비밀번호를 이메일로 전송했습니다")
    }
}