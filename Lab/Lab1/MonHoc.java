public class MonHoc {
    private String tenMonHoc;
    private int tinChi;
    private double diem;

    public MonHoc(String tenMonHoc, int tinChi, double diem) {
        this.tenMonHoc = tenMonHoc;
        this.tinChi = tinChi;
        this.diem = diem;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public int getTinChi() {
        return tinChi;
    }

    public double getDiem() {
        return diem;
    }

    @Override
    public String toString() {
        return "Mon: " + tenMonHoc
                + ", Tin chi: " + tinChi
                + ", Diem: " + diem;
    }
}