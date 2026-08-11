package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.DiscountRuleRequest;
import com.mahalak.media.dto.response.DiscountRuleResponse;

import java.util.List;

public interface IDiscountRuleService {

    DiscountRuleResponse addDiscountRule(DiscountRuleRequest request);
    DiscountRuleResponse getDiscountRuleById(String discountId);
    List<DiscountRuleResponse> getAllDiscountRules();
    DiscountRuleResponse updateDiscountRule(String discountId, DiscountRuleRequest request);
    DiscountRuleResponse deleteDiscountRule(String discountId);
}
