/**
 * 
 */
package com.aashdit.digiverifier.login.service;

import java.security.SecureRandom;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

//import com.warrenstrange.googleauth.GoogleAuthenticator;
//import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

/**
 * Nambi
 */

@Service
public class OTPService {
    
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    // Generate a six-digit OTP
    public String generateOTP() {
//        int otp = random.nextInt(999999); // Generate a number between 0 and 999999
        int otp = 100000 + random.nextInt(900000); // Generate a number between 100000 and 999999
        return String.format("%06d", otp); // Format as a six-digit number
    }
    
    
    private final ConcurrentHashMap<String, OtpDetails> otpCache = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(5); // Example: 5 minutes

    public void saveOtp(String key, String otp) {
        otpCache.put(key, new OtpDetails(otp, System.currentTimeMillis()));
    }

    public boolean validateOtp(String key, Integer otp) {
        OtpDetails details = otpCache.get(key);
//        System.out.println("Details : "+details.toString());
//        System.out.println("OTP : "+otp);
        if (details != null && details.getOtp().equals(otp.toString())) {
            if (System.currentTimeMillis() - details.getTimestamp() < OTP_EXPIRATION_TIME) {
                otpCache.remove(key); // OTP can be used only once
//                System.out.println("OTP removed from cache");
                return true;
            }else {
//                System.out.println("OTP expired");
                otpCache.remove(key); // Optionally remove expired OTP
            }
        }
        return false;
    }
    
    public void printOtpDetails() {
        for (Entry<String, OtpDetails> entry : otpCache.entrySet()) {
            String key = entry.getKey();
            OtpDetails details = entry.getValue();
//            System.out.println("Key: " + key + ", OTP: " + details.getOtp());
        }
    }
 

    private static class OtpDetails {
        private final String otp;
        private final long timestamp;

        public OtpDetails(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }

        public String getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return "OtpDetails{" +
                   "otp='" + otp + '\'' +
                   ", timestamp=" + timestamp +
                   '}';
        }
    }


}
