package com.example.springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Transaction {

    private String account;

    private Date timestamp;

    private BigDecimal amount;
}
