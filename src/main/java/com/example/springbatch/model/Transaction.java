package com.example.springbatch.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author cattle -  稻草鸟人
 * @date 2020/6/3 下午2:04
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction implements Serializable {

    private static final long serialVersionUID = 5262632823464033258L;

    private String account;

    private Date timestamp;

    private BigDecimal amount;
}
