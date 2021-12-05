package cloud.ptl.povserver.data.frame;

import lombok.Data;

import java.util.List;

@Data
public class PovFrame {
    private List<List<Cell>> rows;

    @Data
    public static class Cell {
        private String R;
        private String G;
        private String B;
    }
}
