package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.ShippingRuleRequest;
import com.mahalak.media.dto.response.ShippingRuleResponse;

import java.util.List;

public interface IShippingRuleService {

    ShippingRuleResponse addShippingRule(ShippingRuleRequest request);
    ShippingRuleResponse getShippingRuleById(String shippingId);
    List<ShippingRuleResponse> getAllShippingRules();
    ShippingRuleResponse updateShippingRule(String shippingId, ShippingRuleRequest request);
    ShippingRuleResponse deleteShippingRule(String shippingId);
}
