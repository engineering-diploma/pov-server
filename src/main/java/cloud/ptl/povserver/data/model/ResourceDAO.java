package cloud.ptl.povserver.data.model;

import cloud.ptl.povserver.data.converter.FileToStringConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;
import java.util.List;

@Data
@Entity
@Table(name = "movie")
public class ResourceDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private Long orderr;

    @Convert(converter = FileToStringConverter.class)
    private File image;
    @Convert(converter = FileToStringConverter.class)
    private File movie;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> thumbnailUrls;

    @Setter
    @Getter
    private Boolean isMovie;

    @PostPersist
    public void post() {
        if (this.orderr == null) orderr = id;
    }
}
