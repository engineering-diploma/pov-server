package cloud.ptl.povserver.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.File;

/**
 * Used as part of JPA suite, to store files in db, converts File class into string representing given resource on disk
 */
@Converter
public class FileToStringConverter implements AttributeConverter<File, String> {
    /**
     * Convert into string / path
     *
     * @param file file which will be saved in db
     * @return file path
     */
    @Override
    public String convertToDatabaseColumn(File file) {
        if (file == null) return "";
        else {
            return file.getAbsolutePath();
        }
    }

    /**
     * Convert into file
     *
     * @param s string pointing to file
     * @return inflated file
     */
    @Override
    public File convertToEntityAttribute(String s) {
        if (s.isBlank()) return null;
        else {
            return new File(s);
        }
    }
}
