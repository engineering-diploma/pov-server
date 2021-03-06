package cloud.ptl.povserver.service.metric;

import lombok.Getter;

public enum MetricKeys {
    RPM("rpm"),
    TOTAL_ROTATIONS("total_rotations"),
    LAST_MINUTE_ROTATIONS("last_minute_rotations"),
    DIODE_SWITCHES("diode_switches"),
    FRAMES_DISPLAYED("frames_displayed"),
    DATA_TRANSFERRED_TO_DISPLAY("data_transferred_to_display");

    @Getter
    private final String name;

    MetricKeys(String name) {
        this.name = name;
    }
}
