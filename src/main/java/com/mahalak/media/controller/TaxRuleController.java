package com.mahalak.media.controller;

import com.mahalak.media.IServices.ITaxRuleService;
import com.mahalak.media.dto.request.TaxRuleRequest;
import com.mahalak.media.dto.response.TaxRuleResponse;
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
@RequestMapping("/rest/tax-rule")
@RequiredArgsConstructor
public class TaxRuleController {

    private final ITaxRuleService taxRuleService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<TaxRuleResponse>> addTaxRule(
            @Valid @RequestBody TaxRuleRequest request) {
        TaxRuleResponse response = taxRuleService.addTaxRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                HttpStatus.CREATED.value(), "Tax rule added successfully.", response));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<TaxRuleResponse>> getTaxRuleById(
            @RequestParam String taxId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Tax rule fetched successfully.", taxRuleService.getTaxRuleById(taxId)));
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<TaxRuleResponse>>> getAllTaxRules() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Tax rules fetched successfully.", taxRuleService.getAllTaxRules()));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<TaxRuleResponse>> updateTaxRule(
            @RequestParam String taxId, @Valid @RequestBody TaxRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Tax rule updated successfully.", taxRuleService.updateTaxRule(taxId, request)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<TaxRuleResponse>> deleteTaxRule(
            @RequestParam String taxId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
                "Tax rule deleted successfully.", taxRuleService.deleteTaxRule(taxId)));
    }
}
