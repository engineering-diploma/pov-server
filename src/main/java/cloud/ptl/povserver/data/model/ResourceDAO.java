package cloud.ptl.povserver.data.model;

import cloud.ptl.povserver.data.converter.FileToStringConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.File;
import java.util.List;

/**
 * This is top level entity storing information about any resource in system.
 * For further development there are flags stored to point what kind resource is
 */
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
    @Convert(converter = FileToStringConverter.class)
    private File frameStream;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> thumbnailUrls;

    @Setter
    @Getter
    private Boolean isMovie;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "resource_id"),
            inverseJoinColumns = @JoinColumn(name = "resolution_id"),
            name = "resource_resolution"
    )
    private List<ResolutionDAO> resolutions;

    @Column(columnDefinition = "LONGTEXT")
    private String downloadUrl;

    @PostPersist
    public void post() {
        // this is the only simple and declarative method to create sequence of exclusive numbers
        // which will have meaning of order
        if (this.orderr == null) orderr = id;
    }
}
