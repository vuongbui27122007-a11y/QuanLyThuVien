package quanlythuvien;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class FrmTheLoai extends JFrame {
    private final JTextField txtMa = new JTextField(20);
    private final JTextField txtTen = new JTextField(20);
    private final JTextField txtMoTa = new JTextField(20);
    private final JTextField txtTim = new JTextField(20);
    private final JTable tblTheLoai = new JTable();

    public FrmTheLoai() {
        setTitle("Quản lý thể loại");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dungGiaoDien();
        napBang();
        lamMoi();
    }

    private void dungGiaoDien() {
        JLabel tieuDe = new JLabel("QUẢN LÝ THỂ LOẠI", SwingConstants.CENTER);
        tieuDe.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tieuDe.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(tieuDe, BorderLayout.NORTH);

        JPanel pNhap = new JPanel(new GridBagLayout());
        pNhap.setBorder(BorderFactory.createTitledBorder("Thông tin thể loại"));
        txtMa.setEditable(false);
        TienIch.themDong(pNhap, 0, "Mã thể loại", txtMa);
        TienIch.themDong(pNhap, 1, "Tên thể loại", txtTen);
        TienIch.themDong(pNhap, 2, "Mô tả", txtMoTa);
        JPanel west = new JPanel(new BorderLayout());
        west.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        west.add(pNhap, BorderLayout.NORTH);
        add(west, BorderLayout.WEST);

        JButton btnTim = new JButton("Tìm kiếm");
        JButton btnTatCa = new JButton("Hiện tất cả");
        JPanel pTim = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTim.add(new JLabel("Tìm theo tên:"));
        pTim.add(txtTim);
        pTim.add(btnTim);
        pTim.add(btnTatCa);

        tblTheLoai.setRowHeight(24);
        tblTheLoai.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTheLoai.getTableHeader().setReorderingAllowed(false);

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
        giua.add(new JScrollPane(tblTheLoai), BorderLayout.CENTER);
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
        tblTheLoai.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThongTin();
            }
        });
    }

    private void napBang() {
        String sql = "SELECT tl.MaTL AS [Mã TL], tl.TenTL AS [Tên thể loại], tl.MoTa AS [Mô tả], "
                + "(SELECT COUNT(*) FROM Sach s WHERE s.MaTL = tl.MaTL) AS [Số đầu sách] "
                + "FROM TheLoai tl WHERE tl.TenTL LIKE ? ORDER BY tl.MaTL";
        TienIch.napBang(tblTheLoai, sql, "%" + txtTim.getText().trim() + "%");
    }

    private String o(int dong, int cot) {
        Object giaTri = tblTheLoai.getValueAt(dong, cot);
        if (giaTri == null) {
            return "";
        }
        return giaTri.toString();
    }

    private void hienThongTin() {
        int d = tblTheLoai.getSelectedRow();
        if (d < 0) {
            return;
        }
        txtMa.setText(o(d, 0));
        txtTen.setText(o(d, 1));
        txtMoTa.setText(o(d, 2));
    }

    private void them() {
        if (txtTen.getText().trim().isEmpty()) {
            TienIch.canhBao(this, "Chưa nhập tên thể loại");
            return;
        }
        String moTa = txtMoTa.getText().trim();
        try {
            String ma = KetNoi.taoMa("TheLoai", "MaTL", "TL", 3);
            try (Connection con = KetNoi.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO TheLoai (MaTL, TenTL, MoTa) VALUES (?, ?, ?)")) {
                ps.setString(1, ma);
                ps.setString(2, txtTen.getText().trim());
                ps.setString(3, moTa.isEmpty() ? null : moTa);
                ps.executeUpdate();
            }
            TienIch.thongBao(this, "Đã thêm thể loại " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void sua() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Chọn một thể loại trong bảng để sửa");
            return;
        }
        if (txtTen.getText().trim().isEmpty()) {
            TienIch.canhBao(this, "Chưa nhập tên thể loại");
            return;
        }
        String moTa = txtMoTa.getText().trim();
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE TheLoai SET TenTL = ?, MoTa = ? WHERE MaTL = ?")) {
            ps.setString(1, txtTen.getText().trim());
            ps.setString(2, moTa.isEmpty() ? null : moTa);
            ps.setString(3, ma);
            ps.executeUpdate();
            TienIch.thongBao(this, "Đã cập nhật thể loại " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            TienIch.baoLoi(this, e);
        }
    }

    private void xoa() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) {
            TienIch.canhBao(this, "Chọn một thể loại trong bảng để xóa");
            return;
        }
        if (!TienIch.xacNhan(this, "Xóa thể loại " + txtTen.getText() + "?")) {
            return;
        }
        try (Connection con = KetNoi.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TheLoai WHERE MaTL = ?")) {
            ps.setString(1, ma);
            ps.executeUpdate();
            TienIch.thongBao(this, "Đã xóa thể loại " + ma);
            napBang();
            lamMoi();
        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                TienIch.canhBao(this, "Thể loại này vẫn còn sách nên không xóa được");
            } else {
                TienIch.baoLoi(this, e);
            }
        }
    }

    private void lamMoi() {
        tblTheLoai.clearSelection();
        txtMa.setText("");
        txtTen.setText("");
        txtMoTa.setText("");
        txtTen.requestFocus();
    }
}
