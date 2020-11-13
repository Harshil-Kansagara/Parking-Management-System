package com.example.duepark_consumer.Helper;

public interface GetOtpInterface {
    void onOtpReceived(String otp);
    void onOtpTimeout();
}
