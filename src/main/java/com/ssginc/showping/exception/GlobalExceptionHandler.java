package com.ssginc.showping.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class GlobalExceptionHandler implements ErrorController {

    // 기본 예외 처리
    // security설정 후는 모든 Exception은 error를 호출하게 함.
    // 결국 모든 error가 Exception핸들러에 걸림.
    // 내부적으로 자동으로 @RequestMapping("/error")가 호출됨.
    // --->   @ExceptionHandler(Exception.class)를 호출함.
    // --->   templates아래 error를 호출
    /*
    @ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "서버 내부 오류가 발생했습니다.");
        return "error";
    }
    */
   // @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // HTTP 상태 코드 가져오기
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String result = "error/error";
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            model.addAttribute("status", statusCode);

            // 404 에러 페이지
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("error", "페이지를 찾을 수 없습니다.");
                result = "error/404"; // error/404.html
            }

            // 403 에러 페이지
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("error", "접근 권한이 없습니다.");
                result = "error/403"; // error/403.html
            }

            // 500 에러 페이지 (기타 서버 에러)
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("error", "서버 내부 오류가 발생했습니다.");
                result = "error/error"; // error/500.html
            }
        }

        // 기타 예외는 기본 에러 페이지로 이동
        model.addAttribute("error", "알 수 없는 오류가 발생했습니다.");
        return result; // templates/error.html
    }
}
