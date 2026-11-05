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
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class FrmSach extends JFrame {
    private final JTextField txtMa = new JTextField(20);
    private final JTextField txtTen = new JTextField(20);
    private final JComboBox<MucChon> cboTheLoai = new JComboBox<>();
    private final JComboBox<MucChon> cboNXB = new JComboBox<>();
    private final JTextField txtNamXB = new JTextField(20);
    private final JTextField txtSoTrang = new JTextField(20);
    private final JTextField txtGiaBia = new JTextField(20);
    private final JTextField txtSoLuong = new JTextField(20);
    private final JTextField txtViTri = new JTextField(20);
    private final DefaultListModel<MucChon> dsTacGia = new DefaultListModel<>();
    private final JList<MucChon> lstTacGia = new JList<>(dsTacGia);
    private final JTextField txtTim = new JTextField(25);
    private final JTable tblSach = new JTable();

    public FrmSach() {
        setTitle("Quản lý sách");
        setSize(1200, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dungGiaoDien();
        napDanhMuc();
        napBang();
        lamMoi();
    }

    private void dungGiaoDien() {
        JLabel tieuDe = new JLabel("QUẢN LÝ SÁCH", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(tieuDe, BorderLayout.NORTH);

        JPanel pNhap = new JPanel(new GridBagLayout());
        pNhap.setBorder(BorderFactory.createTitledBorder("Thông tin sách"));
        txtMa.setEditable(false);
        lstTacGia.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane cuonTacGia = new JScrollPane(lstTacGia);
        cuonTacGia.setPreferredSize(new Dimension(200, 110));
        TienIch.themDong(pNhap, 0, "Mã sách", txtMa);
        TienIch.themDong(pNhap, 1, "Tên sách", txtTen);
        TienIch.themDong(pNhap, 2, "Thể loại", cboTheLoai);
        TienIch.themDong(pNhap, 3, "Nhà xuất bản", cboNXB);
        TienIch.themDong(pNhap, 4, "Năm xuất bản", txtNamXB);
        TienIch.themDong(pNhap, 5, "Số trang", txtSoTrang);
        TienIch.themDong(pNhap, 6, "Giá bìa", txtGiaBia);
        TienIch.themDong(pNhap, 7, "Tổng số lượng", txtSoLuong);
        TienIch.themDong(pNhap, 8, "Vị trí kệ", txtViTri);
        TienIch.themDong(pNhap, 9, "Tác giả", cuonTacGia);
        JPanel west = new JPanel(new BorderLayout());
        west.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        west.add(pNhap, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);

        JButton btnTim = new JButton("Tìm kiếm");
        JButton btnTatCa = new JButton("Hiện tất cả");
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTim.add(new JLabel("Tìm theo tên sách, thể loại, tác giả:"));
        pTim.add(txtTim);
        pTim.add(btnTim);
        pTim.add(btnTatCa);

        tblSach.setRowHeight(24);
        tblSach.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSach.getTableHeader().setReorderingAllowed(false);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");
        JPanel pNut = new JPanel(new FlowLayout());
        pNut.add(btnThem);
        pNut.add(btnSua);
        pNut.add(btnXoa);
        pNut.add(btnLamMoi);

        JPanel giua = new JPanel(new BorderLayout(5, 5));
        giua.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        giua.add(pTim, BorderLayout.NORTH);
        giua.add(new JScrollPane(tblSach), BorderLayout.CENTER);
        giua.add(pNut, BorderLayout.SOUTH);
        add(giua, BorderLayout.CENTER);

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTim.addActionListener(e -> napBang());
        txtTim.addActionListener(e -> napBang());
        btnTatCa.addActionListener(e -> {
            txtTim.setText("");
            napBang();
        });
        tblSach.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThongTin();
            }
        });
    }

    private void napDanhMuc() {
        try {
            for (MucChon m : TienIch.layMuc("SELECT MaTL, TenTL FROM TheLoai ORDER BY TenTL")) {
                cboTheLoai.addItem(m);
            }
            for (MucChon m : TienIch.layMuc("SELECT MaNXB, TenNXB FROM NhaXuatBan ORDER BY TenNXB")) {
                cboNXB.addItem(m);
            }
            for (MucChon m : TienIch.layMuc("SELECT MaTG, TenTG FROM TacGia ORDER BY TenTG")) {
                dsTacGia.addElement(m);
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void napBang() {
        String tu = "%" + txtTim.getText().trim() + "%";
        String sql = "SELECT s.MaSach AS [Mã sách], s.TenSach AS [Tên sách], tl.TenTL AS [Thể loại], "
                + "nxb.TenNXB AS [NXB], s.NamXB AS [Năm XB], s.SoTrang AS [Số trang], s.GiaBia AS [Giá bìa], "
                + "s.SoLuongTong AS [Tổng], s.SoLuongCon AS [Còn], s.ViTri AS [Vị trí], "
                + "(SELECT STRING_AGG(tg.TenTG, N', ') FROM SachTacGia stg JOIN TacGia tg ON stg.MaTG = tg.MaTG "
                + "WHERE stg.MaSach = s.MaSach) AS [Tác giả] "
                + "FROM Sach s JOIN TheLoai tl ON s.MaTL = tl.MaTL JOIN NhaXuatBan nxb ON s.MaNXB = nxb.MaNXB "
                + "WHERE s.TenSach LIKE ? OR tl.TenTL LIKE ? OR EXISTS (SELECT 1 FROM SachTacGia x "
                + "JOIN TacGia y ON x.MaTG = y.MaTG WHERE x.MaSach = s.MaSach AND y.TenTG LIKE ?) "
                + "ORDER BY s.MaSach";
        TienIch.napBang(tblSach, sql, tu, tu, tu);
    }

    private String o(int dong, int cot) {
        Object giaTri = tblSach.getValueAt(dong, cot);
        if (giaTri == null) {
            return "";
        }
        return giaTri.toString();
    }

    private void hienThongTin() {
        int d = tblSach.getSelectedRow();
        if (d < 0) {
            return;
        }
        txtMa.setText(o(d, 0));
        txtTen.setText(o(d, 1));
        TienIch.chonTheoTen(cboTheLoai, o(d, 2));
        TienIch.chonTheoTen(cboNXB, o(d, 3));
        txtNamXB.setText(o(d, 4));
        txtSoTrang.setText(o(d, 5));
        txtGiaBia.setText(String.valueOf(TienIch.docSo(o(d, 6))));
        txtSoLuong.setText(o(d, 7));
        txtViTri.setText(o(d, 9));
        chonTacGia(o(d, 0));
    }

    private void chonTacGia(String maSach) {
        lstTacGia.clearSelection();
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT MaTG FROM SachTacGia WHERE MaSach = ?")) {
            ps.setString(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ma = rs.getString(1).trim();
                    for (int i = 0; i < dsTacGia.size(); i++) {
                        if (dsTacGia.get(i).getMa().equals(ma)) {
                            lstTacGia.addSelectionInterval(i, i);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private boolean kiemTra() {
        if (txtTen.getText().trim().isEmpty()) {
            TienIch.canhBao(this, "Chưa nhập tên sách");
            txtTen.requestFocus();
            return false;
        }
        if (cboTheLoai.getSelectedItem() == null || cboNXB.getSelectedItem() == null) {
            TienIch.canhBao(this, "Chưa chọn thể loại hoặc nhà xuất bản");
            return false;
        }
        try {
            String nam = txtNamXB.getText().trim();
            if (!nam.isEmpty()) {
                int n = Integer.parseInt(nam);
                if (n < 1900 || n > 2100) {
                    TienIch.canhBao(this, "Năm xuất bản phải từ 1900 đến 2100");
                    return false;
                }
            }
            String trang = txtSoTrang.getText().trim();
            if (!trang.isEmpty() && Integer.parseInt(trang) <= 0) {
                TienIch.canhBao(this, "Số trang phải lớn hơn 0");
                return false;
            }
            if (Integer.parseInt(txtSoLuong.getText().trim()) < 0) {
                TienIch.canhBao(this, "Số lượng không được âm");
                return false;
            }
        } catch (NumberFormatException e) {
            TienIch.canhBao(this, "Năm xuất bản, số trang và số lượng phải là số nguyên");
            return false;
        }
        return true;
    }

    private void datThamSo(PreparedStatement ps, String ma) throws SQLException {
        int tong = Integer.parseInt(txtSoLuong.getText().trim());
        String viTri = txtViTri.getText().trim();
        ps.setString(1, txtTen.getText().trim());
        ps.setString(2, ((MucChon) cboTheLoai.getSelectedItem()).getMa());
        ps.setString(3, ((MucChon) cboNXB.getSelectedItem()).getMa());
        TienIch.datSo(ps, 4, txtNamXB.getText());
        TienIch.datSo(ps, 5, txtSoTrang.getText());
        ps.setLong(6, TienIch.docSo(txtGiaBia.getText()));
        ps.setString(7, viTri.isEmpty() ? null : viTri);
        ps.setInt(8, tong);
        ps.setInt(9, tong);
        ps.setString(10, ma);
    }

    private void luuTacGia(Connection con, String ma) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM SachTacGia WHERE MaSach = ?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO SachTacGia (MaSach, MaTG) VALUES (?, ?)")) {
            for (MucChon tg : lstTacGia.getSelectedValuesList()) {
                ps.setString(1, ma);
                ps.setString(2, tg.getMa());
                ps.executeUpdate();
            }
        }
    }

    private void them() {
        if (!kiemTra()) {
            return;
        }
        String sql = "INSERT INTO Sach (TenSach, MaTL, MaNXB, NamXB, SoTrang, GiaBia, ViTri, SoLuongTong, SoLuongCon, MaSach) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = KetNoi.getConnection()) {
            String ma = KetNoi.taoMa("Sach", "MaSach", "S", 5);
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    datThamSo(ps, ma);
                    ps.executeUpdate();
                }
                luuTacGia(con, ma);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
            TienIch.thongBao(this, "Đã thêm sách " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Chọn một cuốn sách trong bảng để sửa");
            return;
        }
        if (!kiemTra()) {
            return;
        }
        String sql = "UPDATE Sach SET TenSach = ?, MaTL = ?, MaNXB = ?, NamXB = ?, SoTrang = ?, GiaBia = ?, ViTri = ?, "
                + "SoLuongCon = SoLuongCon + (? - SoLuongTong), SoLuongTong = ? WHERE MaSach = ?";
        try (Connection con = KetNoi.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    datThamSo(ps, ma);
                    ps.executeUpdate();
                }
                luuTacGia(con, ma);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
            TienIch.thongBao(this, "Đã cập nhật sách " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                TienIch.canhBao(this, "Tổng số lượng không được nhỏ hơn số cuốn đang cho mượn");
            } else {
                TienIch.baoLoi(this, e);
            }
        }
    }

    private void xoa() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Chọn một cuốn sách trong bảng để xóa");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa sách " + ma + " - " + txtTen.getText() + "?")) {
            return;
        }
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Sach WHERE MaSach = ?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
            TienIch.thongBao(this, "Đã xóa sách " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                TienIch.canhBao(this, "Sách này đã có trong phiếu mượn nên không xóa được");
            } else {
                TienIch.baoLoi(this, e);
            }
        }
    }

    private void lamMoi() {
        tblSach.clearSelection();
        txtMa.setText("");
        txtTen.setText("");
        txtNamXB.setText("");
        txtSoTrang.setText("");
        txtGiaBia.setText("");
        txtSoLuong.setText("");
        txtViTri.setText("");
        lstTacGia.clearSelection();
        if (cboTheLoai.getItemCount() > 0) {
            cboTheLoai.setSelectedIndex(0);
        }
        if (cboNXB.getItemCount() > 0) {
            cboNXB.setSelectedIndex(0);
        }
        txtTen.requestFocus();
    }
}
