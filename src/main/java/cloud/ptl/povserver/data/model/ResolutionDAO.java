package cloud.ptl.povserver.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "resolution")
public class ResolutionDAO {
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
