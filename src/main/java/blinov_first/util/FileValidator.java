package blinov_first.util;

import blinov_first.config.UploadProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates uploaded files against the rules defined in
 * {@link UploadProperties}.
 *
 * PATTERN — Singleton: @Component — one instance per Spring context.
 */
@Component
public class FileValidator {

    private final UploadProperties props;

    public FileValidator(UploadProperties props) {
        this.props = props;
    }

    public boolean isValid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        if (file.getSize() > props.getMaxSizeBytes()) {
            return false;
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            return false;
        }
        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        Set<String> allowed = new HashSet<>(
                Arrays.asList(props.getAllowedExtensions().split(",")));
        return allowed.contains(ext);
    }
}
