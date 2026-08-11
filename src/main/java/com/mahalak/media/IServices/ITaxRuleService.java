package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.TaxRuleRequest;
import com.mahalak.media.dto.response.TaxRuleResponse;

import java.util.List;

public interface ITaxRuleService {

    TaxRuleResponse addTaxRule(TaxRuleRequest request);

    TaxRuleResponse getTaxRuleById(String taxId);

    List<TaxRuleResponse> getAllTaxRules();

    TaxRuleResponse updateTaxRule(String taxId, TaxRuleRequest request);

    TaxRuleResponse deleteTaxRule(String taxId);
}
