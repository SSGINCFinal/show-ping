package com.ssginc.showping.controller;

import com.ssginc.showping.dto.request.CartRequestDto;
import com.ssginc.showping.dto.response.CartDto;
import com.ssginc.showping.entity.Member;
import com.ssginc.showping.repository.MemberRepository;
import com.ssginc.showping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberRepository memberRepository;

    //특정 회원의 장바구니 조회
    @GetMapping("/{memberNo}")
    public ResponseEntity<List<CartDto>> getCartByMemberNo(@PathVariable Long memberNo) {
        List<CartDto> cartList = cartService.getCartByMemberNo(memberNo);
        return ResponseEntity.ok(cartList);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam Long memberNo, @RequestBody CartRequestDto requestDto) {
        cartService.addToCart(memberNo, requestDto);
        return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
    }

    //장바구니 상품 수량 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateCartItem(@RequestParam Long memberNo, @RequestBody CartRequestDto requestDto) {
        cartService.updateCartItem(memberNo, requestDto);
        return ResponseEntity.ok("장바구니 상품 수량이 수정되었습니다.");
    }

    //장바구니 상품 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(@RequestParam Long memberNo, @RequestParam Long productNo) {
        cartService.removeCartItem(memberNo, productNo);
        return ResponseEntity.ok("장바구니에서 상품이 삭제되었습니다.");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String username = userDetails.getUsername();
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(Map.of("memberNo", member.getMemberNo()));
    }
}
