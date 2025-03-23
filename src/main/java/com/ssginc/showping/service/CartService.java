package com.ssginc.showping.service;

import com.ssginc.showping.dto.request.CartRequestDto;
import com.ssginc.showping.dto.response.CartDto;
import com.ssginc.showping.entity.Cart;
import com.ssginc.showping.entity.CartId;
import com.ssginc.showping.entity.Member;
import com.ssginc.showping.entity.Product;
import com.ssginc.showping.repository.CartRepository;
import com.ssginc.showping.repository.MemberRepository;
import com.ssginc.showping.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    //회원(memberNo)의 장바구니 조회
    public List<CartDto> getCartByMemberNo(Long memberNo) {
        List<Cart> carts = cartRepository.findByMemberNo(memberNo);
        return carts.stream()
                .map(cart -> new CartDto(
                        cart.getProduct().getProductNo(),
                        cart.getProduct().getProductName(),
                        cart.getProduct().getProductPrice(),
                        cart.getCartProductQuantity(),
                        cart.getProduct().getProductImg(),
                        cart.getProduct().getProductSale(),
                        cart.getProduct().getProductPrice() - (cart.getProduct().getProductPrice() * cart.getProduct().getProductSale() / 100)
                ))
                .collect(Collectors.toList());
    }

    //장바구니에 상품 추가 (중복 상품이면 수량 증가)
    @Transactional
    public void addToCart(Long memberNo, CartRequestDto requestDTO) {
        Long productNo = requestDTO.getProductNo();
        Long quantity = requestDTO.getQuantity();

        //회원과 상품 정보 가져오기
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        //장바구니에서 해당 상품이 있는지 확인
        Optional<Cart> existingCart = cartRepository.findByMemberProductNo(memberNo, productNo);

        if (existingCart.isPresent()) {
            //이미 장바구니에 있는 경우 -> 수량 증가
            Cart cart = existingCart.get();
            cart.setCartProductQuantity(cart.getCartProductQuantity() + quantity);
            cartRepository.save(cart);
        } else {
            //장바구니에 없는 경우 -> 새로 추가
            Cart newCart = new Cart(new CartId(memberNo, productNo), member, product, quantity);
            cartRepository.save(newCart);
        }
    }

    //장바구니 수정
    @Transactional
    public void updateCartItem(Long memberNo, CartRequestDto requestDTO) {
        Long productNo = requestDTO.getProductNo();
        Long newQuantity = requestDTO.getQuantity();

        Cart cart = cartRepository.findByMemberProductNo(memberNo, productNo)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));

        if (newQuantity <= 0) {
            // 0 이하의 수량은 삭제 처리
            cartRepository.delete(cart);
        } else {
            // 수량 변경
            cart.setCartProductQuantity(newQuantity);
            cartRepository.save(cart);
        }
    }

    //장바구니 삭제
    @Transactional
    public void removeCartItem(Long memberNo, Long productNo) {
        Cart cart = cartRepository.findByMemberProductNo(memberNo, productNo)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));
        cartRepository.delete(cart);
    }
}
