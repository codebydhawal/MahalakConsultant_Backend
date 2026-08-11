package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.ITaxRuleService;
import com.mahalak.media.dto.request.TaxRuleRequest;
import com.mahalak.media.dto.response.TaxRuleResponse;
import com.mahalak.media.entity.TaxRule;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.TaxRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaxRuleServiceImpl implements ITaxRuleService {

    private final GoogleEntityManager entityManager;
    private final TaxRuleMapper taxRuleMapper;

    @Override
    public TaxRuleResponse addTaxRule(TaxRuleRequest request) {
        validateEffectiveDates(request);
        ensureNameIsAvailable(request.getName(), null);

        TaxRule taxRule = taxRuleMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        taxRule.setCreatedAt(now);
        taxRule.setUpdatedAt(now);

        entityManager.save(taxRule);
        return taxRuleMapper.toResponse(taxRule);
    }

    @Override
    public TaxRuleResponse getTaxRuleById(String taxId) {
        return taxRuleMapper.toResponse(findTaxRule(taxId));
    }

    @Override
    public List<TaxRuleResponse> getAllTaxRules() {
        return entityManager.findAll(TaxRule.class)
                .stream()
                .map(taxRuleMapper::toResponse)
                .toList();
    }

    @Override
    public TaxRuleResponse updateTaxRule(String taxId, TaxRuleRequest request) {
        validateEffectiveDates(request);

        TaxRule taxRule = findTaxRule(taxId);
        ensureNameIsAvailable(request.getName(), taxId);

        taxRuleMapper.updateEntity(request, taxRule);
        taxRule.setUpdatedAt(LocalDateTime.now());
        entityManager.update(taxRule);

        return taxRuleMapper.toResponse(taxRule);
    }

    @Override
    public TaxRuleResponse deleteTaxRule(String taxId) {
        TaxRule taxRule = findTaxRule(taxId);
        entityManager.delete(TaxRule.class, taxId);
        return taxRuleMapper.toResponse(taxRule);
    }

    private TaxRule findTaxRule(String taxId) {
        return entityManager.findById(TaxRule.class, taxId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tax rule not found with id: " + taxId));
    }

    private void ensureNameIsAvailable(String name, String excludedTaxId) {
        boolean exists = entityManager.findAll(TaxRule.class)
                .stream()
                .anyMatch(taxRule -> taxRule.getName() != null
                        && taxRule.getName().equalsIgnoreCase(name.trim())
                        && !Objects.equals(taxRule.getTaxId(), excludedTaxId));

        if (exists) {
            throw new BadRequestException("Tax rule with name '" + name + "' already exists.");
        }
    }

    private void validateEffectiveDates(TaxRuleRequest request) {
        if (request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }
    }
}
