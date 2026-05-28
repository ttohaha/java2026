package blinov_first.config;

/**
 * Upload configuration from the pre-Spring version (used {@code @MultipartConfig}).
 *
 * Replaced by {@link UploadProperties} bound from {@code application.properties}
 * and Spring Boot's auto-configured {@code MultipartResolver}.
 *
 * @deprecated Replaced by {@code UploadProperties} + Spring Boot multipart auto-config.
 */
@Deprecated
public class UploadConfig {}
