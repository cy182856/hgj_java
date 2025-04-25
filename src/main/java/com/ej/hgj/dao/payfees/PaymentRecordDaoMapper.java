package com.ej.hgj.dao.payfees;


import com.ej.hgj.entity.payfees.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface PaymentRecordDaoMapper {

    List<PaymentRecord> getList(PaymentRecord paymentRecord);


}
