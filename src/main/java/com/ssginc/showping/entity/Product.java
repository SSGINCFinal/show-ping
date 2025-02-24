package com.ssginc.showping.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_no")
    private Long productNo;

    // 카테고리
    // 상품 : 카테고리는 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_no", referencedColumnName = "category_no")
    private Category category;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "product_price")
    private Long productPrice;

    @Column(name = "product_quantity")
    private Long productQuantity;

    @ColumnDefault("0")
    @Column(name = "product_sale")
    private Integer productSale;

    @Column(name = "product_img", length = 500)
    private String productImg;

    @Column(name = "product_descript", length = 500)
    private String productDescript;

    // =========== 관계 연결 ===========

    // 리뷰
    // 상품 : 리뷰는 1 : N의 관계를 가진다.
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    // 영상
    // 상품 : 영상은 1 : 1의 관계를 가진다.
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Stream stream;

}
