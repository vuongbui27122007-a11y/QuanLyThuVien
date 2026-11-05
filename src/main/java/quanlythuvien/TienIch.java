package quanlythuvien;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class TienIch {
    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DecimalFormat DINH_DANG_TIEN = new DecimalFormat("#,###");

    public static String ngayRaChuoi(LocalDate ngay) {
        if (ngay == null) {
            return "";
        }
        return ngay.format(DINH_DANG_NGAY);
    }

    public static LocalDate docNgay(String chuoi) {
        chuoi = chuoi.trim();
        if (chuoi.isEmpty()) {
            return null;
        }
        return LocalDate.parse(chuoi, DINH_DANG_NGAY);
    }

    public static long docSo(String chuoi) {
        chuoi = chuoi.replaceAll("[^0-9]", "");
        if (chuoi.isEmpty()) {
            return 0;
        }
        return Long.parseLong(chuoi);
    }

    public static String dinhDangTien(long tien) {
        return DINH_DANG_TIEN.format(tien);
    }

    public static void datSo(PreparedStatement ps, int viTri, String chuoi) throws SQLException {
        chuoi = chuoi.trim();
        if (chuoi.isEmpty()) {
            ps.setNull(viTri, Types.INTEGER);
        } else {
            ps.setInt(viTri, Integer.parseInt(chuoi));
        }
    }

    public static void napBang(JTable bang, String sql, Object... thamSo) {
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < thamSo.length; i++) {
                ps.setObject(i + 1, thamSo[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int soCot = md.getColumnCount();
                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                for (int i = 1; i <= soCot; i++) {
                    model.addColumn(md.getColumnLabel(i));
                }
                while (rs.next()) {
                    Object[] dong = new Object[soCot];
                    for (int i = 1; i <= soCot; i++) {
                        Object giaTri = rs.getObject(i);
                        if (giaTri instanceof java.sql.Date) {
                            dong[i - 1] = ngayRaChuoi(((java.sql.Date) giaTri).toLocalDate());
                        } else if (giaTri instanceof BigDecimal) {
                            dong[i - 1] = DINH_DANG_TIEN.format(giaTri);
                        } else {
                            dong[i - 1] = giaTri;
                        }
                    }
                    model.addRow(dong);
                }
                bang.setModel(model);
            }
        } catch (SQLException e) {
            baoLoi(SwingUtilities.getWindowAncestor(bang), e);
        }
    }

    public static List<MucChon> layMuc(String sql) throws SQLException {
        List<MucChon> ds = new ArrayList<>();
        try (Connection con = KetNoi.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new MucChon(rs.getString(1).trim(), rs.getString(2)));
            }
        }
        return ds;
    }

    public static void chonTheoTen(JComboBox<MucChon> cbo, String ten) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getTen().equals(ten)) {
                cbo.setSelectedIndex(i);
                return;
            }
        }
    }

    public static void themDong(JPanel p, int dong, String nhan, JComponent o) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = dong;
        p.add(new JLabel(nhan), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(o, g);
    }

    public static void thongBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void canhBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Chú ý", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean xacNhan(Component cha, String noiDung) {
        int chon = JOptionPane.showConfirmDialog(cha, noiDung, "Xác nhận", JOptionPane.YES_NO_OPTION);
        return chon == JOptionPane.YES_OPTION;
    }

    public static void baoLoi(Component cha, SQLException e) {
        String noiDung = e.getMessage();
        for (SQLException x = e; x != null; x = x.getNextException()) {
            if (x.getErrorCode() == 50000) {
                noiDung = x.getMessage();
                break;
            }
        }
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            noiDung = "Dữ liệu bị trùng (mã, tên hoặc số điện thoại đã có người dùng).";
        }
        JOptionPane.showMessageDialog(cha, noiDung, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
