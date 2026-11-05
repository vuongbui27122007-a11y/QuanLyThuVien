package quanlythuvien;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class FrmTraSach extends JFrame {
    private final JTextField txtTim = new JTextField(25);
    private final JCheckBox chkDangMuon = new JCheckBox("Chỉ hiện sách đang mượn", true);
    private final JTable tblCT = new JTable();

    public FrmTraSach() {
        setTitle("Trả sách");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dungGiaoDien();
        napBang();
    }

    private void dungGiaoDien() {
        JLabel tieuDe = new JLabel("TRẢ SÁCH VÀ QUẢN LÝ PHIẾU MƯỢN", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(tieuDe, BorderLayout.NORTH);

        JButton btnTim = new JButton("Tìm kiếm");
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTim.add(new JLabel("Tìm theo mã phiếu, độc giả, tên sách:"));
        pTim.add(txtTim);
        pTim.add(chkDangMuon);
        pTim.add(btnTim);

        tblCT.setRowHeight(24);
        tblCT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCT.getTableHeader().setReorderingAllowed(false);
        tblCT.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    comp.setForeground(quaHan(r) ? Color.RED : Color.BLACK);
                }
                return comp;
            }
        });

        JButton btnTra = new JButton("Trả sách");
        JButton btnMat = new JButton("Báo mất sách");
        JButton btnXoaPhieu = new JButton("Xóa phiếu mượn");
        JPanel pNut = new JPanel(new FlowLayout());
        pNut.add(btnTra);
        pNut.add(btnMat);
        pNut.add(btnXoaPhieu);

        JPanel giua = new JPanel(new BorderLayout(5, 5));
        giua.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        giua.add(pTim, BorderLayout.NORTH);
        giua.add(new JScrollPane(tblCT), BorderLayout.CENTER);
        giua.add(pNut, BorderLayout.SOUTH);
        add(giua, BorderLayout.CENTER);

        btnTim.addActionListener(e -> napBang());
        txtTim.addActionListener(e -> napBang());
        chkDangMuon.addActionListener(e -> napBang());
        btnTra.addActionListener(e -> traSach());
        btnMat.addActionListener(e -> matSach());
        btnXoaPhieu.addActionListener(e -> xoaPhieu());
    }

    private boolean quaHan(int dong) {
        if (dong >= tblCT.getRowCount() || !o(dong, 7).equals("Đang mượn")) {
            return false;
        }
        try {
            return TienIch.docNgay(o(dong, 5)).isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String o(int dong, int cot) {
        Object giaTri = tblCT.getValueAt(dong, cot);
        if (giaTri == null) {
            return "";
        }
        return giaTri.toString();
    }

    private void napBang() {
        String tu = "%" + txtTim.getText().trim() + "%";
        String sql = "SELECT ct.MaPM AS [Mã phiếu], ct.MaSach AS [Mã sách], dg.HoTen AS [Độc giả], s.TenSach AS [Tên sách], "
                + "pm.NgayMuon AS [Ngày mượn], pm.HanTra AS [Hạn trả], ct.NgayTra AS [Ngày trả], "
                + "ct.TinhTrang AS [Tình trạng], ct.TienPhat AS [Tiền phạt] "
                + "FROM ChiTietPhieuMuon ct JOIN PhieuMuon pm ON ct.MaPM = pm.MaPM "
                + "JOIN DocGia dg ON pm.MaDG = dg.MaDG JOIN Sach s ON ct.MaSach = s.MaSach "
                + "WHERE (ct.MaPM LIKE ? OR dg.HoTen LIKE ? OR s.TenSach LIKE ?)";
        if (chkDangMuon.isSelected()) {
            sql += " AND ct.TinhTrang = N'Đang mượn'";
        }
        sql += " ORDER BY pm.HanTra, ct.MaPM, ct.MaSach";
        TienIch.napBang(tblCT, sql, tu, tu, tu);
    }

    private long layTienPhat(String maPM, String maSach) throws SQLException {
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT TienPhat FROM ChiTietPhieuMuon WHERE MaPM = ? AND MaSach = ?")) {
            ps.setString(1, maPM);
            ps.setString(2, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    private void traSach() {
        int d = tblCT.getSelectedRow();
        if (d < 0) {
            TienIch.canhBao(this, "Chọn một dòng trong bảng");
            return;
        }
        if (!o(d, 7).equals("Đang mượn")) {
            TienIch.canhBao(this, "Cuốn này đã được xử lý rồi");
            return;
        }
        String maPM = o(d, 0);
        String maSach = o(d, 1);
        LocalDate homNay = LocalDate.now();
        long tre = ChronoUnit.DAYS.between(TienIch.docNgay(o(d, 5)), homNay);
        String hoi = "Xác nhận trả sách \"" + o(d, 3) + "\"?";
        if (tre > 0) {
            hoi += "\nĐộc giả trả trễ " + tre + " ngày.";
        }
        if (!TienIch.xacNhan(this, hoi)) {
            return;
        }
        try (Connection con = KetNoi.getConnection();
             CallableStatement cs = con.prepareCall("{call sp_TraSach(?, ?, ?)}")) {
            cs.setString(1, maPM);
            cs.setString(2, maSach);
            cs.setDate(3, java.sql.Date.valueOf(homNay));
            cs.execute();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            return;
        }
        try {
            long phat = layTienPhat(maPM, maSach);
            String thongBao = "Đã trả sách.";
            if (phat > 0) {
                thongBao += " Tiền phạt: " + TienIch.dinhDangTien(phat) + " đồng.";
            }
            TienIch.thongBao(this, thongBao);
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
        napBang();
    }

    private void matSach() {
        int d = tblCT.getSelectedRow();
        if (d < 0) {
            TienIch.canhBao(this, "Chọn một dòng trong bảng");
            return;
        }
        if (!o(d, 7).equals("Đang mượn")) {
            TienIch.canhBao(this, "Cuốn này đã được xử lý rồi");
            return;
        }
        String maPM = o(d, 0);
        String maSach = o(d, 1);
        if (!TienIch.xacNhan(this, "Báo mất cuốn \"" + o(d, 3) + "\"?\nĐộc giả phải đền gấp đôi giá bìa.")) {
            return;
        }
        String sql = "UPDATE ChiTietPhieuMuon SET TinhTrang = N'Mất sách', NgayTra = ?, "
                + "TienPhat = (SELECT GiaBia * 2 FROM Sach WHERE MaSach = ?) "
                + "WHERE MaPM = ? AND MaSach = ? AND TinhTrang = N'Đang mượn'";
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            ps.setString(2, maSach);
            ps.setString(3, maPM);
            ps.setString(4, maSach);
            ps.executeUpdate();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            return;
        }
        try {
            TienIch.thongBao(this, "Đã ghi nhận mất sách. Tiền đền: " + TienIch.dinhDangTien(layTienPhat(maPM, maSach)) + " đồng.");
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
        napBang();
    }

    private void xoaPhieu() {
        int d = tblCT.getSelectedRow();
        if (d < 0) {
            TienIch.canhBao(this, "Chọn một dòng thuộc phiếu cần xóa");
            return;
        }
        String maPM = o(d, 0);
        if (!TienIch.xacNhan(this, "Xóa toàn bộ phiếu mượn " + maPM + "?\nSách chưa trả trong phiếu sẽ được cộng lại vào kho.")) {
            return;
        }
        String capNhatKho = "UPDATE Sach SET SoLuongCon = SoLuongCon + 1 WHERE MaSach IN "
                + "(SELECT MaSach FROM ChiTietPhieuMuon WHERE MaPM = ? AND TinhTrang = N'Đang mượn')";
        try (Connection con = KetNoi.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(capNhatKho)) {
                    ps.setString(1, maPM);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM PhieuMuon WHERE MaPM = ?")) {
                    ps.setString(1, maPM);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
            TienIch.thongBao(this, "Đã xóa phiếu " + maPM);
            napBang();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }
}
