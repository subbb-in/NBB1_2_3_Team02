package edu.example.kotlindevelop.domain.member.service


import edu.example.kotlindevelop.domain.member.dto.MemberDTO
import edu.example.kotlindevelop.domain.member.dto.MemberDTO.CustomOAuth2User
import edu.example.kotlindevelop.domain.member.dto.OAuth2Response
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.exception.MemberException
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import edu.example.kotlindevelop.domain.member.util.PasswordUtil
import edu.example.kotlindevelop.global.jwt.JwtUtil
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    //사용하지 않는 ModelMapper 제거
    private val jwtUtil: JwtUtil
) {
    class CustomOAuth2UserService : DefaultOAuth2UserService() {
        override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
            val oAuth2User: OAuth2User = super.loadUser(userRequest)
            println(oAuth2User.attributes)

            val registrationId = userRequest.clientRegistration.registrationId
            val oAuth2Response: OAuth2Response = when (registrationId) {
                "naver" -> MemberDTO.NaverResponse(oAuth2User.attributes)
                else -> MemberDTO.NaverResponse(oAuth2User.attributes)
            }

            val role = "ROLE_USER"

            return CustomOAuth2User(oAuth2Response, role)
        }
    }

    @Transactional
    fun create(dto: MemberDTO.CreateRequestDto): MemberDTO.StringResponseDto {
        // 기존의 회원이 있는지 검사
        val member: Optional<Member> = memberRepository.findByLoginId(dto.loginId)
        if (member.isPresent) {
            throw MemberException.MEMBER_ALREADY_EXISTS.memberTaskException
        }

        // 비밀번호 암호화
        dto.pw = passwordEncoder.encode(dto.pw)

        // 회원 저장
        memberRepository.save(dto.toEntity())

        return MemberDTO.StringResponseDto("회원가입이 완료되었습니다")
    }


    @Transactional
    fun update(dto: MemberDTO.Update): MemberDTO.Update {
        val memberOptional: Optional<Member> = memberRepository.findById(dto.id)

        if (memberOptional.isPresent) {
            val member = memberOptional.get().apply {
                loginId = dto.loginId ?: loginId
                pw = passwordEncoder.encode(dto.pw ?: pw)
                name = dto.name ?: name
                mImage = dto.mImage ?: mImage
                email = dto.email ?: email
            }
            memberRepository.save(member)

            return MemberDTO.Update(
                dto.id,
                member.loginId,
                member.pw,
                member.name,
                member.mImage,
                member.email
            )
        } else {
            throw MemberException.MEMBER_NOT_MODIFIED.memberTaskException
        }
    }

    @Transactional
    fun delete(id: Long) {
        val memberOptional: Optional<Member> = memberRepository.findById(id)
        if (memberOptional.isPresent) {
            memberRepository.delete(memberOptional.get())
        } else {
            throw MemberException.MEMBER_NOT_REMOVED.memberTaskException
        }
    }

    fun read(id: Long): MemberDTO.Response {
        val memberOptional: Optional<Member> = memberRepository.findById(id)
        if (memberOptional.isPresent) {
            return MemberDTO.Response(memberOptional.get())
        } else {
            throw MemberException.MEMBER_NOT_REMOVED.memberTaskException
        }
    }

    fun readAll(pageable: Pageable): Page<MemberDTO.Response> {
        val members: Page<Member> = memberRepository.searchMembers(pageable)
        return members.map { MemberDTO.Response(it) }
    }

    private val uploadDir = "upload/" // 현재 디렉토리의 upload 폴더

    @Transactional
    fun changeImage(dto: MemberDTO.ChangeImage, imageFile: MultipartFile): MemberDTO.ChangeImage { // MultipartFile 추가
        val memberOptional: Optional<Member> = memberRepository.findById(dto.id)
        if (memberOptional.isPresent) {
            val member: Member = memberOptional.get()
            val fileName = saveImage(imageFile)

            member.mImage = fileName // URL 저장
            memberRepository.save(member)

            return MemberDTO.ChangeImage(dto.id, imageFile)
        } else {
            throw MemberException.MEMBER_IMAGE_NOT_MODIFIED.memberTaskException
        }
    }

    private fun saveImage(imageFile: MultipartFile): String { // MultipartFile로 변경
        try {
            val uploadPath = Paths.get(uploadDir)
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath) // 디렉토리가 존재하지 않으면 생성
            }

            // 파일 이름 생성 및 저장
            val fileName = System.currentTimeMillis().toString() + "_" + imageFile.originalFilename
            val filePath = uploadPath.resolve(fileName)
            Files.copy(imageFile.inputStream, filePath) // 실제 파일 저장

            return fileName // 저장된 파일 이름 반환
        } catch (e: IOException) {
            throw RuntimeException("파일 저장 실패", e)
        }
    }

    fun checkLoginIdAndPassword(loginId: String, pw: String): MemberDTO.LoginResponseDto {
        val opMember: Optional<Member> = memberRepository.findByLoginId(loginId)

        if (opMember.isEmpty) {
            throw MemberException.MEMBER_NOT_FOUND.memberTaskException
        }

        if (!passwordEncoder.matches(pw, opMember.get().pw)) {
            throw MemberException.MEMBER_LOGIN_DENIED.memberTaskException
        }

        return MemberDTO.LoginResponseDto(opMember.get())
    }

    fun getMemberById(id: Long): Member {
        val opMember: Optional<Member> = memberRepository.findById(id)

        if (opMember.isEmpty) {
            throw MemberException.MEMBER_NOT_FOUND.memberTaskException
        }

        return opMember.get()
    }

    fun count(): Int {
        return memberRepository.findAll().size
    }

    @Transactional
    fun setRefreshToken(id: Long, refreshToken: String?) {
        val member: Member = memberRepository.findById(id).get()
        member.updateRefreshToken(refreshToken)
    }

    fun generateAccessToken(id: Long, loginId: String): String {
        val authorities = if (loginId == "admin") {
            listOf("ROLE_ADMIN")
        } else {
            listOf("ROLE_MEMBER")
        }

        return jwtUtil.encodeAccessToken(
            1,
            mapOf(
                "id" to id.toString(),
                "loginId" to loginId,
                "authorities" to  authorities
            )
        )
    }

    fun generateRefreshToken(id: Long, loginId: String): String {
        return jwtUtil.encodeRefreshToken(
            3,
            mapOf(
                "id" to id.toString(),
                "loginId" to loginId
            )
        )
    }

    fun refreshAccessToken(refreshToken: String): String {
        //화이트리스트 처리
        val member: Member = memberRepository.findByRefreshToken(refreshToken)
            .orElseThrow { MemberException.MEMBER_LOGIN_DENIED.memberTaskException }

        //리프레시 토큰이 만료되었다면 로그아웃
        try {
            val claims: Claims = jwtUtil.decode(refreshToken) // 여기서 에러 처리가 남
        } catch (e: ExpiredJwtException) {
            // 클라이언트한테 만료되었다고 알려주기
            throw MemberException.MEMBER_REFRESHTOKEN_EXPIRED.memberTaskException
        }

        // member.id가 null인지 확인
        val memberId: Long = member.id ?: throw MemberException.MEMBER_LOGIN_DENIED.memberTaskException


        return generateAccessToken(memberId, member.loginId)
    }


    fun findByEmail(email: String): String {
        val member: Member = memberRepository.findByEmail(email)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }
        return member.loginId
    }

    @Transactional
    fun setTemplatePassword(loginId: String, email: String): String {
        val member: Member = memberRepository.findByLoginIdAndEmail(loginId, email)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }
        val templatePassword: String = PasswordUtil.generateTempPassword()
        member.pw = (passwordEncoder.encode(templatePassword))
        memberRepository.save(member)

        return templatePassword
    }
}

