package org.koppe.homeplanner.homeplanner_api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class PasswordEncryption {
    /**
     * Logger
     */
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(PasswordEncryption.class);

    /**
     * TODO decide on a better encoder. For dev purposes, this will suffice
     * Encoder
     */
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    /**
     * Encodes given raw password
     * 
     * @param raw Raw password to encode
     * @return Encoded password String
     */
    public static String encode(String raw) {
        return encoder.encode(raw);
    }

    /**
     * Checks if raw password matches the encoded one
     * 
     * @param raw     Raw password
     * @param encoded Encoded password
     * @return True, if password matches the encoded pw, false otherwise
     */
    public static boolean matches(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }
}
