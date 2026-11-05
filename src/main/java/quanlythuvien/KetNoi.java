package quanlythuvien;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class KetNoi {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyThuVien;"
            + "encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "123456";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static String taoMa(String bang, String cot, String tienTo, int soChuSo) throws SQLException {
        int so = 0;
        String sql = "SELECT MAX(" + cot + ") FROM " + bang;
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString(1) != null) {
                so = Integer.parseInt(rs.getString(1).trim().substring(tienTo.length()));
            }
        }
        return tienTo + String.format("%0" + soChuSo + "d", so + 1);
    }
}
