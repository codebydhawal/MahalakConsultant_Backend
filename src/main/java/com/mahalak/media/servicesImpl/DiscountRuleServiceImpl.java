package com.mahalak.media.servicesImpl;

import com.mahalak.media.IServices.IDiscountRuleService;
import com.mahalak.media.dto.request.DiscountRuleRequest;
import com.mahalak.media.dto.response.DiscountRuleResponse;
import com.mahalak.media.entity.DiscountRule;
import com.mahalak.media.exception.BadRequestException;
import com.mahalak.media.exception.ResourceNotFoundException;
import com.mahalak.media.framework.GoogleEntityManager;
import com.mahalak.media.mapper.DiscountRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DiscountRuleServiceImpl implements IDiscountRuleService {

    private final GoogleEntityManager entityManager;
    private final DiscountRuleMapper discountRuleMapper;

    @Override
    public DiscountRuleResponse addDiscountRule(DiscountRuleRequest request) {
        validateEffectiveDates(request);
        ensureNameIsAvailable(request.getName(), null);

        DiscountRule discountRule = discountRuleMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        discountRule.setCreatedAt(now);
        discountRule.setUpdatedAt(now);
        entityManager.save(discountRule);

        return discountRuleMapper.toResponse(discountRule);
    }

    @Override
    public DiscountRuleResponse getDiscountRuleById(String discountId) {
        return discountRuleMapper.toResponse(findDiscountRule(discountId));
    }

    @Override
    public List<DiscountRuleResponse> getAllDiscountRules() {
        return entityManager.findAll(DiscountRule.class).stream()
                .map(discountRuleMapper::toResponse)
                .toList();
    }

    @Override
    public DiscountRuleResponse updateDiscountRule(String discountId, DiscountRuleRequest request) {
        validateEffectiveDates(request);
        DiscountRule discountRule = findDiscountRule(discountId);
        ensureNameIsAvailable(request.getName(), discountId);

        discountRuleMapper.updateEntity(request, discountRule);
        discountRule.setUpdatedAt(LocalDateTime.now());
        entityManager.update(discountRule);

        return discountRuleMapper.toResponse(discountRule);
    }

    @Override
    public DiscountRuleResponse deleteDiscountRule(String discountId) {
        DiscountRule discountRule = findDiscountRule(discountId);
        entityManager.delete(DiscountRule.class, discountId);
        return discountRuleMapper.toResponse(discountRule);
    }

    private DiscountRule findDiscountRule(String discountId) {
        return entityManager.findById(DiscountRule.class, discountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Discount rule not found with id: " + discountId));
    }

    private void ensureNameIsAvailable(String name, String excludedDiscountId) {
        boolean exists = entityManager.findAll(DiscountRule.class).stream()
                .anyMatch(discountRule -> discountRule.getName() != null
                        && discountRule.getName().equalsIgnoreCase(name.trim())
                        && !Objects.equals(discountRule.getDiscountId(), excludedDiscountId));
        if (exists) {
            throw new BadRequestException("Discount rule with name '" + name + "' already exists.");
        }
    }

    private void validateEffectiveDates(DiscountRuleRequest request) {
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date.");
        }
    }
}
