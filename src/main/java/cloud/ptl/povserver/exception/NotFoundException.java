package cloud.ptl.povserver.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Thrown whenever entity cannot be found in database
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class NotFoundException extends Exception {
    public String message;
}
