package quanlythuvien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

public class FrmDocGia extends JFrame {
    private final JTextField txtMa = new JTextField(20);
    private final JTextField txtHoTen = new JTextField(20);
    private final JTextField txtNgaySinh = new JTextField(20);
    private final JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final JTextField txtDiaChi = new JTextField(20);
    private final JTextField txtSDT = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtNgayLap = new JTextField(20);
    private final JTextField txtNgayHet = new JTextField(20);
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Tạm khóa", "Hết hạn"});
    private final JTextField txtTim = new JTextField(25);
    private final JTable tblDocGia = new JTable();

    public FrmDocGia() {
        setTitle("Quản lý độc giả");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dungGiaoDien();
        napBang();
        lamMoi();
    }

    private void dungGiaoDien() {
        JLabel tieuDe = new JLabel("QUẢN LÝ ĐỘC GIẢ", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(tieuDe, BorderLayout.NORTH);

        JPanel pNhap = new JPanel(new GridBagLayout());
        pNhap.setBorder(BorderFactory.createTitledBorder("Thông tin độc giả"));
        txtMa.setEditable(false);
        TienIch.themDong(pNhap, 0, "Mã độc giả", txtMa);
        TienIch.themDong(pNhap, 1, "Họ tên", txtHoTen);
        TienIch.themDong(pNhap, 2, "Ngày sinh (dd/MM/yyyy)", txtNgaySinh);
        TienIch.themDong(pNhap, 3, "Giới tính", cboGioiTinh);
        TienIch.themDong(pNhap, 4, "Địa chỉ", txtDiaChi);
        TienIch.themDong(pNhap, 5, "Số điện thoại", txtSDT);
        TienIch.themDong(pNhap, 6, "Email", txtEmail);
        TienIch.themDong(pNhap, 7, "Ngày lập thẻ", txtNgayLap);
        TienIch.themDong(pNhap, 8, "Ngày hết hạn", txtNgayHet);
        TienIch.themDong(pNhap, 9, "Trạng thái thẻ", cboTrangThai);
        JPanel west = new JPanel(new BorderLayout());
        west.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        west.add(pNhap, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);

        JButton btnTim = new JButton("Tìm kiếm");
        JButton btnTatCa = new JButton("Hiện tất cả");
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTim.add(new JLabel("Tìm theo mã, họ tên, số điện thoại:"));
        pTim.add(txtTim);
        pTim.add(btnTim);
        pTim.add(btnTatCa);

        tblDocGia.setRowHeight(24);
        tblDocGia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDocGia.getTableHeader().setReorderingAllowed(false);

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
        giua.add(new JScrollPane(tblDocGia), BorderLayout.CENTER);
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
        tblDocGia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThongTin();
            }
        });
    }

    private void napBang() {
        String tu = "%" + txtTim.getText().trim() + "%";
        String sql = "SELECT MaDG AS [Mã ĐG], HoTen AS [Họ tên], NgaySinh AS [Ngày sinh], GioiTinh AS [Giới tính], "
                + "DiaChi AS [Địa chỉ], SDT AS [SĐT], Email, NgayLapThe AS [Ngày lập thẻ], "
                + "NgayHetHan AS [Ngày hết hạn], TrangThai AS [Trạng thái] "
                + "FROM DocGia WHERE MaDG LIKE ? OR HoTen LIKE ? OR SDT LIKE ? ORDER BY MaDG";
        TienIch.napBang(tblDocGia, sql, tu, tu, tu);
    }

    private String o(int dong, int cot) {
        Object giaTri = tblDocGia.getValueAt(dong, cot);
        if (giaTri == null) {
            return "";
        }
        return giaTri.toString();
    }

    private void hienThongTin() {
        int d = tblDocGia.getSelectedRow();
        if (d < 0) {
            return;
        }
        txtMa.setText(o(d, 0));
        txtHoTen.setText(o(d, 1));
        txtNgaySinh.setText(o(d, 2));
        cboGioiTinh.setSelectedItem(o(d, 3));
        txtDiaChi.setText(o(d, 4));
        txtSDT.setText(o(d, 5));
        txtEmail.setText(o(d, 6));
        txtNgayLap.setText(o(d, 7));
        txtNgayHet.setText(o(d, 8));
        cboTrangThai.setSelectedItem(o(d, 9));
    }

    private boolean kiemTra() {
        if (txtHoTen.getText().trim().isEmpty()) {
            TienIch.canhBao(this, "Chưa nhập họ tên");
            txtHoTen.requestFocus();
            return false;
        }
        if (!txtSDT.getText().trim().matches("\\d{9,11}")) {
            TienIch.canhBao(this, "Số điện thoại phải gồm 9 đến 11 chữ số");
            txtSDT.requestFocus();
            return false;
        }
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches(".+@.+\\..+")) {
            TienIch.canhBao(this, "Email không đúng định dạng");
            txtEmail.requestFocus();
            return false;
        }
        try {
            TienIch.docNgay(txtNgaySinh.getText());
            LocalDate lap = TienIch.docNgay(txtNgayLap.getText());
            LocalDate het = TienIch.docNgay(txtNgayHet.getText());
            if (lap == null || het == null) {
                TienIch.canhBao(this, "Phải nhập ngày lập thẻ và ngày hết hạn");
                return false;
            }
            if (!het.isAfter(lap)) {
                TienIch.canhBao(this, "Ngày hết hạn phải sau ngày lập thẻ");
                return false;
            }
        } catch (DateTimeParseException e) {
            TienIch.canhBao(this, "Ngày phải nhập theo dạng dd/MM/yyyy, ví dụ 25/12/2026");
            return false;
        }
        return true;
    }

    private void datThamSo(PreparedStatement ps, String ma) throws SQLException {
        LocalDate ngaySinh = TienIch.docNgay(txtNgaySinh.getText());
        String diaChi = txtDiaChi.getText().trim();
        String email = txtEmail.getText().trim();
        ps.setString(1, txtHoTen.getText().trim());
        if (ngaySinh == null) {
            ps.setNull(2, Types.DATE);
        } else {
            ps.setDate(2, java.sql.Date.valueOf(ngaySinh));
        }
        ps.setString(3, cboGioiTinh.getSelectedItem().toString());
        ps.setString(4, diaChi.isEmpty() ? null : diaChi);
        ps.setString(5, txtSDT.getText().trim());
        ps.setString(6, email.isEmpty() ? null : email);
        ps.setDate(7, java.sql.Date.valueOf(TienIch.docNgay(txtNgayLap.getText())));
        ps.setDate(8, java.sql.Date.valueOf(TienIch.docNgay(txtNgayHet.getText())));
        ps.setString(9, cboTrangThai.getSelectedItem().toString());
        ps.setString(10, ma);
    }

    private void them() {
        if (!kiemTra()) {
            return;
        }
        String sql = "INSERT INTO DocGia (HoTen, NgaySinh, GioiTinh, DiaChi, SDT, Email, NgayLapThe, NgayHetHan, TrangThai, MaDG) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            String ma = KetNoi.taoMa("DocGia", "MaDG", "DG", 4);
            try (Connection con = KetNoi.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                datThamSo(ps, ma);
                ps.executeUpdate();
            }
            TienIch.thongBao(this, "Đã thêm độc giả " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Chọn một độc giả trong bảng để sửa");
            return;
        }
        if (!kiemTra()) {
            return;
        }
        String sql = "UPDATE DocGia SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, DiaChi = ?, SDT = ?, Email = ?, "
                + "NgayLapThe = ?, NgayHetHan = ?, TrangThai = ? WHERE MaDG = ?";
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            datThamSo(ps, ma);
            ps.executeUpdate();
            TienIch.thongBao(this, "Đã cập nhật độc giả " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void xoa() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Chọn một độc giả trong bảng để xóa");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa độc giả " + ma + " - " + txtHoTen.getText() + "?")) {
            return;
        }
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM DocGia WHERE MaDG = ?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
            TienIch.thongBao(this, "Đã xóa độc giả " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                TienIch.canhBao(this, "Độc giả này đã có phiếu mượn nên không xóa được.\nCó thể đổi trạng thái thẻ sang Tạm khóa.");
            } else {
                TienIch.baoLoi(this, e);
            }
        }
    }

    private void lamMoi() {
        tblDocGia.clearSelection();
        txtMa.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtDiaChi.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtNgayLap.setText(TienIch.ngayRaChuoi(LocalDate.now()));
        txtNgayHet.setText(TienIch.ngayRaChuoi(LocalDate.now().plusYears(1)));
        cboTrangThai.setSelectedIndex(0);
        txtHoTen.requestFocus();
    }
}
