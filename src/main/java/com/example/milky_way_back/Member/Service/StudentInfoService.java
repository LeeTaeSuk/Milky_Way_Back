package com.example.milky_way_back.Member.Service;

import com.example.milky_way_back.Member.Dto.StatusResponse;
import com.example.milky_way_back.Member.Dto.StudentInformaiton;
import com.example.milky_way_back.Member.Entity.Career;
import com.example.milky_way_back.Member.Entity.Member;
import com.example.milky_way_back.Member.Entity.StudentInfo;
import com.example.milky_way_back.Member.Jwt.JwtUtils;
import com.example.milky_way_back.Member.Repository.CareerRepository;
import com.example.milky_way_back.Member.Repository.MemberRepository;
import com.example.milky_way_back.Member.Repository.StudentInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentInfoService {

    private final JwtUtils jwtUtils;
    private final StudentInfoRepository studentInfoRepository;
    private final CareerRepository careerRepository;
    private final MemberRepository memberRepository;

    // 등록
    public ResponseEntity<StatusResponse> inputStudentInfo(StudentInformaiton studentInformaitonRequest, String accessToken) {

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String memberId = userDetails.getUsername();

        Member member = memberRepository.findByMemberId(memberId).orElseThrow();

        // 이력 부분
        StudentInfo studentInfo = StudentInfo.builder()
                .studentGrade(studentInformaitonRequest.getStudentGrade())
                .studentMajor(studentInformaitonRequest.getStudentMajor())
                .studentOneLineShow(studentInformaitonRequest.getStudentOneLineShow())
                .studentLocate(studentInformaitonRequest.getStudentLocate())
                .member(member)
                .build();

        // 경력 부분
        Career career = Career.builder()
                .careerName(studentInformaitonRequest.getCareerName())
                .careerStartDay(studentInformaitonRequest.getCareerStartDay())
                .careerEndDay(studentInformaitonRequest.getCareerEndDay())
                .member(member)
                .build();

        careerRepository.save(career);
        studentInfoRepository.save(studentInfo);

        return ResponseEntity.status(HttpStatus.OK).body(new StatusResponse(HttpStatus.OK.value(), "유저 정보 저장 성공"));
    }



    // 조회
    public StudentInformaiton viewInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow();
        StudentInfo studentInfo = studentInfoRepository.findByMember(member);
        Career career = careerRepository.findByMember(member);

        return convertToResponse(studentInfo, career);
    }

    private StudentInformaiton convertToResponse(StudentInfo studentInfo, Career career) {

        StudentInformaiton response = new StudentInformaiton();
        Career careerInfo = new Career();

        // 이력 부분
        response.setStudentGrade(studentInfo.getStudentGrade());
        response.setStudentMajor(studentInfo.getStudentMajor());
        response.setStudentOneLineShow(studentInfo.getStudentOneLineShow());
        response.setStudentLocate(studentInfo.getStudentLocate());

        // 경력 부분
        response.setCareerName(careerInfo.getCareerName());
        response.setCareerStartDay(careerInfo.getCareerStartDay());
        response.setCareerEndDay(careerInfo.getCareerEndDay());

        return response;
    }

}


