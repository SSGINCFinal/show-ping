package com.ssginc.showping.controller;

import com.ssginc.showping.dto.request.CartRequestDto;
import com.ssginc.showping.dto.response.CartDto;
import com.ssginc.showping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

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
}
