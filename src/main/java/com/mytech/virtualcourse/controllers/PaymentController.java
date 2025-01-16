package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // PayPal
    @PostMapping("/create-paypal-payment")
    public String createPaypalPayment(@RequestParam Long courseId) throws Exception {
        return paymentService.initiatePaypalPayment(courseId);
    }

    @PostMapping("/execute-paypal-payment")
    public Payment executePaypalPayment(@RequestParam String paymentId, @RequestParam String payerId) throws Exception {
        return paymentService.completePaypalPayment(paymentId, payerId);
    }

    @PostMapping("/create-paypal-payment-multiple")
    public String createPaypalPaymentMultiple(@RequestBody List<Long> courseIds) throws Exception {
        return paymentService.initiatePaypalPaymentForMultipleCourses(courseIds);
    }

    // VNPAY
    @PostMapping("/create-vnpay-payment")
    public String createVnpayPayment(@RequestParam Long courseId) throws Exception {
        return paymentService.initiateVnPayPayment(courseId);
    }

    @PostMapping("/create-vnpay-payment-multiple")
    public String createVnpayPaymentMultiple(@RequestBody List<Long> courseIds) throws Exception {
        return paymentService.initiateVnPayPaymentForMultipleCourses(courseIds);
    }

    @GetMapping("/vnpay-return")
    public void handleVnpayReturnGet(HttpServletRequest request, HttpServletResponse response) {
        String queryString = request.getQueryString(); // lấy toàn bộ query chưa decode
        Map<String, String> params = parseQueryString(queryString);
        try {
            paymentService.handleVnpayReturn(params);

            String transactionStatus = params.get("vnp_TransactionStatus");
            String txnRef = params.get("vnp_TxnRef");

            // Chuyển hướng về frontend (trang thành công hoặc thất bại)
            if ("00".equals(transactionStatus)) {
                String successUrl = "http://localhost:3000/success-vnpay?vnp_TxnRef=" + txnRef + "&vnp_TransactionStatus=" + transactionStatus;
                response.sendRedirect(successUrl);
            } else {
                String failUrl = "http://localhost:3000/fail";
                response.sendRedirect(failUrl);
            }
        } catch (Exception e) {
            try {
                String failUrl = "http://localhost:3000/fail";
                response.sendRedirect(failUrl);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    // Hàm parse query string mà không decode
    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            // Tách theo '&'
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1) {
                    String key = pair.substring(0, idx);
                    // Value giữ nguyên, không decode
                    String value = pair.substring(idx + 1);
                    params.put(key, value);
                }
            }
        }
        return params;
    }
}
