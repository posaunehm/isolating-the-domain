package example.domain.model.contract;

/**
 * 深夜時給割増額
 */
public class MidnightHourlyExtraWage {
    HourlyWage value;

    public MidnightHourlyExtraWage(HourlyWage value) {
        this.value = value;
    }

    public HourlyWage value() {
        return value;
    }
}
