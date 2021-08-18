package cloud.ptl.povserver.data.model;

import cloud.ptl.povserver.data.converter.FileToStringConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;

@Data
@Entity
@Table(name = "movie")
public class ResourceDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Convert(converter = FileToStringConverter.class)
    private File image;
    @Convert(converter = FileToStringConverter.class)
    private File thumbnail;
    @Convert(converter = FileToStringConverter.class)
    private File movie;

    @Setter
    @Getter
    private Boolean isMovie;
}
