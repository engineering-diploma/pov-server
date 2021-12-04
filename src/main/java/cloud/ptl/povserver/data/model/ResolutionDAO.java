package cloud.ptl.povserver.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Each video stored in system can be converted into any resolution.
 * This entity is used to store this information, so we do not have to re-convert it all around
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "resolution")
public class ResolutionDAO {
    public enum Format {
        VIDEO, FRAMES
    }

    @ManyToMany(mappedBy = "resolutions")
    List<ResourceDAO> resources;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int height;
    private int width;

    public ResolutionDAO(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
