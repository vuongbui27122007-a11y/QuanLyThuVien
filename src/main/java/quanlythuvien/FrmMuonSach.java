package quanlythuvien;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class FrmMuonSach extends JFrame {
    private final JTextField txtMaPM = new JTextField(20);
    private final JComboBox<MucChon> cboDocGia = new JComboBox<>();
    private final JComboBox<MucChon> cboNhanVien = new JComboBox<>();
    private final JTextField txtNgayMuon = new JTextField(20);
    private final JTextField txtHanTra = new JTextField(20);
    private final JTextField txtGhiChu = new JTextField(20);
    private final JComboBox<MucChon> cboSach = new JComboBox<>();
    private final DefaultTableModel mdChon = new DefaultTableModel(new String[]{"Mã sách", "Sách"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblChon = new JTable(mdChon);
    private final JTable tblPhieu = new JTable();

    public FrmMuonSach() {
        setTitle("Lập phiếu mượn");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dungGiaoDien();
        napDanhMuc();
        lamMoi();
    }

    private void dungGiaoDien() {
        JLabel tieuDe = new JLabel("LẬP PHIẾU MƯỢN SÁCH", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(tieuDe, BorderLayout.NORTH);

        JPanel pNhap = new JPanel(new GridBagLayout());
        pNhap.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu"));
        txtMaPM.setEditable(false);
        TienIch.themDong(pNhap, 0, "Mã phiếu", txtMaPM);
        TienIch.themDong(pNhap, 1, "Độc giả", cboDocGia);
        TienIch.themDong(pNhap, 2, "Nhân viên lập", cboNhanVien);
        TienIch.themDong(pNhap, 3, "Ngày mượn", txtNgayMuon);
        TienIch.themDong(pNhap, 4, "Hạn trả", txtHanTra);
        TienIch.themDong(pNhap, 5, "Ghi chú", txtGhiChu);
        JPanel west = new JPanel(new BorderLayout());
        west.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        west.add(pNhap, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);

        JButton btnThemSach = new JButton("Thêm vào phiếu");
        JPanel pChon = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pChon.add(new JLabel("Chọn sách:"));
        pChon.add(cboSach);
        pChon.add(btnThemSach);

        tblChon.setRowHeight(24);
        tblChon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblChon.getTableHeader().setReorderingAllowed(false);

        JButton btnBoSach = new JButton("Bỏ sách đã chọn");
        JButton btnLuu = new JButton("Lưu phiếu mượn");
        JButton btnLamMoi = new JButton("Làm mới");
        JPanel pNut = new JPanel(new FlowLayout());
        pNut.add(btnBoSach);
        pNut.add(btnLuu);
        pNut.add(btnLamMoi);

        JPanel giua = new JPanel(new BorderLayout(5, 5));
        giua.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        giua.add(pChon, BorderLayout.NORTH);
        giua.add(new JScrollPane(tblChon), BorderLayout.CENTER);
        giua.add(pNut, BorderLayout.SOUTH);
        add(giua, BorderLayout.CENTER);

        tblPhieu.setRowHeight(22);
        tblPhieu.getTableHeader().setReorderingAllowed(false);
        JScrollPane cuonPhieu = new JScrollPane(tblPhieu);
        cuonPhieu.setPreferredSize(new Dimension(100, 200));
        cuonPhieu.setBorder(BorderFactory.createTitledBorder("Phiếu mượn gần đây"));
        JPanel duoi = new JPanel(new BorderLayout());
        duoi.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        duoi.add(cuonPhieu, BorderLayout.CENTER);
        add(duoi, BorderLayout.SOUTH);

        btnThemSach.addActionListener(e -> themSach());
        btnBoSach.addActionListener(e -> boSach());
        btnLuu.addActionListener(e -> luuPhieu());
        btnLamMoi.addActionListener(e -> lamMoi());
    }

    private void napDanhMuc() {
        try {
            for (MucChon m : TienIch.layMuc("SELECT MaDG, MaDG + N' - ' + HoTen FROM DocGia ORDER BY MaDG")) {
                cboDocGia.addItem(m);
            }
            for (MucChon m : TienIch.layMuc("SELECT MaNV, MaNV + N' - ' + HoTen FROM NhanVien ORDER BY MaNV")) {
                cboNhanVien.addItem(m);
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void napSach() {
        cboSach.removeAllItems();
        String sql = "SELECT MaSach, TenSach + N' (' + MaSach + N', còn ' + CAST(SoLuongCon AS NVARCHAR(10)) + N')' "
                + "FROM Sach WHERE SoLuongCon > 0 ORDER BY TenSach";
        try {
            for (MucChon m : TienIch.layMuc(sql)) {
                cboSach.addItem(m);
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void napPhieu() {
        String sql = "SELECT TOP 30 pm.MaPM AS [Mã phiếu], dg.HoTen AS [Độc giả], nv.HoTen AS [Nhân viên lập], "
                + "pm.NgayMuon AS [Ngày mượn], pm.HanTra AS [Hạn trả], COUNT(ct.MaSach) AS [Số sách], pm.GhiChu AS [Ghi chú] "
                + "FROM PhieuMuon pm JOIN DocGia dg ON pm.MaDG = dg.MaDG JOIN NhanVien nv ON pm.MaNV = nv.MaNV "
                + "LEFT JOIN ChiTietPhieuMuon ct ON ct.MaPM = pm.MaPM "
                + "GROUP BY pm.MaPM, dg.HoTen, nv.HoTen, pm.NgayMuon, pm.HanTra, pm.GhiChu "
                + "ORDER BY pm.MaPM DESC";
        TienIch.napBang(tblPhieu, sql);
    }

    private void themSach() {
        MucChon sach = (MucChon) cboSach.getSelectedItem();
        if (sach == null) {
            TienIch.canhBao(this, "Trong kho không còn cuốn nào để mượn");
            return;
        }
        for (int i = 0; i < mdChon.getRowCount(); i++) {
            if (mdChon.getValueAt(i, 0).equals(sach.getMa())) {
                TienIch.canhBao(this, "Sách này đã có trong phiếu, mỗi đầu sách chỉ mượn một cuốn");
                return;
            }
        }
        mdChon.addRow(new Object[]{sach.getMa(), sach.getTen()});
    }

    private void boSach() {
        int d = tblChon.getSelectedRow();
        if (d < 0) {
            TienIch.canhBao(this, "Chọn một dòng trong danh sách để bỏ");
            return;
        }
        mdChon.removeRow(d);
    }

    private boolean docGiaDuocMuon(String maDG, LocalDate ngayMuon) throws SQLException {
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT TrangThai, NgayHetHan FROM DocGia WHERE MaDG = ?")) {
            ps.setString(1, maDG);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                if (!rs.getString(1).equals("Hoạt động")) {
                    TienIch.canhBao(this, "Thẻ độc giả đang ở trạng thái \"" + rs.getString(1) + "\", không được mượn sách");
                    return false;
                }
                if (rs.getDate(2).toLocalDate().isBefore(ngayMuon)) {
                    TienIch.canhBao(this, "Thẻ độc giả đã hết hạn từ ngày " + TienIch.ngayRaChuoi(rs.getDate(2).toLocalDate()));
                    return false;
                }
            }
        }
        return true;
    }

    private void luuPhieu() {
        MucChon docGia = (MucChon) cboDocGia.getSelectedItem();
        MucChon nhanVien = (MucChon) cboNhanVien.getSelectedItem();
        if (docGia == null || nhanVien == null) {
            TienIch.canhBao(this, "Chưa chọn độc giả hoặc nhân viên");
            return;
        }
        if (mdChon.getRowCount() == 0) {
            TienIch.canhBao(this, "Chưa chọn cuốn sách nào");
            return;
        }
        LocalDate ngayMuon;
        LocalDate hanTra;
        try {
            ngayMuon = TienIch.docNgay(txtNgayMuon.getText());
            hanTra = TienIch.docNgay(txtHanTra.getText());
        } catch (DateTimeParseException e) {
            TienIch.canhBao(this, "Ngày phải nhập theo dạng dd/MM/yyyy, ví dụ 25/12/2026");
            return;
        }
        if (ngayMuon == null || hanTra == null) {
            TienIch.canhBao(this, "Phải nhập ngày mượn và hạn trả");
            return;
        }
        if (!hanTra.isAfter(ngayMuon)) {
            TienIch.canhBao(this, "Hạn trả phải sau ngày mượn");
            return;
        }
        if (ChronoUnit.DAYS.between(ngayMuon, hanTra) > 30) {
            TienIch.canhBao(this, "Mỗi phiếu chỉ được mượn tối đa 30 ngày");
            return;
        }
        try {
            if (!docGiaDuocMuon(docGia.getMa(), ngayMuon)) {
                return;
            }
            String ma = KetNoi.taoMa("PhieuMuon", "MaPM", "PM", 5);
            try (Connection con = KetNoi.getConnection()) {
                con.setAutoCommit(false);
                try {
                    String ghiChu = txtGhiChu.getText().trim();
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO PhieuMuon (MaPM, MaDG, MaNV, NgayMuon, HanTra, GhiChu) VALUES (?, ?, ?, ?, ?, ?)")) {
                        ps.setString(1, ma);
                        ps.setString(2, docGia.getMa());
                        ps.setString(3, nhanVien.getMa());
                        ps.setDate(4, java.sql.Date.valueOf(ngayMuon));
                        ps.setDate(5, java.sql.Date.valueOf(hanTra));
                        ps.setString(6, ghiChu.isEmpty() ? null : ghiChu);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO ChiTietPhieuMuon (MaPM, MaSach) VALUES (?, ?)")) {
                        for (int i = 0; i < mdChon.getRowCount(); i++) {
                            ps.setString(1, ma);
                            ps.setString(2, mdChon.getValueAt(i, 0).toString());
                            ps.executeUpdate();
                        }
                    }
                    con.commit();
                } catch (SQLException e) {
                    try {
                        con.rollback();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                    throw e;
                }
            }
            TienIch.thongBao(this, "Đã lập phiếu mượn " + ma);
            lamMoi();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
            napSach();
        }
    }

    private void lamMoi() {
        try {
            txtMaPM.setText(KetNoi.taoMa("PhieuMuon", "MaPM", "PM", 5));
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
        txtNgayMuon.setText(TienIch.ngayRaChuoi(LocalDate.now()));
        txtHanTra.setText(TienIch.ngayRaChuoi(LocalDate.now().plusDays(14)));
        txtGhiChu.setText("");
        mdChon.setRowCount(0);
        if (cboDocGia.getItemCount() > 0) {
            cboDocGia.setSelectedIndex(0);
        }
        if (cboNhanVien.getItemCount() > 0) {
            cboNhanVien.setSelectedIndex(0);
        }
        napSach();
        napPhieu();
    }
}
