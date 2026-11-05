package quanlythuvien;

public class MucChon {
    private final String ma;
    private final String ten;

    public MucChon(String ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }

    public String getMa() {
        return ma;
    }

    public String getTen() {
        return ten;
    }

    @Override
    public String toString() {
        return ten;
    }
}
