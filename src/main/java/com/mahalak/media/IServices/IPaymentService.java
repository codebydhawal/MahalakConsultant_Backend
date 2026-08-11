package com.mahalak.media.IServices;

import com.mahalak.media.dto.request.PaymentRejectionRequest;
import com.mahalak.media.dto.request.PaymentSubmissionRequest;
import com.mahalak.media.dto.response.PaymentResponse;
import com.mahalak.media.enums.PaymentMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPaymentService {

    PaymentResponse createInitialPayment(String orderId, PaymentMethod paymentMethod, Double amount);

    PaymentResponse getMyPayment(String paymentId);

    PaymentResponse submitOnlinePayment(String paymentId, PaymentSubmissionRequest request, MultipartFile screenshot);

    List<PaymentResponse> getPaymentsPendingVerification();

    PaymentResponse verifyPayment(String paymentId);

    PaymentResponse rejectPayment(String paymentId, PaymentRejectionRequest request);
}
