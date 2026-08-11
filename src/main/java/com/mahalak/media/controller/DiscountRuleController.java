package com.mahalak.media.controller;

import com.mahalak.media.IServices.IDiscountRuleService;
import com.mahalak.media.dto.request.DiscountRuleRequest;
import com.mahalak.media.dto.response.DiscountRuleResponse;
import com.mahalak.media.dto.wrapper.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/discount-rule")
@RequiredArgsConstructor
public class DiscountRuleController {

    private final IDiscountRuleService discountRuleService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<DiscountRuleResponse>> addDiscountRule(
            @Valid @RequestBody DiscountRuleRequest request) {
        DiscountRuleResponse response = discountRuleService.addDiscountRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "Discount rule added successfully.", response));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<DiscountRuleResponse>> getDiscountRuleById(
            @RequestParam String discountId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Discount rule fetched successfully.", discountRuleService.getDiscountRuleById(discountId)));
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<DiscountRuleResponse>>> getAllDiscountRules() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Discount rules fetched successfully.", discountRuleService.getAllDiscountRules()));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<DiscountRuleResponse>> updateDiscountRule(
            @RequestParam String discountId, @Valid @RequestBody DiscountRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Discount rule updated successfully.",
                discountRuleService.updateDiscountRule(discountId, request)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<DiscountRuleResponse>> deleteDiscountRule(
            @RequestParam String discountId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Discount rule deleted successfully.", discountRuleService.deleteDiscountRule(discountId)));
    }
}
